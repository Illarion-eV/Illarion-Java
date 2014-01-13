/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * The Illarion Build Utility is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public License along with the Illarion Build Utility. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package illarion.build

import groovy.xml.MarkupBuilder
import illarion.build.imagepacker.ImagePacker
import illarion.common.data.Book
import illarion.common.util.Crypto
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import org.w3c.dom.Document
import org.xml.sax.SAXException

import javax.annotation.Nonnull
import javax.imageio.ImageIO
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * This converter mainly converts the PNG image files into a format optimized for OpenGL, in order to improve the speed
 * of loading the client. It also put the texture images together to image maps. And it checks the contents of the
 * archives and removes useless contents.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ResourceConverter extends DefaultTask {
    /**
     * The file names of the book files that were found but not handled yet.
     */
    @Nonnull
    private final def bookFiles = [] as List<File>

    /**
     * Crypto instance used to crypt the table files.
     */
    @Nonnull
    final def crypto = new Crypto();

    /**
     * The file names of the misc files that were found but not handled yet.
     */
    @Nonnull
    private final def miscFiles = [] as List<File>

    /**
     * The file that contains the private key to use
     */
    @InputFile
    def File privateKey;

    /**
     * The file names of the table files that were found but not handled yet.
     */
    @Nonnull
    private final def tableFiles = [] as List<File>;

    /**
     * The base name for the texture atlas files
     */
    @Input
    def String atlasName;

    @InputFiles
    def FileCollection resources;

    def File resourceDirectory

    @OutputDirectory
    def File outputDirectory;

    /**
     * The file names of texture files that were found in the list and were not handled yet.
     */
    @Nonnull
    private final def textureFiles = [] as List<File>;

    /**
     * This is the task action that causes the converter to be executed.
     */
    @SuppressWarnings("GroovyUnusedDeclaration")
    @TaskAction
    def processResources() {
        getResources().each { file ->
            analyseAndOrderFile(file)
        }
        convert(getResourceDirectory(), getOutputDirectory())
    }

    /**
     * Read one file and put it into the file list based on its file name.
     *
     * @param file the file
     */
    void analyseAndOrderFile(File file) {
        if (!file.file) {
            return;
        }
        final def fileName = file.absolutePath

        if (fileName.contains("notouch_") || fileName.contains("mouse_cursors")) {
            miscFiles.add(file)
        } else if (fileName.endsWith(".png")) { //$NON-NLS-1$
            textureFiles.add(file)
        } else if (fileName.endsWith(".tbl")) { //$NON-NLS-1$
            tableFiles.add(file)
        } else if (fileName.endsWith(".book.xml")) { //$NON-NLS-1$
            bookFiles.add(file)
        } else if (fileName.startsWith("META-INF")) { //$NON-NLS-1$
        } else {
            miscFiles.add(file)
        }
    }

    /**
     * Starting function of the converter.
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    @SuppressWarnings("nls")
    def void convert(File rootDir, File targetDirectory) {
        delete(targetDirectory)
        targetDirectory.mkdirs()

        convertTableFiles(targetDirectory)
        logger.info("Table files are done!")

        convertMiscFiles(targetDirectory)
        logger.info("Misc files done.")

        convertBookFiles(targetDirectory)
        logger.info("Bookfiles done")

        convertTextureFiles(targetDirectory, rootDir)
        logger.info("Textures done")
    }

    private static void delete(final File file) {
        if (file == null || !file.exists()) {
            return
        }
        if (file.file) {
            file.delete();
        } else if (file.directory) {
            file.listFiles().each { delete(it) }
        }
    }

    /**
     * Write the book files to the new archive. This simply checks if the book files can be properly read into the
     * data structures without errors and considers them valid then. Doing so should prevent the client to run into
     * any trouble when loading the book files.
     *
     * @param outJar the target archive the compressed book files are written to
     * @throws IOException in case there is anything wrong with the input or the output file stream
     */
    @SuppressWarnings("nls")
    private void convertBookFiles(final File targetDirectory) {
        if (bookFiles.empty) {
            return
        }

        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance()

        bookFiles.each {file ->
            final Document document = docBuilderFactory.newDocumentBuilder().parse(file)
            final Book book = new Book(document)

            logger.info("Book ${file.name} read with ${book.englishBook.pageCount} pages")

            file.withInputStream {is ->
                getTargetFile(targetDirectory, file).withOutputStream {os ->
                    os << is
                }
            }
        }
        bookFiles.clear()
    }

    def getTargetFile(File targetDirectory, File sourceFile, Closure<String> additionalReplace = null) {
        def resourceDir = getResourceDirectory().absolutePath
        def filePath = sourceFile.absolutePath.replace(resourceDir, "")
        if (additionalReplace != null) {
            filePath = additionalReplace.call(filePath)
        }
        final def targetFile = new File(targetDirectory, filePath)
        targetFile.mkdirs()
        if (targetFile.directory) {
            targetFile.deleteDir()
        }
        return targetFile
    }

    /**
     * Write all files stored in {@link #miscFiles} into the output file stream without changing them.
     *
     * @param outJar the output stream the new data entries are stored in.
     * @throws IOException in case there is anything wrong with the input or the output file stream
     */
    @SuppressWarnings("nls")
    private void convertMiscFiles(final File targetDirectory) {
        if (miscFiles.empty) {
            return;
        }

        miscFiles.each {file ->
            file.withInputStream {is ->
                getTargetFile(targetDirectory, file, {it.replace("notouch_", "")}).withOutputStream {os ->
                    os << is
                }
            }
        }
        miscFiles.clear()
    }

    /**
     * Encrypt and write the table files to the new archive.
     *
     * @param outJar the target archive the encrypted table files are written to
     * @throws IOException in case there is anything wrong with the input or the output file stream
     */
    @SuppressWarnings("nls")
    private void convertTableFiles(final File targetDirectory) {
        if (tableFiles.empty) {
            return
        }

        if (getPrivateKey() != null) {
            try {
                crypto.loadPrivateKey(new FileInputStream(getPrivateKey()))
            } catch (@Nonnull final FileNotFoundException ignored) {
                // did not work
            }
        }
        if (!crypto.hasPrivateKey()) {
            crypto.loadPrivateKey()
        }
        if (!crypto.hasPrivateKey()) {
            throw new StopExecutionException("Failed to load the required private key.")
        }

        tableFiles.each {file ->
            file.withInputStream {is ->
                getTargetFile(targetDirectory, file, {it.replace(".tbl", ".dat")}).withOutputStream {os ->
                    crypto.encrypt(is, os)
                }
            }
        }
        tableFiles.clear()
    }

    private void convertTextureFiles(final File targetDirectory, final File rootDir) {
        if (textureFiles.empty) {
            return
        }

        final ImagePacker packer = new ImagePacker(rootDir, logger)
        packer.addImages(textureFiles)
        packer.printTypeCounts()
        textureFiles.clear()

        def baseName = "${getAtlasName()}-atlas"
        def atlasFiles = 0
        def atlasMarkupWriter = new StringWriter()
        def atlasMarkup = new MarkupBuilder(atlasMarkupWriter)
        while (!packer.everythingDone) {
            def fileName = "${baseName}-${atlasFiles++}.png"

            def spriteMarkupWriter = new StringWriter()
            def spriteMarkup = new MarkupBuilder(spriteMarkupWriter)
            def resultImage = packer.packImages(spriteMarkup)

            if (resultImage == null) {
                break
            }

            ImageIO.write(resultImage, "png", new File(targetDirectory, fileName));

            atlasMarkup.atlas (file: fileName) {
                mkp.yieldUnescaped(spriteMarkupWriter.toString())
            }
        }

        def xmlWriter;
        try {
            xmlWriter = new BufferedWriter(new FileWriter(new File(targetDirectory, "${baseName}.xml")))
            new MarkupBuilder(xmlWriter).atlasList (atlasCount: atlasFiles) {
                mkp.yieldUnescaped(atlasMarkupWriter.toString())
            }
        } finally {
            xmlWriter?.flush()
            xmlWriter?.close()
        }
    }
}

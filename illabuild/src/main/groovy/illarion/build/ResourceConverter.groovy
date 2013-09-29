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

import groovy.xml.MarkupBuilder;
import illarion.build.imagepacker.ImagePacker;
import illarion.common.data.Book;
import illarion.common.util.Crypto;
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

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
    private final def crypto = new Crypto();

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
        def extensionData = project.extensions.findByName("converter") as ConverterExtension
        atlasName = extensionData.atlasNameExtension
        privateKey = extensionData.privateKey

        project.convention.getPlugin(JavaPluginConvention).sourceSets.each {
            def resourceSet = it.resources
            resourceSet.each {file -> analyseAndOrderFile(file)}
            convert(resourceSet.srcDirs, it.output.resourcesDir)
        }
    }

    /**
     * Read one file and put it into the file list based on its file name.
     *
     * @param file the file
     */
    void analyseAndOrderFile(File file) {
        final def fileName = file.name

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
    def void convert(Set<File> rootDirs, File targetDirectory) {
        targetDirectory.mkdirs()

        convertTableFiles(targetDirectory)
        logger.info("Table files are done!")

        convertMiscFiles(targetDirectory)
        logger.info("Misc files done.")

        convertBookFiles(targetDirectory)
        logger.info("Bookfiles done")

        convertTextureFiles(targetDirectory, rootDirs)
        logger.info("Textures done")
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

            logger.debug("Book ${file.name} read with ${book.getEnglishBook().getPageCount()} pages")

            file.withInputStream {is ->
                new File(targetDirectory, file.name).withOutputStream {os ->
                    os << is
                }
            }
        }
        bookFiles.clear()
    }

    private static void closeSilently(@Nullable final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (@Nonnull final IOException ignored) {
                // nothing to do
            }
        }
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
                new File(targetDirectory, file.name.replace("notouch_", "")).withOutputStream {os ->
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

        if (privateKey != null) {
            try {
                crypto.loadPrivateKey(new FileInputStream(privateKey))
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
                new File(targetDirectory, file.name.replace(".tbl", ".dat")).withOutputStream {os ->
                    crypto.encrypt(is, os)
                }
            }
        }
        tableFiles.clear()
    }

    private void convertTextureFiles(final File targetDirectory, final Set<File> rootDirs) {
        if (textureFiles.empty) {
            return
        }

        final ImagePacker packer = new ImagePacker(rootDirs)
        packer.addImages(textureFiles)
        packer.printTypeCounts()
        textureFiles.clear()

        def atlasFiles = 0
        def atlasMarkupWriter = new StringWriter()
        def atlasMarkup = new MarkupBuilder(atlasMarkupWriter)
        while (!packer.everythingDone) {
            def fileName = "${atlasName}-atlas-${atlasFiles++}.png"


            def spriteMarkupWriter = new StringWriter()
            def spriteMarkup = new MarkupBuilder(spriteMarkupWriter)
            def resultImage = packer.packImages(spriteMarkup)

            if (resultImage == null) {
                break
            }

            ImageIO.write(resultImage, "png", new File(targetDirectory, fileName));

            atlasMarkup.atlas (file: fileName) {
                spriteMarkupWriter.toString()
            }
        }

        def xmlWriter;
        try {
            xmlWriter = new BufferedWriter(new FileWriter(new File(targetDirectory, "${atlasName}-atlas.xml")))
            new MarkupBuilder(xmlWriter).atlasList {
                atlasMarkupWriter.toString()
            }
        } finally {
            xmlWriter?.flush()
            xmlWriter?.close()
        }
    }
}

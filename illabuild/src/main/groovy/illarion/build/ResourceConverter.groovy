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
package illarion.build;

import illarion.build.imagepacker.ImagePacker;
import illarion.common.data.Book;
import illarion.common.util.Crypto;
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.javascript.base.JavaScriptBasePlugin;
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
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;
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
     * The identifier of the texture format.
     */
    private static final String TEXTURE_FORMAT = "png";

    /**
     * The file names of the book files that were found but not handled yet.
     */
    @Nonnull
    private final def bookFiles = [] as List<File>

    /**
     * Crypto instance used to crypt the table files.
     */
    @Nonnull
    private final Crypto crypto;

    /**
     * The file lists that are supposed to be converted.
     */
    def FileCollection sourceFiles;

    /**
     * The file names of the misc files that were found but not handled yet.
     */
    @Nonnull
    private final def miscFiles = [] as List<File>

    /**
     * The file that contains the private key to use
     */
    def File privateKey;

    /**
     * The file names of the table files that were found but not handled yet.
     */
    @Nonnull
    private final def tableFiles = [] as List<File>;

    /**
     * The jar source file that will be processed.
     */
    def File targetFile;

    /**
     * The root directory that is assumed for all received files. This portion of the path will be removed from the
     * path entry that are stored in the file lists.
     */
    def File rootDirectory;

    /**
     * The file names of texture files that were found in the list and were not handled yet.
     */
    @Nonnull
    private final def textureFiles = [] as List<File>;

    /**
     * The file names of texture files that shall be get included into a texture pack and rather get a texture alone.
     */
    @Nonnull
    private final def textureNoPackFiles = [] as List<File>;

    /**
     * Constructor of the Texture converter. Sets up all needed variables for the proper conversion to the OpenGL
     * texture format.
     */
    public ResourceConverter() {
        crypto = new Crypto();
    }

    /**
     * This is the task action that causes the converter to be executed.
     */
    @SuppressWarnings("GroovyUnusedDeclaration")
    @TaskAction
    def processResources() {
        def extensionData = project.extensions.findByType(ConvertPlugin)
        project.convention.getPlugin(JavaPluginConvention).getSourceSets().each {
            def resourceSet = it.resources
            resourceSet.each {file -> analyseAndOrderFile(file)}
            convert(it.resources.srcDirs, it.getOutput().resourcesDir)
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
            if (fileName.contains("nopack_")) { //$NON-NLS-1$
                textureNoPackFiles.add(file)
            } else {
                textureFiles.add(file)
            }
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
        System.out.println("Bookfiles done")

        JarOutputStream outJar = null
        try {
            // build the filelists
            buildFileList()
            logger.info("File list done")
            logger.info("Misc: " + miscFiles.size())
            logger.info("Table: " + tableFiles.size())
            logger.info("Texture: " + textureFiles.size())
            logger.info("Book: " + bookFiles.size())

            // open the output filestream
            targetFile.parentFile.mkdirs();
            outJar = new JarOutputStream(new FileOutputStream(targetFile))
            outJar.setLevel(3)
            outJar.setMethod(ZipEntry.DEFLATED)

            // write the texture files
            writeTextureFiles(outJar)
            System.out.println("texturefiles done")

            // write the texture files that do not get packed
            writeTextureNoPackFiles(outJar)
            System.out.println("not packed texturefiles done")

        } catch (@Nonnull final FileNotFoundException e) {
            logger.error("ERROR: File ${targetFile.getAbsolutePath()} was not found, stopping converter", e)
            logger.error(e.message)
        } catch (@Nonnull final IOException e) {
            logger.error("ERROR: File ${targetFile.getAbsolutePath()} was not readable, stopping converter", e)
            logger.error(e.message)

        } finally {
            closeSilently(outJar)
        }
    }

    /**
     * Check if the settings of this task are good to be executed.
     */
    @SuppressWarnings("nls")
    private void validate() {
        if (targetFile == null) {
            throw new BuildException("a target file is needed", null);
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

            logger.debug("Book ${file.name} read with ${book.getEnglishBook().getPageCount()} pages")

            file.withInputStream {is ->
                new File(targetDirectory, file.name).withOutputStream {os ->
                    os << is
                }
            }
        }
        bookFiles.clear();
    }

    private static void closeSilently(@Nullable final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
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
                    crypto.encrypt(is, os);
                }
            }
        }
        tableFiles.clear();
    }

    private static int packTextures(@Nonnull final JarOutputStream outJar, final String folder, @Nonnull final ImagePacker packer,
                                    @Nullable final String filename) {
        int atlasFiles = 0;
        while (!packer.isEverythingDone()) {
            try {
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder builder = factory.newDocumentBuilder();
                final Document document = builder.newDocument();
                final Node rootNode = document.createElement("sprites");
                document.appendChild(rootNode);

                BufferedImage result;
                if ((result = packer.packImages(document, rootNode)) == null) {
                    break;
                }

                final String usedFileName;
                if (filename == null) {
                    usedFileName = folder + "atlas-" + atlasFiles;
                } else {
                    usedFileName = folder + filename;
                }

                outJar.putNextEntry(new JarEntry(usedFileName + "." + TEXTURE_FORMAT));
                ImageIO.write(result, TEXTURE_FORMAT, outJar);
                outJar.closeEntry();

                outJar.putNextEntry(new JarEntry(usedFileName + ".xml"));
                // Prepare the DOM document for writing
                final Source source = new DOMSource(document);
                final Result xmlResult = new StreamResult(outJar);

                // Write the DOM document to the file
                final Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.transform(source, xmlResult);
                outJar.closeEntry();

                atlasFiles++;
            } catch (@Nonnull final Exception e) {
                throw new BuildException("Error while packing textures", e);
            }
        }

        return atlasFiles;
    }

    /**
     * Pack the textures files together, convert them for OpenGL and store them in the the new archive file.
     *
     * @param outJar the target archive the encrypted table files are written t
     */
    @SuppressWarnings("nls")
    private void convertTextureFiles(final Set<File> rootDirs, final File targetDirectory) {
        if (textureFiles.isEmpty()) {
            return;
        }

        final ImagePacker packer = new ImagePacker();
        final String folder = "data/" + targetFile.getParentFile().getName() + '/';

        for (final File fileEntry : textureFiles) {
            packer.addImage(fileEntry);
        }
        textureFiles.clear();

        packer.printTypeCounts();

        final int atlasFiles = packTextures(outJar, folder, packer, null);

        final DataOutputStream stream;
        try {
            outJar.putNextEntry(new JarEntry(folder + "atlas.count"));
            stream = new DataOutputStream(outJar);
            stream.writeInt(atlasFiles);
            stream.flush();
        } catch (@Nonnull final Exception e) {
            throw new BuildException("Error while building textures", e);
        } finally {
            try {
                outJar.closeEntry();
                outJar.flush();
            } catch (@Nonnull final IOException ignored) {
            }
        }
    }

    /**
     * Convert the texture files for OpenGL and store them in the the new archive file.
     *
     * @param outJar the target archive the encrypted table files are written to
     * @throws BuildException In case anything goes wrong
     */
    @SuppressWarnings("nls")
    private void writeTextureNoPackFiles(@Nonnull final JarOutputStream outJar) {
        if (textureNoPackFiles.isEmpty()) {
            return;
        }

        final ImagePacker packer = new ImagePacker();

        final String folder = "data/" + targetFile.getParentFile().getName() + '/';

        for (final File fileEntry : textureNoPackFiles) {
            packer.addImage(fileEntry);

            String filename = stripDirectory(targetFile.getParentFile(), fileEntry);
            filename = filename.replace("notouch_", "");
            filename = filename.substring(0, filename.lastIndexOf('.'));

            packTextures(outJar, folder, packer, filename);
        }
        textureNoPackFiles.clear();
    }

    private static String stripDirectory(@Nonnull final File dir, @Nonnull final File file) {
        return file.getAbsolutePath().replace(dir.getAbsolutePath(), "");
    }
}

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
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.TaskAction;
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
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
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
public final class ResourceConverter extends DefaultTask {
    /**
     * The identifier of the texture format.
     */
    private static final String TEXTURE_FORMAT = "png";

    /**
     * The file names of the book files that were found but not handled yet.
     */
    @Nonnull
    private final List<File> bookFiles;

    /**
     * Crypto instance used to crypt the table files.
     */
    @Nonnull
    private final Crypto crypto;

    /**
     * The file lists that are supposed to be converted.
     */
    @Nonnull
    private FileCollection sourceFiles;

    /**
     * The file names of the misc files that were found but not handled yet.
     */
    @Nonnull
    private final List<File> miscFiles;

    /**
     * The file that contains the private key to use
     */
    private File privateKeyFile;

    /**
     * The file names of the table files that were found but not handled yet.
     */
    @Nonnull
    private final List<File> tableFiles;

    /**
     * The jar source file that will be processed.
     */
    private File targetFile;

    /**
     * The root directory that is assumed for all received files. This portion of the path will be removed from the
     * path entry that are stored in the file lists.
     */
    private File rootDirectory;

    /**
     * The file names of texture files that were found in the list and were not handled yet.
     */
    @Nonnull
    private final List<File> textureFiles;

    /**
     * The file names of texture files that shall be get included into a texture pack and rather get a texture alone.
     */
    @Nonnull
    private final List<File> textureNoPackFiles;

    /**
     * Constructor of the Texture converter. Sets up all needed variables for the proper conversion to the OpenGL
     * texture format.
     */
    public ResourceConverter() {
        crypto = new Crypto();
        tableFiles = new ArrayList<File>();
        bookFiles = new ArrayList<File>();
        miscFiles = new ArrayList<File>();
        textureNoPackFiles = new ArrayList<File>();
        textureFiles = new ArrayList<File>();
    }

    /**
     * Converts an integer value into a byte array.
     *
     * @param value the integer value that should be converted to a byte array
     * @return the byte array created
     */
    @Nonnull
    public static byte[] intToByteArray(final int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
    }

    /**
     * Set the file collection that is supposed to be used as source.
     *
     * @param list the collection of files to be processed
     */
    public void setSourceFiles(@Nonnull final FileCollection list) {
        sourceFiles = list;
    }

    /**
     * Set the root directory.
     *
     * @param rootDirectory the new root directory
     */
    public void setRootDirectory(@Nonnull final File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /**
     * Get the root directory.
     *
     * @return the root directory
     */
    @Nonnull
    public File getRootDirectory() {
        return rootDirectory;
    }

    /**
     * Execute the task and pack the jar files that are specified.
     */
    @TaskAction
    public void convertResources() {
        validate();
        convert();
    }

    /**
     * Check if the target of the file operation is set.
     *
     * @return {@code true} if the source file is set
     */
    public boolean hasTarget() {
        return targetFile != null;
    }

    /**
     * Set the file that contains the private key used to encrypt the data
     *
     * @param file the file with the private key
     */
    public void setPrivateKey(final File file) {
        privateKeyFile = file;
    }

    /**
     * Set the file that will be used to store all data.
     *
     * @param file target file
     */
    public void setTarget(@Nonnull final File file) {
        targetFile = file;
    }

    /**
     * Read one file and put it into the file list based on its file name.
     *
     * @param file the file
     */
    private void analyseAndOrderFile(@Nonnull final File file) {
        final String fileName = file.getName();

        if (fileName.contains("notouch_") || fileName.contains("mouse_cursors")) {
            miscFiles.add(file);
        } else if (fileName.endsWith(".png")) { //$NON-NLS-1$
            if (fileName.contains("nopack_")) { //$NON-NLS-1$
                textureNoPackFiles.add(file);
            } else {
                textureFiles.add(file);
            }
        } else if (fileName.endsWith(".tbl")) { //$NON-NLS-1$
            tableFiles.add(file);
        } else if (fileName.endsWith(".book.xml")) { //$NON-NLS-1$
            bookFiles.add(file);
        } else if (fileName.startsWith("META-INF")) { //$NON-NLS-1$
            return;
        } else {
            miscFiles.add(file);
        }
    }

    /**
     * Analyze all set files and order them into the processing lists
     *
     * @throws IOException    in case dumping the file list fails
     */
    @SuppressWarnings("nls")
    private void buildFileList()
            throws IOException {
        for (final File file : sourceFiles) {
            analyseAndOrderFile(file);
        }
    }

    /**
     * Starting function of the converter.
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    @SuppressWarnings("nls")
    private void convert() {
        JarOutputStream outJar = null;
        try {
            // build the filelists
            buildFileList();
            getLogger().info("File list done");
            getLogger().info("Misc: " + miscFiles.size());
            getLogger().info("Table: " + tableFiles.size());
            getLogger().info("Texture: " + textureFiles.size());
            getLogger().info("Book: " + bookFiles.size());

            // open the output filestream
            outJar = new JarOutputStream(new FileOutputStream(targetFile));
            outJar.setLevel(3);
            outJar.setMethod(ZipEntry.DEFLATED);

            // write the table files
            writeTableFiles(outJar);
            System.out.println("tablefiles done!!!");

            // write the texture files
            writeTextureFiles(outJar);
            System.out.println("texturefiles done");

            // write the texture files that do not get packed
            writeTextureNoPackFiles(outJar);
            System.out.println("not packed texturefiles done");

            // write the misc files
            writeMiskFiles(outJar);
            System.out.println("Miscfiles done");

            writeBookFiles(outJar);
            System.out.println("Bookfiles done");
        } catch (@Nonnull final FileNotFoundException e) {
            System.out.println("ERROR: File " + targetFile.getAbsolutePath() + " was not found, stopping converter");
            return;
        } catch (@Nonnull final IOException e) {
            System.out.println("ERROR: File " + targetFile.getName() + " was not readable, stopping converter");
            return;
        } finally {
            if (outJar != null) {
                try {
                    outJar.close();
                } catch (@Nonnull final IOException e) {
                    // closing failed
                }
            }
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
    private void writeBookFiles(@Nonnull final JarOutputStream outJar) {
        if (bookFiles.isEmpty()) {
            return;
        }

        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

        for (final File fileEntry : bookFiles) {
            FileChannel inChannel = null;
            try {
                final Document document = docBuilderFactory.newDocumentBuilder().parse(fileEntry);
                final Book book = new Book(document);
                final String fileName = fileEntry.getName();

                getLogger().debug("Book " + fileName + " read with "
                        + book.getEnglishBook().getPageCount() + " pages");

                final JarEntry dstEntry = new JarEntry(fileName);
                dstEntry.setMethod(ZipEntry.DEFLATED);

                // write data to zip
                outJar.putNextEntry(dstEntry);

                inChannel = new FileInputStream(fileEntry).getChannel();
                final WritableByteChannel outChannel = Channels.newChannel(outJar);

                final long size = inChannel.size();
                final long maxSize = Math.min(size, 67076096);
                long position = 0;
                while (position < size) {
                    position += inChannel.transferTo(position, maxSize, outChannel);
                }
                outJar.closeEntry();
            } catch (@Nonnull final Exception e) {
                throw new BuildException("Error while building the books", e);
            } finally {
                closeSilently(inChannel);
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
    private void writeMiskFiles(@Nonnull final JarOutputStream outJar) {
        if (miscFiles.isEmpty()) {
            System.gc();
            return;
        }

        for (final File fileEntry : miscFiles) {
            FileChannel inChannel = null;
            try {
                final JarEntry entry = new JarEntry(fileEntry.getName().replace("notouch_", ""));
                entry.setMethod(ZipEntry.DEFLATED);
                outJar.putNextEntry(new JarEntry(entry));

                inChannel = new FileInputStream(fileEntry).getChannel();
                final WritableByteChannel outChannel = Channels.newChannel(outJar);

                final long size = inChannel.size();
                final long maxSize = Math.min(size, 67076096);
                long position = 0;
                while (position < size) {
                    position += inChannel.transferTo(position, maxSize, outChannel);
                }
                outJar.closeEntry();
            } catch (@Nonnull final IOException e) {
                throw new BuildException("Error while converting the misc files.", e);
            } finally {
                closeSilently(inChannel);
            }
        }
    }

    /**
     * Encrypt and write the table files to the new archive.
     *
     * @param outJar the target archive the encrypted table files are written to
     * @throws IOException in case there is anything wrong with the input or the output file stream
     */
    @SuppressWarnings("nls")
    private void writeTableFiles(@Nonnull final JarOutputStream outJar) {

        if (tableFiles.isEmpty()) {
            return;
        }

        if (privateKeyFile != null) {
            try {
                crypto.loadPrivateKey(new FileInputStream(privateKeyFile));
            } catch (@Nonnull final FileNotFoundException e) {
                // did not work
            }
        }
        if (!crypto.hasPrivateKey()) {
            crypto.loadPrivateKey();
        }

        for (final File fileEntry : tableFiles) {
            InputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(fileEntry));
                final ByteArrayOutputStream dst = new ByteArrayOutputStream((int) fileEntry.length());

                crypto.encrypt(in, dst);
                final byte[] outBuf = dst.toByteArray();

                final JarEntry dstEntry = new JarEntry(fileEntry.getName().replace(".tbl", ".dat"));
                dstEntry.setMethod(ZipEntry.STORED);
                dstEntry.setSize(outBuf.length);

                // build crc
                final CRC32 crc = new CRC32();
                crc.update(outBuf);
                dstEntry.setCrc(crc.getValue());

                // write data to zip
                outJar.putNextEntry(dstEntry);
                outJar.write(outBuf);
                outJar.closeEntry();

            } catch (@Nonnull final Exception e) {
                throw new BuildException("Error while converting table files", e);
            } finally {
                closeSilently(in);
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

                BufferedImage result = null;
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
    private void writeTextureFiles(@Nonnull final JarOutputStream outJar) {
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
            } catch (IOException e) {
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

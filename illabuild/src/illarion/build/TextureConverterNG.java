/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Build Utility. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.Manifest.Attribute;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import illarion.build.imagepacker.ImagePacker;

import illarion.common.util.Crypto;

import illarion.graphics.TextureAtlas;
import illarion.graphics.common.TextureIO;

/**
 * This converter mainly converts the PNG image files into a format optimized
 * for OpenGL, in order to improve the speed of loading the client. It also put
 * the texture images together to image maps. And it checks the contents of the
 * archives and removes useless contents.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class TextureConverterNG extends Task {
    /**
     * This is a small helper class used to ensure that the directory and the
     * name of the file remain separated in a specific manner.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    public static final class FileEntry {
        /**
         * The directory.
         */
        private final File directory;

        /**
         * The name of the file
         */
        private final String fileName;

        /**
         * Create a new file entry that stores directory and filename separated.
         * 
         * @param dir the directory
         * @param file the file
         */
        public FileEntry(final File dir, final String file) {
            directory = dir;
            fileName = file;
        }

        /**
         * Get the directory of this file entry.
         * 
         * @return the directory
         */
        public File getDirectory() {
            return directory;
        }

        /**
         * Get the full file construct that points to the file described by this
         * entry.
         * 
         * @return the newly created file construct
         */
        public File getFile() {
            return new File(directory, fileName);
        }

        /**
         * Get the filename of this file entry.
         * 
         * @return the filename
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Strip a part of the directory from the filename.
         * 
         * @param stripDir the part of the directory to strip away
         * @return the new file entry
         */
        public FileEntry stripDirectory(final String stripDir) {
            return new FileEntry(new File(directory, stripDir),
                fileName.replace(stripDir, "")); //$NON-NLS-1$
        }
    }

    /**
     * The file names of the book files that were found but not handled yet.
     */
    private final List<FileEntry> bookFiles;

    /**
     * Crypto instance used to crypt the table files.
     */
    private final Crypto crypto;

    /**
     * The file lists that are supposed to be converted.
     */
    private final List<FileList> filelists;

    /**
     * The filename of the file that is converted used in case the images are
     * dumbed to the file system.
     */
    private String fileName = null;

    /**
     * The file sets that are supposed to be converted.
     */
    private final List<FileSet> filesets;

    /**
     * The manifest that is put into the target file.
     */
    private Manifest man;

    /**
     * The file names of the misc files that were found but not handled yet.
     */
    private final List<FileEntry> miscFiles;

    /**
     * The file that contains the private key to use
     */
    private File privateKeyFile;

    /**
     * Print a filelist to the system.
     */
    private boolean showFileList = false;

    /**
     * Write the image files outside of the archive for preview reasons.
     */
    private boolean showImages = false;

    /**
     * The file names of the table files that were found but not handled yet.
     */
    private final List<FileEntry> tableFiles;

    /**
     * The jar source file that will be processed.
     */
    private File targetFile;

    /**
     * The file names of texture files that were found in the list and were not
     * handled yet.
     */
    private final List<FileEntry> textureFiles;

    /**
     * The file names of texture files that shall be get included into a texture
     * pack and rather get a texture alone.
     */
    private final List<FileEntry> textureNoPackFiles;

    /**
     * This variable stores of the image builder is supposed to generate a
     * transparency mask in addition.
     */
    private boolean transparency = false;

    /**
     * Constructor of the Texture converter. Sets up all needed variables for
     * the proper conversion to the OpenGL texture format.
     */
    public TextureConverterNG() {
        crypto = new Crypto();
        tableFiles = new ArrayList<FileEntry>();
        bookFiles = new ArrayList<FileEntry>();
        miscFiles = new ArrayList<FileEntry>();
        textureNoPackFiles = new ArrayList<FileEntry>();
        textureFiles = new ArrayList<FileEntry>();
        filesets = new ArrayList<FileSet>();
        filelists = new ArrayList<FileList>();
    }

    /**
     * Converts an integer value into a byte array.
     * 
     * @param value the integer value that should be converted to a byte array
     * @return the byte array created
     */
    public static final byte[] intToByteArray(final int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
            (byte) (value >>> 8), (byte) value };
    }

    /**
     * Main method to start this converter. It expects one archive file starting
     * with raw_ as argument.
     * 
     * @param args The name of the archive file starting with raw_. A file with
     *            the replaced header rsc_ will be created that contains the new
     *            data.
     */
    @SuppressWarnings("nls")
    public static void main(final String[] args) {
        final TextureConverterNG converter = new TextureConverterNG();
        for (final String arg : args) {
            if (arg.contains("dump")) {
                converter.setDumpImages(true);
            } else if (arg.contains("filelist")) {
                converter.setDumpFilelist(true);
            } else if (arg.contains("transparency")) {
                converter.setGenerateTransparency(true);
            } else {
                converter.setTarget(new File(arg));
            }
        }

        if (!converter.hasTarget()) {
            return;
        }
        converter.convert();
    }

    /**
     * Set the manifest that is supposed to be used in the target file.
     * 
     * @param manifest the manifest
     */
    public void addConfigutedManifest(final Manifest manifest) {
        man = manifest;
    }

    /**
     * Adds a list of files to process.
     * 
     * @param list a list of files to process
     */
    public void addFilelist(final FileList list) {
        filelists.add(list);
    }

    /**
     * Adds a set of files to process.
     * 
     * @param set a set of files to process
     */
    public void addFileset(final FileSet set) {
        filesets.add(set);
    }

    /**
     * Set the manifest that is supposed to be used in the target file.
     * 
     * @param manifest the manifest
     */
    public void addManifest(final Manifest manifest) {
        man = manifest;
    }

    /**
     * Execute the task and pack the jar files that are specified.
     */
    @Override
    public void execute() throws BuildException {
        validate();
        convert();
    }

    /**
     * Check if the target of the file operation is set.
     * 
     * @return <code>true</code> if the source file is set
     */
    public boolean hasTarget() {
        return (targetFile != null);
    }

    /**
     * Set the dump file list flag. This causes that all processed files are
     * written to the console.
     * 
     * @param value the dump file list task
     */
    public void setDumpFilelist(final boolean value) {
        showFileList = value;
    }

    /**
     * Set if the generated images are supposed to be displayed or not.
     * 
     * @param value the new value of this flag
     */
    public void setDumpImages(final boolean value) {
        showImages = value;
    }

    /**
     * Set the flag that causes the generation of the transparency mask.
     * 
     * @param value the new value of this flag
     */
    public void setGenerateTransparency(final boolean value) {
        transparency = value;
    }

    /**
     * Set the manifest that is supposed to be used in the target file.
     * 
     * @param manifest the manifest
     */
    public void setManifest(final Manifest manifest) {
        man = manifest;
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
    public void setTarget(final File file) {
        targetFile = file;
        fileName = file.getName().replace("raw_", "").replace("rsc_", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            .replace(".jar", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Read one file and put it into the file list based on its file name.
     * 
     * @param directory the directory of the file
     * @param filename the name of the file
     */
    private void analyseAndOrderFile(final File directory,
        final String filename) {
        final String cleanFileName = filename.replace('\\', '/');
        final FileEntry entry = new FileEntry(directory, cleanFileName);
        // sort in the files to build the ToDo list
        if (cleanFileName.endsWith(".png") && !cleanFileName.contains("notouch_")) { //$NON-NLS-1$ //$NON-NLS-2$
            if (cleanFileName.contains("nopack_")) { //$NON-NLS-1$
                textureNoPackFiles.add(entry);
            } else {
                textureFiles.add(entry);
            }
        } else if (cleanFileName.endsWith(".tbl")) { //$NON-NLS-1$
            tableFiles.add(entry);
        } else if (cleanFileName.endsWith(".xml")) { //$NON-NLS-1$
            bookFiles.add(entry);
        } else if (cleanFileName.startsWith("META-INF")) { //$NON-NLS-1$
            return;
        } else {
            miscFiles.add(entry);
        }
    }

    /**
     * Analyze all set files and order them into the processing lists
     * 
     * @throws BuildException in case anything goes wrong
     * @throws IOException in case dumping the file list fails
     */
    @SuppressWarnings("nls")
    private void buildFileList() throws BuildException, IOException {
        for (final FileSet fileset : filesets) {
            final DirectoryScanner ds =
                fileset.getDirectoryScanner(getProject());

            final File dir = fileset.getDir(getProject());
            for (final String file : ds.getIncludedFiles()) {
                analyseAndOrderFile(dir, file);
            }
        }

        for (final FileList filelist : filelists) {
            final File dir = filelist.getDir(getProject());
            for (final String file : filelist.getFiles(getProject())) {
                analyseAndOrderFile(dir, file);
            }
        }

        if (showFileList) {
            final File ausgabedatei = new File(fileName + ".txt");
            final FileWriter fw = new FileWriter(ausgabedatei);
            final BufferedWriter bw = new BufferedWriter(fw);
            final PrintWriter pw = new PrintWriter(bw);
            pw.println("Table Files:");
            for (final FileEntry entry : tableFiles) {
                pw.println("-- " + entry.getFileName());
            }
            pw.println();
            pw.println("Misc Files:");
            for (final FileEntry entry : miscFiles) {
                pw.println("-- " + entry.getFileName());
            }
            pw.println();
            pw.println("Texture Files:");
            for (final FileEntry entry : textureFiles) {
                pw.println("-- " + entry.getFileName());
            }
            pw.println();
            pw.println("Not packed Files:");
            for (final FileEntry entry : textureNoPackFiles) {
                pw.println("-- " + entry.getFileName());
            }
            pw.println();
            pw.println("Book Files:");
            for (final FileEntry entry : bookFiles) {
                pw.println("-- " + entry.getFileName());
            }
            pw.close();
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
            System.out.println("File list done");
            System.out.println("Misc: " + miscFiles.size());
            System.out.println("Table: " + tableFiles.size());
            System.out.println("Texture: " + textureFiles.size());
            System.out.println("Book: " + bookFiles.size());

            if (man == null) {
                man = new Manifest();
            }
            try {
                man.getMainSection().addAttributeAndCheck(
                    new Attribute("Images-packed-by",
                        "Illarion TextureConverterNG 1.3"));

                man.addConfiguredAttribute(new Attribute(
                    Manifest.ATTRIBUTE_MANIFEST_VERSION,
                    Manifest.DEFAULT_MANIFEST_VERSION));
            } catch (final ManifestException e) {
                // ignore
            }

            // open the output filestream
            outJar = new JarOutputStream(new FileOutputStream(targetFile));
            outJar.setLevel(3);
            outJar.setMethod(ZipEntry.DEFLATED);

            outJar.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            final PrintWriter pWriter =
                new PrintWriter(new OutputStreamWriter(outJar));
            man.write(pWriter);
            pWriter.flush();
            outJar.closeEntry();

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
        } catch (final FileNotFoundException e) {
            System.out.println("ERROR: File " + targetFile.getAbsolutePath()
                + " was not found, stopping converter");
            return;
        } catch (final IOException e) {
            System.out.println("ERROR: File " + targetFile.getName()
                + " was not readable, stopping converter");
            return;
        } finally {
            if (outJar != null) {
                try {
                    outJar.close();
                } catch (final IOException e) {
                    // closing failed
                }
            }
        }
    }

    /**
     * Check if the settings of this task are good to be executed.
     * 
     * @throws BuildException in case anything at the settings for this task is
     *             wrong
     */
    @SuppressWarnings("nls")
    private void validate() throws BuildException {
        if (targetFile == null) {
            throw new BuildException("a file is needed");
        }
    }

    /**
     * Compress and write the book files to the new archive.
     * 
     * @param outJar the target archive the compressed book files are written to
     * @throws IOException in case there is anything wrong with the input or the
     *             output file stream
     */
    @SuppressWarnings("nls")
    private void writeBookFiles(final JarOutputStream outJar)
        throws BuildException {
        if (bookFiles.isEmpty()) {
            System.gc();
            return;
        }

        // final URL schemaFile =
        // TextureConverterNG.class.getClassLoader().getResource(
        // "bookschema.xsd");

        String titleEN = "";
        String titleDE = "";
        final List<String> pageTextDE = new ArrayList<String>();
        final List<String> pageTextEN = new ArrayList<String>();
        for (final FileEntry fileEntry : bookFiles) {
            try {
                pageTextDE.clear();
                pageTextEN.clear();

                final DocumentBuilderFactory DBF =
                    DocumentBuilderFactory.newInstance();
                DBF.setIgnoringElementContentWhitespace(true);
                final Document D =
                    DBF.newDocumentBuilder().parse(fileEntry.getFile());

                // validator.validate(new DOMSource(D));
                final Node bookNode = D.getFirstChild();
                final NodeList langNodes = bookNode.getChildNodes();
                for (int n = 0; n < langNodes.getLength(); n++) {
                    if (langNodes.item(n).getNodeType() == Node.ELEMENT_NODE) {
                        final NamedNodeMap langAttributes =
                            langNodes.item(n).getAttributes();
                        final Node languageID =
                            langAttributes.getNamedItem("id");
                        if (languageID.getNodeValue().equalsIgnoreCase("de")) {
                            final NodeList pages =
                                langNodes.item(n).getChildNodes();
                            for (int p = 0; p < pages.getLength(); p++) {
                                if (pages.item(p).getNodeType() == Node.ELEMENT_NODE) {
                                    if (pages.item(p).getNodeName()
                                        .equalsIgnoreCase("title")) {
                                        titleDE =
                                            pages.item(p).getTextContent();
                                    } else {
                                        pageTextDE.add(pages.item(p)
                                            .getTextContent());
                                    }
                                }
                            }
                        } else if (languageID.getNodeValue().equalsIgnoreCase(
                            ("us"))) {
                            final NodeList pages =
                                langNodes.item(n).getChildNodes();
                            for (int p = 0; p < pages.getLength(); p++) {
                                if (pages.item(p).getNodeType() == Node.ELEMENT_NODE) {
                                    if (pages.item(p).getNodeName()
                                        .equalsIgnoreCase("title")) {
                                        titleEN =
                                            pages.item(p).getTextContent();
                                    } else {
                                        pageTextEN.add(pages.item(p)
                                            .getTextContent());
                                    }
                                }
                            }
                        }
                    }
                }

                // store encrypted code in zip
                // create uncompressed zip entry
                final JarEntry dstEntry =
                    new JarEntry(fileEntry.getFileName().replaceAll(".xml",
                        ".book"));
                dstEntry.setMethod(ZipEntry.DEFLATED);

                // write data to zip
                outJar.putNextEntry(dstEntry);

                outJar.write(intToByteArray(titleEN.getBytes("UTF-8").length));
                outJar.write(titleEN.getBytes("UTF-8"));

                outJar.write(intToByteArray(titleDE.getBytes("UTF-8").length));
                outJar.write(titleDE.getBytes("UTF-8"));

                outJar.write(intToByteArray(Math.max(pageTextEN.size(),
                    pageTextDE.size())));

                String pageText;

                for (int page = 0; page < Math.max(pageTextEN.size(),
                    pageTextDE.size()); page++) {
                    if (pageTextEN.size() <= page) {
                        outJar.write(intToByteArray(pageTextDE.get(page - 1)
                            .getBytes("UTF-8").length));
                    } else {
                        pageText = pageTextEN.get(page).replace("\\n", "\n");
                        outJar
                            .write(intToByteArray(pageText.getBytes("UTF-8").length));
                        outJar.write(pageText.getBytes("UTF-8"));
                    }
                    if (pageTextDE.size() <= page) {
                        outJar.write(intToByteArray(pageTextEN.get(page)
                            .getBytes("UTF-8").length));
                    } else {
                        pageText = pageTextDE.get(page).replace("\\n", "\n");
                        outJar
                            .write(intToByteArray(pageText.getBytes("UTF-8").length));
                        outJar.write(pageText.getBytes("UTF-8"));
                    }
                }

                outJar.closeEntry();
            } catch (final Exception e) {
                throw new BuildException(e);
            }
        }
        bookFiles.clear();
    }

    /**
     * Write all files stored in {@link #miscFiles} into the output file stream
     * without changing them.
     * 
     * @param inJar the source input stream that is used to read the original
     *            files
     * @param outJar the output stream the new data entries are stored in.
     * @throws IOException in case there is anything wrong with the input or the
     *             output file stream
     */
    @SuppressWarnings("nls")
    private void writeMiskFiles(final JarOutputStream outJar)
        throws BuildException {
        if (miscFiles.isEmpty()) {
            System.gc();
            return;
        }

        for (final FileEntry fileEntry : miscFiles) {
            FileChannel inChannel = null;
            try {
                final JarEntry entry =
                    new JarEntry(fileEntry.getFileName().replace("notouch_",
                        ""));
                entry.setMethod(ZipEntry.DEFLATED);
                outJar.putNextEntry(new JarEntry(entry));

                inChannel =
                    new FileInputStream(fileEntry.getFile()).getChannel();
                final WritableByteChannel outChannel =
                    Channels.newChannel(outJar);

                final long size = inChannel.size();
                final long maxSize = Math.min(size, 67076096);
                long position = 0;
                while (position < size) {
                    position +=
                        inChannel.transferTo(position, maxSize, outChannel);
                }
                outJar.closeEntry();
            } catch (final IOException e) {
                throw new BuildException(e);
            } finally {
                if ((inChannel != null) && inChannel.isOpen()) {
                    try {
                        inChannel.close();
                    } catch (final IOException e) {
                        // nothing to do
                    }
                }
            }
        }
    }

    /**
     * Encrypt and write the table files to the new archive.
     * 
     * @param inJar the source archive the table files are read from
     * @param outJar the target archive the encrypted table files are written to
     * @throws IOException in case there is anything wrong with the input or the
     *             output file stream
     */
    @SuppressWarnings("nls")
    private void writeTableFiles(final JarOutputStream outJar)
        throws BuildException {

        if (tableFiles.isEmpty()) {
            return;
        }

        if (privateKeyFile != null) {
            try {
                crypto.loadPrivateKey(new FileInputStream(privateKeyFile));
            } catch (final FileNotFoundException e) {
                // did not work
            }
        }
        if (!crypto.hasPrivateKey()) {
            crypto.loadPrivateKey();
        }

        for (final FileEntry fileEntry : tableFiles) {
            final File currentFile = fileEntry.getFile();
            InputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(currentFile));
                final ByteArrayOutputStream dst =
                    new ByteArrayOutputStream((int) currentFile.length());

                crypto.encrypt(in, dst);
                final byte[] outBuf = dst.toByteArray();

                final JarEntry dstEntry =
                    new JarEntry(fileEntry.getFileName().replace(".tbl",
                        ".dat"));
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

            } catch (final Exception e) {
                throw new BuildException(e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        // nothing
                    }
                }
            }
        }
        tableFiles.clear();
    }

    /**
     * Pack the textures files together, convert them for OpenGL and store them
     * in the the new archive file.
     * 
     * @param inJar the source archive the table files are read from
     * @param outJar the target archive the encrypted table files are written t
     */
    @SuppressWarnings("nls")
    private void writeTextureFiles(final JarOutputStream outJar)
        throws BuildException {
        if (textureFiles.isEmpty()) {
            return;
        }

        final ImagePacker packer = new ImagePacker();
        packer.setGenerateTransparencyMask(transparency);
        packer.setDumpImages(showImages);
        packer.setDumpImageBaseName(fileName);

        final String folder = "data/" + fileName + "/";

        for (final FileEntry fileEntry : textureFiles) {
            packer.addImage(fileEntry.stripDirectory(folder));
        }
        textureFiles.clear();

        packer.printTypeCounts();

        int altasFiles = 0;
        while (!packer.allDone()) {
            try {
                final TextureAtlas resultAtlas = packer.packImages();
                outJar.putNextEntry(new JarEntry(folder + "atlas-"
                    + altasFiles + ".itx"));
                TextureIO.writeTexture(Channels.newChannel(outJar),
                    resultAtlas);
                resultAtlas.removeTexture();
                outJar.closeEntry();
                altasFiles++;
            } catch (final IOException e) {
                throw new BuildException(e);
            }
        }
    }

    /**
     * Convert the texture files for OpenGL and store them in the the new
     * archive file.
     * 
     * @param outJar the target archive the encrypted table files are written to
     * @throws BuildException In case anything goes wrong
     */
    @SuppressWarnings("nls")
    private void writeTextureNoPackFiles(final JarOutputStream outJar)
        throws BuildException {
        if (textureNoPackFiles.isEmpty()) {
            return;
        }

        final ImagePacker packer = new ImagePacker();
        packer.setGenerateTransparencyMask(transparency);
        packer.setDumpImages(showImages);
        packer.setDumpImageBaseName(fileName);

        final String folder = "data/" + fileName + "/";

        for (final FileEntry fileEntry : textureNoPackFiles) {
            packer.addImage(fileEntry.stripDirectory(folder));

            while (!packer.allDone()) {
                try {
                    final String entryFileName =
                        fileEntry.getFileName().replace(".png", "")
                            .replace(folder, "").replace("nopack_", "");
                    final TextureAtlas resultAtlas = packer.packImages();
                    outJar.putNextEntry(new JarEntry(folder + entryFileName
                        + ".itx"));
                    TextureIO.writeTexture(Channels.newChannel(outJar),
                        resultAtlas);
                    resultAtlas.removeTexture();
                    outJar.closeEntry();
                } catch (final IOException e) {
                    throw new BuildException(e);
                }
            }
        }
        textureNoPackFiles.clear();
    }

}

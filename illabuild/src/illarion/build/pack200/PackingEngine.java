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
package illarion.build.pack200;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.Deflater;

/**
 * This is the packing engine that takes care for converting the jar archives
 * into pack200 archives.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class PackingEngine {
    /**
     * This constant is used as code attribute value to set how the local
     * variable table is handled.
     */
    @SuppressWarnings("nls")
    public static final String CODE_ATTRIB_LOCAL_VAR_TABLE =
        "LocalVariableTable";

    /**
     * The file extension that is used for the created pack200 files.
     */
    @SuppressWarnings("nls")
    private static final String FILE_EXTENSION = ".pack.gz";

    /**
     * The pointer to the directory that is the destination of the packing
     * operation.
     */
    private File destination;

    /**
     * The properties that are used to setup the behavior of the packer.
     */
    private final Map<String, String> props;

    /**
     * The constructor of this engine that prepares the packing system to
     * process archives.
     */
    public PackingEngine() {
        props = new Hashtable<String, String>();

        setProperty(Pack200.Packer.KEEP_FILE_ORDER, Pack200.Packer.FALSE);
        setProperty(Pack200.Packer.MODIFICATION_TIME, Pack200.Packer.LATEST);
        setProperty(Pack200.Packer.EFFORT, Integer.toString(9));
        setProperty(Pack200.Packer.DEFLATE_HINT, Pack200.Packer.KEEP);
        setProperty(Pack200.Unpacker.DEFLATE_HINT, Pack200.Unpacker.KEEP);
        setProperty(
            Pack200.Packer.CODE_ATTRIBUTE_PFX
                .concat(CODE_ATTRIB_LOCAL_VAR_TABLE),
            Pack200.Packer.STRIP);
    }

    /**
     * Pack a jar file into a pack200 file.
     * 
     * @param jarFile pack the jar file into a pack 200 file
     */
    @SuppressWarnings("nls")
    public void pack(final File jarFile) {
        File dir = jarFile.getParentFile();
        if (destination != null) {
            dir = destination;
        }
        final String baseName = jarFile.getName().concat(FILE_EXTENSION);
        final File targetFile = new File(dir, baseName);

        if (targetFile.exists() && !targetFile.delete()) {
            System.err.println("Can't delete old version of the target file.");
            return;
        }
        pack(jarFile, new File(dir, baseName), true);
    }

    /**
     * Repack a jar file. That means the file is packed into a pack200 archive
     * and unpacked again right away. This is needed to the file can be used
     * properly with the signing utility.
     * 
     * @param jarFile the jar file that is processed
     */
    @SuppressWarnings("nls")
    public void repack(final File jarFile) {
        File tempFile;
        try {
            tempFile = File.createTempFile("illapack200temp", FILE_EXTENSION);
        } catch (final IOException e) {
            System.err.println("Failed to create temporary file.");
            e.printStackTrace(System.err);
            return;
        }
        tempFile.deleteOnExit();

        if (!pack(jarFile, tempFile, false)) {
            return;
        }

        if (!jarFile.delete()) {
            System.err.println("Can't remove old jar file.");
            return;
        }

        if (!unpack(tempFile, jarFile)) {
            return;
        }
    }

    /**
     * Set the destination directory. All files created by this class will be
     * placed in this directory. In case the directory does not exist, it will
     * be created.
     * 
     * @param dir the directory the files will be created in
     * @throws IllegalArgumentException in case in case the parameter points to
     *             a file
     */
    @SuppressWarnings("nls")
    public void setDestinationDir(final File dir) {
        if (dir == null) {
            destination = null;
            return;
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + dir);
        }
        destination = dir;
    }

    /**
     * Set a property value that will be passed to the packing system.
     * 
     * @param key the key of the property
     * @param value the value of the property
     */
    public void setProperty(final String key, final String value) {
        props.put(key, value);
    }

    /**
     * Copy the properties set as values for the packer to another map.
     * 
     * @param target the map that is to receive the properties
     */
    private void copyPropertiesTo(final Map<String, String> target) {
        target.putAll(props);
    }

    /**
     * Create the packer utility that is used to create pack200 archives and set
     * it up correctly.
     * 
     * @return the newly created pack200 packer
     */
    private Pack200.Packer createPacker() {
        final Pack200.Packer p = Pack200.newPacker();
        copyPropertiesTo(p.properties());
        return p;
    }

    /**
     * Create the packer utility that is used to unpack pack200 archives and set
     * it up correctly.
     * 
     * @return the newly created pack200 unpacker
     */
    private Pack200.Unpacker createUnpacker() {
        final Pack200.Unpacker p = Pack200.newUnpacker();
        copyPropertiesTo(p.properties());
        return p;
    }

    /**
     * This function is used to pack a source jar archive to a pack200 target
     * archive.
     * 
     * @param source the source file that will be packed
     * @param target the file that receives the packed data
     * @param compress apply a gzip output stream in addition
     * @return <code>true</code> in case everything went fine
     */
    @SuppressWarnings("nls")
    private boolean pack(final File source, final File target,
        final boolean compress) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(target);
            if (compress) {
                out = new GZIPOutputStreamEx(out, Deflater.BEST_COMPRESSION);
            }
            out = new BufferedOutputStream(out);
            createPacker().pack(new JarFile(source), out);
            out.flush();
        } catch (final FileNotFoundException e) {
            System.err.println("File was not found.");
            e.printStackTrace(System.err);
            return false;
        } catch (final IOException e) {
            System.err.println("Failed to write data to file.");
            e.printStackTrace(System.err);
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    System.err.println("Failed to close output stream.");
                    e.printStackTrace(System.err);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * This function is used to unpack a source pack200 file to a jar target.
     * Its the reverse function to {@link #pack(File, File)}.
     * 
     * @param source the pack200 source archive
     * @param target the jar target the file is unpacked to
     * @return <code>true</code> in case everything went fine
     */
    @SuppressWarnings("nls")
    private boolean unpack(final File source, final File target) {
        JarOutputStream out = null;
        try {
            out =
                new JarOutputStream(new BufferedOutputStream(
                    new FileOutputStream(target)));
            createUnpacker().unpack(source, out);
            out.flush();
        } catch (final FileNotFoundException e) {
            System.err.println("File was not found.");
            e.printStackTrace(System.err);
            return false;
        } catch (final IOException e) {
            System.err.println("Failed to write data to file.");
            e.printStackTrace(System.err);
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    System.err.println("Failed to close output stream.");
                    e.printStackTrace(System.err);
                    return false;
                }
            }
        }

        return true;
    }
}

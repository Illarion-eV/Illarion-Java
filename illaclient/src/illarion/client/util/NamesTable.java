/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;

import illarion.client.world.Game;

/**
 * This class handles everything around the table that stores the names of the
 * current character. It also offers the loaded data to other parts of the
 * client.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class NamesTable {
    /**
     * Helper class that is used to write the content of the names table to a
     * byte buffer.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class WriteNameTableHelper implements
        TIntObjectProcedure<String> {
        /**
         * The buffer used to store the data.
         */
        private final byte[] buffer;

        /**
         * The instance of NamesTable using this helper class.
         */
        private final NamesTable parent;

        /**
         * Constructor of this helper class that fetches the required values to
         * allow this class to work properly.
         * 
         * @param parentInstance the NamesTable instance that is using this
         *            class
         * @param writeBuffer the buffer that is used to store the data
         */
        public WriteNameTableHelper(final NamesTable parentInstance,
            final byte[] writeBuffer) {
            buffer = writeBuffer;
            parent = parentInstance;
        }

        /**
         * Write the data to the buffer.
         */
        @Override
        public boolean execute(final int key, final String value) {
            parent.encodeInteger(buffer, key);
            parent.encodeString(buffer, value);
            return true;
        }

    }

    /**
     * The size of the buffer that is used to load and store the table files. In
     * case the value is too small this will cause major problems.
     */
    private static final int BUFFER_SIZE = 10000000;

    /**
     * AND-Mask for masking the lowerst byte of a value.
     */
    private static final int BYTE_MASK_1 = (1 << Byte.SIZE) - 1;

    /**
     * AND-Mask for masking the second byte of a value.
     */
    private static final int BYTE_MASK_2 = BYTE_MASK_1 << Byte.SIZE;

    /**
     * AND-Mask for masking the third byte of a value.
     */
    private static final int BYTE_MASK_3 = BYTE_MASK_2 << Byte.SIZE;

    /**
     * AND-Mask for masking the fourth byte of a value.
     */
    private static final int BYTE_MASK_4 = BYTE_MASK_3 << Byte.SIZE;

    /**
     * The string of the encoding used for the names table.
     */
    @SuppressWarnings("nls")
    private static final String ENCODING = "ISO-8859-1".intern();

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(NamesTable.class);

    /**
     * The compression level for the zip output stream. 0 means no compression,
     * 9 means maximal compression (and is also the slowest).
     */
    private static final int ZIP_LEVEL = 9;

    /**
     * A cursor variable that is used as pointer to the current position for
     * reading and writing operations in the byte buffers.
     */
    private int cursor = 0;

    /**
     * If this flag is set to true, this class will perform file operations such
     * as reading and saving the table file. This won't be done in case this
     * variable is set to false. This is used to ensure that the table file is
     * not overwritten in case the reading part failed.
     */
    private boolean fileOperations = true;

    /**
     * The HashMap that stores the names of the characters that are known
     * currently in relationship to the IDs of the characters.
     */
    private final TIntObjectHashMap<String> names;

    /**
     * The file that contains the names that shall be loaded.
     */
    private final File nameTable;

    /**
     * The default constructor that loads the name data from the table file that
     * is specified in the constructor.
     * 
     * @param tableFile the table file that is the target and the source of the
     *            load and save operations this class will perform
     */
    public NamesTable(final File tableFile) {
        nameTable = tableFile;
        names = new TIntObjectHashMap<String>();
        loadTable();
    }

    /**
     * Add a name to the list of names stored here.
     * 
     * @param charID the character ID that shall be added to this list
     * @param charName the name of the character that shall be added
     */
    @SuppressWarnings("boxing")
    public void addName(final long charID, final String charName) {
        final int charIDint = (int) (charID - (1 << Integer.SIZE));
        names.put(charIDint, charName);
    }

    /**
     * Read a string from the input buffer and encode it for further usage.
     * 
     * @param buffer the buffer that is used as source for the string data
     * @return the decoded string
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    @SuppressWarnings("nls")
    public String decodeString(final byte[] buffer) throws IOException {
        final int len = decodeByte(buffer);

        if ((cursor + len) > buffer.length) {
            throw new IndexOutOfBoundsException(
                "reading beyond receive buffer");
        }

        final String ret = new String(buffer, cursor, len, ENCODING);
        cursor += len;
        return ret;
    }

    /**
     * Get a name from the stored list.
     * 
     * @param charID the character ID of the char who's name is wanted
     * @return the name of the character that was found or null
     */
    public String getName(final long charID) {
        final int charIDint = (int) (charID - (1 << Integer.SIZE));
        return names.get(charIDint);
    }

    /**
     * Save the table data to the file system.
     */
    @SuppressWarnings("nls")
    public void saveTable() {
        if (nameTable.exists() && !nameTable.canWrite()) {
            LOGGER.error("Nametable File locked, can't write the"
                + " name table.");
            return;
        }

        final byte[] buffer = new byte[BUFFER_SIZE];

        final byte[] keyName = new byte[DESKeySpec.DES_KEY_LEN];
        final String charName = Game.getPlayer().getCharacter().getName();
        byte[] convString;
        try {
            convString = charName.getBytes("ISO-8859-1");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("Encoding problem ISO-8859-1 not found");
            convString = charName.getBytes();
        }
        for (int i = 0; i < keyName.length; ++i) {
            if (i < convString.length) {
                keyName[i] = convString[i];
            } else {
                keyName[i] = (byte) i;
            }

        }
        final Key key = new SecretKeySpec(keyName, "DES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (final Exception e) {
            LOGGER.error("Problem while creating cipher. File "
                + "operations disabled.", e);
            fileOperations = false;
            return;
        }

        cursor = 0;

        FileOutputStream outStream = null;
        ZipOutputStream gOutStream = null;
        CipherOutputStream cOutStream = null;
        try {
            outStream = new FileOutputStream(nameTable);
            cOutStream = new CipherOutputStream(outStream, cipher);
            gOutStream = new ZipOutputStream(cOutStream);
            gOutStream.setLevel(ZIP_LEVEL);

            // store the name of the character in the file to ensure that this
            // really is the file of the character.
            encodeString(buffer, Game.getPlayer().getCharacter().getName());

            // encode the amount of characters in the list, not really needed
            // but a way to check if the file is valid or not.
            encodeInteger(buffer, names.size());

            // store all the names the character knows in the list
            names.forEachEntry(new WriteNameTableHelper(this, buffer));

            gOutStream.putNextEntry(new ZipEntry("names"));
            gOutStream.write(buffer, 0, cursor);
            gOutStream.closeEntry();
            gOutStream.flush();
        } catch (final FileNotFoundException e) {
            LOGGER
                .error(
                    "Can't write to the name table file "
                        + nameTable.getPath(), e);
        } catch (final IOException e) {
            LOGGER.error(
                "Error accessing the stream to the file "
                    + nameTable.getPath(), e);
        } finally {
            if (gOutStream != null) {
                try {
                    gOutStream.close();
                } catch (final IOException e) {
                    LOGGER.error("Closing the file " + nameTable.getPath()
                        + " correctly failed", e);
                }
            }
        }
    }

    /**
     * Encode 4 byte to the buffer and encode it as signed integer(32bit).
     * 
     * @param buffer the buffer that stores the data for the network interface
     * @param value the value that shall be encoded
     */
    void encodeInteger(final byte[] buffer, final int value) {
        buffer[cursor++] =
            (byte) ((value & BYTE_MASK_4) >> Byte.SIZE >> Byte.SIZE >> Byte.SIZE);
        buffer[cursor++] =
            (byte) ((value & BYTE_MASK_3) >> Byte.SIZE >> Byte.SIZE);
        buffer[cursor++] = (byte) ((value & BYTE_MASK_2) >> Byte.SIZE);
        buffer[cursor++] = (byte) (value & BYTE_MASK_1);
    }

    /**
     * Encode a string and put it into the buffer.
     * 
     * @param buffer the buffer that contains the the already encoded data
     * @param txt the string that shall be encoded
     */
    @SuppressWarnings("nls")
    void encodeString(final byte[] buffer, final String txt) {
        final int len = txt.length();
        buffer[cursor++] = (byte) (len & BYTE_MASK_1);
        byte[] convString;
        try {
            convString = txt.getBytes(ENCODING);
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("Encoding problem " + ENCODING + " not found");
            convString = txt.getBytes();
        }
        for (final byte value : convString) {
            buffer[cursor++] = value;
        }
    }

    /**
     * Decode one byte from a buffer and use it as unsigned byte. Reading a byte
     * will automatically increase the {@link #cursor} position by one.
     * 
     * @param buffer the buffer that contains the data that is encoded
     * @return the decoded unsigned byte
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    @SuppressWarnings("nls")
    private int decodeByte(final byte[] buffer) throws IOException {
        if (cursor == buffer.length) {
            throw new IndexOutOfBoundsException("Reading beyond buffer");
        }

        int tmp = buffer[cursor++];
        if (tmp < 0) {
            tmp += (1 << Byte.SIZE);
        }
        return tmp;
    }

    /**
     * Read four bytes from the buffer and handle them as a single signed value.
     * 
     * @param buffer the buffer contains the data that shall be decoded
     * @return The two bytes in the buffer handled as unsigned 4 byte value
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    private int decodeInteger(final byte[] buffer) throws IOException {
        int temp = decodeByte(buffer);
        temp <<= Byte.SIZE;
        temp += decodeByte(buffer);
        temp <<= Byte.SIZE;
        temp += decodeByte(buffer);
        temp <<= Byte.SIZE;
        temp += decodeByte(buffer);
        return temp;
    }

    /**
     * Load the names table from the file system and prepare it for further
     * usage.
     */
    @SuppressWarnings("nls")
    private void loadTable() {
        if (!fileOperations) {
            return;
        }

        if (nameTable == null) {
            throw new IllegalArgumentException("Can't load table from a NULL "
                + "file");
        }

        if (!nameTable.exists()) {
            return;
        }

        final byte[] keyName = new byte[DESKeySpec.DES_KEY_LEN];
        final String charName = Game.getPlayer().getCharacter().getName();
        byte[] convString;
        try {
            convString = charName.getBytes("ISO-8859-1");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("Encoding problem ISO-8859-1 not found");
            convString = charName.getBytes();
        }
        for (int i = 0; i < keyName.length; ++i) {
            if (i < convString.length) {
                keyName[i] = convString[i];
            } else {
                keyName[i] = (byte) i;
            }

        }
        final Key key = new SecretKeySpec(keyName, "DES");

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (final Exception e) {
            LOGGER.error("Problem while creating cipher. File "
                + "operations disabled.", e);
            fileOperations = false;
            return;
        }

        final byte[] buffer = new byte[BUFFER_SIZE];

        cursor = 0;

        FileInputStream inStream = null;
        ZipInputStream gInStream = null;
        CipherInputStream cInStream = null;
        try {
            inStream = new FileInputStream(nameTable);
            cInStream = new CipherInputStream(inStream, cipher);
            gInStream = new ZipInputStream(cInStream);

            gInStream.getNextEntry();
            // read all the data from the stream.
            int read = 0;
            int totalRead = 0;
            while (0 < (read =
                gInStream.read(buffer, totalRead, buffer.length - totalRead))) {
                totalRead += read;
            }
            gInStream.closeEntry();

            String charname = decodeString(buffer);

            if (charname.equals(Game.getPlayer().getCharacter().getName())) {
                final int count = decodeInteger(buffer);
                int i = 0;
                int charID = 0;
                while (count > i++) {
                    charID = decodeInteger(buffer);
                    charname = decodeString(buffer);
                    names.put(charID, charname);
                }
            }

        } catch (final FileNotFoundException e) {
            LOGGER.error(
                "Can't read the name table file " + nameTable.getPath(), e);
        } catch (final IOException e) {
            LOGGER.error(
                "Error accessing the stream to the file "
                    + nameTable.getPath(), e);
        } finally {
            if (gInStream != null) {
                try {
                    gInStream.close();
                } catch (final IOException e) {
                    LOGGER.error("Closing the file " + nameTable.getPath()
                        + " correctly failed", e);
                }
            }
        }
    }
}

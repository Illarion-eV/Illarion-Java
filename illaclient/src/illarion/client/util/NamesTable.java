/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

import illarion.client.Login;
import illarion.client.world.World;
import illarion.common.types.CharacterId;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

/**
 * This class handles everything around the table that stores the names of the current character. It also offers the
 * loaded data to other parts of the client.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class NamesTable {
    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(NamesTable.class);

    /**
     * If this flag is set to true, this class will perform file operations such as reading and saving the table file.
     * This won't be done in case this variable is set to false. This is used to ensure that the table file is not
     * overwritten in case the reading part failed.
     */
    private boolean fileOperations = true;

    /**
     * The HashMap that stores the names of the characters that are known currently in relationship to the IDs of the
     * characters.
     */
    private final Map<CharacterId, String> names;

    /**
     * The file that contains the names that shall be loaded.
     */
    private final File nameTable;

    /**
     * The default constructor that loads the name data from the table file that is specified in the constructor.
     *
     * @param tableFile the table file that is the target and the source of the load and save operations this class
     *                  will perform
     */
    public NamesTable(final File tableFile) {
        nameTable = tableFile;
        names = new HashMap<CharacterId, String>();
        loadTable();
    }

    /**
     * Add a name to the list of names stored here.
     *
     * @param charID   the character ID that shall be added to this list
     * @param charName the name of the character that shall be added
     */
    @SuppressWarnings("boxing")
    public void addName(final CharacterId charID, final String charName) {
        names.put(charID, charName);
    }

    /**
     * Get a name from the stored list.
     *
     * @param charID the character ID of the char who's name is wanted
     * @return the name of the character that was found or null
     */
    public String getName(final CharacterId charID) {
        return names.get(charID);
    }

    /**
     * Save the table data to the file system.
     */
    @SuppressWarnings("nls")
    public void saveTable() {
        if (nameTable.exists() && !nameTable.canWrite()) {
            LOGGER.error("Name table File locked, can't write the name table.");
            return;
        }

        final String charName = World.getPlayer().getCharacter().getName();
        byte[] conversationString;
        try {
            conversationString = charName.getBytes("ISO-8859-1");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("Encoding problem ISO-8859-1 not found");
            conversationString = charName.getBytes();
        }

        final byte[] keyName = new byte[DESKeySpec.DES_KEY_LEN];
        for (int i = 0; i < keyName.length; ++i) {
            if (i < conversationString.length) {
                keyName[i] = conversationString[i];
            } else {
                keyName[i] = (byte) i;
            }

        }
        final Key key = new SecretKeySpec(keyName, "DES");
        final Cipher cipher;
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (final Exception e) {
            LOGGER.error("Problem while creating cipher. File operations disabled.", e);
            fileOperations = false;
            return;
        }

        OutputStream outputStream = null;
        try {
            final FileOutputStream fOutStream = new FileOutputStream(nameTable);
            final CipherOutputStream cOutStream = new CipherOutputStream(fOutStream, cipher);
            final GZIPOutputStream gOutStream = new GZIPOutputStream(cOutStream);
            final ObjectOutputStream oOutStream = new ObjectOutputStream(gOutStream);

            outputStream = oOutStream;

            oOutStream.writeObject(World.getPlayer().getCharacter().getName());
            oOutStream.writeInt(names.size());
            for (final Map.Entry<CharacterId, String> values : names.entrySet()) {
                oOutStream.writeObject(values.getKey());
                oOutStream.writeObject(values.getValue());
            }
        } catch (final FileNotFoundException e) {
            LOGGER.error("Can't write to the name table file " + nameTable.getPath(), e);
        } catch (final IOException e) {
            LOGGER.error("Error accessing the stream to the file " + nameTable.getPath(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (final IOException e) {
                    LOGGER.error("Closing the file " + nameTable.getPath() + " correctly failed", e);
                }
            }
        }
    }

    /**
     * Load the names table from the file system and prepare it for further usage.
     */
    @SuppressWarnings("nls")
    private void loadTable() {
        if (!fileOperations) {
            return;
        }

        if (nameTable == null) {
            throw new IllegalArgumentException("Can't load table from a NULL file");
        }

        if (!nameTable.exists()) {
            return;
        }

        final String charName = Login.getInstance().getLoginCharacter();
        byte[] conversationString;
        try {
            conversationString = charName.getBytes("ISO-8859-1");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("Encoding problem ISO-8859-1 not found");
            conversationString = charName.getBytes();
        }

        final byte[] keyName = new byte[DESKeySpec.DES_KEY_LEN];
        for (int i = 0; i < keyName.length; ++i) {
            if (i < conversationString.length) {
                keyName[i] = conversationString[i];
            } else {
                keyName[i] = (byte) i;
            }
        }
        final Key key = new SecretKeySpec(keyName, "DES");

        final Cipher cipher;
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (final Exception e) {
            LOGGER.error("Problem while creating cipher. File operations disabled.", e);
            fileOperations = false;
            return;
        }

        InputStream inputStream = null;
        try {
            final FileInputStream fInStream = new FileInputStream(nameTable);
            final CipherInputStream cInStream = new CipherInputStream(fInStream, cipher);
            final GZIPInputStream gInStream = new GZIPInputStream(cInStream);
            final ObjectInputStream oInStream = new ObjectInputStream(gInStream);

            inputStream = oInStream;

            if (charName.equals(oInStream.readObject())) {
                final int count = oInStream.readInt();
                int i = 0;
                while (count > i++) {
                    final CharacterId charId = (CharacterId) oInStream.readObject();
                    final String characterName = (String) oInStream.readObject();
                    names.put(charId, characterName);
                }
            }
        } catch (final ZipException e) {
            LOGGER.error("Failed to decode file compression " + nameTable.getPath());
        } catch (final FileNotFoundException e) {
            LOGGER.error("Can't read the name table file " + nameTable.getPath(), e);
        } catch (final IOException e) {
            LOGGER.error("Error accessing the stream to the file " + nameTable.getPath(), e);
        } catch (final ClassNotFoundException e) {
            LOGGER.error("Illegal file contents " + nameTable.getPath(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    LOGGER.error("Closing the file " + nameTable.getPath() + " correctly failed", e);
                }
            }
        }
    }
}

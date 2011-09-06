/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.common;

import illarion.common.util.FastMath;
import illarion.graphics.Graphics;
import illarion.graphics.Texture;
import illarion.graphics.TextureAtlas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import javolution.context.ArrayFactory;

/**
 * A utility class for reading and writing the data from textures. This class
 * allows all implementations of the graphic port to read and write the textures
 * in exactly the same way.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public final class TextureIO {
    public final static String FORMAT = "sgi";
    
    
    /**
     * Read a texture atlas from a byte channel. This method does only check the
     * texture version to be valid. Everything else remains unchecked in order
     * not to slow down the loading of the texture.
     * 
     * @param inChannel the channel that is the source of the data
     * @return the texture created from the data delivered by the input channel
     * @throws IOException in case anything goes wrong while reading the input
     *             channel
     */
    @SuppressWarnings("nls")
    public static TextureAtlas readTexture(final File dataFile, final File metaFile)
        throws IOException {
        
        final TextureAtlas result = Graphics.getInstance().getTextureAtlas();
        try {
            result.loadTextureData(dataFile);
        } catch (Exception e1) {
            throw new IOException(e1);
        }
        
        ObjectInputStream oIn = null;
        try {
            oIn = new ObjectInputStream(new FileInputStream(metaFile));
            
            final int cnt = oIn.readInt();
            for (int i = 0; i < cnt; i++) {
                try {
                    result.addImage((String) oIn.readObject(), oIn.readInt(), oIn.readInt(), oIn.readInt(), oIn.readInt());
                } catch (ClassNotFoundException e) {
                    // one texture failed... what ever
                }
            }
        } finally {
            if (oIn != null) {
                oIn.close();
            }
        }

        return result;
    }
    
    @SuppressWarnings("nls")
    public static TextureAtlas readTexture(final InputStream dataStream, final InputStream metaStream)
        throws IOException {
        
        final TextureAtlas result = Graphics.getInstance().getTextureAtlas();
        try {
            result.loadTextureData(dataStream);
        } catch (Exception e1) {
            throw new IOException(e1);
        }
        
        ObjectInputStream oIn = null;
        try {
            oIn = new ObjectInputStream(metaStream);
            
            final int cnt = oIn.readInt();
            for (int i = 0; i < cnt; i++) {
                try {
                    result.addImage((String) oIn.readObject(), oIn.readInt(), oIn.readInt(), oIn.readInt(), oIn.readInt());
                } catch (ClassNotFoundException e) {
                    // one texture failed... what ever
                }
            }
        } finally {
            if (oIn != null) {
                oIn.close();
            }
        }

        return result;
    }

    /**
     * Write the texture data to a output channel.
     * 
     * @param outChannel the channel that shall receive the texture data
     * @param tex the texture atlas that shall be written to the channel
     * @throws IOException in case the writing to the channel caused any problem
     * @throws IllegalStateException in case any validation check of the texture
     *             data failed
     */
    @SuppressWarnings("nls")
    public static void writeTexture(final File dataFile, final File metaFile,
        final TextureAtlas tex, final Collection<SubTextureCoord> coordinates)
        throws IOException {

        tex.writeTextureDataToFile(dataFile);

        ObjectOutputStream oOut = null;
        try {
            oOut = new ObjectOutputStream(new FileOutputStream(metaFile));
            oOut.writeInt(coordinates.size());
            for (SubTextureCoord coord : coordinates) {
                oOut.writeObject(coord.getName());
                oOut.writeInt(coord.getX());
                oOut.writeInt(coord.getY());
                oOut.writeInt(coord.getWidth());
                oOut.writeInt(coord.getHeight());
            }
            oOut.flush();
        } finally {
            if (oOut != null) {
                oOut.close();
            }
        }
    }

    /**
     * Private constructor to avoid any instances created from this utility
     * class.
     */
    private TextureIO() {
        // nothing to be done
    }
}

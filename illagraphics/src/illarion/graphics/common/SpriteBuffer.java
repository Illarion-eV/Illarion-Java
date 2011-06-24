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

import java.util.Iterator;
import java.util.Map;

import javolution.text.TextBuilder;
import javolution.util.FastComparator;
import javolution.util.FastMap;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.Texture;

/**
 * A function that stores the created sprites and orders them by name of the the
 * sprite so other parts of the client do not create more and more sprites of
 * the same thing to render.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public final class SpriteBuffer {
    /**
     * The singleton instance of this object.
     */
    private static final SpriteBuffer INSTANCE = new SpriteBuffer();

    /**
     * The storage tables that store the sprites that were generated already.
     */
    private Map<String, Sprite> storage;

    /**
     * Private constructor so nothing can create a additional instance of the
     * Sprite buffer.
     */
    private SpriteBuffer() {
        final FastMap<String, Sprite> storageMap =
            new FastMap<String, Sprite>();
        storageMap.setKeyComparator(FastComparator.STRING);
        storage = storageMap;
    }

    /**
     * Get the singleton instance of the SpriteBuffer object.
     * 
     * @return the singleton instance object
     */
    public static SpriteBuffer getInstance() {
        return INSTANCE;
    }

    /**
     * Cleaning up the sprite buffer results in removing all buffered sprites.
     * This should be done after the loading of the sprites is finished in order
     * to free the space that is taken by the string identifiers of the sprites
     * and the HashMap that stores the buffered sprites.
     */
    public void cleanup() {
        storage.clear();
        storage = null;
    }

    /**
     * Drop a sprite from the sprite buffer. A dropped sprite is not available
     * anymore and maybe leads to removing the texture. Its not a good idea to
     * remove sprites with textures that need to be used later.
     * 
     * @param droppingSprite the sprite that shall be dropped
     */
    public void dropSprite(final Sprite droppingSprite) {
        if (storage == null) {
            droppingSprite.remove();
            return;
        }

        if (storage.containsValue(droppingSprite)) {
            final Iterator<Sprite> itr2 = storage.values().iterator();
            while (itr2.hasNext()) {
                final Sprite testSprite = itr2.next();
                if (testSprite.equals(droppingSprite)) {
                    itr2.remove();
                    droppingSprite.remove();
                    return;
                }
            }
        }
    }

    /**
     * Shorted get Sprite call for textures that do not need a offset and are
     * aligned in the upper left corner.
     * 
     * @param path the path the sprite file is located at
     * @param name the name of the sprite that is searched
     * @param smooth true in case the picture needs to be smoothed due resizing
     *            operations
     * @return the sprite that was created or loaded from the cache
     */
    public Sprite getSprite(final String path, final String name,
        final boolean smooth) {
        return getSprite(path, name, 1, 0, 0, Sprite.HAlign.left,
            Sprite.VAlign.bottom, smooth, false);
    }

    /**
     * Get a sprite, either from the cache or generate a new one with the
     * parameters that are handed over here.
     * 
     * @param path the path the sprite file is located at
     * @param name the name of the sprite that is searched
     * @param frames the amount of frames or variations this sprite has
     * @param offX the sprite picture offset in X direction
     * @param offY the sprite picture offset in Y direction
     * @param horz the horizontal alignment of the picture
     * @param vert the vertical alignment of the picture
     * @param smooth true in case the picture needs to be smoothed due resizing
     *            operations
     * @param mirror show the sprite horizontal mirrored
     * @return the sprite that was created or loaded from the cache
     */
    @SuppressWarnings("nls")
    public Sprite getSprite(final String path, final String name,
        final int frames, final int offX, final int offY,
        final Sprite.HAlign horz, final Sprite.VAlign vert,
        final boolean smooth, final boolean mirror) {

        if ((frames <= 0) && (name != null)) {
            throw new IllegalArgumentException("Tried to get a sprite with "
                + "0 or less frames. Sprite source: " + path + "/" + name);
        }

        TextBuilder nameBuilder = TextBuilder.newInstance();
        nameBuilder.setLength(0);
        nameBuilder.append(path);
        nameBuilder.append(name);
        nameBuilder.append('-');
        nameBuilder.append(offX);
        nameBuilder.append('-');
        nameBuilder.append(offY);
        nameBuilder.append('-');
        nameBuilder.append(horz.ordinal());
        nameBuilder.append('-');
        nameBuilder.append(vert.ordinal());
        nameBuilder.append('-');
        if (mirror) {
            nameBuilder.append('m');
        } else {
            nameBuilder.append('n');
        }

        final String spriteName = nameBuilder.toString();
        TextBuilder.recycle(nameBuilder);
        nameBuilder = null;

        if (storage.containsKey(spriteName)) {
            final Sprite returnSprite = storage.get(spriteName);
            // summTime += (System.currentTimeMillis() - startTime);
            return returnSprite;
        }

        final Sprite retSprite = Graphics.getInstance().getSprite(frames);
        retSprite.setOffset(offX, offY);
        retSprite.setAlign(horz, vert);
        retSprite.setMirror(mirror);

        if (frames == 1) {
            final Texture tex =
                TextureLoader.getInstance().getTexture(path, name, smooth,
                    true);

            retSprite.addTexture(tex);
        } else {
            nameBuilder = TextBuilder.newInstance();
            for (int i = 0; i < frames; ++i) {
                nameBuilder.setLength(0);
                nameBuilder.append(name);
                nameBuilder.append('-');
                nameBuilder.append(i);

                final Texture tex =
                    TextureLoader.getInstance().getTexture(path,
                        nameBuilder.toString(), smooth, true);
                retSprite.addTexture(tex);
            }
            TextBuilder.recycle(nameBuilder);
        }

        storage.put(spriteName, retSprite);
        return retSprite;
    }
}

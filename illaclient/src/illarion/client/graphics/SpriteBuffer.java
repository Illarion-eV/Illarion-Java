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
package illarion.client.graphics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A function that stores the created sprites and orders them by name of the the
 * sprite so other parts of the client do not create more and more sprites of
 * the same thing to render.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
public final class SpriteBuffer {
    /**
     * The singleton instance of this object.
     */
    private static final SpriteBuffer INSTANCE = new SpriteBuffer();

    /**
     * Get the singleton instance of the SpriteBuffer object.
     *
     * @return the singleton instance object
     */
    @Nonnull
    public static SpriteBuffer getInstance() {
        return INSTANCE;
    }

    /**
     * The storage tables that store the sprites that were generated already.
     */
    @Nullable
    private Map<String, Sprite> storage;

    /**
     * Private constructor so nothing can create a additional instance of the
     * Sprite buffer.
     */
    private SpriteBuffer() {
        storage = new ConcurrentHashMap<String, Sprite>();
    }

    /**
     * Cleaning up the sprite buffer results in removing all buffered sprites.
     * This should be done after the loading of the sprites is finished in order
     * to free the space that is taken by the string identifiers of the sprites
     * and the HashMap that stores the buffered sprites.
     */
    public void cleanup() {
        storage = null;
    }

    /**
     * Get a sprite, either from the cache or generate a new one with the
     * parameters that are handed over here.
     *
     * @param path   the path the sprite file is located at
     * @param name   the name of the sprite that is searched
     * @param frames the amount of frames or variations this sprite has
     * @param offX   the sprite picture offset in X direction
     * @param offY   the sprite picture offset in Y direction
     * @param horz   the horizontal alignment of the picture
     * @param vert   the vertical alignment of the picture
     * @param mirror show the sprite horizontal mirrored
     * @return the sprite that was created or loaded from the cache
     */
    @SuppressWarnings("nls")
    public Sprite getSprite(final String path, @Nonnull final String name, final int frames, final int offX,
                            final int offY, @Nonnull final Sprite.HAlign horz, @Nonnull final Sprite.VAlign vert,
                            final boolean mirror) {
        if (storage == null) {
            throw new IllegalStateException("Can't request sprites after the buffer got cleaned up.");
        }
        if (frames <= 0) {
            throw new IllegalArgumentException("Tried to get a sprite with 0 or less frames. Sprite source: " + path
                    + '/' + name);
        }

        final StringBuilder nameBuilder = new StringBuilder();
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
        if (storage.containsKey(spriteName)) {
            final Sprite returnSprite = storage.get(spriteName);
            // summTime += (System.currentTimeMillis() - startTime);
            return returnSprite;
        }

        final Sprite retSprite = new Sprite(frames);
        retSprite.setOffset(offX, offY);
        retSprite.setAlign(horz, vert);
        retSprite.setMirror(mirror);

        if (frames == 1) {

            retSprite.addImage(TextureLoader.getInstance().getTexture(path,
                    name));
        } else {
            nameBuilder.setLength(0);
            nameBuilder.append(name);
            nameBuilder.append('-');
            final int targetLength = nameBuilder.length();
            for (int i = 0; i < frames; ++i) {
                nameBuilder.setLength(targetLength);
                nameBuilder.append(i);
                retSprite.addImage(TextureLoader.getInstance().getTexture(
                        path, nameBuilder.toString()));
            }
        }

        storage.put(spriteName, retSprite);
        return retSprite;
    }
}

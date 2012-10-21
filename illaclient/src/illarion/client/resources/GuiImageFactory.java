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
package illarion.client.resources;

import illarion.client.graphics.GuiImage;
import illarion.common.graphics.Sprite;
import illarion.common.util.ObjectSource;
import javolution.util.FastComparator;
import javolution.util.FastMap;

/**
 * This class is used to load and store the graphics that are needed for
 * displaying the GUI of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GuiImageFactory implements ObjectSource<Sprite>,
        ResourceFactory<GuiImage> {
    /**
     * The map that is used to store the values load into this factory.
     */
    private final FastMap<String, GuiImage> sprites;

    private static final GuiImageFactory INSTANCE = new GuiImageFactory();

    public static GuiImageFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Create a new GUI image factory. This will prepare the internal data
     * structures for operation.
     */
    public GuiImageFactory() {
        sprites = new FastMap<String, GuiImage>();
        sprites.setKeyComparator(FastComparator.STRING);
    }

    /**
     * Check if a key is assigned to a object in this object source.
     */
    @Override
    public boolean containsObject(final String key) {
        return sprites.containsKey(key);
    }

    /**
     * Remove a object from this factory.
     */
    @Override
    public void disposeObject(final String key, final Sprite object) {
        if (!containsObject(key)) {
            return;
        }
        sprites.remove(key);
    }

    /**
     * Get a object from this factory.
     */
    @Override
    public Sprite getObject(final String key) {
        final GuiImage image = sprites.get(key);

        if (image == null) {
            return null;
        }
        return sprites.get(key).getSprite();
    }

    /**
     * Initialize the factory and prepare it for receiving data. In this case
     * this function does nothing.
     */
    @Override
    public void init() {
    }

    /**
     * Finish the loading sequence and prepare the factory for normal operation.
     */
    @Override
    public void loadingFinished() {
    }

    /**
     * Store a resource in this factory.
     */
    @Override
    public void storeResource(final GuiImage resource) {
        sprites.put(resource.getImageName(), resource);
    }
}

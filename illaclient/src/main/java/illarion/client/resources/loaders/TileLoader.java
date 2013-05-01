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
package illarion.client.resources.loaders;

import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.TileTemplate;
import illarion.common.graphics.TileInfo;
import illarion.common.util.TableLoaderSink;
import illarion.common.util.TableLoaderTiles;
import org.apache.log4j.Logger;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;

/**
 * This class is used to load the tile definitions from the resource table that was created using the configuration
 * tool. The class will create the required tile objects and send them to the tile factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TileLoader extends AbstractResourceLoader<TileTemplate> implements
        TableLoaderSink<TableLoaderTiles> {
    /**
     * The logger that is used to report error messages.
     */
    private static final Logger LOGGER = Logger.getLogger(ItemLoader.class);

    /**
     * The assets of the game engine that are required to load the data needed for the tiles.
     */
    @Nonnull
    private final Assets assets;

    /**
     * Create a new tile loader.
     *
     * @param assets the assets instance of the game engine that is used to load the data
     */
    public TileLoader(@Nonnull final Assets assets) {
        this.assets = assets;
    }

    /**
     * Trigger the loading sequence for this loader.
     */
    @Override
    public ResourceFactory<TileTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<TileTemplate> factory = getTargetFactory();

        factory.init();
        new TableLoaderTiles(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    private static final String TILE_PATH = "data/tiles/";

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderTiles loader) {
        final int id = loader.getTileId();
        final int mode = loader.getTileMode();
        final String name = loader.getResourceName();
        final TileInfo info = new TileInfo(loader.getTileColor(), loader.getMovementCost(), loader.isOpaque());

        final int frames;
        final int speed;
        switch (mode) {
            case TableLoaderTiles.TILE_MODE_ANIMATED:
                frames = loader.getFrameCount();
                speed = loader.getAnimationSpeed();
                break;

            case TableLoaderTiles.TILE_MODE_VARIANT:
                frames = loader.getFrameCount();
                speed = 0;
                break;

            default:
                frames = 1;
                speed = 0;
                break;
        }

        final Sprite tileSprite = assets.getSpriteFactory().createSprite(getTextures(assets.getTextureManager(),
                TILE_PATH, name, frames), 0, 0, SpriteFactory.CENTER, SpriteFactory.CENTER, false);
        final TileTemplate template = new TileTemplate(id, tileSprite, frames, speed, info);

        try {
            getTargetFactory().storeResource(template);
        } catch (@Nonnull final IllegalStateException ex) {
            LOGGER.error("Failed adding tile to internal factory. ID: " + id + " - Filename: " + name);
        }

        return true;
    }

}

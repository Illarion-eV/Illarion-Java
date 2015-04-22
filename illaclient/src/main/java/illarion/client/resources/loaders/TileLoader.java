/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.resources.loaders;

import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.TileTemplate;
import illarion.common.graphics.TileInfo;
import illarion.common.util.TableLoaderSink;
import illarion.common.util.TableLoaderTiles;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class is used to load the tile definitions from the resource table that was created using the configuration
 * tool. The class will create the required tile objects and send them to the tile factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TileLoader extends AbstractResourceLoader<TileTemplate>
        implements TableLoaderSink<TableLoaderTiles> {
    /**
     * The logger that is used to report error messages.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(TileLoader.class);

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
    public TileLoader(@Nonnull Assets assets) {
        this.assets = assets;
    }

    /**
     * Trigger the loading sequence for this loader.
     */
    @Nonnull
    @Override
    public ResourceFactory<TileTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        ResourceFactory<TileTemplate> factory = getTargetFactory();

        factory.init();
        new TableLoaderTiles(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    private static final String TILE_PATH = "tiles/";

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(int line, @Nonnull TableLoaderTiles loader) {
        int id = loader.getTileId();
        int mode = loader.getTileMode();
        String name = loader.getResourceName();
        TileInfo info = new TileInfo(loader.getTileColor(), loader.isOpaque());

        int frames;
        int speed;
        switch (mode) {
            case TableLoaderTiles.TILE_MODE_ANIMATED:
                frames = loader.getFrameCount();
                speed = loader.getAnimationSpeed();
                break;
            case TableLoaderTiles.TILE_MODE_VARIANT:
                frames = loader.getFrameCount();
                speed = 0;
                break;
            case TableLoaderTiles.TILE_MODE_SIMPLE:
                frames = 1;
                speed = 0;
                break;
            default:
                log.error("Unknown mode {} for tile {}", mode, id);
                frames = 1;
                speed = 0;
                break;
        }

        Sprite tileSprite = assets.getSpriteFactory()
                .createSprite(getTextures(assets.getTextureManager(), TILE_PATH, name, frames), 0, 0,
                              SpriteFactory.CENTER, SpriteFactory.CENTER, false);

        try {
            TileTemplate template = new TileTemplate(id, tileSprite, frames, speed, info);
            getTargetFactory().storeResource(template);
        } catch (@Nonnull IllegalStateException ex) {
            log.error("Failed adding tile to internal factory. ID: {} - Filename: {}", id, name);
        }

        return true;
    }
}

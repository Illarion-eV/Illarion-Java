/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
import illarion.client.resources.data.OverlayTemplate;
import illarion.common.util.TableLoaderOverlay;
import illarion.common.util.TableLoaderSink;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class is used to load the overlay definitions from the resource table that was created using the
 * configuration tool. The class will create the required overlay objects and send them to the overlay factory that
 * takes care for distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class OverlayLoader extends AbstractResourceLoader<OverlayTemplate>
        implements TableLoaderSink<TableLoaderOverlay> {
    /**
     * The logger that is used to report error messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemLoader.class);

    /**
     * The assets of the game engine that are required to load the data needed for the overlays.
     */
    @Nonnull
    private final Assets assets;

    /**
     * Create a new overlay loader.
     *
     * @param assets the assets instance of the game engine that is used to load the data
     */
    public OverlayLoader(@Nonnull final Assets assets) {
        this.assets = assets;
    }

    /**
     * Trigger the loading sequence for this loader.
     */
    @Nonnull
    @Override
    public ResourceFactory<OverlayTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<OverlayTemplate> factory = getTargetFactory();

        factory.init();
        new TableLoaderOverlay(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    public static final String OVERLAY_PATH = "tiles/";

    public static final int OVERLAY_VARIATIONS = 28;

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderOverlay loader) {
        final int id = loader.getTileId();
        final String name = loader.getOverlayFile();

        final Sprite overlaySprite = assets.getSpriteFactory()
                .createSprite(getTextures(assets.getTextureManager(), OVERLAY_PATH, name, OVERLAY_VARIATIONS), 0, 0,
                              SpriteFactory.CENTER, SpriteFactory.CENTER, false);

        final OverlayTemplate template = new OverlayTemplate(id, overlaySprite);

        try {
            getTargetFactory().storeResource(template);
        } catch (@Nonnull final IllegalStateException ex) {
            LOGGER.error("Failed adding overlay to internal factory. ID: " + id + " - Filename: " + name);
        }

        return true;
    }
}

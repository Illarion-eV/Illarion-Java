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

import illarion.client.resources.MiscImageFactory;
import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.MiscImageTemplate;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.assets.TextureManager;
import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;

/**
 * This class is used to load the misc images required in the graphics.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MiscImageLoader extends AbstractResourceLoader<MiscImageTemplate> {
    /**
     * The path inside the resources where the GUI images are stored.
     */
    @Nonnull
    private static final String GUI_PATH = "gui/";

    /**
     * The assets of the game engine that are required to load the data needed for the misc images.
     */
    @Nonnull
    private final Assets assets;

    /**
     * Create a new misc image loader.
     *
     * @param assets the assets instance of the game engine that is used to load the data
     */
    public MiscImageLoader(@Nonnull Assets assets) {
        this.assets = assets;
    }

    /**
     * Trigger the loading sequence for this loader.
     */
    @Nonnull
    @Override
    public ResourceFactory<MiscImageTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        ResourceFactory<MiscImageTemplate> factory = getTargetFactory();

        factory.init();

        SpriteFactory sf = assets.getSpriteFactory();
        ResourceFactory<MiscImageTemplate> tf = getTargetFactory();
        TextureManager tm = assets.getTextureManager();

        Sprite attackMarkerSprite = sf
                .createSprite(getTextures(tm, GUI_PATH, "attackMarker", 1), 0, 0, SpriteFactory.CENTER,
                              SpriteFactory.CENTER, false);
        tf.storeResource(new MiscImageTemplate(MiscImageFactory.ATTACK_MARKER, attackMarkerSprite, 1));

        Sprite miniMapArrowSprite = sf
                .createSprite(getTextures(tm, GUI_PATH, "minimap_arrow", 1), 0, 71, SpriteFactory.CENTER,
                              SpriteFactory.TOP, false);
        tf.storeResource(new MiscImageTemplate(MiscImageFactory.MINI_MAP_ARROW, miniMapArrowSprite, 1));

        Sprite miniMapPointSprite = sf
                .createSprite(getTextures(tm, GUI_PATH, "minimap_point", 1), 0, 0, SpriteFactory.CENTER,
                              SpriteFactory.CENTER, false);
        tf.storeResource(new MiscImageTemplate(MiscImageFactory.MINI_MAP_POINT, miniMapPointSprite, 1));

        Sprite miniMapExclSprite = sf
                .createSprite(getTextures(tm, GUI_PATH, "minimap_exclamation", 1), 0, 0, SpriteFactory.CENTER,
                              SpriteFactory.CENTER, false);
        tf.storeResource(new MiscImageTemplate(MiscImageFactory.MINI_MAP_EXCLAMATION, miniMapExclSprite, 1));

        Sprite questMarker1 = sf
                .createSprite(getTextures(tm, GUI_PATH, "question_mark", 1), 0, 23, SpriteFactory.CENTER,
                              SpriteFactory.BOTTOM, false);
        tf.storeResource(new MiscImageTemplate(MiscImageFactory.QUEST_MARKER_QUESTION_MARK, questMarker1, 1));

        Sprite questMarker2 = sf
                .createSprite(getTextures(tm, GUI_PATH, "exclamation_mark", 1), 0, 23, SpriteFactory.CENTER,
                              SpriteFactory.BOTTOM, false);
        tf.storeResource(new MiscImageTemplate(MiscImageFactory.QUEST_MARKER_EXCLAMATION_MARK, questMarker2, 1));

        factory.loadingFinished();

        loadingDone();

        return factory;
    }
}

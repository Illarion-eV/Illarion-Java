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

import illarion.client.resources.CharacterFactory;
import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.AvatarClothTemplate;
import illarion.client.resources.data.AvatarTemplate;
import illarion.common.util.TableLoaderClothes;
import illarion.common.util.TableLoaderSink;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class takes care for loading the avatar clothes, sorts them to the avatars and prepares to render them.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ClothLoader extends AbstractResourceLoader<AvatarClothTemplate>
        implements TableLoaderSink<TableLoaderClothes> {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(ClothLoader.class);

    /**
     * The assets of the game engine that are required to load the data needed for the clothes.
     */
    @Nonnull
    private final Assets assets;

    /**
     * Create a new cloth loader.
     *
     * @param assets the assets instance of the game engine that is used to load the data
     */
    public ClothLoader(@Nonnull Assets assets) {
        this.assets = assets;
    }

    @Nonnull
    @Override
    public ResourceFactory<AvatarClothTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        ResourceFactory<AvatarClothTemplate> factory = getTargetFactory();

        factory.init();
        new TableLoaderClothes(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    /**
     * The resource path to the avatar graphics. All graphics need to be located at this path within the JAR-resource
     * files.
     */
    private static final String CLOTH_PATH = "chars/";

    /**
     * Handle one record from the table that is loaded by this function. This
     * function is called by the table loader.
     *
     * @param line the line in the list that is currently processed
     * @param loader the table loader class that handles the table that is
     * currently loading
     * @return true in case the loader shall go on reading the table, false if
     * it should stop
     */
    @SuppressWarnings("nls")
    @Override
    public boolean processRecord(int line, @Nonnull TableLoaderClothes loader) {
        int avatarID = loader.getReferenceCharacterId();
        int itemID = loader.getReferenceItemId();
        int location = loader.getClothSlot();
        String name = loader.getResourceName();
        int frames = loader.getFrameCount();
        boolean mirror = loader.isMirrored();

        AvatarTemplate avatarTemplate = CharacterFactory.getInstance().getTemplate(avatarID);

        int offsetX = loader.getOffsetX() + avatarTemplate.getSprite().getOffsetX();
        int offsetY = loader.getOffsetY() + avatarTemplate.getSprite().getOffsetY();

        Sprite clothSprite = assets.getSpriteFactory()
                .createSprite(getTextures(assets.getTextureManager(), CLOTH_PATH, name, frames), offsetX, offsetY,
                              SpriteFactory.CENTER, SpriteFactory.BOTTOM, mirror);
        AvatarClothTemplate template = new AvatarClothTemplate(itemID, clothSprite, loader.getFrameCount(), avatarID,
                                                               location);

        try {
            getTargetFactory().storeResource(template);
        } catch (@Nonnull IllegalStateException e) {
            log.error("Error adding paperdolling item to avatar: {} in group: {} to item: {}", avatarID, location,
                    itemID);
        }
        return true;
    }
}

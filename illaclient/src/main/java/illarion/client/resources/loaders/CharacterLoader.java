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

import illarion.client.graphics.AvatarInfo;
import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.AvatarTemplate;
import illarion.common.types.Direction;
import illarion.common.util.TableLoaderCharacters;
import illarion.common.util.TableLoaderSink;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class is used to load the character definitions from the resource table that was created using the
 * configuration tool. The class will create the required character objects and send them to the character factory
 * that takes care for distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharacterLoader extends AbstractResourceLoader<AvatarTemplate>
        implements TableLoaderSink<TableLoaderCharacters> {
    /**
     * The logger that is used to report error messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterLoader.class);

    /**
     * The assets of the game engine that are required to load the data needed for the characters.
     */
    @Nonnull
    private final Assets assets;

    /**
     * Create a new character loader.
     *
     * @param assets the assets instance of the game engine that is used to load the data
     */
    public CharacterLoader(@Nonnull Assets assets) {
        this.assets = assets;
    }

    @Nonnull
    @Override
    public ResourceFactory<AvatarTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        ResourceFactory<AvatarTemplate> factory = getTargetFactory();

        factory.init();
        new TableLoaderCharacters(this);
        factory.loadingFinished();
        AvatarInfo.cleanup();

        loadingDone();

        return factory;
    }

    /**
     * The resource path to the avatar graphics. All graphics need to be located at this path within the JAR-resource
     * files.
     */
    private static final String CHAR_PATH = "chars/";

    @Override
    public boolean processRecord(int line, @Nonnull TableLoaderCharacters loader) {
        int avatarId = loader.getAvatarId();
        String name = loader.getResourceName();
        int frames = loader.getFrameCount();
        int stillFrame = loader.getStillFrame();
        int offsetX = loader.getOffsetX();
        int offsetY = loader.getOffsetY();
        int shadowOffset = loader.getShadowOffset();
        boolean mirror = loader.isMirrored();
        Direction direction = loader.getDirection();
        int appearance = loader.getAppearance();
        int visibleMod = loader.getVisibilityMod();
        int animationID = loader.getAnimationId();
        int skinRed = loader.getSkinColorRed();
        int skinGreen = loader.getSkinColorGreen();
        int skinBlue = loader.getSkinColorBlue();

        AvatarInfo info = AvatarInfo.getInstance(appearance, visibleMod);
        info.reportAnimation(animationID);

        Color defaultColor = new Color(skinRed, skinGreen, skinBlue);

        Sprite avatarSprite = assets.getSpriteFactory()
                .createSprite(getTextures(assets.getTextureManager(), CHAR_PATH, name, frames), offsetX, offsetY,
                              SpriteFactory.CENTER, SpriteFactory.BOTTOM, mirror);

        AvatarTemplate template = new AvatarTemplate(avatarId, avatarSprite, frames, stillFrame, defaultColor,
                                                           shadowOffset, direction, info);

        try {
            getTargetFactory().storeResource(template);
        } catch (@Nonnull IllegalStateException ex) {
            LOGGER.error("Failed adding avatar to internal factory. ID: {} - Filename: {}", avatarId, name);
        }

        return true;
    }
}

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

import illarion.client.graphics.AvatarInfo;
import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.AvatarTemplate;
import illarion.common.util.TableLoaderCharacters;
import illarion.common.util.TableLoaderSink;
import org.apache.log4j.Logger;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;

/**
 * This class is used to load the character definitions from the resource table that was created using the
 * configuration tool. The class will create the required character objects and send them to the character factory
 * that takes care for distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharacterLoader extends AbstractResourceLoader<AvatarTemplate> implements
        TableLoaderSink<TableLoaderCharacters> {
    /**
     * The logger that is used to report error messages.
     */
    private static final Logger LOGGER = Logger.getLogger(ItemLoader.class);

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
    public CharacterLoader(@Nonnull final Assets assets) {
        this.assets = assets;
    }

    @Override
    public ResourceFactory<AvatarTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<AvatarTemplate> factory = getTargetFactory();

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
    public boolean processRecord(final int line, @Nonnull final TableLoaderCharacters loader) {
        final int avatarId = loader.getAvatarId();
        final String name = loader.getResourceName();
        final int frames = loader.getFrameCount();
        final int stillFrame = loader.getStillFrame();
        final int offsetX = loader.getOffsetX();
        final int offsetY = loader.getOffsetY();
        final int shadowOffset = loader.getShadowOffset();
        final boolean mirror = loader.isMirrored();
        final int direction = loader.getDirection();
        final int appearance = loader.getAppearance();
        final int visibleMod = loader.getVisibilityMod();
        final int animationID = loader.getAnimationId();
        final int skinRed = loader.getSkinColorRed();
        final int skinGreen = loader.getSkinColorGreen();
        final int skinBlue = loader.getSkinColorBlue();

        final AvatarInfo info = AvatarInfo.getInstance(appearance, visibleMod);
        info.reportAnimation(animationID);

        final Color defaultColor = new Color(skinRed, skinGreen, skinBlue);

        final Sprite avatarSprite = assets.getSpriteFactory().createSprite(getTextures(assets.getTextureManager(),
                CHAR_PATH, name, frames), offsetX, offsetY, SpriteFactory.CENTER, SpriteFactory.BOTTOM, mirror);

        final AvatarTemplate template = new AvatarTemplate(avatarId, avatarSprite, frames, stillFrame, defaultColor,
                shadowOffset, direction, info);

        try {
            getTargetFactory().storeResource(template);
        } catch (@Nonnull final IllegalStateException ex) {
            LOGGER.error("Failed adding avatar to internal factory. ID: " + avatarId + " - Filename: " + name);
        }

        return true;
    }
}

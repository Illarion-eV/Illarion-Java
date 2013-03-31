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

import illarion.client.resources.CharacterFactory;
import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.AvatarClothTemplate;
import illarion.client.resources.data.AvatarTemplate;
import illarion.common.util.TableLoaderClothes;
import illarion.common.util.TableLoaderSink;
import org.apache.log4j.Logger;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;

/**
 * This class takes care for loading the avatar clothes, sorts them to the avatars and prepares to render them.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ClothLoader extends AbstractResourceLoader<AvatarClothTemplate> implements
        TableLoaderSink<TableLoaderClothes> {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ClothLoader.class);

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
    public ClothLoader(@Nonnull final Assets assets) {
        this.assets = assets;
    }

    @Override
    public ResourceFactory<AvatarClothTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<AvatarClothTemplate> factory = getTargetFactory();

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
    private static final String CLOTH_PATH = "data/chars/";

    /**
     * Handle one record from the table that is loaded by this function. This
     * function is called by the table loader.
     *
     * @param line   the line in the list that is currently processed
     * @param loader the table loader class that handles the table that is
     *               currently loading
     * @return true in case the loader shall go on reading the table, false if
     *         it should stop
     */
    @SuppressWarnings("nls")
    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderClothes loader) {
        final int avatarID = loader.getReferenceCharacterId();
        final int itemID = loader.getReferenceItemId();
        final int location = loader.getClothSlot();
        final String name = loader.getResourceName();
        final int frames = loader.getFrameCount();
        final boolean mirror = loader.isMirrored();

        final AvatarTemplate avatarTemplate = CharacterFactory.getInstance().getTemplate(avatarID);

        final int offsetX = loader.getOffsetX() + avatarTemplate.getSprite().getOffsetX();
        final int offsetY = loader.getOffsetY() + avatarTemplate.getSprite().getOffsetY();

        final Sprite clothSprite = assets.getSpriteFactory().createSprite(getTextures(assets.getTextureManager(),
                CLOTH_PATH, name, frames), offsetX, offsetY, SpriteFactory.CENTER, SpriteFactory.BOTTOM, mirror);
        final AvatarClothTemplate template = new AvatarClothTemplate(itemID, clothSprite, loader.getFrameCount(),
                avatarID, location);

        try {
            getTargetFactory().storeResource(template);
        } catch (@Nonnull final IllegalStateException e) {
            LOGGER.error("Error adding paperdolling item to avatar: " + avatarID + " in group: " + location + " to " +
                    "item: " + itemID);
        }
        return true;
    }
}

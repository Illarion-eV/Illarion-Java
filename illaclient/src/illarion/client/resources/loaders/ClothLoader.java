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

import illarion.client.graphics.Sprite;
import illarion.client.graphics.SpriteBuffer;
import illarion.client.resources.CharacterFactory;
import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.AvatarClothTemplate;
import illarion.client.resources.data.AvatarTemplate;
import illarion.common.util.TableLoaderClothes;
import illarion.common.util.TableLoaderSink;
import org.apache.log4j.Logger;

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

    @Override
    public ResourceFactory<AvatarClothTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<AvatarClothTemplate> factory = getTargetFactory();

        factory.init();
        new TableLoaderClothes(this);
        factory.loadingFinished();

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

        final AvatarTemplate avatarTemplate = CharacterFactory.getInstance().getTemplate(avatarID);

        final Sprite clothSprite = SpriteBuffer.getInstance().getSprite(CLOTH_PATH, loader.getResourceName(),
                loader.getFrameCount(), loader.getOffsetX() + avatarTemplate.getSprite().getOffsetX(),
                loader.getOffsetY() + avatarTemplate.getSprite().getOffsetY(), Sprite.HAlign.center,
                Sprite.VAlign.bottom, loader.isMirrored());
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

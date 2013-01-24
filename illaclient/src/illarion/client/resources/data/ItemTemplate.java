/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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
package illarion.client.resources.data;

import illarion.client.graphics.FrameAnimation;
import illarion.client.graphics.Sprite;
import illarion.client.graphics.TextureLoader;
import illarion.common.graphics.ItemInfo;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This is the template that contains the required data to create the graphical representation of a item on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public class ItemTemplate extends AbstractAnimatedEntityTemplate {
    /**
     * The minimal height of the item image in pixels that is needed so the item graphic fades out in case the player
     * avatar is (partly) hidden by the item.
     */
    private static final int FADING_LIMIT = 70;

    /**
     * The general item information.
     */
    @Nonnull
    private final ItemInfo itemInfo;

    /**
     * The referenced paperdolling ID of this item.
     */
    private final int paperdollingId;

    /**
     * The color that is used in case his item refers to a paperdolling item.
     */
    private final Color paperdollingColor;

    /**
     * The GUI texture of this item.
     */
    @Nonnull
    private final Image guiTexture;

    /**
     * The sharedAnimation instance all the items share.
     */
    @Nonnull
    private final FrameAnimation sharedAnimation;

    /**
     * The constructor of this class.
     *
     * @param id                the identification number of the entity
     * @param name              the name of the item graphic (used to check for a GUI graphic
     * @param sprite            the sprite used to render the entity
     * @param frames            the total amount of frames
     * @param shadowOffset      the offset of the shadow
     * @param speed             the animation speed
     * @param itemInfo          the general item information
     * @param paperdollingId    the referenced paperdolling id
     * @param paperdollingColor the color that is applied to the paperdolling item
     */
    public ItemTemplate(final int id, @Nonnull final String name, @Nonnull final Sprite sprite, final int frames,
                        final int shadowOffset, final int speed, @Nonnull final ItemInfo itemInfo,
                        final int paperdollingId, @Nullable final Color paperdollingColor) {
        super(id, sprite, frames, 0, speed, null, shadowOffset);

        this.itemInfo = itemInfo;
        this.paperdollingId = paperdollingId;
        this.paperdollingColor = paperdollingColor;
        sharedAnimation = new FrameAnimation();
        sharedAnimation.setup(frames, 0, speed, FrameAnimation.LOOPED);

        final Image guiTexture = TextureLoader.getInstance().getTexture("data/gui/", "items/" + name, null);
        if (guiTexture == null) {
            this.guiTexture = getSprite().getTexture(0);
        } else {
            this.guiTexture = guiTexture;
        }
    }

    @Nonnull
    public ItemInfo getItemInfo() {
        return itemInfo;
    }

    public int getPaperdollingId() {
        return paperdollingId;
    }

    @Nullable
    public Color getPaperdollingColor() {
        return paperdollingColor;
    }

    @Nonnull
    public Image getGuiTexture() {
        return guiTexture;
    }

    public boolean isEffectedByFadingCorridor() {
        return getSprite().getHeight() > FADING_LIMIT;
    }

    @Nonnull
    public FrameAnimation getSharedAnimation() {
        return sharedAnimation;
    }
}

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
package illarion.client.resources.data;

import illarion.client.graphics.AvatarClothManager;
import illarion.client.graphics.AvatarInfo;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This is the template that contains the required data to create the graphical representation of a avatar on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public class AvatarTemplate extends AbstractMultiFrameEntityTemplate {
    /**
     * The direction this avatar is facing.
     */
    private final int direction;

    /**
     * The clothes this avatar is able to wear.
     */
    @Nonnull
    private final AvatarClothManager clothes;

    /**
     * General information about this avatar.
     */
    @Nonnull
    private final AvatarInfo avatarInfo;

    /**
     * The constructor of this class.
     *
     * @param id the identification number of the entity
     * @param sprite the sprite used to render the entity
     * @param frames the total amount of frames
     * @param stillFrame the frame that is displayed as first and last frame and in case the animation is stopped
     * @param defaultColor the color that is used to render this avatar
     * @param shadowOffset the size of the shadow or of light effects in the graphics
     * @param direction the direction the character is facing
     * @param avatarInfo the general avatar information object
     */
    public AvatarTemplate(
            final int id,
            @Nonnull final Sprite sprite,
            final int frames,
            final int stillFrame,
            @Nullable final Color defaultColor,
            final int shadowOffset,
            final int direction,
            @Nonnull final AvatarInfo avatarInfo) {
        super(id, sprite, frames, stillFrame, defaultColor, shadowOffset);

        this.direction = direction;
        this.avatarInfo = avatarInfo;
        clothes = new AvatarClothManager();
    }

    public int getDirection() {
        return direction;
    }

    @Nonnull
    public AvatarClothManager getClothes() {
        return clothes;
    }

    @Nonnull
    public AvatarInfo getAvatarInfo() {
        return avatarInfo;
    }
}

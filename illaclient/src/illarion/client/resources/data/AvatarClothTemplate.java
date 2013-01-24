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

import illarion.client.graphics.Sprite;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This is the template that contains the required data to create the graphical representation of a avatar cloth.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public class AvatarClothTemplate extends AbstractMultiFrameEntityTemplate {
    /**
     * The ID of the parent avatar.
     */
    private final int avatarId;

    /**
     * The ID of the cloth slot this cloth is displayed in.
     */
    private final int clothSlot;

    /**
     * The constructor of this class.
     *
     * @param id        the identification number of the entity
     * @param sprite    the sprite used to render the entity
     * @param frames    the total amount of frames
     * @param avatarId  the ID of the avatar this cloth belongs to
     * @param clothSlot the ID of the slot this cloth belongs to
     */
    public AvatarClothTemplate(final int id, @Nonnull final Sprite sprite, final int frames, final int avatarId,
                               final int clothSlot) {
        super(id, sprite, frames, 0, null, 0);

        this.avatarId = avatarId;
        this.clothSlot = clothSlot;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public int getClothSlot() {
        return clothSlot;
    }
}

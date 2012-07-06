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
package illarion.client.graphics;

import illarion.client.resources.CharacterFactory;
import illarion.client.resources.ClothFactory;
import illarion.client.resources.Resource;
import org.newdawn.slick.Color;

/**
 * A avatar cloth definition stores all data about a cloth that are needed to
 * know. It also allows to render a cloth part.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AvatarCloth extends AbstractEntity implements Resource {
    /**
     * The resource path to the avatar graphics. All graphics need to be located
     * at this path within the JAR-resource files.
     */
    private static final String CLOTH_PATH = "data/chars/"; //$NON-NLS-1$

    /**
     * The factory that created and handles this instance of Avatar Cloth.
     */
    private transient ClothFactory parent;

    /**
     * The ID of the avatar this cloth belongs to.
     */
    private int avatar;

    /**
     * The ID of the location the piece of cloth is located at.
     */
    private int locationId;

    /**
     * Definition constructor. This one sets up a new avatar cloth and sets all
     * needed configurations.
     *
     * @param avatarId the ID of the avatar the cloth is assigned to
     * @param itemID   the ID of the avatar cloth that is
     * @param name     the name of the cloth, that is the file name of the cloth
     *                 graphic
     * @param location the ID of the location this cloth is located at
     * @param frames   the count of frames this cloth contains
     * @param still    the number of the frame that is the start and the end of the
     *                 animation
     * @param offX     the x offset
     * @param offY     the y offset
     * @param mirror   true in case the graphic should be mirrored
     * @param baseCol  the base coloring graphic
     */
    public AvatarCloth(final int avatarId, final int itemID,
                       final String name, final int location, final int frames,
                       final int still, final int offX, final int offY, final boolean mirror,
                       final Color baseCol) {
        super(itemID, CLOTH_PATH, name, frames, still, offX
                + getAvatarOffsetX(avatarId), offY + getAvatarOffsetY(avatarId),
                0, Sprite.HAlign.center, Sprite.VAlign.bottom, true, mirror,
                baseCol);

        avatar = avatarId;
        locationId = location;
        reset();
        setFadingCorridorEffectEnabled(false);
    }

    private static int getAvatarOffsetX(final int avatarId) {
        if (avatarId == 0) {
            return 0;
        }
        final Avatar ava =
                CharacterFactory.getInstance().getPrototype(avatarId);
        if (ava == null) {
            return 0;
        }
        return ava.getSprite().getOffsetX();
    }

    private static int getAvatarOffsetY(final int avatarId) {
        if (avatarId == 0) {
            return 0;
        }
        final Avatar ava =
                CharacterFactory.getInstance().getPrototype(avatarId);
        if (ava == null) {
            return 0;
        }
        return ava.getSprite().getOffsetY();
    }

    /**
     * Get the cloth that is used by default.
     *
     * @return the default cloth
     */
    public static AvatarCloth getDefaultCloth() {
        return new AvatarCloth();
    }

    /**
     * The constructor to create the single default cloth.
     */
    private AvatarCloth() {
        super(0, CLOTH_PATH, null, 0, 0, 0, 0, 0, Sprite.HAlign.center,
                Sprite.VAlign.bottom, true, false, null);
        avatar = 0;
        locationId = 0;
    }

    /**
     * Copy constructor. Create a copy of this object.
     *
     * @param org the instance of AvatarCloth that shall be copied
     */
    private AvatarCloth(final AvatarCloth org) {
        super(org);
        parent = org.parent;
        avatar = org.avatar;
        locationId = org.locationId;
        reset();
    }

    /**
     * Get the ID of the avatar that piece of cloth belongs to.
     */
    public int getAvatarId() {
        return avatar;
    }

    /**
     * The ID of the location where the cloth is displayed on the character.
     *
     * @return the location ID
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * Create a duplicate of the AvatarCloth object.
     *
     * @return the new created instance of AvatarCloth that is a copy of the
     *         current instance
     */
    @Override
    public AvatarCloth clone() {
        return new AvatarCloth(this);
    }

    /**
     * Recycle this instance of Avatar Cloth and send it back into its factory.
     */
    @Override
    public void recycle() {
        hide();
        super.changeBaseColor(null);
        parent.recycle(this);
    }

    /**
     * Set the factory this Cloth is managed by.
     *
     * @param parentFactory the factory that is the parent of this cloth object
     */
    public void setFactory(final ClothFactory parentFactory) {
        parent = parentFactory;
    }

    @Override
    public final void show() {
        // do nothing
    }

    @Override
    public final void hide() {
        // do nothing
    }
}

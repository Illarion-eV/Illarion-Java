/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

/**
 * A avatar cloth definition stores all data about a cloth that are needed to
 * know. It also allows to render a cloth part.
 * 
 * @author Martin Karing
 * @since 1.22
 */
final class AvatarCloth extends AbstractEntity {
    /**
     * The resource path to the avatar graphics. All graphics need to be located
     * at this path within the JAR-resource files.
     */
    private static final String CLOTH_PATH = "data/chars/"; //$NON-NLS-1$

    /**
     * The factory that created and handles this instance of Avatar Cloth.
     */
    private transient AvatarClothFactory parent;

    /**
     * Definition constructor. This one sets up a new avatar cloth and sets all
     * needed configurations.
     * 
     * @param itemID the ID of the avatar cloth that is
     * @param name the name of the cloth, that is the file name of the cloth
     *            graphic
     * @param frames the count of frames this cloth contains
     * @param still the number of the frame that is the start and the end of the
     *            animation
     * @param offX the x offset
     * @param offY the y offset
     * @param mirror true in case the graphic should be mirrored
     * @param baseCol the base coloring graphic
     */
    protected AvatarCloth(final int itemID, final String name,
        final int frames, final int still, final int offX, final int offY,
        final boolean mirror, final SpriteColor baseCol) {
        super(itemID, CLOTH_PATH, name, frames, still, offX, offY, 0,
            Sprite.HAlign.center, Sprite.VAlign.bottom, true, mirror, baseCol);
        reset();
    }

    /**
     * Copy constructor. Create a copy of this object.
     * 
     * @param org the instance of AvatarCloth that shall be copied
     */
    private AvatarCloth(final AvatarCloth org) {
        super(org);
        parent = org.parent;
        reset();
    }

    /**
     * Activate the cloth instance. Normally the ID is overwritten with this
     * command, but that would be terrible here.
     * 
     * @param overwriteID the requested ID
     */
    @Override
    public void activate(final int overwriteID) {
        // nothing to do
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
        parent.recycle(this);
        super.changeBaseColor(null);
    }

    /**
     * Set the factory this Cloth is managed by.
     * 
     * @param parentFactory the factory that is the parent of this cloth object
     */
    public void setFactory(final AvatarClothFactory parentFactory) {
        parent = parentFactory;
    }

    /**
     * Update the alpha value regarding the needed fading operations. Since the
     * fading needs to be done in the same way as the character sprite, this
     * function won't change anything.
     * 
     * @param delta the time in milliseconds since the last render run
     * @return the new alpha value
     */
    @Override
    public void update(final int delta) {
        // nothing to do
    }
}

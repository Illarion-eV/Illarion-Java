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

import org.newdawn.slick.Color;

/**
 * This class is used to store the markers that are displayed below a avatar.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AvatarMarker extends AbstractEntity {
    public AvatarMarker(final AvatarMarker org) {
        super(org);
    }

    public AvatarMarker(final int entityId, final Sprite displayedSprite, final int still, final Color baseCol) {
        super(entityId, displayedSprite, still, 0, baseCol);
    }

    /**
     * The clone operation creates a copy of the entity in case it is needed.
     */
    @Override
    public AbstractEntity clone() {
        return new AvatarMarker(this);
    }

    /**
     * Mark this object as unused and store it for later usage.
     */
    @Override
    public void recycle() {
        // nothing
    }
}

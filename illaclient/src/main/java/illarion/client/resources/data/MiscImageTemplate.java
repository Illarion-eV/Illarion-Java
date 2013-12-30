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

import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This is the template that contains the required data to create the graphical representation of a utility or GUI
 * graphic.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public class MiscImageTemplate extends AbstractMultiFrameEntityTemplate {
    /**
     * The constructor of this class.
     *
     * @param id     the identification number of the entity
     * @param sprite the sprite used to render the entity
     * @param frames the total amount of frames
     */
    public MiscImageTemplate(final int id, @Nonnull final Sprite sprite, final int frames) {
        super(id, sprite, frames, 0, null, 0);
    }
}

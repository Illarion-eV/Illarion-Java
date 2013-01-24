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
 * This is the template that contains the required data to create the graphical representation of a effect on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public class EffectTemplate extends AbstractAnimatedEntityTemplate {
    /**
     * The encoded light value of this effect.
     */
    private final int light;

    /**
     * The constructor of this class.
     *
     * @param id     the identification number of the entity
     * @param sprite the sprite used to render the entity
     * @param frames the total amount of frames
     * @param speed  the animation speed
     */
    public EffectTemplate(final int id, @Nonnull final Sprite sprite, final int frames,
                          final int speed, final int light) {
        super(id, sprite, frames, 0, speed, null, 0);

        this.light = light;
    }

    public int getLight() {
        return light;
    }
}

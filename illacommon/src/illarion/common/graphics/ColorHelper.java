/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.graphics;

import org.newdawn.slick.Color;

import javax.annotation.Nonnull;

public final class ColorHelper {
    /**
     * Private constructor to avoid the creation of any instances of this class.
     */
    private ColorHelper() {
    }

    ;

    public static float getLuminationf(@Nonnull final Color color) {
        return (color.r + color.g + color.b) / 3.f;
    }
}

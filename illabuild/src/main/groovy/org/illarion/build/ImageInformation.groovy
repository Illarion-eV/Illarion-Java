/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Build Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.build

/**
 * This class is used to store the information about a single image.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
private class ImageInformation {
    /**
     * The source file.
     */
    File source

    /**
     * The height of the image.
     */
    int height

    /**
     * The width of the image.
     */
    int width

    /**
     * This stores if the image contains alpha.
     */
    boolean hasAlpha

    /**
     * This stores if the image uses colors.
     */
    boolean hasColor
}

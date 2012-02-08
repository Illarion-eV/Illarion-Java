/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
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
package illarion.client.graphics;

import java.util.Comparator;
import org.lwjgl.opengl.DisplayMode;

/**
 *
 * @author Stefano Bonicatti &lt;smjert@gmail.com&gt;
 */
public class DisplayModeSorter implements Comparator<DisplayMode> {

    @Override
    public int compare(DisplayMode a, DisplayMode b) {
        //Width
        if (a.getWidth() != b.getWidth())
            return (a.getWidth() > b.getWidth()) ?  1 : -1;
        //Height
        if (a.getHeight() != b.getHeight())
            return (a.getHeight() > b.getHeight()) ?  1 : -1;
        //Bit depth
        if (a.getBitsPerPixel()!= b.getBitsPerPixel())
            return (a.getBitsPerPixel() > b.getBitsPerPixel()) ?  1 : -1;
        //Refresh rate
        if (a.getFrequency()!= b.getFrequency())
            return (a.getFrequency()> b.getFrequency()) ?  1 : -1;
        //All fields are equal
        return 0;
    }
}

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
package illarion.client.guiNG.elements;

import javolution.xml.XMLSerializable;

/**
 * The desktop wallpaper contains a function that is called when the background
 * image of a desktop widget shall be drawn.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public interface DesktopWallpaper extends XMLSerializable {
    /**
     * Draw the wallpaper on a desktop widget.
     * 
     * @param delta the time since the drawing function was called last time
     * @param width the width of the area the wallpaper is placed in
     * @param height the height of the are the wallpaper is placed in
     */
    void draw(int delta, int width, int height);
}

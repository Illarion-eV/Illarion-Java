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
package illarion.client.guiNG;

import illarion.graphics.SpriteColor;

/**
 * This interface provides access to the indicators in the GUI. This are the
 * mana, food and health bars. By accessing this indicators its possible to set
 * the value of such a indicator to a new value or changing the color of the
 * displayed bar.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public interface Indicator {
    /**
     * Reset the color of the health bar to the default one.
     */
    void resetColor();

    /**
     * Set the color of the indicator. This can be useful for special effects as
     * in coloring the health bar green in case the character is poisoned.
     * 
     * @param color the new color of the indicator bar
     */
    void setColor(SpriteColor color);

    /**
     * Set the value of the indicator to a new one.
     * 
     * @param newValue the new value the indicator is supposed to display.
     */
    void setValue(int newValue);
}

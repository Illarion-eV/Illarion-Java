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
package illarion.client.gui;

import illarion.common.types.Location;

import javax.annotation.Nonnull;

/**
 * This is the GUI controller that manages the GUI elements that are directly related to the elements on the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface GameMapGui {
    /**
     * Show a item tooltip in the GUI.
     *
     * @param location the location of the item on the map
     * @param tooltip  the tooltip of the item that is supposed to be displayed
     */
    void showItemTooltip(@Nonnull Location location, @Nonnull Tooltip tooltip);

    /**
     * Toggle the pulsing animation of the run button.
     */
    void toggleRunMode();
}

/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls;

import de.lessvoid.nifty.render.NiftyImage;

/**
 * This interfaces defines a entry in the select dialog that contains the selectable entry.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface SelectListEntry {
    /**
     * Get the image that is supposed to be displayed in the entry.
     *
     * @return the nifty image to display
     */
    NiftyImage getItemImage();

    /**
     * Get the name of the item.
     *
     * @return the name of the item
     */
    String getName();

    /**
     * The index of the select item in the list as it was transferred from the server.
     *
     * @return the index
     */
    int getIndex();
}

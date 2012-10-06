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
import illarion.common.types.ItemId;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Karing
 * Date: 05.10.12
 * Time: 19:50
 * To change this template use File | Settings | File Templates.
 */
public interface CraftingListEntry {
    int getIngredientCount();

    ItemId getIngredientItemId(int index);

    int getIngredientCount(int index);

    String getName();

    NiftyImage getImage();

    /**
     * Get the crafting time in seconds.
     *
     * @return the crafting time in seconds
     */
    double getCraftTime();

    NiftyImage getIngredientImage(int index);
}

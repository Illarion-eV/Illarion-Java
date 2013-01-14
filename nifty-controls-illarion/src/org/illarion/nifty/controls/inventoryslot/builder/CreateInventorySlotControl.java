/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2013 - Illarion e.V.
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
package org.illarion.nifty.controls.inventoryslot.builder;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyIdCreator;
import de.lessvoid.nifty.controls.dynamic.attributes.ControlAttributes;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.loaderv2.types.ControlType;
import de.lessvoid.nifty.loaderv2.types.ElementType;
import de.lessvoid.nifty.screen.Screen;
import org.illarion.nifty.controls.InventorySlot;

public class CreateInventorySlotControl extends ControlAttributes {
    /**
     * The identifier string of the inventory slot control.
     */
    static final String NAME = "inventorySlot";

    /**
     * Create a new inventory slot with a automatically generated ID.
     */
    public CreateInventorySlotControl() {
        setAutoId(NiftyIdCreator.generate());
        setName(NAME);
    }

    /**
     * Create a new inventory slot with a user defined ID.
     *
     * @param id the ID of the new control
     */
    public CreateInventorySlotControl(final String id) {
        setId(id);
        setName(NAME);
    }

    /**
     * Create the inventory slot.
     *
     * @param nifty  the instance of the Nifty-GUI that will display the slot
     * @param screen the screen this slot will be a part of
     * @param parent the parent element of this slot
     * @return the newly created inventory slot
     */
    public InventorySlot create(final Nifty nifty, final Screen screen,
                                final Element parent) {
        nifty.addControl(screen, parent, getStandardControl());
        nifty.addControlsWithoutStartScreen();
        return parent.findNiftyControl(attributes.get("id"),
                InventorySlot.class);
    }

    /**
     * Create the element type of this slot.
     *
     * @return the element type of the slot
     */
    @Override
    public ElementType createType() {
        return new ControlType(attributes);
    }
}

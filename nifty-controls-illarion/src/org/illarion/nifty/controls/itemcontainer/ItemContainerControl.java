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
package org.illarion.nifty.controls.itemcontainer;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;
import org.illarion.nifty.controls.InventorySlot;
import org.illarion.nifty.controls.ItemContainer;
import org.illarion.nifty.controls.inventoryslot.builder.InventorySlotBuilder;

import java.util.Properties;

/**
 * This is the control class for the item containers. It takes care for the proper initialization of the container.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Use the {@link ItemContainer} interface for accessing this class
 */
@Deprecated
public class ItemContainerControl extends WindowControl implements ItemContainer {
    /**
     * The default size in pixels applied to the height and the width of a inventory slot in case no different value
     * is supplied.
     */
    private static final int SLOT_DEFAULT_SIZE = 32;

    /**
     * The array that stores the inventory slots that are a part of this class.
     */
    private InventorySlot[] slots;

    /**
     * The amount of slots in one rows.
     */
    private int columns;

    /**
     * Default constructor.
     */
    public ItemContainerControl() {
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen, final Element element, final Properties parameter, final Attributes controlDefinitionAttributes) {
        super.bind(nifty, screen, element, parameter, controlDefinitionAttributes);

        final int slotCount;
        if (controlDefinitionAttributes.isSet("slots")) {
            slotCount = controlDefinitionAttributes.getAsInteger("slots");
        } else {
            throw new IllegalStateException("Amount of slots not set!!");
        }

        columns = (int) Math.ceil(Math.sqrt(slotCount));

        final int slotHeight = controlDefinitionAttributes.getAsInteger("slotHeight", SLOT_DEFAULT_SIZE);
        final int slotWidth = controlDefinitionAttributes.getAsInteger("slotWidth", SLOT_DEFAULT_SIZE);
        final String slotBackground = "data/gui/containerslot.png";

        final Element contentPanel = getElement().findElementByName("#contentPanel");
        PanelBuilder currentPanelBuilder = null;

        for (int i = 0; i < slotCount; i++) {
            if (currentPanelBuilder == null) {
                currentPanelBuilder = new PanelBuilder();
                currentPanelBuilder.childLayoutHorizontal();
                currentPanelBuilder.width(currentPanelBuilder.pixels(slotWidth * columns));
                currentPanelBuilder.height(currentPanelBuilder.pixels(slotHeight));
                currentPanelBuilder.marginBottom("1px");
            }

            currentPanelBuilder.control(buildSlot(i, slotHeight, slotWidth, slotBackground));

            if (((i + 1) % columns) == 0) {
                currentPanelBuilder.build(nifty, screen, contentPanel);
                currentPanelBuilder = null;
            }
        }

        if (currentPanelBuilder != null) {
            currentPanelBuilder.build(nifty, screen, contentPanel);
        }

        slots = new InventorySlot[slotCount];
        for (int i = 0; i < slotCount; i++) {
            slots[i] = getElement().findNiftyControl("#slot" + i, InventorySlot.class);
        }

        getElement().getParent().layoutElements();
    }

    /**
     * Build a inventory slot.
     *
     * @param index          the index of the slot
     * @param height         the height of the slot
     * @param width          the width of the slot
     * @param slotBackground the background image of the slot
     * @return the builder of the inventory slot
     */
    private static ControlBuilder buildSlot(final int index, final int height, final int width, final String slotBackground) {
        final InventorySlotBuilder builder = new InventorySlotBuilder("#slot" + index);
        builder.height(builder.pixels(height));
        builder.width(builder.pixels(width));

        if (slotBackground != null) {
            builder.background(slotBackground);
        }
        return builder;
    }

    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent) {
        return true;
    }

    @Override
    public int getSlotCount() {
        return slots.length;
    }

    @Override
    public InventorySlot getSlot(final int index) {
        return slots[index];
    }
}

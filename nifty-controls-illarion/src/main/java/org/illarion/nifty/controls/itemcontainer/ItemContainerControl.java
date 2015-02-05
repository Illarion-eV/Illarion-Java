/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package org.illarion.nifty.controls.itemcontainer;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import org.illarion.nifty.controls.InventorySlot;
import org.illarion.nifty.controls.ItemContainer;
import org.illarion.nifty.controls.ItemContainerCloseEvent;
import org.illarion.nifty.controls.inventoryslot.builder.InventorySlotBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    private Nifty niftyInstance;

    @Override
    public void bind(
            @Nonnull Nifty nifty, @Nonnull Screen screen, @Nonnull Element element, @Nonnull Parameters parameter) {
        super.bind(nifty, screen, element, parameter);

        niftyInstance = nifty;

        int slotCount = parameter.getAsInteger("slots", -1);
        if (slotCount == -1) {
            throw new IllegalStateException("Amount of slots not set!!");
        }

        int columns = (int) Math.ceil(Math.sqrt(slotCount));

        int slotHeight = parameter.getAsInteger("slotHeight", SLOT_DEFAULT_SIZE);
        int slotWidth = parameter.getAsInteger("slotWidth", SLOT_DEFAULT_SIZE);
        String slotBackground = "gui/containerslot.png";

        Element contentPanel = getContentPanel();
        if (contentPanel == null) {
            throw new IllegalStateException("Content panel is not set.");
        }

        SizeValue contentWidth = SizeValue.px((slotWidth + 2) * columns);

        PanelBuilder contentPanelBuilder = new PanelBuilder();
        contentPanelBuilder.childLayoutVertical();
        contentPanelBuilder.width(contentWidth);

        @Nullable PanelBuilder currentPanelBuilder = null;

        for (int i = 0; i < slotCount; i++) {
            if ((i % columns) == 0) {
                if (currentPanelBuilder != null) {
                    contentPanelBuilder.panel(currentPanelBuilder);
                }
                currentPanelBuilder = null;
            }
            if (currentPanelBuilder == null) {
                currentPanelBuilder = new PanelBuilder();
                currentPanelBuilder.childLayoutHorizontal();
                currentPanelBuilder.width(SizeValue.px((slotWidth + 2) * columns));
                currentPanelBuilder.height(SizeValue.px(slotHeight + 2));
            }

            currentPanelBuilder.control(buildSlot(contentPanel.getId(), i, slotHeight, slotWidth, slotBackground));
        }

        if (currentPanelBuilder != null) {
            contentPanelBuilder.panel(currentPanelBuilder);
        }
        contentPanelBuilder.build(nifty, screen, contentPanel);

        contentPanel.setConstraintWidth(contentWidth);
        element.setConstraintWidth(SizeValue.px(((slotWidth + 2) * columns) + 26 + 16));

        slots = new InventorySlot[slotCount];
        for (int i = 0; i < slotCount; i++) {
            slots[i] = contentPanel.findNiftyControl("#slot" + i, InventorySlot.class);
        }
        layoutWindow();
    }

    private void layoutWindow() {
        Element element = getElement();
        if (element != null) {
            element.getParent().layoutElements();
        }
    }

    /**
     * Build a inventory slot.
     *
     * @param prefix the string that is prepend to the ID of the slot
     * @param index the index of the slot
     * @param height the height of the slot
     * @param width the width of the slot
     * @param slotBackground the background image of the slot
     * @return the builder of the inventory slot
     */
    @Nonnull
    private static ControlBuilder buildSlot(
            String prefix, int index, int height, int width, @Nullable String slotBackground) {
        InventorySlotBuilder builder = new InventorySlotBuilder(prefix + "#slot" + index);
        builder.height(SizeValue.px(height));
        builder.width(SizeValue.px(width));
        builder.margin("1px");

        if (slotBackground != null) {
            builder.background(slotBackground);
        }
        return builder;
    }

    @Override
    public boolean inputEvent(@Nonnull NiftyInputEvent inputEvent) {
        return true;
    }

    @Override
    public int getSlotCount() {
        return slots.length;
    }

    @Nonnull
    @Override
    public InventorySlot getSlot(int index) {
        return slots[index];
    }

    @Override
    public void closeWindow() {
        super.closeWindow();
        String id = getId();
        if (id != null) {
            niftyInstance.publishEvent(id, new ItemContainerCloseEvent());
        }
    }

    @Nullable
    private Element getContentPanel() {
        Element content = getContent();
        if (content == null) {
            return null;
        }
        return content.findElementById("#contentPanel");
    }
}

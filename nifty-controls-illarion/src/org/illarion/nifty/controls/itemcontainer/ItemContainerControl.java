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
import de.lessvoid.nifty.controls.TabGroup;
import de.lessvoid.nifty.controls.TabSelectedEvent;
import de.lessvoid.nifty.controls.tabs.builder.TabBuilder;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;
import illarion.common.util.FastMath;
import org.bushe.swing.event.EventTopicSubscriber;
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
     * The amount of slots in one column.
     */
    private int rows;

    /**
     * This is the event topic subscriber that is used to monitor the changes of the tab selection.
     */
    private final EventTopicSubscriber<TabSelectedEvent> tabSelectionEventSubscriber;

    private Nifty niftyGui;
    private Screen parentScreen;

    /**
     * Default constructor.
     */
    public ItemContainerControl() {
        tabSelectionEventSubscriber = new EventTopicSubscriber<TabSelectedEvent>() {
            @Override
            public void onEvent(final String topic, final TabSelectedEvent data) {
                for (final InventorySlot slot : slots) {
                    slot.restoreVisibility();
                }
            }
        };
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

        niftyGui = nifty;
        parentScreen = screen;

        columns = controlDefinitionAttributes.getAsInteger("columns", 5);
        rows = controlDefinitionAttributes.getAsInteger("rows", 4);

        final int slotHeight = controlDefinitionAttributes.getAsInteger("slotHeight", SLOT_DEFAULT_SIZE);
        final int slotWidth = controlDefinitionAttributes.getAsInteger("slotWidth", SLOT_DEFAULT_SIZE);
        final String slotBackground = controlDefinitionAttributes.get("slotBackground");

        slots = new InventorySlot[slotCount];

        final TabGroup tabsControl = getTabsControl();

        int currentTabIndex = -1;
        TabBuilder currentTab = null;
        PanelBuilder currentRow = null;
        for (int i = 0; i < slotCount; i++) {
            if ((i % (columns * rows)) == 0) {
                if (currentTab != null) {
                    tabsControl.addTab(currentTab);
                }
                currentTabIndex++;
                currentTab = buildTab(Integer.toString(currentTabIndex + 1));
            }
            assert currentTab != null;
            if ((i % columns) == 0) {
                currentRow = buildRow(slotHeight);
                currentTab.panel(currentRow);
            }
            assert currentRow != null;
            currentRow.control(buildSlot(i, slotHeight, slotWidth, slotBackground));
        }

        if (currentTab != null) {
            tabsControl.addTab(currentTab);
        }

        for (int i = 0; i < slotCount; i++) {
            slots[i] = getElement().findNiftyControl("#slot" + i, InventorySlot.class);
        }

        tabsControl.setSelectedTabIndex(0);
    }

    private TabGroup getTabsControl() {
        return getElement().findNiftyControl("#mainTabs", TabGroup.class);
    }

    /**
     * Create a new tab with the specified title.
     *
     * @param title the title of the new tab
     * @return the builder that is going to construct the tab
     */
    private TabBuilder buildTab(final String title) {
        final String prefix = getElement().findElementByName("#mainTabs").getId();
        final TabBuilder builder = new TabBuilder(prefix + "#tab-" + title, "Tab #" + title);
        builder.childLayoutVertical();
        builder.width("100%");
        return builder;
    }

    /**
     * Construct the panel that holds a single row of inventory slots.
     *
     * @return the builder for the row
     */
    private static PanelBuilder buildRow(final int height) {
        final PanelBuilder builder = new PanelBuilder();
        builder.alignCenter();
        builder.valignTop();
        builder.childLayoutHorizontal();
        builder.width("100%");
        builder.height(builder.pixels(height));
        return builder;
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
    public void onStartScreen() {
        super.onStartScreen();

        niftyGui.subscribe(parentScreen, getTabsControl().getId(), TabSelectedEvent.class, tabSelectionEventSubscriber);
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
    public int getTabCount() {
        return FastMath.ceil(getSlotCount() / (float) (rows * columns));
    }

    @Override
    public void setTabName(final int index, final String text) {
        getTabsControl().setTabCaption(index, text);
    }

    @Override
    public String getTabName(final int index) {
        return getTabsControl().getSelectedTab().getCaption();
    }

    @Override
    public InventorySlot getSlot(final int index) {
        return slots[index];
    }
}

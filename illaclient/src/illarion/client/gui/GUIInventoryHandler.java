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
package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DraggableDragCanceledEvent;
import de.lessvoid.nifty.controls.DraggableDragStartedEvent;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.ElementShowEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import illarion.client.graphics.Item;
import illarion.client.input.InputReceiver;
import illarion.client.net.server.events.InventoryUpdateEvent;
import illarion.client.resources.ItemFactory;
import illarion.client.world.Inventory;
import illarion.client.world.World;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.InventorySlot;

import java.util.Arrays;

/**
 * This handler takes care for showing and hiding objects in the inventory. Also
 * it monitors all dropping operations on the slots of the inventory.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GUIInventoryHandler implements
    EventSubscriber<InventoryUpdateEvent>, EventTopicSubscriber<String> {

    /**
     * This class is used as drag end operation and used to move a object that
     * was dragged out of the inventory back in so the server can send the
     * commands to clean everything up.
     * 
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static class EndOfDragOperation implements Runnable {
        /**
         * The inventory slot that requires the reset.
         */
        private final InventorySlot invSlot;

        /**
         * Create a new instance of this class and set the effected elements.
         * 
         * @param slot the inventory slot to reset
         */
        public EndOfDragOperation(final InventorySlot slot) {
            invSlot = slot;
        }

        /**
         * Execute this operation.
         */
        @Override
        public void run() {
            invSlot.retrieveDraggable();
        }
    }

    /**
     * This class is used to handle the events that are triggered once the inventory window is set visible. Once this
     * happens its needed to restore the visibility values of the labels as the Nifty-GUI sets all child elements to
     * visible as well for some strange reason.
     *
     * This entire event subscriber can be erased in case the Nifty-GUI ever changes this behaviour.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private class GetVisibleEventSubscriber implements EventTopicSubscriber<ElementShowEvent> {
        /**
         * Handle the event.
         *
         * @param topic the topic of the event, that should equal the ID of the inventory window
         * @param data the actual event
         */
        @Override
        public void onEvent(final String topic, final ElementShowEvent data) {
            if (topic.equals(inventoryWindow.getId())) {
                restoreSlotLabelVisibility();
            }
        }
    }

    private final String[] slots;
    private final Element[] invSlots;
    private final boolean[] slotLabelVisibility;
    private Element inventoryWindow;
    private Nifty activeNifty;
    private Screen activeScreen;
    private GetVisibleEventSubscriber visibilityEventSubscriber;

    private static final String INVSLOT_HEAD = "invslot_";

    public GUIInventoryHandler() {
        slots = new String[Inventory.SLOT_COUNT];
        slots[0] = "invslot_bag";
        slots[1] = "invslot_head";
        slots[2] = "invslot_neck";
        slots[3] = "invslot_chest";
        slots[4] = "invslot_hands";
        slots[5] = "invslot_lhand";
        slots[6] = "invslot_rhand";
        slots[7] = "invslot_lfinger";
        slots[8] = "invslot_rfinger";
        slots[9] = "invslot_legs";
        slots[10] = "invslot_feet";
        slots[11] = "invslot_cloak";
        slots[12] = "invslot_belt1";
        slots[13] = "invslot_belt2";
        slots[14] = "invslot_belt3";
        slots[15] = "invslot_belt4";
        slots[16] = "invslot_belt5";
        slots[17] = "invslot_belt6";

        invSlots = new Element[Inventory.SLOT_COUNT];
        slotLabelVisibility = new boolean[Inventory.SLOT_COUNT];
        Arrays.fill(slotLabelVisibility, false);
        visibilityEventSubscriber = new GetVisibleEventSubscriber();
    }

    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;

        inventoryWindow = screen.findElementByName("inventory");

        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            invSlots[i] = inventoryWindow.findElementByName(slots[i]);
        }
    }

    public void onStartScreen() {
        activeNifty.subscribeAnnotations(this);

        EventBus.subscribe(InventoryUpdateEvent.class, this);
        EventBus.subscribe(InputReceiver.EB_TOPIC, this);
        activeNifty.subscribe(activeScreen, inventoryWindow.getId(), ElementShowEvent.class, visibilityEventSubscriber);
    }

    public void showInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.show();
        }
    }

    public void hideInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.hide();
        }
    }

    public void toggleInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.setVisible(!inventoryWindow.isVisible());
        }
    }

    @NiftyEventSubscriber(pattern = INVSLOT_HEAD + ".*")
    public void dropInInventory(final String topic,
        final DroppableDroppedEvent data) {
        final int slotId = getSlotNumber(topic);
        World.getInteractionManager().dropAtInventory(slotId);
    }

    @NiftyEventSubscriber(pattern = INVSLOT_HEAD + ".*")
    public void dragFromInventory(final String topic,
        final DraggableDragStartedEvent data) {
        final int slotId = getSlotNumber(topic);
        World.getInteractionManager().notifyDraggingInventory(
                slotId,
                new EndOfDragOperation(invSlots[slotId]
                        .getNiftyControl(InventorySlot.class)));
    }

    @NiftyEventSubscriber(pattern = INVSLOT_HEAD + ".*")
         public void cancelDragging(final String topic,
                                    final DraggableDragCanceledEvent data) {
        World.getInteractionManager().cancelDragging();
    }

    @NiftyEventSubscriber(pattern = INVSLOT_HEAD + ".*")
    public void clickInventory(final String topic,
                               final NiftyMousePrimaryClickedEvent data) {
        final int slotId = getSlotNumber(topic);
        World.getPlayer().getInventory().getItem(slotId).getInteractive().use();
    }

    /**
     * Get the number of a slot based on the name.
     *
     * @param name the name of the slot
     * @return the number of the slot fitting the name
     */
    private int getSlotNumber(final String name) {
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            if (name.startsWith(slots[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Set a new item to a slot.
     *
     * @param slotId the ID of the slot to change
     * @param itemId the ID of the item that shall be displayed in the slot             
     * @param count the amount of items displayed in this slot
     */
    public void setSlotItem(final int slotId, final int itemId, final int count) {
        if (slotId < 0 || slotId >= Inventory.SLOT_COUNT) {
            throw new IllegalArgumentException("Slot ID out of valid range.");
        }

        final InventorySlot invSlot =
            invSlots[slotId].getNiftyControl(InventorySlot.class);

        if (itemId > 0) {
            final Item displayedItem =
                ItemFactory.getInstance().getPrototype(itemId);
            final NiftyImage niftyImage =
                new NiftyImage(activeNifty.getRenderEngine(),
                    new EntitySlickRenderImage(displayedItem));

            invSlot.setImage(niftyImage);
            invSlot.setLabelText(Integer.toString(count));
            if (count > 1) {
                slotLabelVisibility[slotId] = true;
                invSlot.showLabel();
            } else {
                slotLabelVisibility[slotId] = false;
                invSlot.hideLabel();
            }
        } else {
            invSlot.setImage(null);
            invSlot.hideLabel();
        }
    }
    
    void restoreSlotLabelVisibility() {
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            final InventorySlot invSlot =
                    invSlots[i].getNiftyControl(InventorySlot.class);
            if (slotLabelVisibility[i]) {
                invSlot.showLabel();
            } else {
                invSlot.hideLabel();
            }
        }
    }

    /**
     * Fired upon the inventory publishing a update event.
     *
     * @param data the published data
     */
    @Override
    public void onEvent(final InventoryUpdateEvent data) {
        setSlotItem(data.getSlotId(), data.getItemId(), data.getCount());
    }

    /*
     * (non-Javadoc)
     * @see org.bushe.swing.event.EventTopicSubscriber#onEvent(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void onEvent(String topic, String data) {
        if (data.equals("ToggleInventory")) {
            toggleInventory();
        }
    }
}

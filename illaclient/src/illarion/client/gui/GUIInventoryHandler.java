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

import illarion.client.graphics.Item;
import illarion.client.input.InputReceiver;
import illarion.client.net.server.events.InventoryUpdateEvent;
import illarion.client.resources.ItemFactory;
import illarion.client.world.Inventory;
import illarion.client.world.World;
import illarion.common.graphics.FontLoader;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.InventorySlot;
import org.illarion.nifty.controls.inventoryslot.InventorySlotControl;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.controls.DraggableDragCanceledEvent;
import de.lessvoid.nifty.controls.DraggableDragStartedEvent;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.dragndrop.builder.DraggableBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;

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
         * @param invSlot the inventory slot to reset
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

    private final String[] slots;
    private final Element[] invSlots;
    private Element inventoryWindow;
    private Nifty activeNifty;
    private Screen activeScreen;

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

    }

    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;

        inventoryWindow = screen.findElementByName("inventory");

        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            invSlots[i] = inventoryWindow.findElementByName(slots[i]);
        }

        activeNifty.subscribeAnnotations(this);

        EventBus.subscribe(InventoryUpdateEvent.class, this);
        EventBus.subscribe(InputReceiver.EB_TOPIC, this);
    }

    public void showInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.setVisible(true);
        }
    }

    public void hideInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.setVisible(false);
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

    private int getSlotNumber(final String name) {
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            if (name.startsWith(slots[i])) {
                return i;
            }
        }
        return -1;
    }

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
            invSlot.setLabelText(Integer.toString(itemId));
            if (itemId > 1) {
                invSlot.showLabel();
            } else {
                invSlot.hideLabel();
            }
        } else {
            invSlot.setImage(null);
            invSlot.hideLabel();
        }
    }

    /**
     * Fired upon the inventory publishing a update event.
     * 
     * @param topic the topic of the publisher
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

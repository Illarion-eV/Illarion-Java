/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DraggableDragCanceledEvent;
import de.lessvoid.nifty.controls.DraggableDragStartedEvent;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.effects.Effect;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.effects.impl.Hint;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.ElementShowEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.graphics.Item;
import illarion.client.input.InputReceiver;
import illarion.client.net.server.events.InventoryUpdateEvent;
import illarion.client.net.server.events.LookAtInventoryEvent;
import illarion.client.resources.ItemFactory;
import illarion.client.world.World;
import illarion.client.world.items.Inventory;
import illarion.common.gui.AbstractMultiActionHelper;
import illarion.common.util.Timer;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.InventorySlot;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This handler takes care for showing and hiding objects in the inventory. Also it monitors all dropping operations on
 * the slots of the inventory.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GUIInventoryHandler implements EventSubscriber<InventoryUpdateEvent>,
        EventTopicSubscriber<String>, ScreenController, UpdatableHandler {

    /**
     * This class is used as drag end operation and used to move a object that was dragged out of the inventory back in
     * so the server can send the commands to clean everything up.
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
        EndOfDragOperation(final InventorySlot slot) {
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
     * <p/>
     * This entire event subscriber can be erased in case the Nifty-GUI ever changes this behaviour.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private class GetVisibleEventSubscriber implements EventTopicSubscriber<ElementShowEvent> {
        /**
         * Handle the event.
         *
         * @param topic the topic of the event, that should equal the ID of the inventory window
         * @param data  the actual event
         */
        @Override
        public void onEvent(final String topic, final ElementShowEvent data) {
            if (topic.equals(inventoryWindow.getId())) {
                restoreSlotLabelVisibility();
            }
        }
    }

    /**
     * This class is used to handle multiple clicks into the inventory. It records the clicks and reacts on them in
     * regard to the resulting amount of clicks.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class InventoryClickActionHelper extends AbstractMultiActionHelper {
        /**
         * The ID of the slot that was clicked at.
         */
        private int slotId;

        /**
         * The constructor for this class. The timeout time is set to the system default double click interval.
         */
        InventoryClickActionHelper() {
            super(IllaClient.getCfg().getInteger("doubleClickInterval"));
        }

        /**
         * Set the ID of the slot that was clicked at.
         *
         * @param id the ID of the slot
         */
        public void setSlotId(final int id) {
            slotId = id;
        }

        @Override
        public void executeAction(final int count) {
            final illarion.client.world.items.InventorySlot slot = World.getPlayer().getInventory().getItem(slotId);

            if (!slot.containsItem()) {
                return;
            }

            switch (count) {
                case 1:
                    slot.getInteractive().lookAt();
                    break;
                case 2:
                    if (slot.getItemPrototype().isContainer()) {
                        slot.getInteractive().openContainer();
                    } else {
                        slot.getInteractive().use();
                    }
                    break;
            }
        }
    }

    private static final String INVSLOT_HEAD = "invslot_";

    private final String[] slots;
    private final Element[] invSlots;
    private final boolean[] slotLabelVisibility;
    private Element inventoryWindow;
    private Nifty activeNifty;
    private Screen activeScreen;
    private GetVisibleEventSubscriber visibilityEventSubscriber;
    private EventSubscriber<LookAtInventoryEvent> lookAtInventoryEventEventSubscriber;

    private int clickCount = 0;
    private boolean wasDoubleClick = false;
    private Timer timer = null;

    /**
     * The instance of the inventory click helper that is used in this instance of the GUI inventory handler.
     */
    private final InventoryClickActionHelper inventoryClickActionHelper = new InventoryClickActionHelper();

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

        lookAtInventoryEventEventSubscriber = new EventSubscriber<LookAtInventoryEvent>() {
            @Override
            public void onEvent(final LookAtInventoryEvent event) {
                showHint(event.getSlot(), event.getText(), event.getValue());
            }
        };
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;

        inventoryWindow = screen.findElementByName("inventory");

        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            invSlots[i] = inventoryWindow.findElementByName(slots[i]);
        }
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void cancelDragging(final String topic, final DraggableDragCanceledEvent data) {
        World.getInteractionManager().cancelDragging();
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void clickInventory(final String topic, final NiftyMousePrimaryClickedEvent data) {
        final int slotId = getSlotNumber(topic);

        inventoryClickActionHelper.setSlotId(slotId);
        inventoryClickActionHelper.pulse();
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void dragFromInventory(final String topic, final DraggableDragStartedEvent data) {
        final int slotId = getSlotNumber(topic);
        World.getInteractionManager().notifyDraggingInventory(slotId,
                new EndOfDragOperation(invSlots[slotId].getNiftyControl(InventorySlot.class)));
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void dropInInventory(final String topic, final DroppableDroppedEvent data) {
        final int slotId = getSlotNumber(topic);
        World.getInteractionManager().dropAtInventory(slotId);
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

    public void hideInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.hide();
        }
    }

    @Override
    public void onEndScreen() {

    }

    /**
     * Fired upon the inventory publishing a update event.
     *
     * @param data the published data
     */
    @Override
    public void onEvent(final InventoryUpdateEvent data) {
        slotUpdateQueue.offer(data);
    }

    private final Queue<InventoryUpdateEvent> slotUpdateQueue = new ConcurrentLinkedQueue<InventoryUpdateEvent>();

    @Override
    public void update(final int delta) {
        while (true) {
            final InventoryUpdateEvent data = slotUpdateQueue.poll();
            if (data == null) {
                return;
            }

            setSlotItem(data.getSlotId(), data.getItemId(), data.getCount());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.bushe.swing.event.EventTopicSubscriber#onEvent(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void onEvent(final String topic, final String data) {
        if (data.equals("ToggleInventory")) {
            toggleInventory();
        }
    }

    public void showHint(final int slot, final String text, final long value) {
        final Element element = invSlots[slot];
        final List<Effect> hintEffects = element.getEffects(EffectEventId.onCustom, Hint.class);

        if (hintEffects.size() != 1) {
            throw new IllegalStateException("sanity check failed");
        }

        final Effect effect = hintEffects.get(0);

        effect.getParameters().setProperty("hintText", text);

        element.startEffectWithoutChildren(EffectEventId.onCustom);
    }

    public void toggleInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.setVisible(!inventoryWindow.isVisible());
        }
    }

    @Override
    public void onStartScreen() {
        activeNifty.subscribeAnnotations(this);

        EventBus.subscribe(InventoryUpdateEvent.class, this);
        EventBus.subscribe(LookAtInventoryEvent.class, lookAtInventoryEventEventSubscriber);
        EventBus.subscribe(InputReceiver.EB_TOPIC, this);
        activeNifty.subscribe(activeScreen, inventoryWindow.getId(), ElementShowEvent.class,
                visibilityEventSubscriber);

        final Inventory inventory = World.getPlayer().getInventory();
        illarion.client.world.items.InventorySlot invSlot;
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            invSlot = inventory.getItem(i);
            if (invSlot.getItemID() == 0) {
                continue;
            }
            setSlotItem(invSlot.getSlot(), invSlot.getItemID(), invSlot.getCount());
        }
    }

    /**
     * Set a new item to a slot.
     *
     * @param slotId the ID of the slot to change
     * @param itemId the ID of the item that shall be displayed in the slot
     * @param count  the amount of items displayed in this slot
     */
    public void setSlotItem(final int slotId, final int itemId, final int count) {
        if ((slotId < 0) || (slotId >= Inventory.SLOT_COUNT)) {
            throw new IllegalArgumentException("Slot ID out of valid range.");
        }

        final InventorySlot invSlot = invSlots[slotId].getNiftyControl(InventorySlot.class);

        if (itemId > 0) {
            final Item displayedItem = ItemFactory.getInstance().getPrototype(itemId);

            final NiftyImage niftyImage = new NiftyImage(activeNifty.getRenderEngine(),
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
            slotLabelVisibility[slotId] = false;
            invSlot.setImage(null);
            invSlot.hideLabel();
        }

        invSlots[slotId].getParent().layoutElements();
    }

    void restoreSlotLabelVisibility() {
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            final InventorySlot invSlot = invSlots[i].getNiftyControl(InventorySlot.class);
            if (slotLabelVisibility[i]) {
                invSlot.showLabel();
            } else {
                invSlot.hideLabel();
            }
        }
        inventoryWindow.layoutElements();
    }

    public void showInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.show();
        }
    }
}

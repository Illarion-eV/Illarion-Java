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
import de.lessvoid.nifty.elements.events.NiftyMouseMovedEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.graphics.Item;
import illarion.client.gui.events.TooltipsRemovedEvent;
import illarion.client.input.InputReceiver;
import illarion.client.net.server.events.DialogMerchantReceivedEvent;
import illarion.client.net.server.events.InventoryItemLookAtEvent;
import illarion.client.net.server.events.InventoryUpdateEvent;
import illarion.client.net.server.events.LookAtInventoryEvent;
import illarion.client.resources.ItemFactory;
import illarion.client.world.World;
import illarion.client.world.events.CloseDialogEvent;
import illarion.client.world.interactive.InteractionManager;
import illarion.client.world.items.Inventory;
import illarion.client.world.items.MerchantItem;
import illarion.client.world.items.MerchantList;
import illarion.common.gui.AbstractMultiActionHelper;
import illarion.common.util.Rectangle;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.nifty.controls.InventorySlot;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

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
public final class GUIInventoryHandler implements ScreenController, UpdatableHandler {
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

        @Override
        public void executeAction(final int count) {
            final illarion.client.world.items.InventorySlot slot = World.getPlayer().getInventory().getItem(slotId);

            if (!slot.containsItem()) {
                return;
            }

            switch (count) {
                case 1:
                    //slot.getInteractive().lookAt();
                    break;
                case 2:
                    if (World.getPlayer().hasMerchantList()) {
                        slot.getInteractive().sell();
                    } else if (slot.getItemPrototype().isContainer()) {
                        slot.getInteractive().openContainer();
                    } else {
                        slot.getInteractive().use();
                    }
                    break;
            }
        }

        /**
         * Set the ID of the slot that was clicked at.
         *
         * @param id the ID of the slot
         */
        public void setSlotId(final int id) {
            slotId = id;
        }
    }

    private final class InventorySlotUpdate implements Runnable {
        private final InventoryUpdateEvent event;

        InventorySlotUpdate(final InventoryUpdateEvent updateEvent) {
            event = updateEvent;
        }

        @Override
        public void run() {
            setSlotItem(event.getSlotId(), event.getItemId(), event.getCount());
        }
    }

    private final String[] slots;
    private final Element[] invSlots;
    private final boolean[] slotLabelVisibility;
    private Element inventoryWindow;
    private Nifty activeNifty;
    private Screen activeScreen;
    private final NumberSelectPopupHandler numberSelect;
    private final TooltipHandler tooltip;
    private Input input;

    /**
     * The instance of the inventory click helper that is used in this instance of the GUI inventory handler.
     */
    private final InventoryClickActionHelper inventoryClickActionHelper = new InventoryClickActionHelper();

    private final Queue<Runnable> updateQueue = new ConcurrentLinkedQueue<Runnable>();
    private final Runnable updateMerchantOverlays = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
                updateMerchantOverlay(i, World.getPlayer().getInventory().getItem(i).getItemID());
            }
        }
    };

    public GUIInventoryHandler(final NumberSelectPopupHandler numberSelectPopupHandler,
                               final TooltipHandler tooltipHandler) {
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

        numberSelect = numberSelectPopupHandler;
        tooltip = tooltipHandler;
    }

    @EventSubscriber
    public void onInventoryUpdateEvent(final InventoryUpdateEvent event) {
        updateQueue.offer(new GUIInventoryHandler.InventorySlotUpdate(event));
    }

    @EventSubscriber
    public void onLookAtInventoryEvent(final LookAtInventoryEvent event) {
        showHint(event.getSlot(), event.getText(), event.getValue());
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

    @EventSubscriber
    public void onDialogClosedEvent(final CloseDialogEvent event) {
        switch (event.getDialogType()) {
            case Any:
            case Merchant:
                updateQueue.offer(updateMerchantOverlays);

            case Message:
                break;
            case Input:
                break;
        }
    }

    @EventSubscriber
    public void onMerchantDialogReceivedHandler(final DialogMerchantReceivedEvent event) {
        updateQueue.offer(updateMerchantOverlays);
    }

    @EventSubscriber
    public void onInventoryItemLookAtHandler(final InventoryItemLookAtEvent event) {
        final Element slot = invSlots[event.getSlot()];
        final Rectangle rect = new Rectangle();
        rect.set(slot.getX(), slot.getY(), slot.getWidth(), slot.getHeight());

        tooltip.showToolTip(rect, event);
    }

    @EventTopicSubscriber(topic = InputReceiver.EB_TOPIC)
    public void onInputEvent(final String topic, final String data) {
        if (data.equals("ToggleInventory")) {
            toggleInventory();
        }
    }

    public void toggleInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.setVisible(!inventoryWindow.isVisible());
        }
    }

    @NiftyEventSubscriber(id = "inventory")
    public void onChangeWindowVisibility(final String topic, final ElementShowEvent event) {
        restoreSlotLabelVisibility();
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

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void cancelDragging(final String topic, final DraggableDragCanceledEvent data) {
        World.getInteractionManager().cancelDragging();
    }

    private int lastLookedAtSlot = -1;

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void onMouseMoveOverInventory(final String topic, final NiftyMouseMovedEvent event) {
        final int slotId = getSlotNumber(topic);

        if (lastLookedAtSlot == slotId) {
            return;
        }

        lastLookedAtSlot = slotId;

        World.getPlayer().getInventory().getItem(slotId).getInteractive().lookAt();
    }

    @EventSubscriber
    public void onTooltipsRemovedEventsReceived(final TooltipsRemovedEvent event) {
        lastLookedAtSlot = -1;
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void clickInventory(final String topic, final NiftyMousePrimaryClickedEvent data) {
        final int slotId = getSlotNumber(topic);

        inventoryClickActionHelper.setSlotId(slotId);
        inventoryClickActionHelper.pulse();
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

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void dragFromInventory(final String topic, final DraggableDragStartedEvent data) {
        final int slotId = getSlotNumber(topic);
        World.getInteractionManager().notifyDraggingInventory(slotId,
                new GUIInventoryHandler.EndOfDragOperation(invSlots[slotId].getNiftyControl(InventorySlot.class)));
    }

    private boolean isShiftPressed() {
        return (input != null) && (input.isKeyDown(Input.KEY_LSHIFT) || input.isKeyDown(Input.KEY_RSHIFT));
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void dropInInventory(final String topic, final DroppableDroppedEvent data) {
        final int slotId = getSlotNumber(topic);

        final int amount = World.getInteractionManager().getMovedAmount();
        final InteractionManager iManager = World.getInteractionManager();
        if ((amount > 1) && isShiftPressed()) {
            numberSelect.requestNewPopup(1, amount, new NumberSelectPopupHandler.Callback() {
                @Override
                public void popupCanceled() {
                    // nothing
                }

                @Override
                public void popupConfirmed(final int value) {
                    iManager.dropAtInventory(slotId, value);
                }
            });
        } else {
            iManager.dropAtInventory(slotId, World.getInteractionManager().getMovedAmount());
        }
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

    @Override
    public void onEndScreen() {

    }

    @Override
    public void onStartScreen() {
        activeNifty.subscribeAnnotations(this);
        AnnotationProcessor.process(this);

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
            updateMerchantOverlay(slotId, itemId);
        } else {
            slotLabelVisibility[slotId] = false;
            invSlot.setImage(null);
            invSlot.hideLabel();
            updateMerchantOverlay(slotId, itemId);
        }

        invSlots[slotId].getParent().layoutElements();
    }

    private void updateMerchantOverlay(final int slot, final int itemId) {
        final InventorySlot control = invSlots[slot].getNiftyControl(InventorySlot.class);

        if (itemId == 0) {
            control.hideMerchantOverlay();
            return;
        }

        final MerchantList merchantList = World.getPlayer().getMerchantList();
        if (merchantList != null) {
            for (int i = 0; i < merchantList.getItemCount(); i++) {
                final MerchantItem item = merchantList.getItem(i);
                if (item.getItemId() == itemId) {
                    switch (item.getType()) {
                        case BuyingPrimaryItem:
                            control.showMerchantOverlay(InventorySlot.MerchantBuyLevel.Gold);
                            return;
                        case BuyingSecondaryItem:
                            control.showMerchantOverlay(InventorySlot.MerchantBuyLevel.Silver);
                            return;
                        case SellingItem:
                            break;
                    }
                }
            }
        }
        control.hideMerchantOverlay();
    }

    @Override
    public void update(final GameContainer container, final int delta) {
        input = container.getInput();
        while (true) {
            final Runnable task = updateQueue.poll();
            if (task == null) {
                return;
            }

            task.run();
        }
    }

    public void hideInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.hide();
        }
    }

    public void showInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.show();
        }
    }
}

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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.ElementShowEvent;
import de.lessvoid.nifty.elements.events.NiftyMouseMovedEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.IllaClient;
import illarion.client.gui.EntitySlickRenderImage;
import illarion.client.gui.InventoryGui;
import illarion.client.gui.Tooltip;
import illarion.client.input.InputReceiver;
import illarion.client.net.client.PickUpAllItemsCmd;
import illarion.client.net.server.events.DialogMerchantReceivedEvent;
import illarion.client.resources.ItemFactory;
import illarion.client.resources.data.ItemTemplate;
import illarion.client.util.Lang;
import illarion.client.util.LookAtTracker;
import illarion.client.util.UpdateTask;
import illarion.client.world.World;
import illarion.client.world.events.CloseDialogEvent;
import illarion.client.world.interactive.InteractionManager;
import illarion.client.world.items.Inventory;
import illarion.client.world.items.MerchantItem;
import illarion.client.world.items.MerchantList;
import illarion.common.gui.AbstractMultiActionHelper;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Rectangle;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.GameContainer;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.illarion.nifty.controls.InventorySlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * This handler takes care for showing and hiding objects in the inventory. Also it monitors all dropping operations on
 * the slots of the inventory.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GUIInventoryHandler implements InventoryGui, ScreenController {
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
            super(IllaClient.getCfg().getInteger("doubleClickInterval"), 2);
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
                    } else //noinspection ConstantConditions
                        if (slot.getItemTemplate().getItemInfo().isContainer()) {
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

    private final class InventorySlotUpdate implements UpdateTask {
        private final int slotId;
        @Nullable
        private final ItemId itemId;
        @Nullable
        private final ItemCount itemCount;

        InventorySlotUpdate(final int slotId, @Nullable final ItemId itemId, @Nullable final ItemCount itemCount) {
            this.slotId = slotId;
            this.itemId = itemId;
            this.itemCount = itemCount;
        }

        @Override
        public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
            setSlotItem(slotId, itemId, itemCount);
        }
    }

    /**
     * The logger that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(GUIInventoryHandler.class);

    @Nonnull
    private final String[] slots;
    @Nonnull
    private final Element[] invSlots;
    @Nonnull
    private final boolean[] slotLabelVisibility;
    private Element inventoryWindow;
    private Nifty activeNifty;
    private Screen activeScreen;
    private final NumberSelectPopupHandler numberSelect;
    private final TooltipHandler tooltipHandler;
    @Nonnull
    private final Input input;

    /**
     * The instance of the inventory click helper that is used in this instance of the GUI inventory handler.
     */
    private final InventoryClickActionHelper inventoryClickActionHelper = new InventoryClickActionHelper();

    private final UpdateTask updateMerchantOverlays = new UpdateTask() {
        @Override
        public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
            final Inventory inventory = World.getPlayer().getInventory();
            for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
                updateMerchantOverlay(i, inventory.getItem(i).getItemID());
            }
        }
    };

    public GUIInventoryHandler(@Nonnull final Input input, final NumberSelectPopupHandler numberSelectPopupHandler,
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
        this.tooltipHandler = tooltipHandler;
        this.input = input;
    }

    @EventSubscriber
    public void onDialogClosedEvent(@Nonnull final CloseDialogEvent event) {
        switch (event.getDialogType()) {
            case Any:
            case Merchant:
                World.getUpdateTaskManager().addTask(updateMerchantOverlays);

            case Message:
                break;
            case Input:
                break;
        }
    }

    @EventSubscriber
    public void onMerchantDialogReceivedHandler(final DialogMerchantReceivedEvent event) {
        World.getUpdateTaskManager().addTask(updateMerchantOverlays);
    }

    @EventTopicSubscriber(topic = InputReceiver.EB_TOPIC)
    public void onInputEvent(final String topic, @Nonnull final String data) {
        if (data.equals("ToggleInventory")) {
            toggleInventory();
        }
    }

    @NiftyEventSubscriber(id = "pickUpItemsBtn")
    public void onPickUpItemsBtnClick(final String topic, @Nonnull final ButtonClickedEvent event) {
        World.getNet().sendCommand(new PickUpAllItemsCmd());
    }

    public void toggleInventory() {
        if (inventoryWindow != null) {
            if (inventoryWindow.isVisible()) {
                hideInventory();
            } else {
                showInventory();
            }
        }
    }

    @Override
    public void hideInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.hide();
        }
    }

    @Override
    public void showInventory() {
        if (inventoryWindow != null) {
            inventoryWindow.show(new EndNotify() {
                @Override
                public void perform() {
                    inventoryWindow.getNiftyControl(Window.class).moveToFront();
                }
            });
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

    @NiftyEventSubscriber(id = "openInventoryBtn")
    public void onInventoryButtonClicked(final String topic, final ButtonClickedEvent data) {
        toggleInventory();
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void cancelDragging(final String topic, final DraggableDragCanceledEvent data) {
        World.getInteractionManager().cancelDragging();
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void clickInventory(@Nonnull final String topic, final NiftyMousePrimaryClickedEvent data) {
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
    private int getSlotNumber(@Nonnull final String name) {
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            if (name.startsWith(slots[i])) {
                return i;
            }
        }
        return -1;
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void dragFromInventory(@Nonnull final String topic, final DraggableDragStartedEvent data) {
        final int slotId = getSlotNumber(topic);
        World.getInteractionManager().notifyDraggingInventory(slotId,
                new GUIInventoryHandler.EndOfDragOperation(invSlots[slotId].getNiftyControl(InventorySlot.class)));
        tooltipHandler.hideToolTip();
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void dropInInventory(@Nonnull final String topic, final DroppableDroppedEvent data) {
        final int slotId = getSlotNumber(topic);

        final InteractionManager iManager = World.getInteractionManager();
        final ItemCount amount = iManager.getMovedAmount();
        if (amount == null) {
            LOGGER.error("Corrupted dragging detected.");
            iManager.cancelDragging();
            return;
        }
        if (ItemCount.isGreaterOne(amount) && isShiftPressed()) {
            numberSelect.requestNewPopup(1, amount.getValue(), new NumberSelectPopupHandler.Callback() {
                @Override
                public void popupCanceled() {
                    // nothing
                }

                @Override
                public void popupConfirmed(final int value) {
                    iManager.dropAtInventory(slotId, ItemCount.getInstance(value));
                }
            });
        } else {
            iManager.dropAtInventory(slotId, amount);
        }

        inventoryWindow.setFocus();
    }

    private boolean isShiftPressed() {
        return input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void onMouseMoveOverInventory(@Nonnull final String topic, final NiftyMouseMovedEvent event) {
        final int slotId = getSlotNumber(topic);

        if (input.isAnyButtonDown(Button.Left, Button.Right)) {
            return;
        }

        final illarion.client.world.items.InventorySlot slot = World.getPlayer().getInventory().getItem(slotId);

        if (!LookAtTracker.isLookAtObject(slot)) {
            LookAtTracker.setLookAtObject(slot);
            fetchLookAt(slot);
        }
    }

    /**
     * Fetch a look at for a slot. This function does not perform any checks or something. It just requests the look
     * at. Use with care.
     *
     * @param slot the slot to fetch
     */
    private static void fetchLookAt(@Nonnull final illarion.client.world.items.InventorySlot slot) {
        slot.getInteractive().lookAt();
    }

    @Override
    public void bind(final Nifty nifty, @Nonnull final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;

        inventoryWindow = screen.findElementByName("inventory");

        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            invSlots[i] = inventoryWindow.findElementByName(slots[i]);
        }

        inventoryWindow.setConstraintX(new SizeValue(IllaClient.getCfg().getString("inventoryPosX")));
        inventoryWindow.setConstraintY(new SizeValue(IllaClient.getCfg().getString("inventoryPosY")));
        inventoryWindow.getParent().layoutElements();
    }

    @Override
    public boolean isVisible() {
        return inventoryWindow.isVisible();
    }

    @Override
    public void onEndScreen() {
        activeNifty.unsubscribeAnnotations(this);
        AnnotationProcessor.unprocess(this);
        IllaClient.getCfg().set("inventoryPosX", Integer.toString(inventoryWindow.getX()) + "px");
        IllaClient.getCfg().set("inventoryPosY", Integer.toString(inventoryWindow.getY()) + "px");
    }

    @Override
    public void onStartScreen() {
        activeNifty.subscribeAnnotations(this);
        AnnotationProcessor.process(this);

        final Inventory inventory = World.getPlayer().getInventory();
        illarion.client.world.items.InventorySlot invSlot;
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            invSlot = inventory.getItem(i);
            if (ItemId.isValidItem(invSlot.getItemID())) {
                setSlotItem(invSlot.getSlot(), invSlot.getItemID(), invSlot.getCount());
            }
        }
    }

    /**
     * Set a new item to a slot.
     *
     * @param slotId the ID of the slot to change
     * @param itemId the ID of the item that shall be displayed in the slot
     * @param count  the amount of items displayed in this slot
     */
    private void setSlotItem(final int slotId, @Nullable final ItemId itemId, @Nullable final ItemCount count) {
        if ((slotId < 0) || (slotId >= Inventory.SLOT_COUNT)) {
            throw new IllegalArgumentException("Slot ID out of valid range.");
        }

        final InventorySlot invSlot = invSlots[slotId].getNiftyControl(InventorySlot.class);

        if (ItemId.isValidItem(itemId)) {
            assert itemId != null;
            final ItemTemplate displayedItem = ItemFactory.getInstance().getTemplate(itemId.getValue());

            final NiftyImage niftyImage = new NiftyImage(activeNifty.getRenderEngine(),
                    new EntitySlickRenderImage(displayedItem));

            invSlot.setImage(niftyImage);
            if (ItemCount.isGreaterOne(count)) {
                assert count != null;
                invSlot.setLabelText(count.getShortText(Lang.getInstance().getLocale()));
                slotLabelVisibility[slotId] = true;
                invSlot.showLabel();
            } else {
                slotLabelVisibility[slotId] = false;
                invSlot.hideLabel();
            }
            updateMerchantOverlay(slotId, itemId);

            final Element slot = invSlot.getElement();
            final Rectangle rect = new Rectangle();
            rect.set(slot.getX(), slot.getY(), slot.getWidth(), slot.getHeight());

            if (rect.isInside(input.getMouseX(), input.getMouseY())) {
                fetchLookAt(World.getPlayer().getInventory().getItem(slotId));
            }
        } else {
            slotLabelVisibility[slotId] = false;
            invSlot.setImage(null);
            invSlot.hideLabel();
            updateMerchantOverlay(slotId, itemId);
        }

        invSlots[slotId].getParent().layoutElements();
    }

    private void updateMerchantOverlay(final int slot, @Nullable final ItemId itemId) {
        final InventorySlot control = invSlots[slot].getNiftyControl(InventorySlot.class);

        if (!ItemId.isValidItem(itemId)) {
            control.hideMerchantOverlay();
            return;
        }

        final MerchantList merchantList = World.getPlayer().getMerchantList();
        if (merchantList != null) {
            for (int i = 0; i < merchantList.getItemCount(); i++) {
                final MerchantItem item = merchantList.getItem(i);
                if (item.getItemId().equals(itemId)) {
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
    public void setItemSlot(final int slotId, @Nullable final ItemId itemId, @Nullable final ItemCount count) {
        if ((slotId < 0) || (slotId >= Inventory.SLOT_COUNT)) {
            throw new IllegalArgumentException("Slot ID out of valid range.");
        }
        World.getUpdateTaskManager().addTask(new InventorySlotUpdate(slotId, itemId, count));
    }

    @Override
    public void showTooltip(final int slotId, @Nonnull final Tooltip tooltip) {
        final Element slot = invSlots[slotId];
        final Rectangle rect = new Rectangle();
        rect.set(slot.getX(), slot.getY(), slot.getWidth(), slot.getHeight());

        tooltipHandler.showToolTip(rect, tooltip);
    }
}

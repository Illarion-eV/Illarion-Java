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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.ElementShowEvent;
import de.lessvoid.nifty.elements.events.NiftyMouseMovedEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryMultiClickedEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.IllaClient;
import illarion.client.gui.EntitySlickRenderImage;
import illarion.client.gui.InventoryGui;
import illarion.client.gui.Tooltip;
import illarion.client.net.client.PickUpAllItemsCmd;
import illarion.client.resources.ItemFactory;
import illarion.client.resources.data.ItemTemplate;
import illarion.client.util.Lang;
import illarion.client.util.LookAtTracker;
import illarion.client.util.UpdateTask;
import illarion.client.world.World;
import illarion.client.world.interactive.InteractionManager;
import illarion.client.world.items.CarryLoad;
import illarion.client.world.items.Inventory;
import illarion.client.world.items.MerchantItem;
import illarion.client.world.items.MerchantList;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Rectangle;
import org.illarion.engine.GameContainer;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.illarion.nifty.controls.InventorySlot;
import org.illarion.nifty.controls.Progress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        EndOfDragOperation(InventorySlot slot) {
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

    private final class InventorySlotUpdate implements UpdateTask {
        private final int slotId;
        @Nullable
        private final ItemId itemId;
        @Nullable
        private final ItemCount itemCount;

        InventorySlotUpdate(int slotId, @Nullable ItemId itemId, @Nullable ItemCount itemCount) {
            this.slotId = slotId;
            this.itemId = itemId;
            this.itemCount = itemCount;
        }

        @Override
        public void onUpdateGame(@Nonnull GameContainer container, int delta) {
            setSlotItem(slotId, itemId, itemCount);
        }
    }

    /**
     * The logger that takes care for the logging output of this class.
     */
    private static final Logger log = LoggerFactory.getLogger(GUIInventoryHandler.class);

    @Nonnull
    private final String[] slots;
    @Nonnull
    private final Element[] invSlots;
    @Nonnull
    private final boolean[] slotLabelVisibility;
    @Nullable
    private Element inventoryWindow;
    private Nifty activeNifty;
    private Screen activeScreen;
    private final NumberSelectPopupHandler numberSelect;
    private final TooltipHandler tooltipHandler;
    @Nonnull
    private final Input input;

    @Nonnull
    private final UpdateTask updateMerchantOverlays = new UpdateTask() {
        @Override
        public void onUpdateGame(@Nonnull GameContainer container, int delta) {
            Inventory inventory = World.getPlayer().getInventory();
            for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
                updateMerchantOverlay(i, inventory.getItem(i).getItemID());
            }
        }
    };

    public GUIInventoryHandler(
            @Nonnull Input input, NumberSelectPopupHandler numberSelectPopupHandler, TooltipHandler tooltipHandler) {
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

    @NiftyEventSubscriber(id = "pickUpItemsBtn")
    public void onPickUpItemsBtnClick(String topic, @Nonnull ButtonClickedEvent event) {
        World.getNet().sendCommand(new PickUpAllItemsCmd());
    }

    public void updateCarryLoad() {
        if ((inventoryWindow != null) && inventoryWindow.isVisible()) {
            CarryLoad load = World.getPlayer().getCarryLoad();
            Element carryLoadDisplay = inventoryWindow.findElementById("carryLoad");
            if (carryLoadDisplay != null) {
                Element fillElement = carryLoadDisplay.findElementById("#fill");
                if (fillElement != null) {
                    if (load.isRunningPossible()) {
                        carryLoadDisplay.setStyle("illarion-progress");
                        fillElement.setStyle("illarion-progress#fill");
                    } else if (load.isWalkingPossible()) {
                        carryLoadDisplay.setStyle("illarion-progress-yellow");
                        fillElement.setStyle("illarion-progress-yellow#fill");
                    } else {
                        carryLoadDisplay.setStyle("illarion-progress-red");
                        fillElement.setStyle("illarion-progress-red#fill");
                    }
                }
                Progress carryLoadControl = carryLoadDisplay.getNiftyControl(Progress.class);
                if (carryLoadControl != null) {
                    carryLoadControl.setProgress(0.0);
                    carryLoadControl.setProgress(load.getLoadFactor());
                }
                carryLoadDisplay.layoutElements();
            }
        }
    }

    @Override
    public void updateMerchantOverlay() {
        World.getUpdateTaskManager().addTask(updateMerchantOverlays);
    }

    @Override
    public void toggleInventory() {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                if (inventoryWindow != null) {
                    if (inventoryWindow.isVisible()) {
                        hideInventory();
                    } else {
                        showInventory();
                    }
                }
            }
        });
    }

    @Override
    public void hideInventory() {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                if (inventoryWindow != null) {
                    inventoryWindow.hide();
                }
            }
        });
    }

    @Override
    public void showInventory() {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                if (inventoryWindow != null) {
                    inventoryWindow.show(new EndNotify() {
                        @Override
                        public void perform() {
                            World.getUpdateTaskManager().addTaskForLater(new UpdateTask() {
                                @Override
                                public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                                    updateCarryLoad();
                                }
                            });
                            inventoryWindow.getNiftyControl(Window.class).moveToFront();
                        }
                    });
                }
            }
        });
    }

    @NiftyEventSubscriber(id = "inventory")
    public void onChangeWindowVisibility(String topic, ElementShowEvent event) {
        restoreSlotLabelVisibility();
    }

    void restoreSlotLabelVisibility() {
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            InventorySlot invSlot = invSlots[i].getNiftyControl(InventorySlot.class);
            if (slotLabelVisibility[i]) {
                invSlot.showLabel();
            } else {
                invSlot.hideLabel();
            }
        }
        inventoryWindow.layoutElements();
    }

    @NiftyEventSubscriber(id = "openInventoryBtn")
    public void onInventoryButtonClicked(String topic, ButtonClickedEvent data) {
        toggleInventory();
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void cancelDragging(String topic, DraggableDragCanceledEvent data) {
        World.getInteractionManager().cancelDragging();
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void onDoubleClickInventory(@Nonnull String topic, @Nonnull NiftyMousePrimaryMultiClickedEvent data) {
        int slotId = getSlotNumber(topic);
        log.debug("Clicking {} times in inventory slot {}", data.getClickCount(), slotId);

        if (data.getClickCount() == 2) {
            illarion.client.world.items.InventorySlot slot = World.getPlayer().getInventory().getItem(slotId);

            if (!slot.containsItem()) {
                return;
            }

            if (World.getPlayer().hasMerchantList()) {
                slot.getInteractive().sell();
            } else {
                if (slot.getItemTemplate().getItemInfo().isContainer()) {
                    slot.getInteractive().openContainer();
                } else {
                    slot.getInteractive().use();
                }
            }
        }
    }

    /**
     * Get the number of a slot based on the name.
     *
     * @param name the name of the slot
     * @return the number of the slot fitting the name
     */
    private int getSlotNumber(@Nonnull String name) {
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            if (name.startsWith(slots[i])) {
                return i;
            }
        }
        return -1;
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void dragFromInventory(@Nonnull String topic, DraggableDragStartedEvent data) {
        int slotId = getSlotNumber(topic);
        World.getInteractionManager().notifyDraggingInventory(slotId, new GUIInventoryHandler.EndOfDragOperation(
                invSlots[slotId].getNiftyControl(InventorySlot.class)));
        tooltipHandler.hideToolTip();
    }

    @NiftyEventSubscriber(pattern = "invslot_.*")
    public void dropInInventory(@Nonnull String topic, DroppableDroppedEvent data) {
        final int slotId = getSlotNumber(topic);

        final InteractionManager iManager = World.getInteractionManager();
        ItemCount amount = iManager.getMovedAmount();
        if (amount == null) {
            log.error("Corrupted dragging detected.");
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
                public void popupConfirmed(int value) {
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
    public void onMouseMoveOverInventory(@Nonnull String topic, NiftyMouseMovedEvent event) {
        int slotId = getSlotNumber(topic);

        if (input.isAnyButtonDown(Button.Left, Button.Right)) {
            return;
        }

        illarion.client.world.items.InventorySlot slot = World.getPlayer().getInventory().getItem(slotId);

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
    private static void fetchLookAt(@Nonnull illarion.client.world.items.InventorySlot slot) {
        slot.getInteractive().lookAt();
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;

        inventoryWindow = screen.findElementById("inventory");

        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            invSlots[i] = inventoryWindow.findElementById(slots[i]);
        }

        inventoryWindow.setConstraintX(new SizeValue(IllaClient.getCfg().getString("inventoryPosX")));
        inventoryWindow.setConstraintY(new SizeValue(IllaClient.getCfg().getString("inventoryPosY")));
        //inventoryWindow.getParent().layoutElements();

        /* Workaround to fix a internal Nifty-GUI problem with changing the styles. */
        if (inventoryWindow != null) {
            Element carryLoadDisplay = inventoryWindow.findElementById("carryLoad");
            if (carryLoadDisplay != null) {
                Element fillElement = carryLoadDisplay.findElementById("#fill");
                if (fillElement != null) {
                    fillElement.setStyle("illarion-progress#fill");
                }
            }
        }
    }

    @Override
    public boolean isVisible() {
        return inventoryWindow.isVisible();
    }

    @Override
    public void onEndScreen() {
        activeNifty.unsubscribeAnnotations(this);
        IllaClient.getCfg().set("inventoryPosX", Integer.toString(inventoryWindow.getX()) + "px");
        IllaClient.getCfg().set("inventoryPosY", Integer.toString(inventoryWindow.getY()) + "px");
    }

    @Override
    public void onStartScreen() {
        activeNifty.subscribeAnnotations(this);

        Inventory inventory = World.getPlayer().getInventory();
        illarion.client.world.items.InventorySlot invSlot;
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            invSlot = inventory.getItem(i);
            setSlotItem(invSlot.getSlot(), invSlot.getItemID(), invSlot.getCount());
        }
    }

    /**
     * Set a new item to a slot.
     *
     * @param slotId the ID of the slot to change
     * @param itemId the ID of the item that shall be displayed in the slot
     * @param count the amount of items displayed in this slot
     */
    private void setSlotItem(int slotId, @Nullable ItemId itemId, @Nullable ItemCount count) {
        if ((slotId < 0) || (slotId >= Inventory.SLOT_COUNT)) {
            throw new IllegalArgumentException("Slot ID out of valid range.");
        }

        InventorySlot invSlot = invSlots[slotId].getNiftyControl(InventorySlot.class);

        if (ItemId.isValidItem(itemId)) {
            assert itemId != null;
            ItemTemplate displayedItem = ItemFactory.getInstance().getTemplate(itemId.getValue());

            NiftyImage niftyImage = new NiftyImage(activeNifty.getRenderEngine(),
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

            Element slot = invSlot.getElement();
            Rectangle rect = new Rectangle();
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

    private void updateMerchantOverlay(int slot, @Nullable ItemId itemId) {
        InventorySlot control = invSlots[slot].getNiftyControl(InventorySlot.class);

        if (!ItemId.isValidItem(itemId)) {
            control.hideMerchantOverlay();
            return;
        }

        MerchantList merchantList = World.getPlayer().getMerchantList();
        if (merchantList != null) {
            for (int i = 0; i < merchantList.getItemCount(); i++) {
                MerchantItem item = merchantList.getItem(i);
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
    public void setItemSlot(int slotId, @Nullable ItemId itemId, @Nullable ItemCount count) {
        if ((slotId < 0) || (slotId >= Inventory.SLOT_COUNT)) {
            throw new IllegalArgumentException("Slot ID out of valid range.");
        }
        World.getUpdateTaskManager().addTask(new InventorySlotUpdate(slotId, itemId, count));
    }

    @Override
    public void showTooltip(int slotId, @Nonnull Tooltip tooltip) {
        Element slot = invSlots[slotId];
        Rectangle rect = new Rectangle();
        rect.set(slot.getX(), slot.getY(), slot.getWidth(), slot.getHeight());

        tooltipHandler.showToolTip(rect, tooltip);
    }
}

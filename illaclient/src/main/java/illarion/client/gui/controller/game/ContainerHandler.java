/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DraggableDragCanceledEvent;
import de.lessvoid.nifty.controls.DraggableDragStartedEvent;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMouseMovedEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryMultiClickedEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import illarion.client.IllaClient;
import illarion.client.graphics.FontLoader;
import illarion.client.gui.ContainerGui;
import illarion.client.gui.DialogType;
import illarion.client.gui.EntitySlickRenderImage;
import illarion.client.gui.Tooltip;
import illarion.client.net.client.CloseShowcaseCmd;
import illarion.client.net.server.events.DialogMerchantReceivedEvent;
import illarion.client.resources.ItemFactory;
import illarion.client.resources.data.ItemTemplate;
import illarion.client.util.Lang;
import illarion.client.util.LookAtTracker;
import illarion.client.util.UpdateTask;
import illarion.client.world.World;
import illarion.client.world.events.CloseDialogEvent;
import illarion.client.world.interactive.InteractionManager;
import illarion.client.world.items.ContainerSlot;
import illarion.client.world.items.ItemContainer;
import illarion.client.world.items.MerchantItem;
import illarion.client.world.items.MerchantList;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Rectangle;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Font;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.illarion.nifty.controls.InventorySlot;
import org.illarion.nifty.controls.ItemContainerCloseEvent;
import org.illarion.nifty.controls.itemcontainer.builder.ItemContainerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This handler that care for properly managing the displaying of containers on the game screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ContainerHandler implements ContainerGui, ScreenController {
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
        @Nonnull
        private final InventorySlot invSlot;

        /**
         * Create a new instance of this class and set the effected elements.
         *
         * @param slot the inventory slot to reset
         */
        EndOfDragOperation(@Nonnull InventorySlot slot) {
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

    private final class UpdateContainerTask implements UpdateTask {
        private final ItemContainer itemContainer;

        UpdateContainerTask(ItemContainer container) {
            itemContainer = container;
        }

        @Override
        public void onUpdateGame(@Nonnull GameContainer container, int delta) {
            if (!isContainerCreated(itemContainer.getContainerId())) {
                createNewContainer(itemContainer);
            }
            updateContainer(itemContainer);
        }
    }

    /**
     * The pattern to fetch the ID of a slot name.
     */
    private static final Pattern slotPattern = Pattern.compile("slot([0-9]+)");

    /**
     * The pattern to fetch the ID of a container name.
     */
    private static final Pattern containerPattern = Pattern.compile("container([0-9]+)");

    private static final Logger log = LoggerFactory.getLogger(ContainerHandler.class);

    /**
     * The Nifty-GUI instance that is handling the GUI display currently.
     */
    private Nifty activeNifty;

    /**
     * The screen that takes care for the display currently.
     */
    private Screen activeScreen;

    /**
     * The select popup handler that is used to receive money input from the user.
     */
    private final NumberSelectPopupHandler numberSelect;

    /**
     * The tooltip handler that is used to show the tooltips of this container.
     */
    private final TooltipHandler tooltipHandler;

    /**
     * The list of item containers that are currently displayed.
     */
    @Nonnull
    private final TIntObjectHashMap<org.illarion.nifty.controls.ItemContainer> itemContainerMap;

    /**
     * The task that is executed to update the merchant overlays.
     */
    private final UpdateTask updateMerchantOverlays = new UpdateTask() {
        @Override
        public void onUpdateGame(@Nonnull GameContainer container, int delta) {
            updateAllMerchantOverlays();
        }
    };

    /**
     * The input system that is used to query the state of the keyboard.
     */
    private final Input input;

    /**
     * Constructor of this handler.
     *
     * @param numberSelectPopupHandler the number select handler
     * @param tooltip the tooltip handler
     */
    public ContainerHandler(
            Input input, NumberSelectPopupHandler numberSelectPopupHandler, TooltipHandler tooltip) {
        itemContainerMap = new TIntObjectHashMap<>();
        numberSelect = numberSelectPopupHandler;
        tooltipHandler = tooltip;
        this.input = input;
    }

    /**
     * Close the container as needed.
     *
     * @param event the close event that contains the information what dialog is supposed to be closed
     */
    @EventSubscriber
    public void onDialogClosedEvent(@Nonnull CloseDialogEvent event) {
        if (event.isClosingDialogType(DialogType.Merchant)) {
            World.getUpdateTaskManager().addTask(updateMerchantOverlays);
        }
    }

    /**
     * This event is receives in case the client receives a merchant dialog. This is needed to show the overlay on
     * the items.
     *
     * @param event the merchant dialog event
     */
    @EventSubscriber
    public void onMerchantDialogReceivedHandler(DialogMerchantReceivedEvent event) {
        World.getUpdateTaskManager().addTask(updateMerchantOverlays);
    }

    /**
     * This event is received in case a container is closed.
     *
     * @param topic the topic of the event
     * @param data the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*")
    public void onItemContainerClose(String topic, @Nonnull ItemContainerCloseEvent data) {
        World.getPlayer().removeContainer(data.getContainerId());
        if (isContainerCreated(data.getContainerId())) {
            removeItemContainer(data.getContainerId());
            World.getNet().sendCommand(new CloseShowcaseCmd(data.getContainerId()));
        }
    }

    /**
     * Check if a container with a specified ID is already created.
     *
     * @param containerId the container ID
     * @return {@code true} in case the container is already created
     */
    private boolean isContainerCreated(int containerId) {
        return itemContainerMap.containsKey(containerId);
    }

    private void removeItemContainer(int id) {
        org.illarion.nifty.controls.ItemContainer container = itemContainerMap.remove(id);
        if (container == null) {
            return;
        }
        String prefix = getPrefix(id);
        IllaClient.getCfg().set(prefix + "DisplayPosX", container.getElement().getConstraintX().toString());
        IllaClient.getCfg().set(prefix + "DisplayPosY", container.getElement().getConstraintY().toString());

        container.closeWindow();
    }

    /**
     * This event is received in case the dragging of a item is canceled.
     *
     * @param topic the topic of the event
     * @param data the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void cancelDragging(String topic, DraggableDragCanceledEvent data) {
        World.getInteractionManager().cancelDragging();
    }

    /**
     * This event is received in case the user clicks into the container.
     *
     * @param topic the topic of the event
     * @param data the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void onDoubleClickInContainer(@Nonnull String topic, @Nonnull NiftyMousePrimaryMultiClickedEvent data) {
        int slotId = getSlotId(topic);
        int containerId = getContainerId(topic);

        ItemContainer container = World.getPlayer().getContainer(containerId);
        if (container == null) {
            log.error("Used container {} does not exist.", containerId);
            return;
        }
        ContainerSlot slot = container.getSlot(slotId);

        if (!slot.containsItem()) {
            return;
        }

        if (data.getClickCount() == 2) {
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
     * Get the slot ID that is stored in the ID a element.
     *
     * @param key the key of the element
     * @return the extracted ID
     */
    private static int getSlotId(@Nonnull CharSequence key) {
        Matcher matcher = slotPattern.matcher(key);
        if (!matcher.find()) {
            return -1;
        }

        if (matcher.groupCount() == 0) {
            return -1;
        }

        return Integer.parseInt(matcher.group(1));
    }

    /**
     * Get the container ID that is stored in the ID a element.
     *
     * @param key the key of the element
     * @return the extracted ID
     */
    private static int getContainerId(@Nonnull CharSequence key) {
        Matcher matcher = containerPattern.matcher(key);
        if (!matcher.find()) {
            return -1;
        }

        if (matcher.groupCount() == 0) {
            return -1;
        }

        return Integer.parseInt(matcher.group(1));
    }

    /**
     * This event is received in case the user drags the item away from its slot.
     *
     * @param topic the topic of the event
     * @param data the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void dragFrom(@Nonnull String topic, @Nonnull DraggableDragStartedEvent data) {
        int slotId = getSlotId(topic);
        int containerId = getContainerId(topic);

        Element slot = data.getSource().getElement();
        Element parentSlot = (slot == null) ? null : slot.getParent();
        InventorySlot invSlot = (parentSlot == null) ? null : parentSlot.getNiftyControl(InventorySlot.class);
        if (invSlot != null) {
            InteractionManager iManager = World.getInteractionManager();
            Runnable endOp = new EndOfDragOperation(invSlot);
            iManager.notifyDraggingContainer(containerId, slotId, endOp);
        }
    }

    /**
     * This event is received in case the user drops the item away into a slot.
     *
     * @param topic the topic of the event
     * @param data the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void dropIn(String topic, @Nonnull DroppableDroppedEvent data) {
        final int slotId = getSlotId(topic);
        final int containerId = getContainerId(topic);

        final InteractionManager iManager = World.getInteractionManager();
        ItemCount amount = iManager.getMovedAmount();
        if (amount == null) {
            log.error("Corrupted dropping detected.");
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
                    iManager.dropAtContainer(containerId, slotId, ItemCount.getInstance(value));
                }
            });
        } else {
            iManager.dropAtContainer(containerId, slotId, amount);
        }
    }

    /**
     * Check if the shift key is pressed on the keyboard.
     *
     * @return {@code true} in case the shift key on the keyboard
     */
    private boolean isShiftPressed() {
        return (input != null) && input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
    }

    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void onMouseMoveOverSlot(String topic, @Nonnull NiftyMouseMovedEvent event) {
        int slotId = getSlotId(topic);
        int containerId = getContainerId(topic);

        if (input.isAnyButtonDown(Button.Left, Button.Right)) {
            return;
        }

        ItemContainer container = World.getPlayer().getContainer(containerId);
        if (container == null) {
            log.error("MouseOver action on non-existent container!");
            return;
        }

        ContainerSlot slot = container.getSlot(slotId);

        if (!LookAtTracker.isLookAtObject(slot)) {
            LookAtTracker.setLookAtObject(slot);
            slot.getInteractive().lookAt();
        }
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
    }

    @Override
    public void closeContainer(final int containerId) {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                if (isContainerCreated(containerId)) {
                    removeItemContainer(containerId);
                }
            }
        });
    }

    @Override
    public boolean isVisible(int containerId) {
        return isContainerCreated(containerId);
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
        activeNifty.unsubscribeAnnotations(this);

        int[] containerIds = Arrays.copyOf(itemContainerMap.keys(), itemContainerMap.size());
        for (int id : containerIds) {
            removeItemContainer(id);
        }
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);
        activeNifty.subscribeAnnotations(this);
    }

    @Override
    public void showContainer(@Nonnull ItemContainer container) {
        World.getUpdateTaskManager().addTask(new UpdateContainerTask(container));
    }

    @Override
    public void showTooltip(int containerId, int slotId, @Nonnull Tooltip tooltip) {
        @Nullable org.illarion.nifty.controls.ItemContainer container = itemContainerMap.get(containerId);
        if (container == null) {
            return;
        }
        InventorySlot slot = container.getSlot(slotId);
        Element slotElement = slot.getElement();

        if (slotElement != null) {
            Rectangle rect = new Rectangle();
            rect.set(slotElement.getX(), slotElement.getY(), slotElement.getWidth(), slotElement.getHeight());

            tooltipHandler.showToolTip(rect, tooltip);
        }
    }

    /**
     * Create a new container.
     *
     * @param itemContainer the item container the GUI is supposed to display
     */
    private void createNewContainer(@Nonnull ItemContainer itemContainer) {
        ItemContainerBuilder builder = new ItemContainerBuilder("container" + itemContainer.getContainerId(),
                                                                buildTitle(itemContainer));
        builder.slots(itemContainer.getSlotCount());
        builder.slotDim(35, 35);
        builder.containerId(itemContainer.getContainerId());

        Element container = builder.build(activeNifty, activeScreen, activeScreen.findElementById("windows"));
        org.illarion.nifty.controls.ItemContainer conControl = container
                .getNiftyControl(org.illarion.nifty.controls.ItemContainer.class);

        String prefix = getPrefix(itemContainer.getContainerId());
        container.setConstraintX(new SizeValue(IllaClient.getCfg().getString(prefix + "DisplayPosX")));
        container.setConstraintY(new SizeValue(IllaClient.getCfg().getString(prefix + "DisplayPosY")));

        itemContainerMap.put(itemContainer.getContainerId(), conControl);
    }

    private String getPrefix(int containerId) {
        String prefix = "bag";
        if (containerId == 0) {
            prefix = "backpack";
        } else if (containerId < 10) {
            prefix = "depot";
        }
        return prefix;
    }

    private static String buildTitle(@Nonnull ItemContainer container) {
        String title = container.getTitle();

        String description = container.getDescription();
        if (description.isEmpty()) {
            return title;
        } else {
            int slotsInRow = (int) Math.sqrt(container.getSlotCount());
            Font calculationFont = FontLoader.getInstance().getFont(FontLoader.TEXT_FONT);
            int spaceToUse = (slotsInRow * 35) - 25;
            spaceToUse -= calculationFont.getWidth(title);

            String shortDescription = getShortenedDescription(description, "...", calculationFont, spaceToUse);
            if (shortDescription.isEmpty()) {
                return title;
            }
            return title + " (" + shortDescription + ')';
        }
    }

    private static String getShortenedDescription(
            @Nonnull String description, @Nonnull String expansion, @Nonnull Font usedFont, int maxWidth) {
        if (maxWidth <= 0) {
            return "";
        }
        if (usedFont.getWidth(description) <= maxWidth) {
            return description;
        }

        StringBuilder sb = new StringBuilder(description);
        while (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            sb.append(expansion);
            if (usedFont.getWidth(sb) <= maxWidth) {
                return sb.toString();
            }
            sb.setLength(sb.length() - 3);
        }
        return "";
    }

    /**
     * Update the merchant overlays of all active items.
     */
    private void updateAllMerchantOverlays() {
        itemContainerMap.forEachEntry(new TIntObjectProcedure<org.illarion.nifty.controls.ItemContainer>() {
            @Override
            public boolean execute(
                    int id, @Nonnull org.illarion.nifty.controls.ItemContainer itemContainer) {
                int slotCount = itemContainer.getSlotCount();
                for (int i = 0; i < slotCount; i++) {
                    InventorySlot conSlot = itemContainer.getSlot(i);
                    ItemContainer container = World.getPlayer().getContainer(id);
                    if (container == null) {
                        log.error("Container in handler was not created for player!");
                        return true;
                    }
                    updateMerchantOverlay(conSlot, container.getSlot(i).getItemID());
                }
                return true;
            }
        });
    }

    /**
     * Update the overlays of the merchants.
     *
     * @param slot the slot to update
     * @param itemId the item ID in this slot
     */
    private void updateMerchantOverlay(@Nonnull InventorySlot slot, @Nullable ItemId itemId) {
        if (!ItemId.isValidItem(itemId)) {
            slot.hideMerchantOverlay();
            return;
        }

        MerchantList merchantList = World.getPlayer().getMerchantList();
        if (merchantList != null) {
            for (int i = 0; i < merchantList.getItemCount(); i++) {
                MerchantItem item = merchantList.getItem(i);
                if (item.getItemId().equals(itemId)) {
                    switch (item.getType()) {
                        case BuyingPrimaryItem:
                            slot.showMerchantOverlay(InventorySlot.MerchantBuyLevel.Gold);
                            return;
                        case BuyingSecondaryItem:
                            slot.showMerchantOverlay(InventorySlot.MerchantBuyLevel.Silver);
                            return;
                        case SellingItem:
                            break;
                    }
                }
            }
        }
        slot.hideMerchantOverlay();
    }

    /**
     * Update the items inside the container.
     *
     * @param itemContainer the item container that contains the new data of the container
     */
    private void updateContainer(@Nonnull ItemContainer itemContainer) {
        @Nullable org.illarion.nifty.controls.ItemContainer conControl = itemContainerMap
                .get(itemContainer.getContainerId());
        if (conControl == null) {
            log.warn("Updating a container that does not exist.");
            return;
        }

        int slotCount = conControl.getSlotCount();
        if (itemContainer.getSlotCount() != slotCount) {
            // something is badly wrong. The player class will handle this issue.
            return;
        }

        for (int i = 0; i < slotCount; i++) {
            ContainerSlot containerSlot = itemContainer.getSlot(i);
            InventorySlot conSlot = conControl.getSlot(i);
            ItemId itemId = containerSlot.getItemID();
            ItemCount count = containerSlot.getCount();

            if (ItemId.isValidItem(itemId) && ItemCount.isGreaterZero(count)) {
                assert itemId != null;
                assert count != null;
                ItemTemplate displayedItem = ItemFactory.getInstance().getTemplate(itemId.getValue());

                NiftyImage niftyImage = new NiftyImage(activeNifty.getRenderEngine(),
                                                       new EntitySlickRenderImage(displayedItem));

                conSlot.setImage(niftyImage);
                conSlot.setLabelText(count.getShortText(Lang.getInstance().getLocale()));
                if (count.getValue() > 1) {
                    conSlot.showLabel();
                } else {
                    conSlot.hideLabel();
                }
                updateMerchantOverlay(conSlot, itemId);
            } else {
                conSlot.setImage(null);
                conSlot.hideLabel();
            }
        }

        conControl.getElement().getParent().layoutElements();
    }
}

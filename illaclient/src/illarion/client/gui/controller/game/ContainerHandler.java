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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DraggableDragCanceledEvent;
import de.lessvoid.nifty.controls.DraggableDragStartedEvent;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMouseMovedEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import illarion.client.IllaClient;
import illarion.client.graphics.Item;
import illarion.client.gui.EntitySlickRenderImage;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.CloseShowcaseCmd;
import illarion.client.net.server.events.CloseContainerEvent;
import illarion.client.net.server.events.ContainerItemLookAtEvent;
import illarion.client.net.server.events.DialogMerchantReceivedEvent;
import illarion.client.net.server.events.OpenContainerEvent;
import illarion.client.resources.ItemFactory;
import illarion.client.util.LookAtTracker;
import illarion.client.world.World;
import illarion.client.world.events.CloseDialogEvent;
import illarion.client.world.interactive.InteractionManager;
import illarion.client.world.items.ContainerSlot;
import illarion.client.world.items.ItemContainer;
import illarion.client.world.items.MerchantItem;
import illarion.client.world.items.MerchantList;
import illarion.common.gui.AbstractMultiActionHelper;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Rectangle;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.nifty.controls.InventorySlot;
import org.illarion.nifty.controls.ItemContainerCloseEvent;
import org.illarion.nifty.controls.itemcontainer.builder.ItemContainerBuilder;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This handler that care for properly managing the displaying of containers on the game screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ContainerHandler implements ScreenController, UpdatableHandler {
    /**
     * This class is used as drag end operation and used to move a object that was dragged out of the inventory back in
     * so the server can send the commands to clean everything up.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private class EndOfDragOperation implements Runnable {
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
            if (slot == null) {
                throw new NullPointerException("slot");
            }
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
     * This class is used to handle multiple clicks into a container.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class ContainerClickActionHelper extends AbstractMultiActionHelper {
        /**
         * The ID of the slot that was clicked at.
         */
        private int slotId;

        /**
         * The ID of the container that was clicked at.
         */
        private int containerId;

        /**
         * The constructor for this class. The timeout time is set to the system default double click interval.
         */
        ContainerClickActionHelper() {
            super(IllaClient.getCfg().getInteger("doubleClickInterval") + 150);
        }

        @Override
        public void executeAction(final int count) {
            final ItemContainer container;
            final ContainerSlot slot;

            switch (count) {
                case 1:
                    container = World.getPlayer().getContainer(containerId);
                    slot = container.getSlot(slotId);

                    slot.getInteractive().lookAt();
                    break;
                case 2:
                    container = World.getPlayer().getContainer(containerId);
                    slot = container.getSlot(slotId);

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
         * Set the data that is used for the click operations.
         *
         * @param slot      the slot that is clicked
         * @param container the container that is clicked
         */
        public void setData(final int slot, final int container) {
            slotId = slot;
            containerId = container;
        }
    }

    private final class UpdateContainerTask implements Runnable {
        private final OpenContainerEvent event;

        UpdateContainerTask(final OpenContainerEvent updateEvent) {
            event = updateEvent;
        }

        @Override
        public void run() {
            if (!isContainerCreated(event.getContainerId())) {
                createNewContainer(event);
            }
            updateContainer(event.getContainerId(), event.getItemIterator());
        }
    }

    /**
     * The click helper that is supposed to be used for handling clicks.
     */
    private static final ContainerClickActionHelper clickHelper = new ContainerClickActionHelper();

    /**
     * The pattern to fetch the ID of a slot name.
     */
    private static final Pattern slotPattern = Pattern.compile("slot([0-9]+)");

    /**
     * The pattern to fetch the ID of a container name.
     */
    private static final Pattern containerPattern = Pattern.compile("container([0-9]+)");
    /**
     * The list of tasks that need to be executed upon the next call of the update loop.
     */
    private final Queue<Runnable> updateTasks = new ConcurrentLinkedQueue<Runnable>();

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
    private final TIntObjectHashMap<org.illarion.nifty.controls.ItemContainer> itemContainerMap;

    /**
     * The task that is executed to update the merchant overlays.
     */
    private final Runnable updateMerchantOverlays = new Runnable() {
        @Override
        public void run() {
            updateAllMerchantOverlays();
        }
    };

    /**
     * The input system that is used to query the state of the keyboard.
     */
    private Input input;

    /**
     * Constructor of this handler.
     *
     * @param numberSelectPopupHandler the number select handler
     * @param tooltip                  the tooltip handler
     */
    public ContainerHandler(final NumberSelectPopupHandler numberSelectPopupHandler, final TooltipHandler tooltip) {
        itemContainerMap = new TIntObjectHashMap<org.illarion.nifty.controls.ItemContainer>();
        numberSelect = numberSelectPopupHandler;
        tooltipHandler = tooltip;
    }

    @EventSubscriber
    public void onContainerClosedEvent(final CloseContainerEvent event) {
        if (isContainerCreated(event.getContainerId())) {
            removeItemContainer(event.getContainerId());
        }
    }

    private void removeItemContainer(final int id) {
        final org.illarion.nifty.controls.ItemContainer container = itemContainerMap.remove(id);
        if (container == null) {
            return;
        }

        container.closeWindow();
    }

    /**
     * This event is received in case the server sends a tooltip for a container.
     *
     * @param event the container tooltip
     */
    @EventSubscriber
    public void onContainerItemLookAtHandler(final ContainerItemLookAtEvent event) {
        if (!isContainerCreated(event.getContainerId())) {
            return;
        }

        final org.illarion.nifty.controls.ItemContainer container = itemContainerMap.get(event.getContainerId());
        final InventorySlot slot = container.getSlot(event.getSlot());
        final Element slotElement = slot.getElement();

        final Rectangle rect = new Rectangle();
        rect.set(slotElement.getX(), slotElement.getY(), slotElement.getWidth(), slotElement.getHeight());

        tooltipHandler.showToolTip(rect, event);
    }

    /**
     * Check if a container with a specified ID is already created.
     *
     * @param containerId the container ID
     * @return {@code true} in case the container is already created
     */
    private boolean isContainerCreated(final int containerId) {
        return itemContainerMap.containsKey(containerId);
    }

    /**
     * Close the container as needed.
     *
     * @param event the close event that contains the information what dialog is supposed to be closed
     */
    @EventSubscriber
    public void onDialogClosedEvent(final CloseDialogEvent event) {
        switch (event.getDialogType()) {
            case Any:
            case Merchant:
                updateTasks.offer(updateMerchantOverlays);

            case Message:
                break;
            case Input:
                break;
            case Selection:
                break;
        }
    }

    /**
     * This event is receives in case the client receives a merchant dialog. This is needed to show the overlay on
     * the items.
     *
     * @param event the merchant dialog event
     */
    @EventSubscriber
    public void onMerchantDialogReceivedHandler(final DialogMerchantReceivedEvent event) {
        updateTasks.offer(updateMerchantOverlays);
    }

    /**
     * This event is received in case the server sends a new container.
     *
     * @param event the event data of the new container
     */
    @EventSubscriber
    public void onOpenContainerEvent(final OpenContainerEvent event) {
        updateTasks.offer(new ContainerHandler.UpdateContainerTask(event));
    }

    /**
     * This event is received in case the dragging of a item is canceled.
     *
     * @param topic the topic of the event
     * @param data  the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void cancelDragging(final String topic, final DraggableDragCanceledEvent data) {
        World.getInteractionManager().cancelDragging();
    }

    /**
     * This event is received in case the user clicks into the container.
     *
     * @param topic the topic of the event
     * @param data  the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void clickInContainer(final String topic, final NiftyMousePrimaryClickedEvent data) {
        final int slotId = getSlotId(topic);
        final int containerId = getContainerId(topic);

        clickHelper.setData(slotId, containerId);
        clickHelper.pulse();
    }

    /**
     * This event is received in case a container is closed.
     *
     * @param topic the topic of the event
     * @param data  the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*")
    public void onItemContainerClose(final String topic, final ItemContainerCloseEvent data) {
        World.getPlayer().removeContainer(data.getContainerId());
        if (isContainerCreated(data.getContainerId())) {
            removeItemContainer(data.getContainerId());

            final CloseShowcaseCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_CLOSE_SHOWCASE,
                    CloseShowcaseCmd.class);
            cmd.setShowcaseId(data.getContainerId());
            cmd.send();
        }
    }

    /**
     * Get the slot ID that is stored in the ID a element.
     *
     * @param key the key of the element
     * @return the extracted ID
     */
    private static int getSlotId(final CharSequence key) {
        final Matcher matcher = slotPattern.matcher(key);
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
    private static int getContainerId(final CharSequence key) {
        final Matcher matcher = containerPattern.matcher(key);
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
     * @param data  the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void dragFrom(final String topic, final DraggableDragStartedEvent data) {
        final int slotId = getSlotId(topic);
        final int containerId = getContainerId(topic);

        World.getInteractionManager().notifyDraggingContainer(containerId, slotId,
                new ContainerHandler.EndOfDragOperation(
                        data.getSource().getElement().getParent().getNiftyControl(InventorySlot.class)));
    }

    /**
     * This event is received in case the user drops the item away into a slot.
     *
     * @param topic the topic of the event
     * @param data  the event data
     */
    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void dropIn(final String topic, final DroppableDroppedEvent data) {
        final int slotId = getSlotId(topic);
        final int containerId = getContainerId(topic);

        final ItemCount amount = World.getInteractionManager().getMovedAmount();
        final InteractionManager iManager = World.getInteractionManager();
        if (ItemCount.isGreaterOne(amount) && isShiftPressed()) {
            numberSelect.requestNewPopup(1, amount.getValue(), new NumberSelectPopupHandler.Callback() {
                @Override
                public void popupCanceled() {
                    // nothing
                }

                @Override
                public void popupConfirmed(final int value) {
                    iManager.dropAtContainer(containerId, slotId, ItemCount.getInstance(value));
                }
            });
        } else {
            iManager.dropAtContainer(containerId, slotId, World.getInteractionManager().getMovedAmount());
        }
    }

    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void onMouseMoveOverSlot(final String topic, final NiftyMouseMovedEvent event) {
        final int slotId = getSlotId(topic);
        final int containerId = getContainerId(topic);

        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) || input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            return;
        }

        final ContainerSlot slot = World.getPlayer().getContainer(containerId).getSlot(slotId);

        if (!LookAtTracker.isLookAtObject(slot)) {
            LookAtTracker.setLookAtObject(slot);
            slot.getInteractive().lookAt();
        }

    }

    /**
     * Check if the shift key is pressed on the keyboard.
     *
     * @return {@code true} in case the shift key on the keyboard
     */
    private boolean isShiftPressed() {
        return (input != null) && (input.isKeyDown(Input.KEY_LSHIFT) || input.isKeyDown(Input.KEY_RSHIFT));
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
        activeNifty.unsubscribeAnnotations(this);
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);
        activeNifty.subscribeAnnotations(this);
    }

    @Override
    public void update(final GameContainer container, final int delta) {
        input = container.getInput();
        while (true) {
            final Runnable task = updateTasks.poll();
            if (task == null) {
                break;
            }

            task.run();
        }
    }

    /**
     * Create a new container.
     *
     * @param event the event that contains the data for the new container
     */
    private void createNewContainer(final OpenContainerEvent event) {
        final ItemContainerBuilder builder = new ItemContainerBuilder("container" + event.getContainerId(),
                "${gamescreen-bundle.bag}");
        builder.slots(event.getSlotCount());
        builder.slotDim(35, 35);
        builder.containerId(event.getContainerId());

        final Element container = builder.build(activeNifty, activeScreen, activeScreen.findElementByName("windows"));
        final org.illarion.nifty.controls.ItemContainer conControl = container.getNiftyControl(org.illarion.nifty.controls.ItemContainer.class);

        itemContainerMap.put(event.getContainerId(), conControl);
    }

    /**
     * Update the merchant overlays of all active items.
     */
    private void updateAllMerchantOverlays() {
        itemContainerMap.forEachEntry(new TIntObjectProcedure<org.illarion.nifty.controls.ItemContainer>() {
            @Override
            public boolean execute(final int id, final org.illarion.nifty.controls.ItemContainer itemContainer) {
                final int slotCount = itemContainer.getSlotCount();
                for (int i = 0; i < slotCount; i++) {
                    final InventorySlot conSlot = itemContainer.getSlot(i);
                    updateMerchantOverlay(conSlot, World.getPlayer().getContainer(id).getSlot(i).getItemID());
                }
                return true;
            }
        });
    }

    /**
     * Update the overlays of the merchants.
     *
     * @param slot   the slot to update
     * @param itemId the item ID in this slot
     */
    private void updateMerchantOverlay(final InventorySlot slot, final ItemId itemId) {
        if (!ItemId.isValidItem(itemId)) {
            slot.hideMerchantOverlay();
            return;
        }

        final MerchantList merchantList = World.getPlayer().getMerchantList();
        if (merchantList != null) {
            for (int i = 0; i < merchantList.getItemCount(); i++) {
                final MerchantItem item = merchantList.getItem(i);
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
     * @param containerId the container ID
     * @param itr         the item iterator that updates the container
     */
    private void updateContainer(final int containerId, final TIntObjectIterator<OpenContainerEvent.Item> itr) {
        final org.illarion.nifty.controls.ItemContainer conControl = itemContainerMap.get(containerId);

        final int slotCount = conControl.getSlotCount();
        for (int i = 0; i < slotCount; i++) {
            final InventorySlot conSlot = conControl.getSlot(i);
            conSlot.setImage(null);
            conSlot.hideLabel();
        }

        while (itr.hasNext()) {
            itr.advance();
            final InventorySlot conSlot = conControl.getSlot(itr.key());
            final ItemId itemId = itr.value().getItemId();
            final ItemCount count = itr.value().getCount();

            if (ItemId.isValidItem(itemId)) {
                final Item displayedItem = ItemFactory.getInstance().getPrototype(itemId.getValue());

                final NiftyImage niftyImage = new NiftyImage(activeNifty.getRenderEngine(),
                        new EntitySlickRenderImage(displayedItem));

                conSlot.setImage(niftyImage);
                conSlot.setLabelText(Integer.toString(count.getValue()));
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

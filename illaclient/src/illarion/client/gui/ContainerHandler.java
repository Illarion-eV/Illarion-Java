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
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import illarion.client.IllaClient;
import illarion.client.graphics.Item;
import illarion.client.net.server.events.DialogMerchantReceivedEvent;
import illarion.client.net.server.events.OpenContainerEvent;
import illarion.client.resources.ItemFactory;
import illarion.client.world.World;
import illarion.client.world.events.CloseDialogEvent;
import illarion.client.world.items.ContainerSlot;
import illarion.client.world.items.ItemContainer;
import illarion.client.world.items.MerchantItem;
import illarion.client.world.items.MerchantList;
import illarion.common.gui.AbstractMultiActionHelper;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.nifty.controls.InventorySlot;
import org.illarion.nifty.controls.itemcontainer.builder.ItemContainerBuilder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This handler that care for properly managing the displaying of containers on the game screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ContainerHandler implements ScreenController, UpdatableHandler {

    private final Queue<Runnable> updateTasks = new ConcurrentLinkedQueue<Runnable>();

    @Override
    public void update(final int delta) {
        while (true) {
            final Runnable task = updateTasks.poll();
            if (task == null) {
                break;
            }

            task.run();
        }
    }

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
            super(IllaClient.getCfg().getInteger("doubleClickInterval"));
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
                    } else {
                        slot.getInteractive().use();
                    }
                    break;
            }
        }
    }

    /**
     * The Nifty-GUI instance that is handling the GUI display currently.
     */
    private Nifty activeNifty;

    /**
     * The screen that takes care for the display currently.
     */
    private Screen activeScreen;

    /**
     * The click helper that is supposed to be used for handling clicks.
     */
    private static final ContainerClickActionHelper clickHelper = new ContainerClickActionHelper();

    private final TIntObjectHashMap<org.illarion.nifty.controls.ItemContainer> itemContainerMap;

    public ContainerHandler() {
        itemContainerMap = new TIntObjectHashMap<org.illarion.nifty.controls.ItemContainer>();
    }

    @EventSubscriber
    public void onOpenContainerEvent(final OpenContainerEvent event) {
        updateTasks.offer(new ContainerHandler.UpdateContainerTask(event));
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

    private boolean isContainerCreated(final int containerId) {
        return itemContainerMap.containsKey(containerId);
    }

    private void createNewContainer(final OpenContainerEvent event) {
        final ItemContainerBuilder builder = new ItemContainerBuilder("#container" + event.getContainerId(),
                "Tasche");
        builder.slots(event.getSlotCount());
        builder.slotDim(48, 48);
        builder.width(builder.pixels(288));
        final Element container = builder.build(activeNifty, activeScreen,
                activeScreen.findElementByName("windows"));
        final org.illarion.nifty.controls.ItemContainer conControl = container.getNiftyControl(org.illarion.nifty.controls.ItemContainer.class);

        itemContainerMap.put(event.getContainerId(), conControl);
    }

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
        }
    }

    @EventSubscriber
    public void onMerchantDialogReceivedHandler(final DialogMerchantReceivedEvent event) {
        updateTasks.offer(updateMerchantOverlays);
    }

    private final Runnable updateMerchantOverlays = new Runnable() {
        @Override
        public void run() {
            updateAllMerchantOverlays();
        }
    };

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
            final int itemId = itr.value().getItemId();
            final int count = itr.value().getCount();

            if (itemId > 0) {
                final Item displayedItem = ItemFactory.getInstance().getPrototype(itemId);

                final NiftyImage niftyImage = new NiftyImage(activeNifty.getRenderEngine(),
                        new EntitySlickRenderImage(displayedItem));

                conSlot.setImage(niftyImage);
                conSlot.setLabelText(Integer.toString(count));
                if (count > 1) {
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

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);
        activeNifty.subscribeAnnotations(this);
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
        activeNifty.unsubscribeAnnotations(this);
    }

    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void cancelDragging(final String topic, final DraggableDragCanceledEvent data) {
        World.getInteractionManager().cancelDragging();
    }

    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void clickInContainer(final String topic, final NiftyMousePrimaryClickedEvent data) {
        final int slotId = getSlotId(topic);
        final int containerId = getContainerId(topic);

        clickHelper.setData(slotId, containerId);
        clickHelper.pulse();
    }

    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void dragFrom(final String topic, final DraggableDragStartedEvent data) {
        final int slotId = getSlotId(topic);
        final int containerId = getContainerId(topic);

        World.getInteractionManager().notifyDraggingContainer(containerId, slotId,
                new EndOfDragOperation(data.getSource().getElement().getNiftyControl(InventorySlot.class)));
    }

    @NiftyEventSubscriber(pattern = ".*container[0-9]+.*slot[0-9]+.*")
    public void dropIn(final String topic, final DroppableDroppedEvent data) {
        final int slotId = getSlotId(topic);
        final int containerId = getContainerId(topic);

        System.out.println("Dropped into container(" + Integer.toString(containerId) + ") into slot(" + Integer
                .toString(slotId) + ") that is currently " + (data.getTarget().getElement().isVisible() ? "" : "not ") +
                "visible."
        );

        World.getInteractionManager().dropAtContainer(containerId, slotId);
    }

    private static final Pattern slotPattern = Pattern.compile("slot([0-9]+)");
    private static final Pattern containerPattern = Pattern.compile("container([0-9]+)");

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

    private void updateMerchantOverlay(final InventorySlot slot, final int itemId) {
        if (itemId == 0) {
            slot.hideMerchantOverlay();
            return;
        }

        final MerchantList merchantList = World.getPlayer().getMerchantList();
        if (merchantList != null) {
            for (int i = 0; i < merchantList.getItemCount(); i++) {
                final MerchantItem item = merchantList.getItem(i);
                if (item.getItemId() == itemId) {
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
            slot.showMerchantOverlay(InventorySlot.MerchantBuyLevel.Copper);
        } else {
            slot.hideMerchantOverlay();
        }
    }
}

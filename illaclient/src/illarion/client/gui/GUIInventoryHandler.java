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
import illarion.client.net.server.events.InventoryUpdateEvent;
import illarion.client.resources.ItemFactory;
import illarion.client.world.Inventory;
import illarion.client.world.World;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.controls.DraggableDragCanceledEvent;
import de.lessvoid.nifty.controls.DraggableDragStartedEvent;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.controls.dragndrop.builder.DraggableBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;

/**
 * This handler takes care for showing and hiding objects in the inventory. Also
 * it monitors all dropping operations on the slots of the inventory.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GUIInventoryHandler implements EventSubscriber<InventoryUpdateEvent> {
    private final String[] slots;
    private final Element[] dropSlots;
    private Element inventoryWindow;
    private Nifty activeNifty;
    private Screen activeScreen;

    private static final String INVSLOT_HEAD = "invslot_";
    private static final String SLOTITEM_TAIL = "_item";
    private static final String SLOTITEMIMAGE_TAIL = "_itemimage";

    public GUIInventoryHandler() {
        slots = new String[Inventory.SLOT_COUNT];
        slots[0] = "bag";
        slots[1] = "head";
        slots[2] = "neck";
        slots[3] = "chest";
        slots[4] = "hands";
        slots[5] = "lhand";
        slots[6] = "rhand";
        slots[7] = "lfinger";
        slots[8] = "rfinger";
        slots[9] = "legs";
        slots[10] = "feet";
        slots[11] = "cloak";
        slots[12] = "belt1";
        slots[13] = "belt2";
        slots[14] = "belt3";
        slots[15] = "belt4";
        slots[16] = "belt5";
        slots[17] = "belt6";

        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            slots[i] = INVSLOT_HEAD.concat(slots[i]);
        }

        dropSlots = new Element[Inventory.SLOT_COUNT];

    }

    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;

        inventoryWindow = screen.findElementByName("inventory");

        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            dropSlots[i] = inventoryWindow.findElementByName(slots[i]);
        }

        activeNifty.subscribeAnnotations(this);
        
        EventBus.subscribe(InventoryUpdateEvent.class, this);
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

    @NiftyEventSubscriber(pattern = INVSLOT_HEAD + ".*")
    public void dropInInventory(final String topic,
        final DroppableDroppedEvent data) {
        final int slotId = getSlotNumber(topic);
        System.out.println("Drop in Inventory: " + topic);
        World.getInteractionManager().dropAtInventory(slotId);
    }

    @NiftyEventSubscriber(pattern = INVSLOT_HEAD + ".*")
    public void dragFromInventory(final String topic,
        final DraggableDragStartedEvent data) {
        final int slotId = getSlotNumber(topic);
        System.out.println("Drag from Inventory: " + topic);
        World.getInteractionManager().notifyDraggingInventory(slotId);
    }

    @NiftyEventSubscriber(pattern = INVSLOT_HEAD + ".*")
    public void dragFromInventory(final String topic,
        final DraggableDragCanceledEvent data) {
        System.out.println("Cancel dragging!");
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

        final Element dragObject = getSlotItem(slotId, (itemId > 0));
        
        if (itemId > 0) {
            applyImageToDragSpot(dragObject, slotId, itemId, count);
        } else if (dragObject != null) {
            dragObject.markForRemoval();
        }
    }

    private Element getSlotItem(final int slotId, final boolean create) {
        final String slotItemName = slots[slotId].concat(SLOTITEM_TAIL);
        final String slotItemImageName =
            slots[slotId].concat(SLOTITEMIMAGE_TAIL);
        Element result = dropSlots[slotId].findElementByName(slotItemName);
        if (result == null && create) {
            final Element dragParent = dropSlots[slotId].findElementByName("#droppableContent");
            
            DraggableBuilder dragBuilder = new DraggableBuilder(slotItemName);
            dragBuilder.alignCenter();
            dragBuilder.valignCenter();
            dragBuilder.visibleToMouse();
            dragBuilder.visible(true);
            dragBuilder.childLayoutCenter();
            dragBuilder.revert(true);
            dragBuilder.drop(true);
            dragBuilder.x("0px");
            dragBuilder.y("0px");
            
            ImageBuilder imgBuilder = new ImageBuilder(slotItemImageName);
            dragBuilder.image(imgBuilder);
            imgBuilder.alignCenter();
            imgBuilder.valignCenter();
            imgBuilder.x("0px");
            imgBuilder.y("0px");
            
            result =
                dragBuilder
                    .build(activeNifty, activeScreen, dragParent);
        }

        return result;
    }

    private void applyImageToDragSpot(final Element dragElement,
        final int slotId, final int itemId, final int count) {
        final String slotItemImageName =
            slots[slotId].concat(SLOTITEMIMAGE_TAIL);
        final Element image = dragElement.findElementByName(slotItemImageName);

        final Item displayedItem = ItemFactory.getInstance()
            .getPrototype(itemId);
        image.getRenderer(ImageRenderer.class).setImage(
            new NiftyImage(activeNifty.getRenderEngine(),
                new EntitySlickRenderImage(displayedItem)));
        
        final int objectWidth = displayedItem.getWidth();
        final int objectHeight = displayedItem.getHeight();
        final int parentWidth = dropSlots[slotId].getWidth();
        final int parentHeight = dropSlots[slotId].getHeight();
        
        int fixedWidth = objectWidth;
        int fixedHeight = objectHeight;
        if (fixedWidth > parentWidth) {
            fixedHeight *= ((float) parentWidth / fixedWidth);
            fixedWidth = parentWidth;
        }
        if (fixedHeight > parentHeight) {
            fixedWidth *= ((float) parentHeight / fixedHeight);
            fixedHeight = parentHeight;
        }
        
        final SizeValue width = generateSizeValue(fixedWidth);
        final SizeValue height = generateSizeValue(fixedHeight);
        
        image.setConstraintWidth(width);
        image.setConstraintHeight(height);        
        dragElement.setConstraintWidth(width);
        dragElement.setConstraintHeight(height);
        
        dropSlots[slotId].layoutElements();
    }
    
    private SizeValue generateSizeValue(final int pixels) {
        return new SizeValue(Integer.toString(pixels).concat("px"));
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
}

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
package illarion.client.guiNG;

import java.awt.event.KeyEvent;

import illarion.client.graphics.Item;
import illarion.client.graphics.MarkerFactory;
import illarion.client.guiNG.elements.Image;
import illarion.client.guiNG.elements.SolidColor;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.init.ImageInit;
import illarion.client.guiNG.init.SolidColorInit;
import illarion.client.guiNG.messages.Message;
import illarion.client.guiNG.messages.WindowMessage;
import illarion.client.guiNG.references.DraggingDecoder;
import illarion.client.guiNG.references.InventoryReference;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.UseCmd;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

import illarion.input.KeyboardEvent;
import illarion.input.MouseEvent;

/**
 * This class contains the inventory that is displayed to the player. This
 * inventory displays the content of the inventory of the player character.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class Inventory extends Widget {
    /**
     * This widget extends the image widget and adds some additional functions
     * needed for handling the interaction with the items in the inventory.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    private static final class InventorySlotImage extends Image {
        /**
         * The serialization UID of this inventory slot image.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The parent inventory this slot is a part of.
         */
        private final Inventory parentInv;

        /**
         * The slot this slot image is assigned to.
         */
        private final int slot;

        /**
         * Constructor for a inventory slot that stores the slot its assigned to
         * and the parent inventory.
         * 
         * @param parInv the parent inventory
         * @param invSlot the slot this image is assigned to
         */
        public InventorySlotImage(final Inventory parInv, final int invSlot) {
            parentInv = parInv;
            slot = invSlot;
        }

        @Override
        public void handleMouseEvent(final MouseEvent event) {
            if (!handleDragging(event) && !handleUse(event)) {
                super.handleMouseEvent(event);
            }
        }

        /**
         * Handle all dragging events that could occur on this inventory slot.
         * 
         * @param event the event to handle
         * @return <code>true</code> in case the message got handled here
         */
        private boolean handleDragging(final MouseEvent event) {
            if (event.getEvent() == MouseEvent.EVENT_DRAG_START) {
                if (DraggingDecoder.getInstance().isDragging()) {
                    GUI.getInstance().getMouseCursor().attachImage(null);
                    DraggingDecoder.getInstance().reset();
                }

                final Item dragItem = parentInv.getItemAtSlot(slot);

                if (dragItem != null) {
                    final InventoryReference ref = new InventoryReference();
                    ref.setReferringSlot(slot);
                    DraggingDecoder.getInstance().setDragStart(ref);
                    GUI.getInstance().getMouseCursor()
                        .attachImage(dragItem.getSprite());
                    return true;
                }
            }

            if (event.getEvent() == MouseEvent.EVENT_DRAG_END) {
                if (!DraggingDecoder.getInstance().isDragging()) {
                    return false;
                }
                GUI.getInstance().getMouseCursor().attachImage(null);

                final InventoryReference ref = new InventoryReference();
                ref.setReferringSlot(slot);
                DraggingDecoder.getInstance().setDragEnd(ref);
                DraggingDecoder.getInstance().execute();
                return true;
            }
            return false;
        }

        /**
         * This function handles click events on the inventory slots and
         * triggers uses in case its needed.
         * 
         * @param event the event to process
         * @return <code>true</code> in case the event got handled, else
         *         <code>false</code> is returned
         */
        private boolean handleUse(final MouseEvent event) {
            if (event.getEvent() == MouseEvent.EVENT_KEY_DBLCLICK) {
                final Item dragItem = parentInv.getItemAtSlot(slot);

                if (dragItem != null) {
                    final InventoryReference ref = new InventoryReference();
                    ref.setReferringSlot(slot);

                    final UseCmd cmd =
                        (UseCmd) CommandFactory.getInstance().getCommand(
                            CommandList.CMD_USE);
                    cmd.addUse(ref);
                    cmd.send();
                }
            }

            return false;
        }
    }

    /**
     * The space between the slot rows in the inventory.
     */
    private static final int ROW_SPACE = 5;

    /**
     * The serialization UID of this widget.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Dirty flag for the item graphics. Means the layout of the graphics need
     * to be changed next time the display is drawn.
     */
    private boolean graphicsDirty = false;

    /**
     * The count values of the items displayed.
     */
    private final int[] itemCount;

    /**
     * The items that are displayed on the screen.
     */
    private final Image[] itemDisplay;

    /**
     * The list of item ID that are displayed in the inventory.
     */
    private final int[] itemids;

    /**
     * The list of graphic items displayed in this inventory.
     */
    private final Item[] items;

    /**
     * Dirty flag means the items require a update next time the graphics are
     * rendered.
     */
    private boolean itemsDirty = false;

    /**
     * Constructor for the inventory that creates the all require graphics for
     * the inventory.
     */
    public Inventory() {
        itemids = new int[18];
        itemCount = new int[18];
        items = new Item[18];
        itemDisplay = new Image[18];

        setRelPos(0, 0);
        setHeight(456);
        setWidth(300);
    }

    @Override
    public void cleanup() {
        removeAllChildren();
        itemsDirty = true;
        graphicsDirty = true;
        super.cleanup();
    }

    /**
     * Get a item at one slot of the inventory.
     * 
     * @param slot the slot of the inventory that contains the item
     * @return the item at the inventory slot
     */
    public Item getItemAtSlot(final int slot) {
        updateItems();
        return items[slot];
    }

    @Override
    public boolean handleKeyboardEvent(final KeyboardEvent event) {
        if (event.getKey() == KeyEvent.VK_I) {
            if ((event.getEvent() != KeyboardEvent.EVENT_KEY_UP)
                || event.isRepeated()) {
                return true;
            }

            if (isVisible()) {
                GUI.getInstance().hideInventory();
            } else {
                GUI.getInstance().showInventory();
            }

            return true;
        }

        return super.handleKeyboardEvent(event);
    }

    @Override
    public void handleMessage(final Message msg) {
        if (msg instanceof WindowMessage) {
            final WindowMessage winMsg = (WindowMessage) msg;
            if (winMsg.getMessageType() == WindowMessage.WINDOW_CLOSED) {
                setVisible(false);
            } else {
                setVisible(true);
            }
        }
    }

    @Override
    public void refreshLayout() {
        updateItems();
        updateGraphics();
        super.refreshLayout();
    }

    /**
     * Set the item ID for a specified slot. This causes that the displayed
     * items will be updated the next time the inventory is drawn.
     * 
     * @param slot the slot of the item that shall change
     * @param newID the new item id
     * @param count The count of items on this position in a stack
     */
    @SuppressWarnings("nls")
    public void setItemId(final int slot, final int newID, final int count) {
        if ((slot < 0) || (slot >= itemids.length)) {
            throw new IndexOutOfBoundsException(
                "Too large slot value for inventory");
        }

        if ((itemids[slot] == newID) && (itemCount[slot] == count)) {
            return;
        }
        itemids[slot] = newID;
        itemCount[slot] = count;
        itemsDirty = true;
        layoutInvalid();
    }

    @Override
    public void setVisible(final boolean visible) {
        if (!isVisible() && visible) {
            buildInventory();
        } else if (!visible) {
            removeAllChildren();
        }

        super.setVisible(visible);
    }

    /**
     * Build the inventory display.
     */
    private void buildInventory() {
        removeAllChildren();

        final SpriteColor color = Graphics.getInstance().getSpriteColor();
        color.set(SpriteColor.COLOR_MAX);
        color.setAlpha(0.6f);

        int penY = ROW_SPACE + ROW_SPACE;
        final int totalWidth = getWidth();
        final int oneFourth = totalWidth / 4;
        final int half = totalWidth / 2;
        final int threeFourth = oneFourth + half;

        graphicsDirty = true;

        // Inventory Slot 15 - Belt (bottom - left)
        Image backImage = new InventorySlotImage(this, 15);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_BELT).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(oneFourth - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[15] = new Image();
        backImage.addChild(itemDisplay[15]);
        addChild(backImage);

        // Inventory Slot 16 - Belt (bottom - middle)
        backImage = new InventorySlotImage(this, 16);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_BELT).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(half - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[16] = new Image();
        backImage.addChild(itemDisplay[16]);
        addChild(backImage);

        // Inventory Slot 17 - Belt (bottom - right)
        backImage = new InventorySlotImage(this, 17);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_BELT).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(threeFourth - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[17] = new Image();
        backImage.addChild(itemDisplay[17]);
        addChild(backImage);

        // next row
        penY += backImage.getHeight();
        penY += ROW_SPACE;

        // Inventory Slot 12 - Belt (top - left)
        backImage = new InventorySlotImage(this, 12);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_BELT).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(oneFourth - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[12] = new Image();
        backImage.addChild(itemDisplay[12]);
        addChild(backImage);

        // Inventory Slot 13 - Belt (top - middle)
        backImage = new InventorySlotImage(this, 13);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_BELT).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(half - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[13] = new Image();
        backImage.addChild(itemDisplay[13]);
        addChild(backImage);

        // Inventory Slot 14 - Belt (top - right)
        backImage = new InventorySlotImage(this, 14);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_BELT).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(threeFourth - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[14] = new Image();
        backImage.addChild(itemDisplay[14]);
        addChild(backImage);

        // next row
        penY += backImage.getHeight();
        penY += ROW_SPACE;

        final SolidColor line = new SolidColor();
        line.setInitScript(SolidColorInit.getInstance().setColor(
            SpriteColor.COLOR_MIN, SpriteColor.COLOR_MIN,
            SpriteColor.COLOR_MIN, SpriteColor.COLOR_MAX));
        line.setRelPos(0, penY);
        line.setHeight(1);
        line.setWidth(getWidth());
        addChild(line);

        penY += ROW_SPACE;

        int tempSize;

        // Inventory Slot 10 - Feet
        backImage = new InventorySlotImage(this, 10);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_SHOES).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(half - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[10] = new Image();
        backImage.addChild(itemDisplay[10]);
        addChild(backImage);

        tempSize = backImage.getHeight();

        // Inventory Slot 5 - Left Hands
        backImage = new InventorySlotImage(this, 5);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_WEAPON).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY + ROW_SPACE);
        backImage.setRelX(oneFourth - ((backImage.getWidth()) / 2) - 7);
        backImage.setVisible(true);

        itemDisplay[5] = new Image();
        backImage.addChild(itemDisplay[5]);
        addChild(backImage);

        // Inventory Slot 6 - Right Hands
        backImage = new InventorySlotImage(this, 6);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_SHIELD).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY + ROW_SPACE);
        backImage.setRelX((threeFourth - ((backImage.getWidth()) / 2)) + 7);
        backImage.setVisible(true);

        itemDisplay[6] = new Image();
        backImage.addChild(itemDisplay[6]);
        addChild(backImage);

        // next row
        penY += tempSize;
        penY += ROW_SPACE;

        // Inventory Slot 9 - Legs
        backImage = new InventorySlotImage(this, 9);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_LEGS).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(half - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[9] = new Image();
        backImage.addChild(itemDisplay[9]);
        addChild(backImage);

        // next row
        penY += backImage.getHeight();
        penY += ROW_SPACE;

        // Inventory Slot 4 - Hands
        backImage = new InventorySlotImage(this, 4);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_HANDS).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(half - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[4] = new Image();
        backImage.addChild(itemDisplay[4]);
        addChild(backImage);

        final int handsHeight = backImage.getHeight();

        // next row
        penY += backImage.getHeight();

        // Inventory Slot 7 - Ring (left)
        backImage = new InventorySlotImage(this, 7);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_RING).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY - ((handsHeight - backImage.getHeight()) / 2)
            - backImage.getHeight());
        backImage.setRelX(oneFourth - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[7] = new Image();
        backImage.addChild(itemDisplay[7]);
        addChild(backImage);

        // Inventory Slot 8 - Ring (right)
        backImage = new InventorySlotImage(this, 8);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_RING).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY - ((handsHeight - backImage.getHeight()) / 2)
            - backImage.getHeight());
        backImage.setRelX(threeFourth - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[8] = new Image();
        backImage.addChild(itemDisplay[8]);
        addChild(backImage);

        // next row
        penY += ROW_SPACE;

        // Inventory Slot 3 - Chest
        backImage = new InventorySlotImage(this, 3);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_CHEST).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(half - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[3] = new Image();
        backImage.addChild(itemDisplay[3]);
        addChild(backImage);

        // next row
        penY += backImage.getHeight();
        penY += ROW_SPACE;

        // Inventory Slot 11 - Coat
        backImage = new InventorySlotImage(this, 11);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_COAT).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY - backImage.getHeight());
        backImage.setRelX(threeFourth - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[11] = new Image();
        backImage.addChild(itemDisplay[11]);
        addChild(backImage);

        // Inventory Slot 0 - Bag
        backImage = new InventorySlotImage(this, 0);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_BAG).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY - backImage.getHeight());
        backImage.setRelX(oneFourth - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[0] = new Image();
        backImage.addChild(itemDisplay[0]);
        addChild(backImage);

        // Inventory Slot 1 - Head
        backImage = new InventorySlotImage(this, 1);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_HELMET).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY);
        backImage.setRelX(half - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[1] = new Image();
        backImage.addChild(itemDisplay[1]);
        addChild(backImage);

        // Inventory Slot 2 - Neck
        backImage = new InventorySlotImage(this, 2);
        backImage.setInitScript(ImageInit.getInstance()
            .setImageID(MarkerFactory.GUI_INV_NECK).setColor(color));
        backImage.setSizeToImage();
        backImage.setRelY(penY + ROW_SPACE);
        backImage.setRelX(threeFourth - ((backImage.getWidth()) / 2));
        backImage.setVisible(true);

        itemDisplay[2] = new Image();
        backImage.addChild(itemDisplay[2]);
        addChild(backImage);
    }

    /**
     * Update the graphical representations of the items in the inventory.
     */
    private void updateGraphics() {
        if (!graphicsDirty) {
            return;
        }

        layoutInvalid();
        graphicsDirty = false;

        for (int i = 0; i < itemids.length; i++) {
            if (itemDisplay[i] == null) {
                continue;
            }
            if (items[i] == null) {
                itemDisplay[i].setVisible(false);
                continue;
            }

            itemDisplay[i].setImage(items[i].getSprite());
            itemDisplay[i].setSizeToImage();

            final int imageHeight = itemDisplay[i].getHeight();
            final int imageWidth = itemDisplay[i].getWidth();

            final int offsetX = items[i].getSprite().getScaledOffsetX(1.f);
            final int offsetY = items[i].getSprite().getScaledOffsetY(1.f);

            itemDisplay[i].setDrawingOffset(-offsetX, -offsetY);

            final int parentHeight =
                itemDisplay[i].getParent().getHeight() - 8;
            final int parentWidth = itemDisplay[i].getParent().getWidth() - 8;

            if ((imageWidth <= parentWidth) && (imageHeight <= parentHeight)) {
                Utility.centerWidgetX(itemDisplay[i]);
                Utility.centerWidgetY(itemDisplay[i]);
                itemDisplay[i].setVisible(true);
                continue;
            }

            int newHeight = imageHeight;
            int newWidth = imageWidth;

            if (newWidth > parentWidth) {
                newWidth = parentWidth;

                newHeight =
                    (int) (imageHeight * ((float) newWidth / (float) imageWidth));
            }

            if (newHeight > parentHeight) {
                newHeight = parentHeight;

                newWidth =
                    (int) (imageWidth * ((float) newHeight / (float) imageHeight));
            }

            itemDisplay[i].setHeight(newHeight);
            itemDisplay[i].setWidth(newWidth);

            Utility.centerWidgetX(itemDisplay[i]);
            Utility.centerWidgetY(itemDisplay[i]);
            itemDisplay[i].setVisible(true);
        }
    }

    /**
     * Update the display of all items.
     */
    private void updateItems() {
        if (!itemsDirty) {
            return;
        }

        layoutInvalid();
        itemsDirty = false;

        for (int i = 0; i < itemids.length; i++) {
            if (itemids[i] == 0) {
                if (items[i] != null) {
                    items[i] = null;
                    graphicsDirty = true;
                }
                continue;
            }

            if (items[i] == null) {
                items[i] = Item.create(itemids[i], 0, 0);
                items[i].setCount(itemCount[i]);
                graphicsDirty = true;
            } else {
                if (items[i].getId() != itemids[i]) {
                    items[i].recycle();
                    items[i] = Item.create(itemids[i], 0, 0);
                    graphicsDirty = true;
                }
                if (items[i].getCount() != itemCount[i]) {
                    items[i].setCount(itemCount[i]);
                }
            }
        }
    }
}

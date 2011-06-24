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

import org.apache.log4j.Logger;

import illarion.client.graphics.Item;
import illarion.client.guiNG.elements.Image;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.messages.Message;
import illarion.client.guiNG.messages.WindowMessage;
import illarion.client.guiNG.references.ContainerReference;
import illarion.client.guiNG.references.DraggingDecoder;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.UseCmd;

import illarion.input.MouseEvent;

/**
 * This class contains the container window in which items can be moved around
 * as the player wishes. There is no limit in container windows.
 * 
 * @author Blay09
 * @since 1.22
 */
public class Container extends Widget {

    /**
     * This widget extends the image widget and adds some additional functions
     * needed for handling the interaction with the items in the container.
     * 
     * @author Martin Karing
     * @author Blay09
     * @since 1.22
     */
    private static final class ContainerItemImage extends Image {
        /**
         * The serialization UID of this container item image.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The container item id this image is assigned to.
         */
        private final byte containerItem;

        /**
         * The parent container this item is part of.
         */
        private final Container parentContainer;

        /**
         * Creates a new instance of the ContainerItemImage.
         * 
         * @param parent the parent container
         */
        public ContainerItemImage(final Container parent,
            final byte containerItemID) {
            parentContainer = parent;
            containerItem = containerItemID;
        }

        @Override
        public void handleMouseEvent(final MouseEvent event) {
            if (!handleDragging(event) && !handleUse(event)) {
                super.handleMouseEvent(event);
            }
        }

        /**
         * Handle all dragging events that could occur on this container item.
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

                final ContainerReference ref = new ContainerReference();
                ref.setReferringContainerItemID(containerItem);
                ref.setContainerID(parentContainer.getID());
                parentContainer.setDragImageSize(getWidth(), getHeight());
                DraggingDecoder.getInstance().setDragStart(ref);
                GUI.getInstance()
                    .getMouseCursor()
                    .attachImage(
                        parentContainer.getContainerItem(containerItem)
                            .getSprite());
                return true;
            } else if (event.getEvent() == MouseEvent.EVENT_DRAG_END) {
                if (!DraggingDecoder.getInstance().isDragging()) {
                    return false;
                }
                GUI.getInstance().getMouseCursor().attachImage(null);

                final ContainerReference ref = new ContainerReference();
                ref.setTargetPosition(getRelX(), getRelY());
                ref.setContainerID(parentContainer.getID());
                DraggingDecoder.getInstance().setDragEnd(ref);
                DraggingDecoder.getInstance().execute();
                return true;
            }
            return false;
        }

        /**
         * This function handles click events on the container items and
         * triggers uses in case its needed.
         * 
         * @param event the event to process
         * @return <code>true</code> in case the event got handled, else
         *         <code>false</code> is returned
         */
        private boolean handleUse(final MouseEvent event) {
            if (event.getEvent() == MouseEvent.EVENT_KEY_DBLCLICK) {
                final ContainerReference ref = new ContainerReference();
                ref.setContainerID(parentContainer.getID());
                ref.setReferringContainerItemID(containerItem);

                final UseCmd cmd =
                    (UseCmd) CommandFactory.getInstance().getCommand(
                        CommandList.CMD_USE);
                cmd.addUse(ref);
                cmd.send();
            }

            return false;
        }
    }

    /**
     * The error and debug logger of the book.
     */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(Container.class);

    /**
     * The serialization UID of the container.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The amount of each item in that container.
     */
    private short[] containerItemCount;

    /**
     * The IDs of the items in that container.
     */
    private int[] containerItemIDs;

    /**
     * The Images that will be displayed in that container.
     */
    private Image[] containerItemImages;

    /**
     * The Item instances of the items in that container.
     */
    private Item[] containerItems;

    /**
     * The X position of each item in the container.
     */
    private int[] containerItemX;

    /**
     * The Y position of each item in the container.
     */
    private int[] containerItemY;

    /**
     * If set to true, the update() function will re-align all items in that
     * container.
     */
    private boolean dirtyFlag = false;

    /**
     * The image height of the currently dragged container item.
     */
    private int dragItemHeight;

    /**
     * The image width of the currently dragged container item.
     */
    private int dragItemWidth;

    /**
     * The ID of that container instance.
     */
    private byte id = 0;

    /**
     * If set to true, the container will be saved together with the GUI tree.
     */
    private boolean persistent = false;

    /**
     * Creates a new instance of the container window.
     */
    public Container() {
        super();

        setRelPos(0, 0);
        setHeight(500);
        setWidth(500);
    }

    /**
     * Cleanup the container before saving if it's not persistent. That results
     * in removing the container from the GUI tree.
     */
    @Override
    public void cleanup() {
        super.cleanup();
        if (persistent == false) {
            if (hasParent()) {
                getParent().removeChild(this);
            }
        }
    }

    /**
     * Closes this container and removes it from the GUI tree.
     */
    public void closeContainer() {
        persistent = false;
        if (hasParent() && getParent().hasParent()) {
            getParent().getParent().removeChild(getParent());
        }
        cleanup();
    }

    /**
     * Returns the item with the given container item index.
     * 
     * @param containerItemID the container item index of the item
     * @return the item with the given container item index
     */
    public Item getContainerItem(final int containerItemID) {
        if ((containerItemID >= 0)
            && (containerItemID < containerItems.length)) {
            return containerItems[containerItemID];
        }
        return null;
    }

    /**
     * Returns the id of that container.
     * 
     * @return the id of that container
     */
    public byte getID() {
        return id;
    }

    @Override
    public void handleMessage(final Message msg) {
        if (msg instanceof WindowMessage) {
            final WindowMessage winMsg = (WindowMessage) msg;
            if (winMsg.getMessageType() == WindowMessage.WINDOW_CLOSED) {

                removeAllChildren();
                if (containerItemIDs != null) {
                    for (int i = 0; i < containerItemIDs.length; i++) {
                        containerItems[i].recycle();
                    }
                }

            }
        }
    }

    @Override
    public void handleMouseEvent(final MouseEvent event) {
        if (event.getEvent() == MouseEvent.EVENT_DRAG_END) {
            if (!DraggingDecoder.getInstance().isDragging()) {
                return;
            }
            GUI.getInstance().getMouseCursor().attachImage(null);

            final ContainerReference ref = new ContainerReference();
            ref.setTargetPosition(event.getPosX() - (dragItemWidth / 2),
                event.getPosY() - (dragItemHeight / 2));
            ref.setContainerID(id);
            DraggingDecoder.getInstance().setDragEnd(ref);
            DraggingDecoder.getInstance().execute();
            return;
        }
        super.handleMouseEvent(event);
    }

    /**
     * Returns the persistence state of the container.
     * 
     * @return true if the container is persistent
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Stores the size of the drag item image.
     * 
     * @param width the width of the currently dragged image
     * @param height the height of the currently dragged image
     */
    public void setDragImageSize(final int width, final int height) {
        dragItemWidth = width;
        dragItemHeight = height;
    }

    /**
     * If set to true, the container will be saved together with the GUI tree.
     * 
     * @param newPersistent if set to true, the container will be saved together
     *            with the GUI tree.
     */
    public void setPersistent(final boolean newPersistent) {
        persistent = newPersistent;
    }

    /**
     * This function is called on every frame. If dirtyFlag is set to true, this
     * function reloads and realigns all items in that container.
     */
    public void update() {
        if (dirtyFlag == false) {
            return;
        }
        removeAllChildren();
        for (int i = 0; i < containerItemIDs.length; i++) {
            containerItems[i] = Item.create(containerItemIDs[i], 0, 0);
            containerItems[i].setCount(containerItemCount[i]);

            containerItemImages[i] = new ContainerItemImage(this, (byte) i);
            containerItemImages[i].setImage(containerItems[i].getSprite());
            containerItemImages[i].setSizeToImage();
            final int offsetX =
                containerItems[i].getSprite().getScaledOffsetX(1.f);
            final int offsetY =
                containerItems[i].getSprite().getScaledOffsetY(1.f);
            containerItemImages[i].setDrawingOffset(-offsetX, -offsetY);
            containerItemImages[i].setRelPos(containerItemX[i],
                containerItemY[i]);
            addChild(containerItemImages[i]);
        }
        dirtyFlag = false;
    }

    /**
     * Updates the container with the information from the server. This
     * functions sets the dirtyFlag to true.
     * 
     * @param containerID the ID of that container
     * @param itemId an array of all item ids in that container
     * @param count an array with the ammounts of the items in that container
     * @param itemX an array with the x positions of the items in that container
     * @param itemY an array with the y positions of the items in that container
     */
    public void updateItems(final byte containerID, final int[] itemId,
        final short[] count, final int[] itemX, final int[] itemY) {
        id = containerID;

        if (containerItemIDs != null) {
            for (int i = 0; i < containerItemIDs.length; i++) {
                containerItems[i].recycle();
            }
        }

        containerItemIDs = itemId;
        containerItemCount = count;
        containerItemX = itemX;
        containerItemY = itemY;

        dirtyFlag = true;
    }

}

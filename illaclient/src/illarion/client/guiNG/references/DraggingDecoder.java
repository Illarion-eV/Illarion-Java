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
package illarion.client.guiNG.references;

import illarion.client.guiNG.GUI;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.DragInvInvCmd;
import illarion.client.net.client.DragInvMapCmd;
import illarion.client.net.client.DragInvScCmd;
import illarion.client.net.client.DragMapInvCmd;
import illarion.client.net.client.DragMapMapCmd;
import illarion.client.net.client.DragMapScCmd;
import illarion.client.net.client.DragScInvCmd;
import illarion.client.net.client.DragScMapCmd;
import illarion.client.net.client.DragScScCmd;
import illarion.client.net.client.MoveCmd;
import illarion.client.world.Char;
import illarion.client.world.Game;

/**
 * The dragging decoder takes care drags between different references are send
 * to the server in a proper way.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class DraggingDecoder {
    /**
     * The singleton instance of the dragging decoder.
     */
    private static final DraggingDecoder INSTANCE = new DraggingDecoder();

    /**
     * The reference to the object where the dragging stopped.
     */
    private AbstractReference dragEnd;

    /**
     * This variable stores if there is currently a dragging event in process.
     */
    private boolean dragging = false;

    /**
     * The reference to the object where the dragging started.
     */
    private AbstractReference dragStart;

    /**
     * Private constructor to avoid multiple methods being used.
     */
    private DraggingDecoder() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this dragging decoder.
     * 
     * @return the singleton instance of this class.
     */
    public static DraggingDecoder getInstance() {
        return INSTANCE;
    }

    /**
     * Execute the drag set up in the dragging decoder.
     */
    public void execute() {
        if (dragging || (dragStart == null) || (dragEnd == null)) {
            return;
        }

        final AbstractReference startRef = dragStart;
        final AbstractReference endRef = dragEnd;

        final int fromId = startRef.getId();
        final int toId = endRef.getId();

        if (fromId == AbstractReference.MAP) {
            final MapReference realFrom = (MapReference) startRef;
            if (toId == AbstractReference.MAP) {
                final MapReference realTo = (MapReference) endRef;

                if (Game.getPlayer().getLocation()
                    .isNeighbour(realFrom.getReferringLocation())) {
                    int dir =
                        Game.getPlayer().getLocation()
                            .getDirection(realFrom.getReferringLocation());
                    dir += CommandList.CMD_DRAG_MAP_MAP_N;
                    final DragMapMapCmd cmd =
                        (DragMapMapCmd) CommandFactory.getInstance()
                            .getCommand(dir);
                    cmd.setDragTo(realTo.getReferringLocation());
                    cmd.setCounter();
                    cmd.send();
                }
                dragStart = null;
                dragEnd = null;
                return;
            }

            if (toId == AbstractReference.INVENTORY) {
                final InventoryReference realTo = (InventoryReference) endRef;
                if (Game.getPlayer().getLocation()
                    .isNeighbour(realFrom.getReferringLocation())) {
                    final DragMapInvCmd cmd =
                        (DragMapInvCmd) CommandFactory.getInstance()
                            .getCommand(CommandList.CMD_DRAG_MAP_INV);
                    cmd.setDragFrom(realFrom.getReferringLocation());
                    cmd.setDragTo(realTo.getReferringSlot());
                    cmd.send();
                }
                dragStart = null;
                dragEnd = null;
                return;
            }

            if (toId == AbstractReference.CHARACTER) {
                // final CharReference realTo = (CharReference) endRef;

                dragStart = null;
                dragEnd = null;
                return;
            }

            if (toId == AbstractReference.CONTAINER) {
                final ContainerReference realTo = (ContainerReference) endRef;

                final DragMapScCmd cmd =
                    (DragMapScCmd) CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_MAP_SC);
                cmd.setSource(realFrom.getReferringLocation());
                cmd.setTarget(realTo.getContainerID(), realTo.getTargetX(),
                    realTo.getTargetY());
                cmd.send();

                dragStart = null;
                dragEnd = null;
                return;
            }
        }

        if (fromId == AbstractReference.INVENTORY) {
            final InventoryReference realFrom = (InventoryReference) startRef;
            if (toId == AbstractReference.MAP) {
                final MapReference realTo = (MapReference) endRef;

                final DragInvMapCmd cmd =
                    (DragInvMapCmd) CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_INV_MAP);
                cmd.setDragFrom(realFrom.getReferringSlot());
                cmd.setDragTo(realTo.getReferringLocation());
                cmd.send();

                dragStart = null;
                dragEnd = null;
                return;
            }

            if (toId == AbstractReference.INVENTORY) {
                final InventoryReference realTo = (InventoryReference) endRef;

                final DragInvInvCmd cmd =
                    (DragInvInvCmd) CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_INV_INV);
                cmd.setDrag(realFrom.getReferringSlot(),
                    realTo.getReferringSlot());
                cmd.send();

                dragStart = null;
                dragEnd = null;
                return;
            }

            if (toId == AbstractReference.CONTAINER) {
                final ContainerReference realTo = (ContainerReference) endRef;

                final DragInvScCmd cmd =
                    (DragInvScCmd) CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_INV_SC);
                cmd.setSource((byte) realFrom.getReferringSlot());
                cmd.setTarget(realTo.getContainerID(), realTo.getTargetX(),
                    realTo.getTargetY());
                cmd.send();

                dragStart = null;
                dragEnd = null;
                return;
            }
        }

        if (fromId == AbstractReference.CONTAINER) {
            final ContainerReference realFrom = (ContainerReference) startRef;

            if (toId == AbstractReference.MAP) {
                final MapReference realTo = (MapReference) endRef;
                final DragScMapCmd cmd =
                    (DragScMapCmd) CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_SC_MAP);
                cmd.setSource(realFrom.getContainerID(),
                    realFrom.getReferringContainerItemID());
                cmd.setTarget(realTo.getReferringLocation());
                cmd.send();
                dragStart = null;
                dragEnd = null;
                return;
            }

            if (toId == AbstractReference.INVENTORY) {
                final InventoryReference realTo = (InventoryReference) endRef;

                final DragScInvCmd cmd =
                    (DragScInvCmd) CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_SC_INV);
                cmd.setSource(realFrom.getContainerID(),
                    realFrom.getReferringContainerItemID());
                cmd.setTarget((byte) realTo.getReferringSlot());
                cmd.send();

                dragStart = null;
                dragEnd = null;
                return;
            }

            if (toId == AbstractReference.CONTAINER) {
                final ContainerReference realTo = (ContainerReference) endRef;

                final DragScScCmd cmd =
                    (DragScScCmd) CommandFactory.getInstance().getCommand(
                        CommandList.CMD_DRAG_SC_SC);
                cmd.setSource(realFrom.getContainerID(),
                    realFrom.getReferringContainerItemID());
                cmd.setTarget(realTo.getContainerID(), realTo.getTargetX(),
                    realTo.getTargetY());
                cmd.send();

                dragStart = null;
                dragEnd = null;
                return;
            }
        }

        if (fromId == AbstractReference.CHARACTER) {
            final CharReference realFrom = (CharReference) startRef;
            if (toId == AbstractReference.MAP) {
                final MapReference realTo = (MapReference) endRef;

                final Char tempChar =
                    Game.getPeople().getCharacter(
                        realFrom.getReferringCharacter());

                if (tempChar == null) {
                    return;
                }

                if (Game.getPlayer().getLocation()
                    .isNeighbour(tempChar.getLocation())) {
                    final MoveCmd cmd =
                        (MoveCmd) CommandFactory.getInstance().getCommand(
                            CommandList.CMD_MOVE);
                    cmd.setDirection(
                        tempChar.getCharId(),
                        tempChar.getLocation().getDirection(
                            realTo.getReferringLocation()));
                    cmd.setPushing();
                    cmd.send();
                }
                return;
            }
        }
    }

    /**
     * Get if there is currently a dragging event in process.
     * 
     * @return <code>true</code> in case anything is dragged currently
     */
    public boolean isDragging() {
        return dragging;
    }

    /**
     * Reset the dragging decoder to a former state. This needs to be done in
     * case the input decoders receive invalid data that can't fit to this.
     */
    public void reset() {
        dragStart = null;
        dragEnd = null;
        dragging = false;
        GUI.getInstance().getMouseCursor().attachImage(null);
    }

    /**
     * Set the reference to the point where a dragging event ended.
     * 
     * @param ref the reference to the end of the dragging event
     */
    public void setDragEnd(final AbstractReference ref) {
        if (!dragging) {
            reset();
        }
        dragEnd = ref;
        dragging = false;
    }

    /**
     * Set the reference to the point where a dragging event started.
     * 
     * @param ref the reference to the start of the dragging event
     */
    public void setDragStart(final AbstractReference ref) {
        if (dragging) {
            reset();
        }
        dragStart = ref;
        dragging = true;
    }
}

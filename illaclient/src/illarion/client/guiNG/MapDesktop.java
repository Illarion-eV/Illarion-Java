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

import illarion.client.ClientWindow;
import illarion.client.graphics.Item;
import illarion.client.guiNG.elements.Desktop;
import illarion.client.guiNG.references.CharReference;
import illarion.client.guiNG.references.DraggingDecoder;
import illarion.client.guiNG.references.MapReference;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.LookatCharCmd;
import illarion.client.net.client.LookatTileCmd;
import illarion.client.net.client.UseCmd;
import illarion.client.world.Char;
import illarion.client.world.CombatHandler;
import illarion.client.world.Game;
import illarion.client.world.MapTile;

import illarion.common.util.Location;

import illarion.input.MouseEvent;

/**
 * This is a special version of the desktop that holds all needed functions to
 * handle the user input for the map, that is beyond the normal functions of the
 * desktop.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MapDesktop extends Desktop {
    /**
     * The serialization UID of this map desktop.
     */
    private static final long serialVersionUID = 1L;

    /**
     * This value stores if the player is currently walking after the mouse.
     */
    private transient boolean walkingAfterMouse = false;

    /**
     * Constructor that also sets the desktop to the proper size of the entire
     * screen.
     */
    public MapDesktop() {
        setWidth(ClientWindow.getInstance().getScreenWidth());
        setHeight(ClientWindow.getInstance().getScreenHeight());
    }

    /**
     * Handle a mouse event that was triggered on the map.
     * 
     * @param event the mouse event that was triggered
     */
    @Override
    public void handleMouseEvent(final MouseEvent event) {
        if (!isVisible()) {
            return;
        }

        if (handleAttack(event)) {
            return;
        }

        if (handleMoveTo(event)) {
            return;
        }

        if (handleUse(event)) {
            return;
        }

        if (handleLookAtTile(event)) {
            return;
        }

        if (handleDrag(event)) {
            return;
        }

        if (handleWalkAfterMouse(event)) {
            return;
        }

        super.handleMouseEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initWidget() {
        setWidth(ClientWindow.getInstance().getScreenWidth());
        setHeight(ClientWindow.getInstance().getScreenHeight());
        super.initWidget();
    }

    /**
     * Handle a attacking event. This is done in case a character is clicked and
     * the combat mode is currently active.
     * 
     * @param event the mouse event to handle
     * @return <code>true</code> in case the mouse event was handled here
     */
    private boolean handleAttack(final MouseEvent event) {
        if (!CombatHandler.getInstance().isCombatMode()) {
            return false;
        }

        if (event.getKey() != java.awt.event.MouseEvent.BUTTON1) {
            return false;
        }

        if (event.getEvent() != MouseEvent.EVENT_KEY_CLICK) {
            return false;
        }

        final MapTile clickedTile =
            Game.getMap().getComponentAt(event.getPosX(), event.getPosY());

        if (clickedTile == null) {
            return false;
        }

        final Char targetChar =
            Game.getPeople().getCharacterAt(clickedTile.getLocation());

        if (targetChar == null) {
            return false;
        }

        // don't attack NPCs
        if (targetChar.isNPC()) {
            return true;
        }

        // don't attack yourself
        if (Game.getPlayer().isPlayer(targetChar.getCharId())) {
            return false;
        }

        CombatHandler.getInstance().setAttackTarget(targetChar);
        return true;
    }

    /**
     * Handle a dragging event of some object.
     * 
     * @param event the mouse event that needs to be checked
     * @return <code>true</code> in case the event was handled,
     *         <code>false</code> in case its still free for something else
     */
    private boolean handleDrag(final MouseEvent event) {
        if (event.getKey() != java.awt.event.MouseEvent.BUTTON1) {
            return false;
        }

        if (event.getEvent() == MouseEvent.EVENT_DRAG_START) {
            if (DraggingDecoder.getInstance().isDragging()) {
                DraggingDecoder.getInstance().reset();
            }
            if (!ScreenLocation.isInUseRange(event.getPosX(), event.getPosY())) {
                return false;
            }

            final MapTile draggedTile =
                Game.getMap().getComponentAt(event.getPosX(), event.getPosY());

            if (draggedTile == null) {
                return false;
            }

            final Location playerLoc = Game.getPlayer().getLocation();

            if (playerLoc.isNeighbour(draggedTile.getLocation())) {
                final Char draggedChar =
                    Game.getPeople().getCharacterAt(draggedTile.getLocation());
                if (draggedChar != null) {
                    final CharReference ref = new CharReference();
                    ref.setReferringCharacter(draggedChar.getCharId());
                    DraggingDecoder.getInstance().setDragStart(ref);
                    return true;
                }

                final Item draggedItem = draggedTile.getTopItem();
                if (draggedItem != null) {
                    if (!draggedItem.isMovable()) {
                        return false;
                    }
                    GUI.getInstance().getMouseCursor()
                        .attachImage(draggedItem.getSprite());
                    final MapReference ref = new MapReference();
                    ref.setReferringTile(draggedTile);
                    DraggingDecoder.getInstance().setDragStart(ref);
                    return true;
                }
            }
        } else if (event.getEvent() == MouseEvent.EVENT_DRAG_END) {
            if (!DraggingDecoder.getInstance().isDragging()) {
                return false;
            }

            final MapTile draggedTile =
                Game.getMap().getComponentAt(event.getPosX(), event.getPosY());

            if (draggedTile == null) {
                return false;
            }

            GUI.getInstance().getMouseCursor().attachImage(null);

            final MapReference ref = new MapReference();
            ref.setReferringTile(draggedTile);
            DraggingDecoder.getInstance().setDragEnd(ref);
            DraggingDecoder.getInstance().execute();
            return true;
        }
        return false;
    }

    /**
     * Check if the mouse event is a LookAtItem command
     * 
     * @param event the mouse event that needs to be checked
     * @return <code>true</code> in case the event was handled,
     *         <code>false</code> in case its still free for something else
     */
    private boolean handleLookAtTile(final MouseEvent event) {
        if (event.getKey() != java.awt.event.MouseEvent.BUTTON1) {
            return false;
        }

        if (event.getEvent() != MouseEvent.EVENT_KEY_CLICK) {
            return false;
        }

        final MapTile clickedTile =
            Game.getMap().getComponentAt(event.getPosX(), event.getPosY());

        if (clickedTile == null) {
            return false;
        }

        final MapReference ref = new MapReference();
        ref.setReferringTile(clickedTile);

        if (Game.getPeople().getCharacterAt(clickedTile.getLocation()) != null) {
            final LookatCharCmd cmd =
                (LookatCharCmd) CommandFactory.getInstance().getCommand(
                    CommandList.CMD_LOOKAT_CHAR);
            final Char thisChar =
                Game.getPeople().getCharacterAt(clickedTile.getLocation());
            cmd.examine(thisChar.getCharId(), 1);
            cmd.send();
            return true;
        } else {
            final LookatTileCmd cmd =
                (LookatTileCmd) CommandFactory.getInstance().getCommand(
                    CommandList.CMD_LOOKAT_TILE);
            cmd.setPosition(clickedTile.getLocation());
            cmd.send();
            return true;
        }
    }

    /**
     * Check if the mouse event is a move to command.
     * 
     * @param event the mouse event that needs to be checked
     * @return <code>true</code> in case the event was handled,
     *         <code>false</code> in case its still free for something else
     */
    private boolean handleMoveTo(final MouseEvent event) {
        if (event.getKey() != java.awt.event.MouseEvent.BUTTON3) {
            return false;
        }

        if (event.getEvent() == MouseEvent.EVENT_KEY_DBLCLICK) {
            final MapTile clickedTile =
                Game.getMap().getComponentAt(event.getPosX(), event.getPosY());
            if (clickedTile != null) {
                Game.getPlayer().getMovementHandler()
                    .walkTo(clickedTile.getLocation());
            }
            return true;
        }
        return false;
    }

    /**
     * Check if the mouse event is a use command
     * 
     * @param event the mouse event that needs to be checked
     * @return <code>true</code> in case the event was handled,
     *         <code>false</code> in case its still free for something else
     */
    private boolean handleUse(final MouseEvent event) {
        if (event.getKey() != java.awt.event.MouseEvent.BUTTON1) {
            return false;
        }

        if (event.getEvent() == MouseEvent.EVENT_KEY_DBLCLICK) {
            if (!ScreenLocation.isInUseRange(event.getPosX(), event.getPosY())) {
                return false;
            }

            final MapTile clickedTile =
                Game.getMap().getComponentAt(event.getPosX(), event.getPosY());

            if (clickedTile == null) {
                return false;
            }

            final Location playerLoc = Game.getPlayer().getLocation();

            if (playerLoc.isNeighbour(clickedTile.getLocation())) {
                final MapReference ref = new MapReference();
                ref.setReferringTile(clickedTile);
                final UseCmd cmd =
                    (UseCmd) CommandFactory.getInstance().getCommand(
                        CommandList.CMD_USE);
                cmd.addUse(ref);
                cmd.send();
                return true;
            }
        }
        return false;
    }

    /**
     * Handle a dragging event that causes a walking after the mouse.
     * 
     * @param event the mouse event that needs to be checked
     * @return <code>true</code> in case the event was handled,
     *         <code>false</code> in case its still free for something else
     */
    private boolean handleWalkAfterMouse(final MouseEvent event) {
        if ((event.getEvent() == MouseEvent.EVENT_DRAG_START)
            && (event.getKey() == java.awt.event.MouseEvent.BUTTON3)) {
            walkingAfterMouse = true;
            GUI.getInstance().requestExclusiveMouse(this);
            Game.getPlayer().getMovementHandler()
                .walkTowards(event.getPosX(), event.getPosY());
            return true;
        }

        if (walkingAfterMouse) {
            if (event.getEvent() == MouseEvent.EVENT_LOCATION) {
                GUI.getInstance().requestExclusiveMouse(this);
                Game.getPlayer().getMovementHandler()
                    .walkTowards(event.getPosX(), event.getPosY());
                return true;
            }

            if (event.getEvent() == MouseEvent.EVENT_DRAG_END) {
                Game.getPlayer().getMovementHandler().stopWalkTowards();
                GUI.getInstance().requestExclusiveMouse(null);
                walkingAfterMouse = false;
                return true;
            }
        }
        return false;
    }
}

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
package illarion.client.net;

/**
 * Utility class that contains all constants for the client commands and the
 * server messages.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class CommandList {
    /**
     * Client command to start attacking another character.
     */
    public static final int CMD_ATTACK = 0xFA;

    /**
     * Client command to cast a spell on the map, at the own character or at a
     * item in the inventory or in a showcase.
     */
    public static final int CMD_CAST = 0xFD;

    /**
     * Client command to close one of the showcases.
     */
    public static final int CMD_CLOSE_SHOWCASE = 0xE9;

    /**
     * Client command to drag a item from a inventory slot to another inventory
     * slot.
     */
    public static final int CMD_DRAG_INV_INV = 0xE3;

    /**
     * Client command to drag a item from a inventory slot to a position on the
     * game map.
     */
    public static final int CMD_DRAG_INV_MAP = 0xE4;

    /**
     * Client command to drag an item from a inventory slot to a container.
     */
    public static final int CMD_DRAG_INV_SC = 0xE1;

    /**
     * Client command to drag a item from a position on the game map into the
     * inventory.
     */
    public static final int CMD_DRAG_MAP_INV = 0xE5;

    /**
     * Client command to drag a item located east of the player character to
     * another position on the game map.
     */
    public static final int CMD_DRAG_MAP_MAP_E = 0x22;

    /**
     * Client command to drag a item located north of the player character to
     * another position on the game map.
     */
    public static final int CMD_DRAG_MAP_MAP_N = 0x20;

    /**
     * Client command to drag a item located northeast of the player character
     * to another position on the game map.
     */
    public static final int CMD_DRAG_MAP_MAP_NE = 0x21;

    /**
     * Client command to drag a item located northwest of the player character
     * to another position on the game map.
     */
    public static final int CMD_DRAG_MAP_MAP_NW = 0x27;

    /**
     * Client command to drag a item located south of the player character to
     * another position on the game map.
     */
    public static final int CMD_DRAG_MAP_MAP_S = 0x24;

    /**
     * Client command to drag a item located southeast of the player character
     * to another position on the game map.
     */
    public static final int CMD_DRAG_MAP_MAP_SE = 0x23;

    /**
     * Client command to drag a item located southwest of the player character
     * to another position on the game map.
     */
    public static final int CMD_DRAG_MAP_MAP_SW = 0x25;

    /**
     * Client command to drag a item located west of the player character to
     * another position on the game map.
     */
    public static final int CMD_DRAG_MAP_MAP_W = 0x26;

    /**
     * Client command to drag an item located below the player character to
     * another position on the game map.
     */
    public static final int CMD_DRAG_MAP_MAP_ZERO = 0x2A; // NO_UCD

    /**
     * Client command to drag a item from a position on the game map into a
     * container.
     */
    public static final int CMD_DRAG_MAP_SC = 0xE6;

    /**
     * Client command to drag a item from a container into the inventory.
     */
    public static final int CMD_DRAG_SC_INV = 0xE2;

    /**
     * Client command to drag a item from a container to a location on the game
     * map.
     */
    public static final int CMD_DRAG_SC_MAP = 0xE8;

    /**
     * Client command to drag an item from a container to another container.
     */
    public static final int CMD_DRAG_SC_SC = 0xE7;

    /**
     * Client command to introduce the character to other characters around.
     */
    public static final int CMD_INTRODUCE = 0xF6;

    /**
     * Client command to tell the server that the connection between client and
     * server is still active.
     */
    public static final int CMD_KEEPALIVE = 0xD8; // NO_UCD

    /**
     * Client command to send the login informations.
     */
    public static final int CMD_LOGIN = 0x0D;

    /**
     * Client command to tell the server to logout the character.
     */
    public static final int CMD_LOGOFF = 0xF1;

    /**
     * Client command to look at a character on the map.
     */
    public static final int CMD_LOOKAT_CHAR = 0x18;

    /**
     * Client command to look at a item in the inventory.
     */
    public static final int CMD_LOOKAT_INV = 0xDF;

    /**
     * Client command to look at a item in the menu.
     */
    public static final int CMD_LOOKAT_MENU = 0xDC;

    /**
     * Client command to look at a item in a showcase.
     */
    public static final int CMD_LOOKAT_SHOWCASE = 0xE0;

    /**
     * Client command to look at a item on a tile.
     */
    public static final int CMD_LOOKAT_TILE = 0xFF;

    /**
     * Client command to send the map dimensions the client needs.
     */
    public static final int CMD_MAPDIMENSION = 0xA0;

    /**
     * Client command to move a character. Either the own one or pushing the
     * character of someone else.
     */
    public static final int CMD_MOVE = 0x10;

    /**
     * Client command to open the container in the bag slot of the inventory.
     */
    public static final int CMD_OPEN_BAG = 0xEB;

    /**
     * Client command to open a container on the game map.
     */
    public static final int CMD_OPEN_MAP = 0xEC;

    /**
     * Client command to open a container in one of the showcases.
     */
    public static final int CMD_OPEN_SHOWCASE = 0xEA;

    /**
     * Client command to request the appearance of another yet unknown
     * character.
     */
    public static final int CMD_REQUEST_APPEARANCE = 0x0E;

    /**
     * Client command to send a spoken text.
     */
    public static final int CMD_SAY = 0xF5;

    /**
     * Client command to send a shouted text.
     */
    public static final int CMD_SHOUT = 0xF4;

    /**
     * Client command to stop attacking.
     */
    public static final int CMD_STAND_DOWN = 0xDE;

    /**
     * Client command to turn the player character east.
     */
    public static final int CMD_TURN_E = 0x42; // NO_UCD

    /**
     * Client command to turn the player character north.
     */
    public static final int CMD_TURN_N = 0x40;

    /**
     * Client command to turn the player character north east.
     */
    public static final int CMD_TURN_NE = 0x41; // NO_UCD

    /**
     * Client command to turn the player character north west.
     */
    public static final int CMD_TURN_NW = 0x47; // NO_UCD

    /**
     * Client command to turn the player character south.
     */
    public static final int CMD_TURN_S = 0x44; // NO_UCD

    /**
     * Client command to turn the player character south east.
     */
    public static final int CMD_TURN_SE = 0x43; // NO_UCD

    /**
     * Client command to turn the player character south west.
     */
    public static final int CMD_TURN_SW = 0x45; // NO_UCD

    /**
     * Client command to turn the player character west.
     */
    public static final int CMD_TURN_W = 0x46; // NO_UCD
    
    /**
     * Client command that is used to anser a text request.
     */
    public static final int CMD_TEXT_RESPONSE = 0x50;

    /**
     * Client command to perform a use action of one or two items on different
     * locations.
     */
    public static final int CMD_USE = 0xFE;

    /**
     * Client command to send a whispered text.
     */
    public static final int CMD_WHISPER = 0xF3;

    /**
     * Server message that contains the appearance data of a character.
     */
    public static final int MSG_APPEARANCE = 0xE1;

    /**
     * Server message that the player attacks a target character.
     */
    public static final int MSG_ATTACK = 0xBB;

    /**
     * Server message that contains a attribute of the player character.
     */
    public static final int MSG_ATTRIBUTE = 0xB9;

    /**
     * Server message to show a specific book.
     */
    public static final int MSG_BOOK = 0xCD;

    /**
     * Server message to change a specified item on a tile.
     */
    public static final int MSG_CHANGE_ITEM = 0xD9;

    /**
     * Server message that contains a data of a animation the character has to
     * show.
     */
    public static final int MSG_CHARACTER_ANIMATION = 0xCB;

    /**
     * Server message that closes a showcase in the client window.
     */
    public static final int MSG_CLOSE_SHOWCASE = 0xC4; //

    /**
     * Server message that contains the informations about the current date and
     * time.
     */
    public static final int MSG_DATETIME = 0xB6;

    /**
     * Server message that current connection get canceled.
     */
    public static final int MSG_DISCONNECT = 0xCC;

    /**
     * Server message that contains the data of a graphical effect that shall be
     * played instantly.
     */
    public static final int MSG_GRAPHIC_FX = 0xC9; // NO_UCD

    /**
     * Server message that contains the name another character introduced with.
     */
    public static final int MSG_INTRODUCE = 0xD4;

    /**
     * Server message that contains the items in the slot of the characters
     * inventory.
     */
    public static final int MSG_INVENTORY = 0xC1;

    /**
     * Server message that updates the location of the player character on the
     * game map.
     */
    public static final int MSG_LOCATION = 0xBD;

    /**
     * Server message that contains the data of a lookat event on a character.
     */
    public static final int MSG_LOOKAT_CHAR = 0x18;

    /**
     * Server message that contains the data of a lookat event on a inventory
     * slot.
     */
    public static final int MSG_LOOKAT_INV = 0xBE;

    /**
     * Server message that contains the data of a lookat event on a showcase
     * slot.
     */
    public static final int MSG_LOOKAT_SHOWCASE = 0xBF;

    /**
     * Server message that contains the data of a lookat event on a map tile.
     */
    public static final int MSG_LOOKAT_TILE = 0xC0;

    /**
     * Server message that contains the magic flags of the player character.
     */
    public static final int MSG_MAGIC_FLAG = 0xB8;

    /**
     * Server message to mark that a full map update is transfered completly.
     */
    public static final int MSG_MAP_COMPLETE = 0xA2;

    /**
     * Server message that contains a map stripe. So all tiles along a stipe and
     * the item on the tiles.
     */
    public static final int MSG_MAP_STRIPE = 0xA1;

    /**
     * Server message that sends the contents of a menu.
     */
    public static final int MSG_MENU = 0xBC;

    /**
     * Server message for moving a character. That could be the player character
     * or another character. This message handles pushing and walking movements.
     */
    public static final int MSG_MOVE = 0xDF;

    /**
     * Server message that says what track of the background music shall be
     * played right now.
     */
    public static final int MSG_MUSIC = 0xC8;

    /**
     * Server message that sends the character ID of the player character.
     */
    public static final int MSG_PLAYER_ID = 0xCA;

    /**
     * Server message to place a new item upon a tile.
     */
    public static final int MSG_PUT_ITEM = 0xC2;

    /**
     * Server message to remove a character from the screen.
     */
    public static final int MSG_REMOVE_CHAR = 0xE2;

    /**
     * Server message to remove the top item on a tile.
     */
    public static final int MSG_REMOVE_ITEM = 0xC3;

    /**
     * Server message that contains a spoken text or a emote.
     */
    public static final int MSG_SAY = 0xD7;

    /**
     * Server message that contains a shouted text.
     */
    public static final int MSG_SHOUT = 0xD6;

    /**
     * Server message that sends the contents of a showcase.
     */
    public static final int MSG_SHOWCASE = 0xC5; //

    /**
     * Server message that contains a skill of the player character.
     */
    public static final int MSG_SKILL = 0xD1;

    /**
     * Server message that contains the data of a sound effect that shall be
     * played instantly.
     */
    public static final int MSG_SOUND_FX = 0xC7;

    /**
     * Server message that the target of a attack is lost due death or escape
     * and the player character needs to stand down.
     */
    public static final int MSG_TARGET_LOST = 0xBA;

    /**
     * Server message to turn the character into a specified direction.
     */
    public static final int MSG_TURN_CHAR = 0xE0;
    
    /**
     * Server message to request a text from the player.
     */
    public static final int MSG_TEXT_REQUEST = 0x50;

    /**
     * Server message that contains a full updated list of all items on a tile.
     */
    public static final int MSG_UPDATE_ITEMS = 0x19;

    /**
     * Server message that contains the data of the current weather in the game.
     */
    public static final int MSG_WEATHER = 0xB7;

    /**
     * Server message that contains a whispered text.
     */
    public static final int MSG_WHISPER = 0xD5;

    /**
     * The default size of the header of each command and each message in byte.
     */
    protected static final int HEADER_SIZE = 6;

    /**
     * Private constructor of the class so nothing can create a instance of this
     * utility class.
     */
    private CommandList() {
        // blocking constructor
    }
}

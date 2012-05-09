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
package illarion.client;

/**
 * A list of the available debug flags the client knows.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum Debug {
    /**
     * Enable the deadlock debugger. If this is activated the theads of the
     * client are checked from time to time for deadlocks.
     */
    deadlock,

    /**
     * Write the debug data to a log file. If this debug flag is not set the
     * debug data is written to the console.
     */
    log,

    /**
     * Show the layout of the map. This will cause the the map is rendered at
     * half size and the gui won't be displayed. Instead there will be markers
     * that show the size of the map that is visible and the parts that would be
     * clipped usually.
     */
    mapLayout,

    /**
     * Draw a marker on each texture that shows its borders and its center
     * position. This is used to check of the positions of the sprites are used
     * correctly.
     */
    markup,

    /**
     * Debug the network communication entirely, means all bytes that are send
     * and received are printed to the log.
     */
    net,

    /**
     * Disable the rendering of the mini map and of the world map.
     */
    noMap,

    /**
     * Debug the OpenGL parts of the game and show all events that are done
     * using OpenGL and print out the errors OpenGL sends to the client.
     */
    openGL,

    /**
     * Debug the protocol, this causes that all commands that are received and
     * send are written along with their parameters into the log output.
     */
    protocol,

    /**
     * Debug the renderer that updates the game screen. This option will show the areas of the map that receive
     * updates.
     */
    mapRenderer;
}

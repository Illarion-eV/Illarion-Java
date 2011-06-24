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
package illarion.client;

/**
 * A list of the available debug flags the client knows.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public enum Debug {
    /**
     * Enable the deadlock debugger. If this is activated the theads of the
     * client are checked from time to time for deadlocks.
     * <p>
     * Value: <i>256</i>
     * </p>
     */
    deadlock, // NO_UCD

    /**
     * Write the debug data to a log file. If this debug flag is not set the
     * debug data is written to the console.
     * <p>
     * Value: <i>1</i>
     * </p>
     */
    log,

    /**
     * Show the layout of the map. This will cause the the map is rendered at
     * half size and the gui won't be displayed. Instead there will be markers
     * that show the size of the map that is visible and the parts that would be
     * clipped usually.
     * <p>
     * Value: <i>128</i>
     * </p>
     */
    mapLayout, // NO_UCD

    /**
     * Draw a marker on each texture that shows its borders and its center
     * position. This is used to check of the positions of the sprites are used
     * correctly.
     * <p>
     * Value: <i>4</i>
     * </p>
     */
    markup,

    /**
     * Debug the network communication entirely, means all bytes that are send
     * and received are printed to the log.
     * <p>
     * Value: <i>64</i>
     * </p>
     */
    net, // NO_UCD

    /**
     * Disable the rendering of the mini map and of the world map.
     * <p>
     * Value: <i>32</i>
     * </p>
     */
    noMap, // NO_UCD

    /**
     * Debug the OpenGL parts of the game and show all events that are done
     * using OpenGL and print out the errors OpenGL sends to the client.
     * <p>
     * Value: <i>16</i>
     * </p>
     */
    openGL,

    /**
     * Disable the preloading functions of the client. The client will only load
     * the data in case they are needed.
     * <p>
     * Value: <i>2</i>
     * </p>
     */
    preloadOff,

    /**
     * Debug the protocol, this causes that all commands that are received and
     * send are written along with their parameters into the log output.
     * <p>
     * Value: <i>8</i>
     * </p>
     */
    protocol;
}

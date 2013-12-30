/*
 * This file is part of the client.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

/**
 * This class is used to measure the performance of the connection to the server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ConnectionPerformanceClock {
    /**
     * The last time the ping command was handed over to NetComm.
     */
    private static long lastSendToNetComm = -1;

    /**
     * The last time the command was encoded to the network interface.
     */
    private static long lastEncode = -1;

    /**
     * The last measured time the server took to respond to a command.
     */
    private static long lastServerTime = -1;

    /**
     * The last measured time the command required to pass though NetComm, the server and the publishing queue.
     */
    private static long lastNetCommTime = -1;

    public static boolean isReadyForNewPing() {
        if (lastSendToNetComm == -1) {
            return true;
        }
        return (System.currentTimeMillis() - lastEncode) >= 10000;
    }

    /**
     * Report that the command is just about to be handed over to the network interface.
     */
    public static void notifySendToNetComm() {
        if (lastSendToNetComm == -1) {
            lastSendToNetComm = System.currentTimeMillis();
        }
    }

    /**
     * Report that the command is now encoded to the network device and send to the server.
     */
    public static void notifyNetCommEncode() {
        if (lastEncode == -1) {
            lastEncode = System.currentTimeMillis();
        }
    }

    /**
     * Report that the command was received from the server and is now decoded.
     */
    public static void notifyNetCommDecode() {
        final long localLastEncode = lastEncode;
        lastEncode = -1;
        if (localLastEncode > -1) {
            lastServerTime = System.currentTimeMillis() - localLastEncode;
        }
    }

    /**
     * Report that the command is now published to the client.
     */
    public static void notifyPublishToClient() {
        final long localLastSendToNetComm = lastSendToNetComm;
        lastSendToNetComm = -1;
        if (localLastSendToNetComm > -1) {
            lastNetCommTime = System.currentTimeMillis() - localLastSendToNetComm;
        }
    }

    /**
     * Reset all recoded times.
     */
    public static void reset() {
        lastSendToNetComm = -1;
        lastEncode = -1;
        lastServerTime = -1;
        lastNetCommTime = -1;
    }

    /**
     * Get the time in milliseconds the server took to respond to the measurement command.
     *
     * @return the server response time in milliseconds
     */
    public static long getServerPing() {
        return lastServerTime;
    }

    /**
     * Get the time in milliseconds a command took to pass through NetComm and the server.
     *
     * @return the total processing time of a command in milliseconds
     */
    public static long getNetCommPing() {
        return lastNetCommTime;
    }
}

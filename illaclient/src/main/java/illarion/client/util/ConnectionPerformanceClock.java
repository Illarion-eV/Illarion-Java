/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.util;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * This class is used to measure the performance of the connection to the server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConnectionPerformanceClock {
    /**
     * The last time the ping command was handed over to NetComm.
     */
    private static long lastSendToNetComm;

    /**
     * The last time the command was encoded to the network interface.
     */
    private static long lastEncode;

    /**
     * The last measured time the server took to respond to a command.
     */
    private static long lastServerTime;

    /**
     * The last measured time the command required to pass though NetComm, the server and the publishing queue.
     */
    private static long lastNetCommTime;

    @Nonnull
    private static final long[] lastServerTimes = new long[16];
    private static final long[] lastNetCommTimes = new long[16];
    private static int serverTimesCursor;
    private static int netCommTimesCursor;

    static {
        reset();
    }

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
        long localLastEncode = lastEncode;
        lastEncode = -1;
        if (localLastEncode > -1) {
            lastServerTime = System.currentTimeMillis() - localLastEncode;
            lastServerTimes[serverTimesCursor] = lastServerTime;
            serverTimesCursor = (serverTimesCursor + 1) % lastServerTimes.length;
        }
    }

    /**
     * Report that the command is now published to the client.
     */
    public static void notifyPublishToClient() {
        long localLastSendToNetComm = lastSendToNetComm;
        lastSendToNetComm = -1;
        if (localLastSendToNetComm > -1) {
            lastNetCommTime = System.currentTimeMillis() - localLastSendToNetComm;
            lastNetCommTimes[netCommTimesCursor] = lastServerTime;
            netCommTimesCursor = (netCommTimesCursor + 1) % lastNetCommTimes.length;
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
        Arrays.fill(lastServerTimes, 0);
        Arrays.fill(lastNetCommTimes, 0);
        serverTimesCursor = 0;
        netCommTimesCursor = 0;
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

    public static long getMaxServerPing() {
        long max = 0L;
        for (long lastServerTime : lastServerTimes) {
            if (lastServerTime > max) {
                max = lastServerTime;
            }
        }
        return max;
    }
}

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
package illarion.client.guiNG.messages;

/**
 * This message is a message fired by a window to notify its children that the
 * window got opened or closed.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class WindowMessage implements Message {
    /**
     * This is the type of message in case the window got closed.
     */
    public static final int WINDOW_CLOSED = 0;

    /**
     * This is the type of the message in case the window got opened.
     */
    public static final int WINDOW_OPENED = 1;

    /**
     * The buffered instances of the window message.
     */
    private static final WindowMessage[] BUFFER = new WindowMessage[2];

    /**
     * The type of this message. Valid values are {@link #WINDOW_CLOSED} and
     * {@link #WINDOW_OPENED}.
     */
    private final int messageType;

    /**
     * Private constructor to ensure that only the buffered instances of this
     * messages are used.
     * 
     * @param type the message type stored in this window message instance
     */
    private WindowMessage(final int type) {
        messageType = type;
    }

    /**
     * Get a instance of the window message with the message type as requested
     * with this function.
     * 
     * @param type the message type of the message that is requested
     * @return the message instance with the message type needed
     */
    @SuppressWarnings("nls")
    public static WindowMessage getInstance(final int type) {
        if ((type < WINDOW_CLOSED) || (type > WINDOW_OPENED)) {
            throw new IllegalArgumentException("Illegal Window Message type");
        }

        if (BUFFER[type] == null) {
            BUFFER[type] = new WindowMessage(type);
        }
        return BUFFER[type];
    }

    /**
     * The message types contains the actual value of this window message.
     * Either its a window close message, then {@link #WINDOW_CLOSED} is
     * returned or its a window open message, then {@link #WINDOW_OPENED} is
     * returned.
     * 
     * @return the message value
     */
    public int getMessageType() {
        return messageType;
    }
}

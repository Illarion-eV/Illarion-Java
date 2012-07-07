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
package illarion.client.net.server.events;

/**
 * This is the abstract implementation of a event that is triggered once a inform message was received by the server.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public abstract class AbstractInformReceivedEvent {
    /**
     * The message that was transmitted by the inform.
     */
    private final String message;

    /**
     * Create a new instance of this class and set the message that got send by the inform.
     *
     * @param informMessage the message send by the inform
     */
    protected AbstractInformReceivedEvent(final String informMessage) {
        message = informMessage;
    }

    /**
     * Get the message that was send as inform.
     *
     * @return the message of the inform
     */
    public String getMessage() {
        return message;
    }
}

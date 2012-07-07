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
 * This event is triggered in case the client received a inform message that was issued by a text to.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TextToInformReceivedEvent extends AbstractInformReceivedEvent {
    /**
     * Create a new instance of this class and set the message that got send by the inform.
     *
     * @param informMessage the message send by the inform
     */
    public TextToInformReceivedEvent(final String informMessage) {
        super(informMessage);
    }
}

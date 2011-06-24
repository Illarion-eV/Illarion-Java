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
package illarion.client.util;

/**
 * A session member is a class that is effected by the running sessions of the
 * client. This is used to properly start and end a game session.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface SessionMember {
    /**
     * This method is called once the client gets disconnected.
     */
    void endSession();

    /**
     * This method is called once when the client is started.
     */
    void initSession();

    /**
     * This method is called once the client quits entirely in order to free the
     * resources properly and save the needed data.
     */
    void shutdownSession();

    /**
     * This method is called once after the client was ordered to login in order
     * to prepare the game properly.
     */
    void startSession();
}

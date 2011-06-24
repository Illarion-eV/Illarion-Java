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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import illarion.common.util.StoppableStorage;

/**
 * This class to used to manage the sessions that are active in the client. It
 * takes care to properly load and unload the client.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class SessionManager {
    /**
     * This session state means the session is ready to be started.
     */
    public static final int INITIALIZED = 0;

    /**
     * This session state means the session was shut down. Its impossible to
     * start a new sessions.
     */
    public static final int QUIT = 2;

    /**
     * This session state means the session is currently active and running.
     */
    public static final int RUNNING = 1;

    /**
     * The singleton instance of this class.
     */
    private static final SessionManager INSTANCE = new SessionManager();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(SessionManager.class);

    /**
     * This variable to set true in case the session start to to be canceled.
     */
    private boolean cancelSessionStart;

    /**
     * The list of members that are handled by this manager.
     */
    private final List<SessionMember> members;

    /**
     * The state value of the session. This is used to determine if the session
     * is currently working properly.
     */
    private int sessionState;

    /**
     * Private constructor to ensure that only one instance is ever created.
     */
    private SessionManager() {
        members = new ArrayList<SessionMember>();
        sessionState = INITIALIZED;
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Add a session member to this manager. This will cause the initMember
     * function to be called right away. Also the member will be invoked from
     * now one when a session is started, stopped or the client quits.
     * 
     * @param newMember the member to add to this manager
     * @throws IllegalStateException in case a session is running or the manager
     *             was shut down
     * @throws IllegalArgumentException in case the newMember argument is
     *             <code>null</code> or already added to the list
     */
    @SuppressWarnings("nls")
    public void addMember(final SessionMember newMember) {
        if (sessionState != INITIALIZED) {
            throw new IllegalStateException(
                "Its invalid to add a member while a session was started or quit");
        }
        if (newMember == null) {
            throw new IllegalArgumentException("newMember must not be NULL");
        }
        if (members.contains(newMember)) {
            throw new IllegalArgumentException("Member already added.");
        }

        members.add(newMember);
        newMember.initSession();
    }

    /**
     * This causes that the start of a session is canceled and all members that
     * got already initialized are ended again.
     */
    public void cancelStart() {
        cancelSessionStart = true;
    }

    /**
     * End a session. Once this is done a new session can be started.
     * 
     * @throws IllegalStateException in case no session is running
     */
    public void endSession() {
        endSessionImpl(members.size() - 1);
    }

    /**
     * Shutdown the session manager. This should be part of the exit of the
     * client. It will cause all members to store their values and clean up.
     * 
     * @throws IllegalStateException in case the session manager was already
     *             shut down or a session is currently running
     */
    @SuppressWarnings("nls")
    public void shutdownSession() {
        if (sessionState != INITIALIZED) {
            throw new IllegalStateException(
                "Its invalid to shutdown a session while one is running or the session was quit already.");
        }

        sessionState = QUIT;

        final int count = members.size();
        for (int i = 0; i < count; ++i) {
            try {
                members.get(i).shutdownSession();
            } catch (final Exception ex) {
                LOGGER.fatal("Error while shutting down session for member: "
                    + members.get(i).toString(), ex);
                break;
            }
        }
        StoppableStorage.getInstance().shutdown();
    }

    /**
     * Start a new session for all members added to this manager.
     * 
     * @throws IllegalStateException in case no session is ready to be started
     */
    @SuppressWarnings("nls")
    public void startSession() {
        if (sessionState != INITIALIZED) {
            throw new IllegalStateException(
                "Its invalid to start a session while one was already started or the session was quit.");
        }
        cancelSessionStart = false;

        sessionState = RUNNING;

        final int count = members.size();
        for (int i = 0; i < count; ++i) {
            try {
                members.get(i).startSession();
            } catch (final Exception ex) {
                LOGGER.fatal("Error while starting session for member: "
                    + members.get(i).toString(), ex);
                break;
            }

            if (cancelSessionStart) {
                endSessionImpl(i);
                break;
            }
        }
    }

    /**
     * This implementation for ending a session takes as argument the first
     * member that shall be ended. All members before this one in the list are
     * ended also. that is needed in case the initialization of the session is
     * canceled.
     * 
     * @param start the first index that is ended.
     */
    @SuppressWarnings("nls")
    private void endSessionImpl(final int start) {
        if (sessionState != RUNNING) {
            throw new IllegalStateException(
                "Its invalid to end a session while none is running.");
        }

        sessionState = INITIALIZED;

        for (int i = start; i >= 0; --i) {
            try {
                members.get(i).endSession();
            } catch (final Exception ex) {
                LOGGER.fatal("Error while ending session for member: "
                    + members.get(i).toString(), ex);
                break;
            }
        }
    }
}

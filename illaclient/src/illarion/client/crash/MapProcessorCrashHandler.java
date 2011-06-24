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
package illarion.client.crash;

import illarion.client.world.Game;

/**
 * The crash handler that takes care for crashes of the map processor. It will
 * try to restart the processor in case it crashed.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MapProcessorCrashHandler extends AbstractCrashHandler {
    /**
     * The singleton instance of this crash handler to avoid to many instances
     * of this one.
     */
    private static final MapProcessorCrashHandler INSTANCE =
        new MapProcessorCrashHandler();

    /**
     * The private constructor that is used to avoid the creation of any other
     * instances but the singleton instance.
     */
    private MapProcessorCrashHandler() {
        super();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static MapProcessorCrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Get the message that describes the problem human readable.
     * 
     * @return the error message
     */
    @SuppressWarnings("nls")
    @Override
    protected String getCrashMessage() {
        return "crash.mapprocessor";
    }

    /**
     * Prepare everything for a proper restart of the map processor.
     * 
     * @return <code>true</code> in case a restart of the connection is needed
     */
    @Override
    protected boolean restart() {
        Game.getMap().restartMapProcessor();
        Game.getMap().checkInside();

        return false;
    }
}

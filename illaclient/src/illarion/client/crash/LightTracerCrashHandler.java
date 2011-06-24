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
 * This crash handler takes care for crashes of the light tracer. In case a
 * crash happens its tried to restart the tracer and the game.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class LightTracerCrashHandler extends AbstractCrashHandler {
    /**
     * The singleton instance of this crash handler to avoid to many instances
     * of this one.
     */
    private static final LightTracerCrashHandler INSTANCE =
        new LightTracerCrashHandler();

    /**
     * The private constructor that is used to avoid the creation of any other
     * instances but the singleton instance.
     */
    private LightTracerCrashHandler() {
        super();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static LightTracerCrashHandler getInstance() {
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
        return "crash.lighttracer";
    }

    /**
     * Prepare everything for a proper restart of the light tracer.
     * 
     * @return <code>true</code> in case a restart is needed
     */
    @Override
    protected boolean restart() {
        Game.getLights().saveShutdown();
        Game.getInstance().restartLight();
        Game.getLights().refresh();

        return false;
    }

}

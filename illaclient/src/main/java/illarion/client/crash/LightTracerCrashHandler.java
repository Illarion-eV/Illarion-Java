/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.crash;

import illarion.client.world.World;

import javax.annotation.Nonnull;

/**
 * This crash handler takes care for crashes of the light tracer. In case a
 * crash happens its tried to restart the tracer and the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LightTracerCrashHandler extends AbstractCrashHandler {
    /**
     * The singleton instance of this crash handler to avoid to many instances
     * of this one.
     */
    private static final LightTracerCrashHandler INSTANCE = new LightTracerCrashHandler();

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
    @Nonnull
    public static LightTracerCrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Get the message that describes the problem human readable.
     *
     * @return the error message
     */
    @Nonnull
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
        //World.getLights().saveShutdown();
        //Game.getInstance().restartLight();
        World.getLights().refresh();

        return false;
    }
}

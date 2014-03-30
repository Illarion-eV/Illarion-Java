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
 * The crash handler that takes care for crashes of the map processor. It will
 * try to restart the processor in case it crashed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MapProcessorCrashHandler extends AbstractCrashHandler {
    /**
     * The singleton instance of this crash handler to avoid to many instances
     * of this one.
     */
    private static final MapProcessorCrashHandler INSTANCE = new MapProcessorCrashHandler();

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
    @Nonnull
    public static MapProcessorCrashHandler getInstance() {
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
        return "crash.mapprocessor";
    }

    /**
     * Prepare everything for a proper restart of the map processor.
     *
     * @return <code>true</code> in case a restart of the connection is needed
     */
    @Override
    protected boolean restart() {
        World.getMap().restartMapProcessor();
        World.getMap().checkInside();

        return false;
    }
}

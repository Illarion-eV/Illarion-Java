/*
 * This file is part of the Illarion Sound Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Sound Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Sound Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Sound Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.sound.lwjgl;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;

import illarion.common.util.Stoppable;
import illarion.common.util.StoppableStorage;

/**
 * The purpose of this class is a central control of the sound playback using
 * OpenAL. It manages the context and offers all required informations to the
 * other parts of the playback system
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
final class SoundSystem implements Stoppable {
    /**
     * The singleton instance of this class.
     */
    private static final SoundSystem INSTANCE;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger(SoundSystem.class);
        INSTANCE = new SoundSystem();
    }

    /**
     * This flag is changed to <code>true</code> in case the Sound System is
     * working just fine.
     */
    private boolean working = false;

    /**
     * A private constructor to ensure that only one object of this class is
     * created.
     */
    @SuppressWarnings("nls")
    private SoundSystem() {
        if (AL.isCreated()) {
            return;
        }

        try {
            AL.create(null, 44100, 60, false, true);
            working = true;
            StoppableStorage.getInstance().add(this);
        } catch (final LWJGLException e) {
            LOGGER.error("Failed creating OpenAL System", e);
        }
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static SoundSystem getInstance() {
        return INSTANCE;
    }

    /**
     * Check if the sound system is setup correctly and fine for some playback.
     * 
     * @return <code>true</code> in case everything is ready for playback.
     */
    public boolean isWorking() {
        return working;
    }

    /**
     * Take care for a proper shutdown of the sound system.
     */
    @Override
    public void saveShutdown() {
        if (AL.isCreated()) {
            AL.destroy();
        }
    }
}

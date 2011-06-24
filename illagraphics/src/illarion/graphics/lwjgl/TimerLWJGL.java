/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.lwjgl;

import org.apache.log4j.Logger;
import org.lwjgl.Sys;

import illarion.common.util.FastMath;

/**
 * The timer is used to calculate the frames per second and the time between
 * frames in the fastest way possible. Also its used to slow the main loop down
 * to the expected frames per second.
 * <p>
 * This implementation of the timer uses the LWJGL System functions to get the
 * proper values.
 * </p>
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class TimerLWJGL {

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(TimerLWJGL.class);

    /**
     * The value stores over how many values the calculated values shall be
     * smoothed and order to get a smooth running game.
     */
    private static final int TIMER_SMOOTH = 16;

    /**
     * This variable stores the flag that says if the smoothing values are
     * filled completely with valid values or not.
     */
    private boolean allSmoothed = false;

    /**
     * The inverted resolution value multiplied with the smooth factor. This
     * value is used during the update function.
     */
    private float invResolutionSmooth;

    /**
     * The last calculated frames per second.
     */
    private int lastFPS;

    /**
     * The last time difference between two frames. This value is in Timer
     * Ticks.
     */
    private long lastFrameDiff;

    /**
     * The length of the last frame in milliseconds
     */
    private long lastFrameMS = 0;

    /**
     * The time when the last call of the {@link #sync()} method took place.
     */
    private long lastSyncTick;

    /**
     * The last calculated time per frame.
     */
    private float lastTPF;

    /**
     * The time that was fetched at the last update.
     */
    private long oldTime;

    /**
     * The index in the smoothing values list that shall be filled at the next
     * update of the timer.
     */
    private int smoothIndex;

    /**
     * The FPS value the sync function shall slow the main thread down to.
     */
    private int targetFPS = 60;

    /**
     * The amount of ticks needed per Frame to archive the expected frame rate.
     */
    private long ticksPerFrame;

    /**
     * The value that has to be multiplied to a amount of ticks to get the time
     * in milliseconds.
     */
    private float ticksToMilliseconds;

    /**
     * The storage for the values that are used to smooth the frames per second
     * and the time per frame value.
     */
    private final long[] tpf = new long[TIMER_SMOOTH];

    /**
     * Construct a new instance of this timer.
     */
    public TimerLWJGL() {
        reset();
    }

    /**
     * Get the last calculated value for frames per second.
     * 
     * @return the last calculated frames per second value
     */
    public int getFrameRate() {
        return lastFPS;
    }

    /**
     * Get the last calculated value for the time between 2 frames.
     * 
     * @return the last calculated value for the time between frames
     */
    public float getTimePerFrame() {
        return lastFrameMS;
    }

    /**
     * Reset the timer to its original values.
     */
    public void reset() {
        lastFrameDiff = 0;
        lastFPS = 0;
        lastTPF = 0;

        oldTime = -1;

        smoothIndex = TIMER_SMOOTH - 1;
        allSmoothed = false;

        for (int i = TIMER_SMOOTH; --i >= 0;) {
            tpf[i] = -1;
        }

        ticksToMilliseconds = 1000.f / Sys.getTimerResolution();
        invResolutionSmooth =
            (1.f / (Sys.getTimerResolution() * TIMER_SMOOTH));
    }

    /**
     * Set the value of frames per second that shall be reached using the sync
     * method in every loop.
     * 
     * @param fps the new FPS value that is the target
     */
    public void setFPS(final int fps) {
        targetFPS = fps;
        ticksPerFrame =
            Math.round((float) Sys.getTimerResolution() / (float) targetFPS);
    }

    /**
     * Slow down the main thread to archive the FPS value set to this timer.
     */
    @SuppressWarnings("nls")
    public void sync() {
        Sys.getTimerResolution();
        if (lastSyncTick == -1) {
            lastSyncTick = Sys.getTime();
            return;
        }

        final long currentTicks = Sys.getTime();

        long frameDurationTicks = currentTicks - lastSyncTick;
        lastFrameMS = (long) (frameDurationTicks * ticksToMilliseconds);
        while (frameDurationTicks < ticksPerFrame) {
            final long sleepTime =
                (long) ((ticksPerFrame - frameDurationTicks) * ticksToMilliseconds);
            try {
                Thread.sleep(sleepTime);
            } catch (final InterruptedException exc) {
                LOGGER.warn("Interrupted while sleeping in fixed-framerate",
                    exc);
            }
            frameDurationTicks = Sys.getTime() - lastSyncTick;
        }

        Thread.yield();

        lastSyncTick = currentTicks;
    }

    /**
     * Update all values of the timer. It is expected that this function is
     * called every frame.
     */
    public void update() {
        final long timeRez = Sys.getTimerResolution();
        final long newTime = Sys.getTime();
        final long storedOldTime = oldTime;
        oldTime = newTime;
        if (storedOldTime == -1) {
            lastTPF = 1000.f / targetFPS;
            lastFPS = targetFPS;
            return;
        }

        long frameDiff = newTime - storedOldTime;
        final long storedLastFrameDiff = lastFrameDiff;
        if ((storedLastFrameDiff > 0)
            && (frameDiff > (storedLastFrameDiff * 100))) {
            frameDiff = storedLastFrameDiff * 100;
        }
        lastFrameDiff = frameDiff;
        tpf[smoothIndex] = frameDiff;
        smoothIndex--;
        if (smoothIndex < 0) {
            smoothIndex = TIMER_SMOOTH - 1;
        }

        lastTPF = 0.0f;
        if (!allSmoothed) {
            int smoothCount = 0;
            for (int i = tpf.length; --i >= 0;) {
                if (tpf[i] != -1) {
                    lastTPF += tpf[i];
                    smoothCount++;
                }
            }
            if (smoothCount == tpf.length) {
                allSmoothed = true;
            }
            lastTPF /= (timeRez * smoothCount);
        } else {
            for (int i = tpf.length; --i >= 0;) {
                lastTPF += tpf[i];
            }
            lastTPF *= invResolutionSmooth;
        }
        if (lastTPF < FastMath.FLT_EPSILON) {
            lastTPF = FastMath.FLT_EPSILON;
        }

        lastFPS = (int) (1.f / lastTPF);
        lastTPF *= 1000.f;
    }
}

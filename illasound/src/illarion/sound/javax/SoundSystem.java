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
package illarion.sound.javax;

import javolution.util.FastList;

import org.apache.log4j.Logger;

import illarion.common.util.FastMath;
import illarion.common.util.Location;
import illarion.common.util.Stoppable;
import illarion.common.util.StoppableStorage;

import illarion.sound.SoundSource;

/**
 * This is the sound system that takes care for the proper playback of all
 * sounds in the client. It triggers the updates of the sound source settings
 * and ensures that all input data is load properly.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
final class SoundSystem extends Thread implements Stoppable {
    /**
     * The singleton instance of the sound system.
     */
    private static final SoundSystem INSTANCE;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SoundSystem.class);

    static {
        INSTANCE = new SoundSystem();
        INSTANCE.start();
        StoppableStorage.getInstance().add(INSTANCE);
    }

    /**
     * This variable stores if the main loop shall wait for a update or run
     * again after a short time of waiting.
     */
    private boolean doAnotherRun = false;

    /**
     * The sound listener that is hearing all the sound that is send out.
     */
    private SoundListenerJAVAX listener;

    /**
     * The running flag. As long as this one is set to true the thread keeps
     * running.
     */
    private boolean running;

    /**
     * The list of activated and playing sound sources that require regular
     * updates.
     */
    private final FastList<SoundSourceJAVAX> sources;

    /**
     * Private constructor to ensure no instances being created.
     */
    @SuppressWarnings("nls")
    private SoundSystem() {
        super("Sound System - JavaX");
        super.setDaemon(false);
        running = true;
        sources = new FastList<SoundSourceJAVAX>();
        StoppableStorage.getInstance().add(this);
    }

    /**
     * Get the instance of the sound system that is used to maintain the sound
     * environment.
     * 
     * @return the singleton instance of the sound system.
     */
    public static SoundSystem getInstance() {
        return INSTANCE;
    }

    /**
     * Add a sound source to the sound system. This sound source will receive
     * updates while the sound system is running.
     * 
     * @param source the sound source that shall be added
     */
    public void addSoundSource(final SoundSourceJAVAX source) {
        if (sources.contains(source)) {
            return;
        }
        sources.add(source);
        triggerUpdate();
    }

    /**
     * Get the current sound listener that is activated in the sound system.
     * 
     * @return the current sound listener
     */
    public SoundListenerJAVAX getListner() {
        return listener;
    }

    /**
     * Test if a sound listener is the currently activated listener.
     * 
     * @param test the listener that shall be checked
     * @return <code>true</code> if the test listener and the activated listener
     *         are the same
     */
    public boolean isActiveListener(final SoundListenerJAVAX test) {
        if (test == null) {
            return false;
        }
        if (listener == null) {
            return false;
        }
        return test.equals(listener);
    }

    /**
     * Remove a sound source from the sound system. As soon as its removed it
     * won't receive any more updates.
     * 
     * @param source the sound source that shall be removed
     */
    public void removeSoundSource(final SoundSourceJAVAX source) {
        if (sources.contains(source)) {
            return;
        }
        sources.remove(source);
    }

    /**
     * The main loop of the sound system. The sound sources known to the sound
     * system are updated by this method in proper intervals. Also in case its
     * needed the recalculation of balance and volume is done here.
     */
    @SuppressWarnings("nls")
    @Override
    public void run() {
        while (running) {
            final SoundListenerJAVAX usedListener = listener;
            doAnotherRun = false;
            if (usedListener != null) {
                final boolean forcedUpdate = usedListener.isDirty();
                usedListener.setDirty(false);

                FastList.Node<SoundSourceJAVAX> currNode =
                    sources.head().getNext();
                SoundSourceJAVAX currSource = currNode.getValue();
                while (currSource != null) {
                    if (forcedUpdate || currSource.isDirty()) {
                        currSource.setDirty(false);

                        calculateVolume(usedListener, currSource);
                        calculateBalance(usedListener, currSource);
                    }
                    final boolean newRun = currSource.update();
                    doAnotherRun |= newRun;
                    currNode = currNode.getNext();
                    currSource = currNode.getValue();
                }
            }

            if (doAnotherRun) {
                try {
                    synchronized (this) {
                        this.wait(10);
                    }
                } catch (final InterruptedException e) {
                    LOGGER.error("Waiting time interrupted.");
                }
                continue;
            }

            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (final InterruptedException e) {
                LOGGER.error("Waiting time interrupted.");
            }
        }
    }

    /**
     * After calling this function the thread will die gracefully as soon as
     * possible and free its resources.
     */
    @Override
    public void saveShutdown() {
        running = false;
        triggerUpdate();
    }

    /**
     * Set a new sound listener. This sound listener will be the reference for
     * the sound playback from now on.
     * 
     * @param newListener the new sound listener
     */
    public void setListener(final SoundListenerJAVAX newListener) {
        if (listener != null) {
            if (listener.equals(newListener)) {
                return;
            }
            listener.cleanup();
        }
        listener = newListener;
    }

    /**
     * Ensure that the main loop does another run after the current one to
     * update any changes done.
     */
    public void triggerUpdate() {
        doAnotherRun = true;
        synchronized (this) {
            notify();
        }
    }

    /**
     * Calculate the angle between two vectors.
     * 
     * @param direction The direction of the first vector, the X and Y vector
     *            data is read from the location class using the 4 direction
     *            system.
     * @param x2 X-Coordinate of the point the second vector points to (start is
     *            0,0)
     * @param y2 Y-Coordinate of the point the second vector points to (start is
     *            0,0)
     * @param x2Length length of the second vector
     * @return the angle between the two described vectors
     */
    private float calculateAngle(final int direction, final float x2,
        final float y2, final float x2Length) {
        final int dirX = Location.getDirectionVectorX(direction);
        final int dirY = Location.getDirectionVectorY(direction);
        final float skalarProd = (dirX * x2) + (dirY * y2);
        final float quotient = (skalarProd / x2Length);

        return FastMath.acos(quotient) / FastMath.PI;
    }

    /**
     * Set the balance of a specified source.
     * 
     * @param list the sound listener that hears the sound
     * @param source the sound source that sends out the sound and thats balance
     *            is altered
     */
    private void calculateBalance(final SoundListenerJAVAX list,
        final SoundSourceJAVAX source) {
        float balance = 0.f;
        if (source.hasPos()) {
            final float dX = source.getPosX() - list.getPosX();
            final float dY = source.getPosY() - list.getPosY();
            int playerDir = list.getDirection();
            final float distance =
                FastMath.sqrt(FastMath.sqr(dX) + FastMath.sqr(dY));

            playerDir = (playerDir + 2) % Location.DIR_MOVE8;
            // calculate angle to right hand direction
            final float rightAngle =
                calculateAngle(playerDir, dX, dY, distance);

            playerDir = (playerDir + 4) % Location.DIR_MOVE8;
            // calculate angle to left hand direction
            final float leftAngle =
                calculateAngle(playerDir, dX, dY, distance);

            // calculate balance from angle
            balance = 0.f;
            // closer to right hand side: balance right
            if (rightAngle < leftAngle) {
                balance = 2.f * (0.5f - rightAngle);
            } else {
                balance = 2.f * (0.5f - leftAngle);
            }
        }
        source.setBalance(balance);
    }

    /**
     * Set the volume of a specified source.
     * 
     * @param list the sound listener that hears the sound
     * @param source the sound source that sends out the sound and thats volume
     *            is altered
     */
    private void calculateVolume(final SoundListenerJAVAX list,
        final SoundSourceJAVAX source) {
        float volume = 1.f;
        if (source.getType() == SoundSource.TYPE_EFFECT) {
            volume = list.getEffectVolume();
        } else if (source.getType() == SoundSource.TYPE_MUSIC) {
            volume = list.getMusicVolume();
        }

        if (source.hasPos()) {
            final float distance =
                FastMath.sqrt(FastMath.sqr(list.getPosX() - source.getPosX())
                    + FastMath.sqr(list.getPosY() - source.getPosY())
                    + FastMath.sqr(list.getPosZ() - source.getPosZ()));
            volume *= 1.f - (distance / 15.f);
        }

        source.setVolume(volume);
    }
}

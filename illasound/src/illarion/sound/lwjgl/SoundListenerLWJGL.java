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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import illarion.common.util.Location;

import illarion.sound.SoundListener;

/**
 * This class defines a object in 3D space that hears the sound and the music.
 * It uses the LWJGL port to OpenAL to handle the sounds.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public class SoundListenerLWJGL implements SoundListener {
    /**
     * A buffer that is used to get values out of the OpenAL environment.
     */
    private static final FloatBuffer BUFFER = BufferUtils.createFloatBuffer(6);

    /**
     * This variable holds a reference to the activated listener.
     */
    private static SoundListenerLWJGL enabledListener;

    /**
     * The orientation of the listener. The second three values define the
     * direction of "up".
     */
    private final float[] orientation = new float[6];

    /**
     * The position of the sound listener.
     */
    private final float[] position = new float[3];

    /**
     * The moving speed of the listener
     */
    private final float[] velocity = new float[3];

    /**
     * Constructor for the sound listener that prepares the values of the
     * listener.
     */
    public SoundListenerLWJGL() {
        velocity[0] = 0.f;
        velocity[1] = 0.f;
        velocity[2] = 0.f;
    }

    /**
     * The sound listener that is currently enabled.
     * 
     * @return the currently enabled sound listener
     */
    public static SoundListenerLWJGL getActiveListener() {
        return enabledListener;
    }

    /**
     * Enable the sound source. This will cause all other sources to be
     * disabled.
     */
    @Override
    public void activate() {
        if (SoundSystem.getInstance().isWorking()) {
            BUFFER.clear();
            BUFFER.put(orientation);
            BUFFER.flip();
            AL10.alListener(AL10.AL_ORIENTATION, BUFFER);

            BUFFER.clear();
            BUFFER.put(position);
            BUFFER.flip();
            AL10.alListener(AL10.AL_POSITION, BUFFER);

            BUFFER.clear();
            BUFFER.put(velocity);
            BUFFER.flip();
            AL10.alListener(AL10.AL_VELOCITY, BUFFER);

            if ((enabledListener != null) && enabledListener.equals(this)) {
                return;
            }
            enabledListener = this;
        }
    }

    /**
     * Disable this listener.
     */
    @Override
    public void deactivate() {
        enabledListener = null;
        BUFFER.clear();
        BUFFER.put(Float.MAX_VALUE);
        BUFFER.put(Float.MAX_VALUE);
        BUFFER.put(Float.MAX_VALUE);
        BUFFER.flip();
        AL10.alListener(AL10.AL_POSITION, BUFFER);
    }

    /**
     * Set the direction of this listener.
     */
    @Override
    public void setDirection(final int dir) {
        if (SoundSystem.getInstance().isWorking()) {
            final int dX = Location.getDirectionVectorX(dir);
            final int dY = Location.getDirectionVectorY(dir);

            orientation[0] = dX;
            orientation[1] = dY;
            orientation[2] = 0.f;
            orientation[3] = 0.f;
            orientation[4] = 0.f;
            orientation[5] = 1.f;

            BUFFER.clear();
            BUFFER.put(orientation);
            BUFFER.flip();
            AL10.alListener(AL10.AL_ORIENTATION, BUFFER);
        }
    }

    /**
     * Set the volume of the sound effects.
     * 
     * @param volume the new volume value
     */
    @Override
    public void setEffectVolume(final float volume) {
    }

    /**
     * Set the location of this sound listener in the 3D space.
     * 
     * @param posx the x coordinate of the position of the sound listener
     * @param posy the y coordinate of the position of the sound listener
     * @param posz the z coordinate of the position of the sound listener
     */
    @Override
    public void setLocation(final float posx, final float posy,
        final float posz) {
        if (SoundSystem.getInstance().isWorking()) {
            position[0] = posx;
            position[1] = posy;
            position[2] = posz;

            BUFFER.clear();
            BUFFER.put(position);
            BUFFER.flip();
            AL10.alListener(AL10.AL_POSITION, BUFFER);
        }
    }

    /**
     * Set the location of this sound listener in the 3D space.
     * 
     * @param loc the location of the sound listener
     */
    @Override
    public void setLocation(final Location loc) {
        setLocation(loc.getScX(), loc.getScY(), loc.getScZ());
    }

    /**
     * Set the volume of the background music.
     * 
     * @param volume the new volume for the background music
     */
    @Override
    public void setMusicVolume(final float volume) {
    }

}

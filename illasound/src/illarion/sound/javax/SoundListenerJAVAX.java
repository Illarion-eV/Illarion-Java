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

import illarion.common.util.FastMath;
import illarion.common.util.Location;

import illarion.sound.SoundListener;

/**
 * This sound listener defines the location of the object that hears the sound
 * in the 3D space. Also it allows so set the global volume values.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class SoundListenerJAVAX implements SoundListener {
    /**
     * The direction the listener is looking at.
     */
    private int direction;

    /**
     * The dirty flag. In case this is set <code>true</code> a update of all
     * sound sources is needed.
     */
    private boolean dirty;

    /**
     * The global volume of sound effects.
     */
    private float effectVol = 1.f;

    /**
     * The global volume for the background music.
     */
    private float musicVol = 1.f;

    /**
     * The x coordinate of the location of the listener.
     */
    private float posX;

    /**
     * The y coordinate of the location of the listener.
     */
    private float posY;

    /**
     * The z coordinate of the location of the listener.
     */
    private float posZ;

    /**
     * Activate this sound listener. All further playback will be send to this
     * one.
     */
    @Override
    public void activate() {
        SoundSystem.getInstance().setListener(this);
    }

    /**
     * This function is called by the sound system after the sound listener got
     * disabled. All resources possible are cleared at this point.
     */
    public void cleanup() {
        // nothing to do
    }

    /**
     * Disable the sound listener. This will result in all sound effects that
     * are currently played to be canceled.
     */
    @Override
    public void deactivate() {
        if (SoundSystem.getInstance().isActiveListener(this)) {
            SoundSystem.getInstance().setListener(null);
        }
    }

    /**
     * Get the direction the sound source is looking at.
     * 
     * @return the direction constant form the Location class
     * @see illarion.common.util.Location#DIR_NORTH
     * @see illarion.common.util.Location#DIR_NORTHEAST
     * @see illarion.common.util.Location#DIR_EAST
     * @see illarion.common.util.Location#DIR_SOUTHEAST
     * @see illarion.common.util.Location#DIR_SOUTH
     * @see illarion.common.util.Location#DIR_SOUTHWEST
     * @see illarion.common.util.Location#DIR_WEST
     * @see illarion.common.util.Location#DIR_NORTHWEST
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Get the volume of sound effects in general.
     * 
     * @return the global effect volume
     */
    public float getEffectVolume() {
        return effectVol;
    }

    /**
     * Get the volume of the background music in general.
     * 
     * @return the global background music volume
     */
    public float getMusicVolume() {
        return musicVol;
    }

    /**
     * Get the current x coordinate of the position of the sound listener.
     * 
     * @return the x coordinate
     */
    public float getPosX() {
        return posX;
    }

    /**
     * Get the current y coordinate of the position of the sound listener.
     * 
     * @return the y coordinate
     */
    public float getPosY() {
        return posY;
    }

    /**
     * Get the current z coordinate of the position of the sound listener.
     * 
     * @return the z coordinate
     */
    public float getPosZ() {
        return posZ;
    }

    /**
     * Check if this sound listner is dirty and all sound sources need a update.
     * 
     * @return <code>true</code> in case anything changed all sound sources need
     *         to update
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Set the direction the sound source is looking at.
     * 
     * @param dir the direction constant form the Location class
     * @see illarion.common.util.Location#DIR_NORTH
     * @see illarion.common.util.Location#DIR_NORTHEAST
     * @see illarion.common.util.Location#DIR_EAST
     * @see illarion.common.util.Location#DIR_SOUTHEAST
     * @see illarion.common.util.Location#DIR_SOUTH
     * @see illarion.common.util.Location#DIR_SOUTHWEST
     * @see illarion.common.util.Location#DIR_WEST
     * @see illarion.common.util.Location#DIR_NORTHWEST
     */
    @Override
    public void setDirection(final int dir) {
        direction = dir;
    }

    /**
     * Set the new value for the dirty flag. Setting it true will tell the
     * SoundSystem to update all sound sources at the next run.
     * 
     * @param newDirty the new value for the dirty flag
     */
    public void setDirty(final boolean newDirty) {
        dirty = newDirty;
        if (newDirty && SoundSystem.getInstance().isActiveListener(this)) {
            SoundSystem.getInstance().triggerUpdate();
        }
    }

    /**
     * Set the global volume of the sound effects. This volume may be altered by
     * the location of the effect.
     * 
     * @param volume the new volume value, 0.f is deaf, 1.f is full volume
     */
    @SuppressWarnings("nls")
    @Override
    public void setEffectVolume(final float volume) {
        if ((volume < 0.f) || (volume > 1.f)) {
            throw new IllegalArgumentException("Invalid Volume value: "
                + Float.toString(volume));
        }
        if (FastMath.abs(effectVol - volume) > FastMath.FLT_EPSILON) {
            effectVol = volume;
            setDirty(true);
        }
    }

    /**
     * Set the location of that sound listener. Changing the location of the
     * current sound listener results in a update of the volume and balance of
     * every sound source.
     * 
     * @param posx the x coordinate of the location of the listener
     * @param posy the y coordinate of the location of the listener
     * @param posz the z coordinate of the location of the listener
     */
    @Override
    public void setLocation(final float posx, final float posy,
        final float posz) {
        if ((FastMath.abs(posX - posx) < FastMath.FLT_EPSILON)
            && (FastMath.abs(posY - posy) < FastMath.FLT_EPSILON)
            && (FastMath.abs(posZ - posz) < FastMath.FLT_EPSILON)) {
            return;
        }
        posX = posx;
        posY = posy;
        posZ = posz;
        setDirty(true);
    }

    /**
     * Set the location of that sound listener. Changing the location of the
     * current sound listener results in a update of the volume and balance of
     * every sound source.
     * 
     * @param loc the new location of the sound listener
     */
    @Override
    public void setLocation(final Location loc) {
        setLocation(loc.getScX(), loc.getScY(), loc.getScZ());
    }

    /**
     * Set the global volume of the background music.
     * 
     * @param volume the new volume value, 0.f is deaf, 1.f is full volume
     */
    @SuppressWarnings("nls")
    @Override
    public void setMusicVolume(final float volume) {
        if ((volume < 0.f) || (volume > 1.f)) {
            throw new IllegalArgumentException("Invalid Volume value: "
                + Float.toString(volume));
        }
        if (FastMath.abs(musicVol - volume) > FastMath.FLT_EPSILON) {
            musicVol = volume;
            setDirty(true);
        }
    }
}

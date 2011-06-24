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
package illarion.sound;

import illarion.common.util.Location;

/**
 * In general there should be only one listener to the sound. This one is used
 * to calculate the balance and volume settings for the localized sounds. In
 * case the position of the sound listener (as in the character) changes in the
 * game, the sound listener position has to be updated.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface SoundListener {
    /**
     * This function activates the sound listener. This way it becomes the
     * active listener to a sound and every sound source is played relative to
     * this listener. There has to be at least one activated sound listener.
     * Else its impossible to play any sounds.
     */
    void activate();

    /**
     * This function deactivates the sound listener. It frees all its resources
     * and disables the sound output in general until a new sound listener is
     * loaded.
     */
    void deactivate();

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
    void setDirection(int dir);

    /**
     * Set the volume of the effects played to this sound listener.
     * 
     * @param volume the new volume of this sound listener. 0.f is deaf, 1.f is
     *            maximal
     */
    void setEffectVolume(float volume);

    /**
     * Set the position of the sound listener. The volume and the balance of the
     * sound effects is altered by the difference between the location of the
     * listener of the source.
     * 
     * @param posx the x coordinate of the sound listener
     * @param posy the y coordinate of the sound listener
     * @param posz the z coordinate of the sound listener
     */
    void setLocation(float posx, float posy, float posz);

    /**
     * Set the position of the sound listener. The volume and the balance of the
     * sound effects is altered by the difference between the location of the
     * listener of the source.
     * 
     * @param loc the new listener location
     */
    void setLocation(Location loc);

    /**
     * Set the volume of the music played to this sound listener.
     * 
     * @param volume the new volume of this sound listener. 0.f is deaf, 1.f is
     *            maximal
     */
    void setMusicVolume(float volume);
}

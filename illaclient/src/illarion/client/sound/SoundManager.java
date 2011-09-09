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
package illarion.client.sound;

import illarion.client.world.Game;
import illarion.common.util.Location;

import org.apache.log4j.Logger;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryJOAL;
import paulscode.sound.libraries.LibraryJavaSound;

/**
 * The sound manager is the primary control of the sound output of the client
 * and currently the binding to Pauls SoundSystem.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class SoundManager {
    /**
     * The default name for the background music.
     */
    private static final String BACKGROUND_MUSIC = "background";

    /**
     * The time in milliseconds that is used to fade the background music out
     * and back in.
     */
    private static final int FADING_TIME = 2000;

    /**
     * The singleton instance of this class.
     */
    private static final SoundManager INSTANCE = new SoundManager();

    /**
     * The logger that is used for the proper log output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SoundManager.class);

    /**
     * Get the instance of the sound manager.
     * 
     * @return the instance
     */
    public static SoundManager getInstance() {
        return INSTANCE;
    }

    /**
     * The volume that is applied to all played effects.
     */
    private float effectVol = 1.f;

    /**
     * The sound system instance that links in Pauls SoundSystem.
     */
    private final SoundSystem system;

    /**
     * The constructor that ensures that only this class creates a instance of
     * this class and that prepares the sound system for proper usage.
     */
    private SoundManager() {
        try {
            SoundSystemConfig.addLibrary(LibraryJOAL.class);
        } catch (final SoundSystemException e1) {
            // Using OpenAL failed, trying native JavaSound
            try {
                SoundSystemConfig.addLibrary(LibraryJavaSound.class);
            } catch (final SoundSystemException e2) {
                // Now here we got a utter failure. Its impossible to play music
                // on this system.
                LOGGER.warn("Failed to startup SoundEngine: No sound output.",
                    e2);
                system = null;
                return;
            }
        }

        try {
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
        } catch (final SoundSystemException e) {
            // Loading the codec failed, playback will be impossible.
            LOGGER.warn("Failed loading the playback codec", e);
            system = null;
            return;
        }

        SoundSystemConfig.setNumberStreamingChannels(5);
        SoundSystemConfig.setNumberNormalChannels(27);

        system = new SoundSystem();
    }

    /**
     * This function checks if the volume is inside the valid range and throws a
     * exception in case it is not.
     * 
     * @param vol the volume to check
     * @thrown IllegalArgumentException in case the volume is smaller then 0.f
     *         or larger then 1.f
     */
    private void checkVolume(final float vol) {
        if ((vol < 0.f) || (vol > 1.f)) {
            throw new IllegalArgumentException("Volume out of valid range: "
                + Float.toString(vol));
        }
    }

    /**
     * Get the volume of the background music.
     * 
     * @return the volume of the background music
     */
    public float getBackgroundVolume() {
        if (system != null) {
            return system.getVolume(BACKGROUND_MUSIC);
        }
        return 0.f;
    }

    /**
     * Get the path of a sound effect based on the effect ID from the sound
     * effect factory.
     * 
     * @param effectId the effect id
     * @return the filename of the effect sound effect
     */
    private String getEffectPath(final int effectId) {
        return SoundFactory.getInstance().getSound(effectId);
    }

    /**
     * Get the volume that is currently set as the effect volume.
     * 
     * @return the effect volume
     */
    public float getEffectVolume() {
        return effectVol;
    }

    /**
     * Get the currently set master volume.
     * 
     * @return the master volume
     */
    public float getMasterVolume() {
        if (system != null) {
            return system.getMasterVolume();
        }
        return 0.f;
    }

    /**
     * Play a song as background music.
     * 
     * @param songId the ID of the song, the actual filename is fetched from the
     *            song factory in order to play it
     */
    public void playBackground(final int songId) {
        playBackground(SongFactory.getInstance().getSong(songId));
    }

    /**
     * Play a song as background music.
     * 
     * @param filename the filename of the song to play
     */
    public void playBackground(final String filename) {
        if (system != null) {
            if (system.playing(BACKGROUND_MUSIC)) {
                if (filename == null) {
                    system.fadeOut(BACKGROUND_MUSIC, null, FADING_TIME);
                }
                system.fadeOutIn(BACKGROUND_MUSIC, filename, FADING_TIME,
                    FADING_TIME);
            } else {
                if (filename != null) {
                    system.backgroundMusic(BACKGROUND_MUSIC, filename, true);
                }
            }
        }
    }

    /**
     * Get the path of a sound effect based on the effect ID from the sound
     * effect factory.
     * 
     * @param effectId the effect Id that is used to fetch the actual filename
     *            of the effect sound
     * @param posX the x coordinate of the location where the sound effect is
     *            played
     * @param posY the y coordinate of the location where the sound effect is
     *            played
     * @param posZ the z coordinate of the location where the sound effect is
     *            played
     */
    public void playEffect(final int effectId, final int posX, final int posY,
        final int posZ) {
        playEffect(getEffectPath(effectId), posX, posY, posZ);
    }

    /**
     * Play a sound effect based on the the effect ID on a specified location.
     * 
     * @param effectId the effect Id that is used to fetch the actual filename
     *            of the effect sound
     * @param pos the location where the sound is played
     */
    public void playEffect(final int effectId, Location pos) {
        if (pos == null) {
            pos = Game.getPlayer().getLocation();
        }
        playEffect(getEffectPath(effectId), pos);
    }

    /**
     * Play a sound effect based on the the effect ID on a specified location.
     * 
     * @param filename the filename of the sound effect that is supposed to be
     *            played
     * @param posX the x coordinate of the location where the sound effect is
     *            played
     * @param posY the y coordinate of the location where the sound effect is
     *            played
     * @param posZ the z coordinate of the location where the sound effect is
     *            played
     */
    public void playEffect(final String filename, final int posX,
        final int posY, final int posZ) {
        if (system != null) {
            final String name =
                system.quickPlay(true, filename, false, posX, posZ, posY,
                    SoundSystemConfig.ATTENUATION_ROLLOFF,
                    SoundSystemConfig.getDefaultRolloff());

            system.setVolume(name, effectVol);
        }
    }

    /**
     * Play a sound effect on a specified location.
     * 
     * @param filename the filename of the sound effect that is supposed to be
     *            played
     * @param pos the location where the sound is played
     */
    public void playEffect(final String filename, final Location pos) {
        playEffect(filename, pos.getScX(), pos.getScY(), pos.getScZ());
    }

    /**
     * Set the volume of the background music.
     * 
     * @param volume the volume of the background music
     * @thrown IllegalArgumentException in case the volume is smaller then 0.f
     *         or larger then 1.f
     */
    public void setBackgroundVolume(final float volume) {
        if (system != null) {
            checkVolume(volume);
            system.setVolume(BACKGROUND_MUSIC, volume);
        }
    }

    /**
     * Set the effect volume that is used for the playback of sound effects.
     * 
     * @param vol the volume of the sound effects
     * @thrown IllegalArgumentException in case the volume is smaller then 0.f
     *         or larger then 1.f
     */
    public void setEffectVolume(final float vol) {
        if (system != null) {
            checkVolume(vol);
            effectVol = vol;
        }
    }

    /**
     * Set the direction of the listener.
     * 
     * @param direction the direction constant the listener is looking at
     */
    public void setListenerDirection(final int direction) {
        if (system != null) {
            system.setListenerOrientation(
                Location.getDirectionVectorX(direction), 0,
                Location.getDirectionVectorY(direction), 0, 1, 0);
        }
    }

    /**
     * Set the location of the listener that hears the sound effect.
     * 
     * @param posX the x coordinate of the location where the listener is
     *            located
     * @param posY the y coordinate of the location where the listener is
     *            located
     * @param posZ the z coordinate of the location where the listener is
     *            located
     */
    public void setListenerLocation(final int posX, final int posY,
        final int posZ) {
        if (system != null) {
            system.setListenerPosition(posX, posZ, posY);
        }
    }

    /**
     * Set the location of the listener that hears the sound effect.
     * 
     * @param newLoc the location where the sound is played
     */
    public void setListenerLocation(final Location newLoc) {
        setListenerLocation(newLoc.getScX(), newLoc.getScY(), newLoc.getScZ());
    }

    /**
     * Set the master volume of this class.
     * 
     * @param vol the new volume value
     * @thrown IllegalArgumentException in case the volume is smaller then 0.f
     *         or larger then 1.f
     */
    public void setMasterVolume(final float vol) {
        if (system != null) {
            checkVolume(vol);
            system.setMasterVolume(vol);
        }
    }

    /**
     * Cleanup the sound system and shut it down properly.
     */
    public void shutdown() {
        if (system != null) {
            system.cleanup();
        }
    }
}

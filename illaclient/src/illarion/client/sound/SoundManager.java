/**
 * 
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
 * @author Martin Karing
 */
public final class SoundManager {

    private static final Logger LOGGER = Logger.getLogger(SoundManager.class);

    private final SoundSystem system;

    private static final SoundManager INSTANCE = new SoundManager();

    private SoundManager() {
        try {
            SoundSystemConfig.addLibrary(LibraryJOAL.class);
        } catch (SoundSystemException e1) {
            // Using OpenAL failed, trying native JavaSound
            try {
                SoundSystemConfig.addLibrary(LibraryJavaSound.class);
            } catch (SoundSystemException e2) {
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
        } catch (SoundSystemException e) {
            // Loading the codec failed, playback will be impossible.
            LOGGER.warn("Failed loading the playback codec", e);
            system = null;
            return;
        }

        SoundSystemConfig.setNumberStreamingChannels(5);
        SoundSystemConfig.setNumberNormalChannels(27);

        system = new SoundSystem();
    }

    public static SoundManager getInstance() {
        return INSTANCE;
    }

    public void setMasterVolume(final float vol) {
        if (vol < 0.f || vol > 1.f) {
            throw new IllegalArgumentException("Volume out of valid range: "
                + Float.toString(vol));
        }
        if (system != null) {
            system.setMasterVolume(vol);
        }
    }
    
    public float getMasterVolume() {
        if (system != null) {
            return system.getMasterVolume();
        }
        return 0.f;
    }

    private static final String BACKGROUND_MUSIC = "background";
    private static final int FADING_TIME = 2000;
    
    public void setBackgroundVolume(final float volume) {
        if (system != null) {
            system.setVolume(BACKGROUND_MUSIC, volume);
        }
    }
    
    public float getBackgroundVolume() {
        if (system != null) {
            return system.getVolume(BACKGROUND_MUSIC);
        }
        return 0.f;
    }
    

    public void playBackground(final int songId) {
        playBackground(SongFactory.getInstance().getSong(songId));
    }

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
    
    private String getEffectPath(final int effectId) {
        return SoundFactory.getInstance().getSound(effectId);
    }
    
    public void playEffect(final int effectId, Location pos) {
        if (pos == null) {
            pos = Game.getPlayer().getLocation();
        }
        playEffect(getEffectPath(effectId), pos.getScX(), pos.getScY(), pos.getScZ());
    }
    
    public void playEffect(final String filename, final Location pos) {
        playEffect(filename, pos.getScX(), pos.getScY(), pos.getScZ());
    }
    
    public void playEffect(final int effectId, final int posX,
        final int posY, final int posZ) {
        playEffect(getEffectPath(effectId), posX, posY, posZ);
    }

    public void playEffect(final String filename, final int posX,
        final int posY, final int posZ) {
        if (system != null) {
            system.quickPlay(true, filename, false, posX, posZ, posY,
                SoundSystemConfig.ATTENUATION_ROLLOFF,
                SoundSystemConfig.getDefaultRolloff());
        }
    }

    public void setListenerLocation(Location newLoc) {
        setListenerLocation(newLoc.getScX(), newLoc.getScY(), newLoc.getScZ());
    }
    
    public void setListenerLocation(final int posX, final int posY, final int posZ) {
        if (system != null) {
            system.setListenerPosition(posX, posZ, posY);
        }
    }

    public void setListenerDirection(int direction) {
        if (system != null) {
            system.setListenerOrientation(Location.getDirectionVectorX(direction), 0, Location.getDirectionVectorY(direction), 0, 1, 0);
        }
    }
}

package illarion.client.sound;

import de.lessvoid.nifty.spi.sound.SoundHandle;
import illarion.client.world.Game;

public class NiftySoundEffectHandle implements SoundHandle {
    private final int musicId;
    
    public NiftySoundEffectHandle(final int id) {
        musicId = id;
    }

    @Override
    public void play() {
        SoundManager.getInstance().playEffect(musicId, null);
    }

    @Override
    public void stop() {
    }

    @Override
    public void setVolume(float volume) {
        SoundManager.getInstance().setMasterVolume(volume);
    }

    @Override
    public float getVolume() {
        return SoundManager.getInstance().getMasterVolume();
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void dispose() {
    }
}

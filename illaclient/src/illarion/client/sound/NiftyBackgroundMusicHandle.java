package illarion.client.sound;

import de.lessvoid.nifty.spi.sound.SoundHandle;
import illarion.client.world.Game;

public class NiftyBackgroundMusicHandle implements SoundHandle {
    private final int musicId;
    
    public NiftyBackgroundMusicHandle(final int id) {
        musicId = id;
    }

    @Override
    public void play() {
        Game.getMusicBox().playMusicTrack(musicId);
    }

    @Override
    public void stop() {
        Game.getMusicBox().playDefaultMusic();
    }

    @Override
    public void setVolume(float volume) {
        SoundManager.getInstance().setBackgroundVolume(volume);
    }

    @Override
    public float getVolume() {
        return SoundManager.getInstance().getBackgroundVolume();
    }

    @Override
    public boolean isPlaying() {
        return Game.getMusicBox().isPlaying(musicId);
    }

    @Override
    public void dispose() {
    }
}

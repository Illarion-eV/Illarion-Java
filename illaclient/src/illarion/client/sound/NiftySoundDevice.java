/**
 * 
 */
package illarion.client.sound;

import de.lessvoid.nifty.sound.SoundSystem;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.spi.sound.SoundHandle;

/**
 * @author Martin Karing
 *
 */
public final class NiftySoundDevice implements SoundDevice {
    /* (non-Javadoc)
     * @see de.lessvoid.nifty.spi.sound.SoundDevice#loadSound(de.lessvoid.nifty.sound.SoundSystem, java.lang.String)
     */
    @Override
    public SoundHandle loadSound(SoundSystem soundSystem, String filename) {
        return new NiftySoundEffectHandle(Integer.parseInt(filename));
    }

    /* (non-Javadoc)
     * @see de.lessvoid.nifty.spi.sound.SoundDevice#loadMusic(de.lessvoid.nifty.sound.SoundSystem, java.lang.String)
     */
    @Override
    public SoundHandle loadMusic(SoundSystem soundSystem, String filename) {
        return new NiftyBackgroundMusicHandle(Integer.parseInt(filename));
    }

    /* (non-Javadoc)
     * @see de.lessvoid.nifty.spi.sound.SoundDevice#update(int)
     */
    @Override
    public void update(int delta) {
    }

}

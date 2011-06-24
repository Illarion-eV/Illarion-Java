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

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import illarion.common.util.FastMath;
import illarion.common.util.Location;

import illarion.sound.SoundClip;
import illarion.sound.SoundSource;

/**
 * A sound source in general is able to play some sound. Its able to play sound
 * with and without a specified location.
 * 
 * @serial exclude
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
@SuppressWarnings("nls")
public final class SoundSourceJAVAX implements SoundSource, LineListener {

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(SoundSourceJAVAX.class);

    /**
     * The last set balance value.
     */
    private float balance = 1.f;

    /**
     * The sound clip that offers the data for this sound source.
     */
    private transient SoundClipJAVAX clip;

    /**
     * The boolean flag. In case this is set to <code>true</code> the audio
     * level of the sound source will be updated at the next run.
     */
    private boolean dirty = true;

    /**
     * The operation that is triggered in case the playback of the sound is
     * done.
     */
    private int endOperation = OP_STOP;

    /**
     * Determines of this sound source is bound to a location or not.
     */
    private boolean noPos;

    /**
     * The SourceDataLine used for wave playbale.
     */
    private SourceDataLine playerWave;

    /**
     * The x coordinate of the location of this sound source.
     */
    private float posX;

    /**
     * The y coordinate of the location of this sound source.
     */
    private float posY;

    /**
     * The z coordinate of the location of this sound source.
     */
    private float posZ;

    /**
     * The type of that sound source.
     */
    private int type = TYPE_EFFECT;

    /**
     * The last set volume value.
     */
    private float volume = 0.5f;

    /**
     * This variable stores if the sound data is written completely into the
     * output stream or not.
     */
    private boolean waveDataStored = false;

    /**
     * Stores the amount of bytes written to the sound ouput stream of this
     * sound source already.
     */
    private int writtenSoundData = 0;

    /**
     * Get the current x coordinate of the position of the sound source.
     * 
     * @return the x coordinate
     */
    public float getPosX() {
        return posX;
    }

    /**
     * Get the current y coordinate of the position of the sound source.
     * 
     * @return the y coordinate
     */
    public float getPosY() {
        return posY;
    }

    /**
     * Get the current z coordinate of the position of the sound source.
     * 
     * @return the z coordinate
     */
    public float getPosZ() {
        return posZ;
    }

    /**
     * Get the sound clip stored in this sound source.
     * 
     * @return the sound clip of that sound source
     */
    @Override
    public SoundClip getSoundClip() {
        return clip;
    }

    /**
     * The set type of that sound source. Depending on this type its set if the
     * effect volume of the music volume applies.
     * 
     * @return the type of that sound source
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * Get if this sound source as a position.
     * 
     * @return <code>true</code> in case this sound source has a specified
     *         position
     */
    public boolean hasPos() {
        return !noPos;
    }

    /**
     * Check if this sound source needs a update.
     * 
     * @return <code>true</code> in case anything changed at the sound source
     *         and a update is needed
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Check if the sound source is currently playing something.
     * 
     * @return <code>true</code> in case the sound source is currently playing
     */
    @Override
    public boolean isPlaying() {
        if (clip == null) {
            return false;
        }
        return ((playerWave != null) && playerWave.isRunning());
    }

    /**
     * Set the balance to the sound source.
     * 
     * @param newBalance -1.f is left, 0.f is center, 1.f is right
     */
    public void setBalance(final float newBalance) {
        balance = newBalance;
        if (playerWave != null) {
            FloatControl balanceControl = null;
            if (playerWave.isControlSupported(FloatControl.Type.PAN)) {
                balanceControl =
                    (FloatControl) playerWave
                        .getControl(FloatControl.Type.PAN);
            } else if (playerWave
                .isControlSupported(FloatControl.Type.BALANCE)) {
                balanceControl =
                    (FloatControl) playerWave
                        .getControl(FloatControl.Type.BALANCE);
            }
            if (balanceControl != null) {
                balanceControl.setValue(balance);
            }
        }
    }

    /**
     * Set the new value for the dirty flag. Changing it to <code>true</code>
     * will notify the sound system that another update run is needed.
     * 
     * @param newDirty the new value for the dirty flag
     */
    public void setDirty(final boolean newDirty) {
        dirty = newDirty;
    }

    /**
     * Set the end operation that is done when the sound clip is played
     * completely.
     * 
     * @param op the operation that shall be done
     */
    @Override
    public void setEndOperation(final int op) {
        endOperation = op;
    }

    /**
     * Set this sound source to use no location. The volume and the balance
     * won't be altered.
     */
    @Override
    public void setNoPosition() {
        noPos = true;
    }

    /**
     * Set the location of the sound source.
     * 
     * @param posx the x coordinate of the location
     * @param posy the y coordinate of the location
     * @param posz the z coordinate of the location
     */
    @Override
    public void setPosition(final float posx, final float posy,
        final float posz) {
        if (type == TYPE_MUSIC) {
            throw new IllegalStateException("Locations not allowed for music.");
        }
        if (!noPos && (FastMath.abs(posX - posx) < FastMath.FLT_EPSILON)
            && (FastMath.abs(posY - posy) < FastMath.FLT_EPSILON)
            && (FastMath.abs(posZ - posz) < FastMath.FLT_EPSILON)) {
            return;
        }
        noPos = false;
        posX = posx;
        posY = posy;
        posZ = posz;
        dirty = true;
        SoundSystem.getInstance().triggerUpdate();
    }

    /**
     * Set the location of the sound source.
     * 
     * @param loc the new location of the sound source
     */
    @Override
    public void setPosition(final Location loc) {
        setPosition(loc.getScX(), loc.getScY(), loc.getScZ());
    }

    /**
     * Set the sound clip that shall be used by this source.
     * 
     * @param newClip the should clip that gets added.
     */
    @Override
    public void setSoundClip(final SoundClip newClip) {
        if (newClip instanceof SoundClipJAVAX) {
            synchronized (this) {
                clip = (SoundClipJAVAX) newClip;
                clip = clip.getClip();
                playerWave = null;
                writtenSoundData = 0;
                waveDataStored = false;
            }
        } else {
            throw new IllegalArgumentException(
                "Invalid Implementation of SoundClip.");
        }
    }

    /**
     * Set the type of the sound source. In case this is set to use the music
     * type, the position is automatically removed.
     * 
     * @param newType the type of this sound source
     */
    @Override
    public void setType(final int newType) {
        type = newType;
        if (newType == TYPE_MUSIC) {
            noPos = true;
        }
    }

    /**
     * Set the volume of this sound source.
     * 
     * @param newVolume the new volume of the sound source. 0.f is quiet, 1.f is
     *            maximal volume
     */
    public void setVolume(final float newVolume) {
        volume = newVolume;
        if (playerWave != null) {
            if (playerWave.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                final FloatControl gainControl =
                    (FloatControl) playerWave
                        .getControl(FloatControl.Type.MASTER_GAIN);

                final float calculatedGain = (1f - (volume)) * DEFAULT_BOOST;

                gainControl.setValue(Math.max(-80.f, calculatedGain));
            }
        }
    }

    /**
     * Start playing the clip that is bound to this sound source.
     */
    @Override
    public void start() {
        if (clip == null) {
            throw new IllegalStateException(
                "Can't start a source without clip");
        }
        synchronized (this) {
            SoundSystem.getInstance().addSoundSource(this);
            SoundSystem.getInstance().triggerUpdate();
        }
    }

    /**
     * Stop playing a clip.
     */
    @Override
    public void stop() {
        synchronized (this) {
            if ((playerWave != null) && playerWave.isRunning()) {
                final int oldEnd = endOperation;
                endOperation = OP_STOP;
                playerWave.stop();
                endOperation = oldEnd;
                if (clip.getDataMode() == SoundClip.MODE_STREAM) {
                    clip.discardStreaming();
                }
                SoundSystem.getInstance().removeSoundSource(this);
            }
        }
    }

    /**
     * Update this sound source.
     * 
     * @return <code>true</code> in case another update is needed
     */
    public boolean update() {
        if (clip == null) {
            return false;
        }

        return updateWave();
    }

    /**
     * Update function of the line listener. Execute the end operation that is
     * set for this clip properly.
     * 
     * @param event the line event that triggered this update
     */
    @Override
    public void update(final LineEvent event) {
        if (event.getType() == Type.STOP) {
            if (endOperation == OP_LOOP) {
                LOGGER.error("End event while looping received.");
            } else if (endOperation == OP_STOP) {
                if (playerWave != null) {
                    playerWave.close();
                    playerWave.flush();
                    playerWave = null;
                    writtenSoundData = 0;
                    waveDataStored = false;
                    if (clip.getDataMode() == SoundClip.MODE_STREAM) {
                        clip.discardStreaming();
                    }
                    SoundSystem.getInstance().removeSoundSource(this);
                }
            } else if (endOperation == OP_RECYCLE) {
                if (playerWave != null) {
                    playerWave.close();
                    playerWave.flush();
                    playerWave = null;
                    writtenSoundData = 0;
                    waveDataStored = false;
                    if (clip.getDataMode() == SoundClip.MODE_STREAM) {
                        clip.discardStreaming();
                    }
                    clip = null;
                    SoundSystem.getInstance().removeSoundSource(this);
                }
            }
        }
    }

    /**
     * Update the wave data. This function takes the sound data from the sound
     * clip and writes it to the output stream. This function needs rapid calls
     * in order to play sounds properly.
     * 
     * @return <code>true</code> in case its needed to call this function again
     */
    private boolean updateWave() {
        synchronized (this) {
            SourceDataLine localPlayerWave = playerWave;
            if (waveDataStored) {
                if ((localPlayerWave != null)
                    && (localPlayerWave.available() == localPlayerWave
                        .getBufferSize())) {
                    if (endOperation == OP_LOOP) {
                        if (clip.getDataMode() == SoundClip.MODE_STREAM) {
                            clip.discardStreaming();
                        }
                        waveDataStored = false;
                        writtenSoundData = 0;
                        return true;
                    }
                    localPlayerWave.stop();
                }
                return false;
            }
            if (localPlayerWave == null) {
                if (clip.getSoundDataFormat() == null) {
                    return false;
                }

                final DataLine.Info info =
                    new DataLine.Info(SourceDataLine.class,
                        clip.getSoundDataFormat());

                try {
                    localPlayerWave =
                        (SourceDataLine) AudioSystem.getLine(info);
                    localPlayerWave.open(clip.getSoundDataFormat());
                } catch (final LineUnavailableException e1) {
                    LOGGER.error("Failed getting line", e1);
                    return false;
                }
                localPlayerWave.addLineListener(this);
                setVolume(volume);
                setBalance(balance);
                localPlayerWave.start();
            }

            final int maxData = localPlayerWave.available();
            final byte[] soundData = clip.getSoundData(maxData);
            final int writeData =
                Math.min(maxData, clip.getSoundDataLength() - writtenSoundData);
            if (writeData > 0) {
                writtenSoundData +=
                    localPlayerWave.write(soundData, writtenSoundData,
                        writeData);
            } else if ((writtenSoundData == clip.getSoundDataLength())
                && clip.isReadingDone()) {
                waveDataStored = true;
            }
            if (clip.getDataMode() == SoundClip.MODE_STREAM) {
                writtenSoundData = 0;
            }

            playerWave = localPlayerWave;
            return true;
        }
    }
}

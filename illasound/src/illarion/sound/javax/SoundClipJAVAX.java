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

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;

import illarion.sound.SoundClip;

/**
 * A sound clip for java that stores all required informations to play the
 * sound. It determines the type of the audio and the required way to play it
 * itself.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class SoundClipJAVAX implements SoundClip, Cloneable {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(SoundClipJAVAX.class);

    /**
     * The InputStream that is used to load the audio data.
     */
    private AudioInputStream ain = null;

    /**
     * The load sound data. This is only used to wave based sounds.
     */
    private byte[] data;

    /**
     * The amount of bytes stored in the array.
     */
    private int dataLength;

    /**
     * The current mode how the data is stored.
     */
    private int dataMode = MODE_STORE;

    /**
     * After once decoded this value stores the real size of the clip. This can
     * be used to setup the buffer array correctly from the very beginning one.
     */
    private int finalClipSize = -1;

    /**
     * The sampled audio format. This is only used for wave based sounds.
     */
    private AudioFormat format;

    /**
     * Flag that indicates of the data was loaded completely from the
     * inputstream into the array.
     */
    private boolean loadingDone = false;

    /**
     * The name of the resource that contains the data for this sound clip.
     */
    private String resourceFile;

    /**
     * The ID of this sound clip that is used to identify it.
     */
    private int soundId;

    /**
     * Create a new instance of this class that can be used in the same way as
     * the original one.
     * 
     * @return the newly created instance of this class
     */
    @Override
    public SoundClipJAVAX clone() {
        try {
            return (SoundClipJAVAX) super.clone();
        } catch (final CloneNotSupportedException e) {
            final SoundClipJAVAX newClip = new SoundClipJAVAX();
            newClip.resourceFile = resourceFile;
            newClip.finalClipSize = finalClipSize;
            newClip.soundId = soundId;
            newClip.data = null;
            newClip.dataLength = 0;
            newClip.loadingDone = false;
            newClip.dataMode = dataMode;

            return newClip;
        }
    }

    /**
     * Stop the current reading process and discard all old data.
     */
    public void discardStreaming() {
        if (ain != null) {
            try {
                ain.close();
            } catch (final IOException e) {
                // closing stream did not work
            }
            ain = null;
        }
        data = null;
        dataLength = 0;
        format = null;
        loadingDone = false;
    }

    /**
     * Get a proper instance of this clip. In case this clip is in streaming
     * mode, a new instance is returned. In case its in storage mode, the same
     * instance is used.
     * 
     * @return the useable instance of this clip
     */
    public SoundClipJAVAX getClip() {
        if (dataMode == MODE_STREAM) {
            return clone();
        }
        return this;
    }

    /**
     * Get the current data mode.
     * 
     * @return the current data mode
     * @see #MODE_STORE
     * @see #MODE_STREAM
     */
    public int getDataMode() {
        return dataMode;
    }

    /**
     * Get the ID of the sound effect.
     * 
     * @return the id of this sound clip
     */
    @Override
    public int getId() {
        return soundId;
    }

    /**
     * Get the sound data already load. Note that the amount of data available
     * is not the length of the array, its the return value of
     * {@link #getSoundDataLength()}.
     * 
     * @param maximalLength the maximal amount of byte to read, does only apply
     *            in streaming mode
     * @return the byte array that stores the byte data of that sound
     */
    @SuppressWarnings("nls")
    public byte[] getSoundData(final int maximalLength) {
        if (resourceFile == null) {
            throw new IllegalStateException("Source file not set.");
        }
        if ((dataMode == MODE_STREAM) && (getSoundDataLength() > 0)) {
            dataLength = 0;
        }
        loadWave(maximalLength);
        return data;
    }

    /**
     * Get the format of the audio data that is loaded from the InputStream.
     * 
     * @return the audio format of the sound data
     */
    @SuppressWarnings("nls")
    public AudioFormat getSoundDataFormat() {
        if (resourceFile == null) {
            throw new IllegalStateException("Source file not set.");
        }
        loadWave(-1);
        return format;
    }

    /**
     * Get the amount of byte stored in the data array of this clip.
     * 
     * @return the amount of bytes that are ready to be played
     */
    @SuppressWarnings("nls")
    public int getSoundDataLength() {
        if (resourceFile == null) {
            throw new IllegalStateException("Source file not set.");
        }
        if ((format != null) && (format.getFrameSize() == dataLength)) {
            return 0;
        }
        return dataLength;
    }

    /**
     * Check if reading the input stream is done already.
     * 
     * @return <code>true</code> in case reading the input stream is done
     */
    public boolean isReadingDone() {
        return loadingDone;
    }

    /**
     * Load or at least prepare loading the sound clip from a specified file.
     * 
     * @param filename the name of the file that contains the data for this
     *            sound clip
     */
    @Override
    public void loadEffect(final String filename) {
        resourceFile = filename;
    }

    /**
     * Set the data mode this clip is working with.
     * 
     * @param mode the constant of the data mode
     * @see #MODE_STORE
     * @see #MODE_STREAM
     */
    @Override
    public void setDataMode(final int mode) {
        dataMode = mode;
    }

    /**
     * Set the ID of this sound clip.
     * 
     * @param id the new ID of this sound clip
     */
    @Override
    public void setId(final int id) {
        soundId = id;
    }

    /**
     * Remove all loaded resources from that sound clip.
     */
    @Override
    public void unloadEffect() {
        resourceFile = null;
        data = null;
        format = null;
        loadingDone = false;
        ain = null;
        dataLength = 0;
    }

    /**
     * Load the wave data from the specified audio input stream.
     * 
     * @param maximalSize the maximalSize to load. This does only apply in
     *            streaming mode
     */
    @SuppressWarnings("nls")
    private void loadWave(final int maximalSize) {
        if (loadingDone) {
            return;
        }
        if (ain == null) {
            try {
                final AudioInputStream in =
                    AudioSystem.getAudioInputStream(SoundClipJAVAX.class
                        .getClassLoader().getResourceAsStream(resourceFile));
                final AudioFormat baseFormat = in.getFormat();

                final AudioFormat decodedFormat =
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(), 16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(), false);
                // Get AudioInputStream that will be decoded by underlying
                // VorbisSPI
                ain = AudioSystem.getAudioInputStream(decodedFormat, in);

            } catch (final Exception e) {
                LOGGER.error("Loading Wave-Audio failed: " + resourceFile, e);
            }
            if (ain == null) {
                loadingDone = true;
                return;
            }

            format = ain.getFormat();

            if (finalClipSize > -1) {
                data = new byte[finalClipSize];
            } else if (ain.getFrameLength() == -1) {
                data = new byte[50000];
            } else {
                data =
                    new byte[(int) (ain.getFrameLength() * format
                        .getFrameSize())];
            }
            dataLength = 0;
        }

        if (maximalSize == -1) {
            return;
        }

        try {
            int available = Math.max(format.getFrameSize(), ain.available());
            if (dataMode == MODE_STREAM) {
                available = Math.min(available, maximalSize - dataLength);
            }
            if (available > (data.length - dataLength)) {
                final byte[] newData = new byte[dataLength + (available * 3)];
                System.arraycopy(data, 0, newData, 0, data.length);
                data = newData;
            }
            if (available > 0) {
                final int cntRead = ain.read(data, dataLength, available);
                if (cntRead == -1) {
                    if (dataLength < data.length) {
                        final byte[] newData = new byte[dataLength];
                        System.arraycopy(data, 0, newData, 0, newData.length);
                        data = newData;
                    }
                    if (finalClipSize == -1) {
                        finalClipSize = dataLength;
                    }
                    loadingDone = true;
                    ain.close();
                    ain = null;
                } else {
                    dataLength += cntRead;
                }
            }
        } catch (final IOException e1) {
            LOGGER.error("Loading the WAV data failed");
            loadingDone = false;
            dataLength = 0;
            try {
                ain.close();
            } catch (final IOException e) {
                LOGGER.error("Closing the input stream failed");
            }
            ain = null;
        }
    }
}

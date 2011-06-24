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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import illarion.sound.SoundClip;

/**
 * This sound clip holds the reference to a buffered clip in the OpenAL
 * environment. It will load the data in the best way known.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class SoundClipLWJL implements SoundClip {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SoundClipLWJL.class);

    /**
     * A buffer that is used for the communication with the OpenAL environment.
     */
    private static final IntBuffer TEMP_BUFFER = BufferUtils
        .createIntBuffer(1);

    /**
     * The ID of the sound clip.
     */
    private int clipID;

    /**
     * The ID of the buffer that holds the data of this clip in the OpenAL
     * environment.
     */
    private int openALid = -1;

    /**
     * Get the ID of the sound clip.
     * 
     * @return the id of the sound clip
     */
    @Override
    public int getId() {
        return clipID;
    }

    /**
     * Load the effect and prepare the playback.
     * 
     * @param filename the name of the file the data of the sound is load from
     */
    @SuppressWarnings("nls")
    @Override
    public void loadEffect(final String filename) {
        final URL sourceFile =
            SoundClipLWJL.class.getClassLoader().getResource(filename);

        try {
            final AudioInputStream in =
                AudioSystem.getAudioInputStream(sourceFile);

            // now lets see if the OpenAL binding is able to handle OGG directly
            if (AL10.alIsExtensionPresent("AL_EXT_vorbis")) {
                final ByteBuffer buffer =
                    BufferUtils.createByteBuffer((int) new File(sourceFile
                        .toURI()).length());

                final ReadableByteChannel inChan = Channels.newChannel(in);
                int lastLength = 0;
                while (lastLength > -1) {
                    lastLength = inChan.read(buffer);
                }
                inChan.close();
                AL10.alBufferData(getALid(), AL10.AL_FORMAT_VORBIS_EXT,
                    buffer, (int) in.getFormat().getSampleRate());
            } else {
                final WaveData data = WaveData.create(in);
                AL10.alBufferData(getALid(), data.format, data.data,
                    data.samplerate);
                data.dispose();
            }
        } catch (final UnsupportedAudioFileException e) {
            LOGGER.error("Can't load audio file, unknown encoding", e);
        } catch (final IOException e) {
            LOGGER.error("Can't load audio file, error while reading", e);
        } catch (final URISyntaxException e) {
            LOGGER.error("Can't load audio file, error while accessing", e);
        }
    }

    /**
     * OpenAL does not support streaming or buffering. This function does
     * nothing at all.
     */
    @Override
    public void setDataMode(final int mode) {
        // nothing to do
    }

    /**
     * Set the ID of this sound clip.
     * 
     * @param id the ID of the sound clip
     */
    @Override
    public void setId(final int id) {
        clipID = id;
    }

    /**
     * Dispose the data of the effect. This will cause that the data is removed
     * from the OpenAL environment.
     */
    @Override
    public void unloadEffect() {
        if (openALid > -1) {
            TEMP_BUFFER.clear();
            TEMP_BUFFER.put(openALid);
            TEMP_BUFFER.flip();
            AL10.alDeleteBuffers(TEMP_BUFFER);
            openALid = -1;
        }
    }

    /**
     * Get the OpenAL ID of this buffer.
     * 
     * @return the open al ID of the buffer for this sound
     */
    private int getALid() {
        if (openALid == -1) {
            TEMP_BUFFER.clear();
            AL10.alGenBuffers(TEMP_BUFFER);
            openALid = TEMP_BUFFER.get(0);
        }
        return openALid;
    }

}

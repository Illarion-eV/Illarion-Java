/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.mapedit.util;

import illarion.common.util.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * @author Fredrik K
 */
public class OggPlayer extends Thread implements Stoppable {
    /**
     * This logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OggPlayer.class);

    private volatile boolean running;
    private AudioInputStream audioInputStream;
    private AudioInputStream audioStream;
    @Nullable
    private SourceDataLine line;
    private static OggPlayer player;
    private AudioFormat decodedFormat;

    /**
     * Start playing of a new sound file.
     *
     * @param file name of file
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public static void play(String file) throws IOException, UnsupportedAudioFileException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL songURL = loader.getResource(file);

        synchronized (OggPlayer.class) {
            if (player != null) {
                player.saveShutdown();
            }
            player = new OggPlayer();
        }

        player.loadStream(songURL);
        player.start();
    }

    /**
     * Load the audiostream
     *
     * @param songURL URL to the Ogg file.
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public void loadStream(@Nullable URL songURL) throws IOException, UnsupportedAudioFileException {
        audioInputStream = AudioSystem.getAudioInputStream(songURL);
        if (audioInputStream != null) {
            AudioFormat baseFormat = audioInputStream.getFormat();
            decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
                                            baseFormat.getChannels(), baseFormat.getChannels() * 2,
                                            baseFormat.getSampleRate(), false);

            audioStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
        }
    }

    @Nullable
    private SourceDataLine getLine() throws LineUnavailableException {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(decodedFormat);
        return res;
    }

    private void stopPlaying() throws IOException {

        line.drain();
        line.stop();
        line.close();
        audioStream.close();
        audioInputStream.close();
    }

    /**
     * Playing the audio file
     */
    @Override
    public void run() {
        byte[] data = new byte[4096];
        try {
            line = getLine();
            if (line != null) {
                line.start();
                int nBytesRead = 0, nBytesWritten = 0;
                while (nBytesRead != -1 && running) {
                    nBytesRead = audioStream.read(data, 0, data.length);
                    if (nBytesRead != -1) {
                        nBytesWritten = line.write(data, 0, nBytesRead);
                    }
                }
                stopPlaying();
            }
        } catch (LineUnavailableException | IOException e) {
            LOGGER.error("Failed to read Ogg file.", e);
        }
    }

    /**
     * Start a new thread
     */
    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    /**
     * Stop the thread
     */
    @Override
    public void saveShutdown() {
        running = false;
    }
}

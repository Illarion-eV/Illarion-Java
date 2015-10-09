/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.client.world;

import illarion.client.resources.SongFactory;
import illarion.client.util.AudioPlayer;
import illarion.common.util.Stoppable;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.illarion.engine.Engine;
import org.illarion.engine.sound.Music;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This is the music box. What is does is playing music. This class handles the playback of the background music
 * according to the settings. Also it ensures that the different overwriting levels of the music are kept as they are
 * supposed to be.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public final class MusicBox implements Stoppable{

    /**
     * The ID of the combat music.
     */
    private static final int COMBAT_TRACK = 1;

    /**
     * This is the constant that applies in case no overwrite track is set and the background music is supposed to
     * play the default music.
     */
    public static final int NO_TRACK = 0;

    /**
     * The current music track that is playing.
     */
    private int currentDefaultTrack;

    /**
     * The ID of the music that is currently played.
     */
    private int currentMusicId;

    /**
     * This variable stores the ID of the track that overwrites the current
     * default track.
     */
    private int overrideSoundId;

    @Nonnull
    private final Engine engine;

    @Nonnull
    private final AudioPlayer audioPlayer;

    /**
     * This is the constructor that prepares this class for proper operation.
     */
    MusicBox(@Nonnull Engine engine) {
        this.engine = engine;
        overrideSoundId = NO_TRACK;
        currentDefaultTrack = NO_TRACK;
        audioPlayer = AudioPlayer.getInstance();
        audioPlayer.initAudioPlayer(engine.getSounds());
        AnnotationProcessor.process(this);
    }



    /**
     * Play the default music now that is set by the tile the player is standing on.
     */
    public void playDefaultMusic() {
        playMusicTrack(NO_TRACK);
    }

    /**
     * Set the sound ID that is supposed to be played. This will overwrite the default sound track that is set with
     * the music ID embedded to the tiles.
     *
     * @param musicId the ID of the music to play
     */
    public void playMusicTrack(int musicId) {
        if (musicId != overrideSoundId) {
            overrideSoundId = musicId;
        }
    }

    @Override
    public void saveShutdown() {
        audioPlayer.stopMusic();
    }

    /**
     * Set the sound track that is supposed to be played now. This function does not perform any additional checks.
     * It will plain and simple start playing the newly chosen sound track now. It does this even in case the current
     * and the new sound track are equal.
     *
     * @param id the ID of the sound track to play
     */
    private void setSoundTrack(int id) {
        if (currentMusicId == id) {
            return;
        }

        currentMusicId = id;

        if (id == NO_TRACK) {
            audioPlayer.stopMusic();
            return;
        }

        Music currentMusic = SongFactory.getInstance().getSong(id, engine.getAssets().getSoundsManager());
        if (currentMusic == null) {
            log.error("Requested music was not found: {}", id);
            return;
        }
        audioPlayer.playMusic(currentMusic);
    }

    /**
     * The logging instance that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(MusicBox.class);

    /**
     * Update the location where the player is currently at. This will update the soundtrack that is played in case
     * its needed.
     */
    public void updatePlayerLocation() {
        MapTile tile = World.getMap().getMapAt(World.getPlayer().getLocation());

        int newId = (tile == null) ? currentDefaultTrack : tile.getTileMusic();
        if (newId != currentDefaultTrack) {
            currentDefaultTrack = newId;
        }
    }

    /**
     * This handler is called during the update loop and should be used to change the currently played music to make
     * sure that changing the music is in sync with the rest of the game.
     */
    public void update() {
        if (World.getPlayer().getCombatHandler().isAttacking()) {
            setSoundTrack(COMBAT_TRACK);
        } else if (overrideSoundId > NO_TRACK) {
            setSoundTrack(overrideSoundId);
        } else {
            setSoundTrack(currentDefaultTrack);
        }
    }
}

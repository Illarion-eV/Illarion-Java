/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world;

import illarion.client.IllaClient;
import illarion.client.resources.SongFactory;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.util.Stoppable;
import illarion.common.util.StoppableStorage;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicPatternSubscriber;
import org.newdawn.slick.Music;

/**
 * This is the music box. What is does is playing music. This class handles the playback of the background music
 * according to the settings. Also it ensures that the different overwriting levels of the music are kept as they are
 * supposed to be.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MusicBox implements Stoppable {
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
     * The music that is currently played.
     */
    private Music currentMusic;

    /**
     * The ID of the music that is currently played.
     */
    private int currentMusicId;

    /**
     * This flag is set <code>true</code> in case the music box is supposed to play the fighting background music.
     * This overwrites any other track that is currently played.
     */
    private boolean fightingMusicPlaying;

    /**
     * This variable stores the ID of the track that overwrites the current
     * default track.
     */
    private int overrideSoundId;

    private boolean musicEnabled;
    private float musicVolume;

    /**
     * This is the constructor that prepares this class for proper operation.
     */
    MusicBox() {
        overrideSoundId = NO_TRACK;
        fightingMusicPlaying = false;
        currentDefaultTrack = NO_TRACK;

        StoppableStorage.getInstance().add(this);

        musicEnabled = IllaClient.getCfg().getBoolean("musicOn");
        musicVolume = IllaClient.getCfg().getFloat("musicVolume") / Player.MAX_CLIENT_VOL;

        AnnotationProcessor.process(this);
    }

    @EventTopicPatternSubscriber(topicPattern = "music.*")
    public void onUpdateConfig(final String topic, final ConfigChangedEvent data) {
        if ("musicOn".equals(topic)) {
            musicEnabled = IllaClient.getCfg().getBoolean("musicOn");
        } else if ("musicVolume".equals(topic)) {
            musicVolume = IllaClient.getCfg().getFloat("musicVolume") / Player.MAX_CLIENT_VOL;
        }
    }

    public boolean isPlaying(final int musicId) {
        return overrideSoundId != musicId;
    }

    /**
     * Play the default music now that is set by the tile the player is standing on.
     */
    public void playDefaultMusic() {
        playMusicTrack(NO_TRACK);
    }

    /**
     * Play the fighting sound track now. This will overwrite all other playback.
     */
    public void playFightingMusic() {
        if (!fightingMusicPlaying) {
            fightingMusicPlaying = true;
        }
    }

    /**
     * Set the sound ID that is supposed to be played. This will overwrite the default sound track that is set with
     * the music ID embedded to the tiles.
     *
     * @param musicId the ID of the music to play
     */
    public void playMusicTrack(final int musicId) {
        if (musicId != overrideSoundId) {
            overrideSoundId = musicId;
        }
    }

    @Override
    public void saveShutdown() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    /**
     * Set the sound track that is supposed to be played now. This function does not perform any additional checks.
     * It will plain and simple start playing the newly chosen sound track now. It does this even in case the current
     * and the new sound track are equal.
     *
     * @param id the ID of the sound track to play
     */
    private void setSoundTrack(final int id) {
        if (!musicEnabled || (currentMusicId == id)) {
            return;
        }

        currentMusicId = id;

        if (id == NO_TRACK) {
            if (currentMusic != null) {
                currentMusic.stop();
                currentMusic = null;
            }
            return;
        }

        currentMusic = SongFactory.getInstance().getSong(id);
        currentMusic.loop(1.f, musicVolume);
    }

    /**
     * Stop playing the fighting music and fall back to the last sound track played.
     */
    public void stopFightingMusic() {
        if (fightingMusicPlaying) {
            fightingMusicPlaying = false;
        }
    }

    /**
     * Update the location where the player is currently at. This will update the soundtrack that is played in case
     * its needed.
     */
    public void updatePlayerLocation() {
        final MapTile tile = World.getMap().getMapAt(World.getPlayer().getLocation());

        final int newId;
        if (tile == null) {
            newId = NO_TRACK;
        } else {
            newId = tile.getTileMusic();
        }
        if (newId != currentDefaultTrack) {
            currentDefaultTrack = newId;
        }
    }

    /**
     * This handler is called during the update loop and should be used to change the currently played music to make
     * sure that changing the music is in sync with the rest of the game.
     *
     * @param delta the time in milliseconds since the last update
     */
    public void update(final int delta) {
        if (fightingMusicPlaying) {
            setSoundTrack(COMBAT_TRACK);
        } else if (overrideSoundId > NO_TRACK) {
            setSoundTrack(overrideSoundId);
        } else {
            setSoundTrack(currentDefaultTrack);
        }
    }
}

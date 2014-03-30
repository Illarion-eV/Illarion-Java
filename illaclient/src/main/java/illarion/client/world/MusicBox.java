/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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
import illarion.common.config.Config;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.util.Stoppable;
import illarion.common.util.StoppableStorage;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicPatternSubscriber;
import org.illarion.engine.Engine;
import org.illarion.engine.sound.Music;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This is the music box. What is does is playing music. This class handles the playback of the background music
 * according to the settings. Also it ensures that the different overwriting levels of the music are kept as they are
 * supposed to be.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
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
     * The ID of the music that is currently played.
     */
    private int currentMusicId;

    /**
     * This flag is set {@code true} in case the music box is supposed to play the fighting background music.
     * This overwrites any other track that is currently played.
     */
    private boolean fightingMusicPlaying;

    /**
     * This variable stores the ID of the track that overwrites the current
     * default track.
     */
    private int overrideSoundId;

    @Nonnull
    private final Engine engine;

    /**
     * This is the constructor that prepares this class for proper operation.
     */
    MusicBox(@Nonnull final Engine engine) {
        this.engine = engine;
        overrideSoundId = NO_TRACK;
        fightingMusicPlaying = false;
        currentDefaultTrack = NO_TRACK;

        StoppableStorage.getInstance().add(this);

        AnnotationProcessor.process(this);
        updateSettings(null, IllaClient.getCfg());
    }

    private void updateSettings(@Nullable final String setting, @Nonnull final Config cfg) {
        if ((setting == null) || "musicOn".equals(setting)) {
            final boolean musicEnabled = cfg.getBoolean("musicOn");
            if (musicEnabled) {
                final float musicVolume = cfg.getFloat("musicVolume") / Player.MAX_CLIENT_VOL;
                engine.getSounds().setMusicVolume(musicVolume);
            } else {
                engine.getSounds().setMusicVolume(0.f);
            }
        }
        if ((setting == null) || "musicVolume".equals(setting)) {
            final float musicVolume = cfg.getFloat("musicVolume") / Player.MAX_CLIENT_VOL;
            if (IllaClient.getCfg().getBoolean("musicOn")) {
                engine.getSounds().setMusicVolume(musicVolume);
            }
        }
        if ((setting == null) || "soundOn".equals(setting)) {
            final boolean soundEnabled = cfg.getBoolean("soundOn");
            if (soundEnabled) {
                final float soundVolume = cfg.getFloat("soundVolume") / Player.MAX_CLIENT_VOL;
                engine.getSounds().setSoundVolume(soundVolume);
            } else {
                engine.getSounds().setSoundVolume(0.f);
            }
        }
        if ((setting == null) || "soundVolume".equals(setting)) {
            final float soundVolume = cfg.getFloat("soundVolume") / Player.MAX_CLIENT_VOL;
            if (IllaClient.getCfg().getBoolean("soundOn")) {
                engine.getSounds().setSoundVolume(soundVolume);
            }
        }
    }

    @EventTopicPatternSubscriber(topicPattern = "((music)|(sound))((On)|(Volume))")
    public void onUpdateSoundMusicConfig(@Nonnull final String topic, @Nonnull final ConfigChangedEvent data) {
        updateSettings(topic, data.getConfig());
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
        engine.getSounds().stopMusic(0);
    }

    /**
     * Set the sound track that is supposed to be played now. This function does not perform any additional checks.
     * It will plain and simple start playing the newly chosen sound track now. It does this even in case the current
     * and the new sound track are equal.
     *
     * @param id the ID of the sound track to play
     */
    private void setSoundTrack(final int id) {
        if (currentMusicId == id) {
            return;
        }

        currentMusicId = id;

        if (id == NO_TRACK) {
            engine.getSounds().stopMusic(500);
            return;
        }

        final Music currentMusic = SongFactory.getInstance().getSong(id, engine.getAssets().getSoundsManager());
        if (currentMusic == null) {
            LOGGER.error("Requested music was not found: " + id);
            return;
        }
        engine.getSounds().playMusic(currentMusic, 250, 250);
    }

    /**
     * The logging instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicBox.class);

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
            // in case the tile is not found, stick with the default tracks to prevent the music from acting up
            newId = currentDefaultTrack;
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
     */
    public void update() {
        if (fightingMusicPlaying) {
            setSoundTrack(COMBAT_TRACK);
        } else if (overrideSoundId > NO_TRACK) {
            setSoundTrack(overrideSoundId);
        } else {
            setSoundTrack(currentDefaultTrack);
        }
    }
}

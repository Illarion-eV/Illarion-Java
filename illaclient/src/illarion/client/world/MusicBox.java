/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world;

import illarion.client.sound.SongFactory;

import illarion.sound.SoundClip;
import illarion.sound.SoundManager;
import illarion.sound.SoundSource;

/**
 * This is the music box. What is does is playing music. This class handles the
 * playback of the background music according to the settings. Also it ensures
 * that the different overwriting levels of the music are kept as they are
 * supposed to be.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MusicBox {
    /**
     * This is the constant that applies in case no overwrite track is set and
     * the background music is supposed to play the default music.
     */
    public static final int NO_TRACK = 0;

    /**
     * The ID of the combat music.
     */
    private static final int COMBAT_TRACK = 1;

    /**
     * The sound source that is used to maintain the background music.
     */
    private final SoundSource backgroundMusic;

    /**
     * The current music track that is playing.
     */
    private int currentDefaultTrack;

    /**
     * This flag is set <code>true</code> in case the music box is supposed to
     * play the fighting background music. This overwrites any other track that
     * is currently played.
     */
    private boolean fightingMusicPlaying;

    /**
     * This variable stores the ID of the track that overwrites the current
     * default track.
     */
    private int overrideSoundId;

    /**
     * This is the constructor that prepares this class for proper operation.
     */
    MusicBox() {
        overrideSoundId = NO_TRACK;
        fightingMusicPlaying = false;
        currentDefaultTrack = NO_TRACK;
        backgroundMusic = SoundManager.getInstance().getSoundSource();
        backgroundMusic.setEndOperation(SoundSource.OP_LOOP);
        backgroundMusic.setType(SoundSource.TYPE_MUSIC);
    }

    /**
     * Play the default music now that is set by the tile the player is standing
     * on.
     */
    public void playDefaultMusic() {
        playMusicTrack(NO_TRACK);
    }

    /**
     * Play the fighting sound track now. This will overwrite all other
     * playback.
     */
    public void playFightingMusic() {
        if (!fightingMusicPlaying) {
            fightingMusicPlaying = true;
            updateSoundTrack();
        }
    }

    /**
     * Set the sound ID that is supposed to be played. This will overwrite the
     * default sound track that is set with the music ID embedded to the tiles.
     * 
     * @param musicId the ID of the music to play
     */
    public void playMusicTrack(final int musicId) {
        if (musicId != overrideSoundId) {
            overrideSoundId = musicId;
            updateSoundTrack();
        }
    }

    /**
     * Stop playing the fighting music and fall back to the last sound track
     * played.
     */
    public void stopFightingMusic() {
        if (fightingMusicPlaying) {
            fightingMusicPlaying = false;
            updateSoundTrack();
        }
    }

    /**
     * Update the location where the player is currently at. This will update
     * the soundtrack that is played in case its needed.
     */
    public void updatePlayerLocation() {
        final int newId =
            Game.getMap().getMapAt(Game.getPlayer().getLocation())
                .getTileMusic();
        if (newId != currentDefaultTrack) {
            currentDefaultTrack = newId;
            updateSoundTrack();
        }
    }

    /**
     * This stops the music box and prepares this box for removal from the
     * client instance.
     */
    void shutdownMusicBox() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    /**
     * Set the sound track that is supposed to be played now. This function does
     * not perform any additional checks. It will plain and simple start playing
     * the newly chosen sound track now. It does this even in case the current
     * and the new sound track are equal.
     * 
     * @param id the ID of the sound track to play
     */
    private void setSoundTrack(final int id) {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }

        final SoundClip clip = SongFactory.getInstance().getSong(id);

        if (clip == null) {
            return;
        }

        backgroundMusic.setSoundClip(clip);
        backgroundMusic.start();
    }

    /**
     * Calling this function will cause the sound track to restart. It will
     * select the required sound track based upon the current state of the
     * music.
     */
    private void updateSoundTrack() {
        if (fightingMusicPlaying) {
            setSoundTrack(COMBAT_TRACK);
        }
        if (overrideSoundId > NO_TRACK) {
            setSoundTrack(overrideSoundId);
        }
        setSoundTrack(currentDefaultTrack);
    }
}

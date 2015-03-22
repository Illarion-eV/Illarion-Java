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

import illarion.client.IllaClient;
import illarion.client.graphics.Avatar;
import illarion.client.net.client.RequestAppearanceCmd;
import illarion.client.world.events.CharRemovedEvent;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.types.CharacterId;
import illarion.common.types.ServerCoordinate;
import javolution.util.FastTable;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Handles all characters known to the client but the player character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@SuppressWarnings("ClassNamingConvention")
@ThreadSafe
public final class People {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(People.class);

    /**
     * This is the format string that is displayed in the {@link #toString()} function.
     */
    @SuppressWarnings("nls")
    @Nonnull
    private static final String TO_STRING_TEXT = "People Manager - %d$1 characters in storage";

    /**
     * The list of visible characters.
     */
    @Nonnull
    @GuardedBy("charsLock")
    private final Map<CharacterId, Char> chars;

    /**
     * The lock that is used to secure the chars table properly.
     */
    @Nonnull
    private final ReentrantReadWriteLock charsLock;

    /**
     * A list of characters that are going to be removed.
     */
    @Nonnull
    private final List<Char> removalList;

    private int permanentAvatarTagState;

    /**
     * Default constructor. Sets up all needed base variables to init the class.
     */
    public People() {
        removalList = new FastTable<>();
        chars = new HashMap<>();
        charsLock = new ReentrantReadWriteLock();

        permanentAvatarTagState = IllaClient.getCfg().getInteger("showAvatarTagPermanently");
        AnnotationProcessor.process(this);
    }

    @EventTopicSubscriber(topic = "showAvatarTagPermanently")
    public void onQuestMarkerSettingsChanged(@Nonnull String topic, @Nonnull ConfigChangedEvent event) {
        if ("showAvatarTagPermanently".equals(topic)) {
            permanentAvatarTagState = event.getConfig().getInteger(topic);
        }
    }

    @Contract(value = "null->false", pure = true)
    public boolean isAvatarTagShown(@Nullable CharacterId id) {
        if (id == null) {
            return false;
        }
        switch (permanentAvatarTagState % 3) {
            case 0:
                return false;
            case 1:
                return id.isHuman() && !World.getPlayer().isPlayer(id);
            case 2:
                return !World.getPlayer().isPlayer(id);
            default:
                return false;
        }
    }

    /**
     * Get a character on the client screen. If the character is not available, its created and the appearance is
     * requested from the server.
     *
     * @param id the ID of the character
     * @return the character that was requested
     */
    @Nonnull
    public Char accessCharacter(@Nonnull CharacterId id) {
        if (World.getPlayer().isPlayer(id)) {
            return World.getPlayer().getCharacter();
        }

        Char chara = getCharacter(id);
        if (chara == null) {
            return createNewCharacter(id);
        }
        return chara;
    }

    @Nullable
    public Char getCharOnScreenLoc(int x, int y) {
        charsLock.readLock().lock();
        try {
            @Nullable Char characterOnScreenLocation = null;
            for (Char character : chars.values()) {
                Avatar avatar = character.getAvatar();
                if ((avatar != null) && character.getInteractive().isCharOnScreenLoc(x, y)) {
                    if (characterOnScreenLocation == null) {
                        characterOnScreenLocation = character;
                    } else {
                        Avatar otherAvatar = characterOnScreenLocation.getAvatar();
                        assert otherAvatar != null; /* characterOnScreenLocation can't store a char without avatar */
                        if (avatar.getOrder() < otherAvatar.getOrder()) {
                            characterOnScreenLocation = character;
                        }
                    }
                }
            }
            return characterOnScreenLocation;
        } finally {
            charsLock.readLock().unlock();
        }
    }

    /**
     * Add a character to the list of known characters.
     *
     * @param chara the character that shall be added
     */
    private void addCharacter(@Nonnull Char chara) {
        if (chara.getCharId() == null) {
            throw new IllegalArgumentException("Adding character without ID is illegal.");
        }
        if (World.getPlayer().isPlayer(chara.getCharId())) {
            throw new IllegalArgumentException("Adding player character to the chars list is not allowed.");
        }

        charsLock.writeLock().lock();
        try {
            chars.put(chara.getCharId(), chara);
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Add a character to the list of characters that are going to be removed at the next run of {@link
     * #cleanRemovalList()}.
     *
     * @param removeChar the character that is going to be removed
     */
    public void addCharacterToRemoveList(@Nonnull Char removeChar) {
        if (removeChar.getCharId() == null) {
            throw new IllegalArgumentException("Removing character without ID is illegal.");
        }
        if (World.getPlayer().isPlayer(removeChar.getCharId())) {
            throw new IllegalArgumentException("Removing player character from the chars list is not allowed.");
        }
        removalList.add(removeChar);
    }

    /**
     * Clean up the removal list. All character from the list of character to remove get removed and recycled. The list
     * is cleared after calling this function.
     */
    public void cleanRemovalList() {
        charsLock.writeLock().lock();
        try {
            if (!removalList.isEmpty()) {
                for (Char removeChar : removalList) {
                    CharacterId removeId = removeChar.getCharId();
                    if (removeId == null) {
                        log.error("Character without ID located in remove list.");
                        continue;
                    }
                    removeCharacter(removeId);
                }
                removalList.clear();
            }
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Clear the list of characters and recycle all of them.
     */
    public void clear() {
        if (World.getPlayer().getCombatHandler().isAttacking()) {
            World.getPlayer().getCombatHandler().standDown();
        }
        charsLock.writeLock().lock();
        try {
            cleanRemovalList();
            for (Char character : chars.values()) {
                character.markAsRemoved();
            }
            chars.clear();
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Check all known characters if they are outside of the screen and hide them from the screen. Save them still to
     * the characters that are known to left the screen.
     */
    public void clipCharacters() {
        charsLock.writeLock().lock();
        try {
            @Nonnull Player player = World.getPlayer();
            for (Char character : chars.values()) {
                if (!player.isOnScreen(character.getLocation(), 0)) {
                    addCharacterToRemoveList(character);
                }
            }
            cleanRemovalList();
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * This function creates a new character and requests the required information from the server.
     *
     * @param id the ID of the character to be created
     * @return the created character
     */
    @Nonnull
    private Char createNewCharacter(@Nonnull CharacterId id) {
        log.debug("Creating new character: {}", id);
        Char chara = new Char();
        chara.setCharId(id);

        addCharacter(chara);

        World.getNet().sendCommand(new RequestAppearanceCmd(id));
        return chara;
    }

    /**
     * Get a character out of the list of the known characters.
     *
     * @param id ID of the requested character
     * @return the character or {@code null} if it does not exist
     */
    @Nullable
    @Contract(value = "null -> null", pure = true)
    public Char getCharacter(@Nullable CharacterId id) {
        if (id == null) {
            return null;
        }
        if (World.getPlayer().isPlayer(id)) {
            return World.getPlayer().getCharacter();
        }

        charsLock.readLock().lock();
        try {
            return chars.get(id);
        } finally {
            charsLock.readLock().unlock();
        }
    }

    /**
     * Get the character on a special location on the map.
     *
     * @param coordinate the location the character is searched at
     * @return the character or {@code null} if not found
     */
    @Nullable
    public Char getCharacterAt(@Nonnull ServerCoordinate coordinate) {
        Char playerChar = World.getPlayer().getCharacter();
        if (playerChar.getLocation().equals(coordinate)) {
            return playerChar;
        }

        charsLock.readLock().lock();
        try {

            for (Char character : chars.values()) {
                if (character.getLocation().equals(coordinate)) {
                    return character;
                }
            }
        } finally {
            charsLock.readLock().unlock();
        }

        return null;
    }

    /**
     * Remove a character from the game list and recycle the character reference for later usage. Also clean up
     * everything related to this character such as the attacking marker.
     *
     * @param id the ID of the character that shall be removed
     */
    public void removeCharacter(@Nonnull CharacterId id) {
        if (World.getPlayer().isPlayer(id)) {
            throw new IllegalArgumentException("Removing the player character from the people list is not legal.");
        }

        charsLock.writeLock().lock();
        try {
            Char chara = chars.get(id);
            if (chara != null) {
                EventBus.publish(new CharRemovedEvent(id));
                // cancel attack when character is removed
                if (World.getPlayer().getCombatHandler().isAttacking(chara)) {
                    World.getPlayer().getCombatHandler().standDown();
                }
                chars.remove(id);
                chara.markAsRemoved();
            }
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Get the string representation of this instance.
     */
    @Override
    @Nonnull
    public String toString() {
        charsLock.readLock().lock();
        try {
            return String.format(TO_STRING_TEXT, chars.size());
        } finally {
            charsLock.readLock().unlock();
        }
    }

    /**
     * Force update of light values.
     */
    void updateLight() {
        World.getPlayer().getCharacter().updateLight(Char.LIGHT_UPDATE);

        charsLock.readLock().lock();
        try {
            for (Char character : chars.values()) {
                character.updateLight(Char.LIGHT_UPDATE);
            }
        } finally {
            charsLock.readLock().unlock();
        }
    }
}

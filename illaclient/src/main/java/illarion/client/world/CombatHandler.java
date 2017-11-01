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

import illarion.client.net.client.AttackCmd;
import illarion.client.net.client.StandDownCmd;
import illarion.common.types.CharacterId;
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is used to store and set the current combat mode. It will forward all changes to the combat mode to the
 * other parts of the client, that need to be notified about it.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
public final class CombatHandler {
    /**
     * The logging instance of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(CombatHandler.class);

    /**
     * The character that is currently under attack.
     */
    @Nullable
    private Char attackedChar;

    /**
     * The characters that were not yet confirmed by the server for the attack.
     */
    @Nonnull
    private final Queue<Char> unconfirmedChars = new LinkedList<>();

    /**
     * This flag is used to track if the next target lost command from the server is supposed to be ignored.
     */
    @Nonnull
    private final AtomicBoolean ignoreNextTargetLost = new AtomicBoolean(false);

    /**
     * Test if the player is currently attacking anyone.
     *
     * @return {@code true} in case anyone is attacked
     */
    @Contract(pure = true)
    public boolean isAttacking() {
        return (attackedChar != null) || !unconfirmedChars.isEmpty();
    }

    /**
     * In case this character is attacked, stand down. In case its not attacked, attack it.
     *
     * @param character the character to start or stop attacking
     */
    public void toggleAttackOnCharacter(@Nonnull Char character) {
        if (isAttacking(character)) {
            standDown();
        } else {
            setAttackTarget(character);
        }
    }

    /**
     * Test if a character is currently attacked.
     *
     * @param testChar the char to check if he is the current target
     * @return {@code true} in case the character is the current target
     */
    @Contract(pure = true)
    public boolean isAttacking(@Nonnull Char testChar) {
        return testChar.equals(attackedChar);
    }

    /**
     * Test if a character is about to be attacked, but the attack is not yet confirmed by the server.
     *
     * @param testChar the character to check
     * @return {@code true} if that character is scheduled to be attacked.
     */
    @Contract(pure = true)
    public boolean isGoingToAttack(@Nonnull Char testChar) {
        return unconfirmedChars.contains(testChar);
    }

    /**
     * Stop the current attack any report this to the server in case its needed. This does not directly stop the
     * attack. It just requests to stop the attack from the server.
     */
    public void standDown() {
        Char localAttackedChat = attackedChar;
        attackedChar = null;
        if (localAttackedChat != null) {
            ignoreNextTargetLost.set(true);
            World.getNet().sendCommand(new StandDownCmd());
        } else {
            Char localUnconfirmed = unconfirmedChars.poll();
            if (localUnconfirmed != null) {
                ignoreNextTargetLost.set(true);
                World.getNet().sendCommand(new StandDownCmd());
            }
        }
    }

    /**
     * This function does nearly the same as {@link #standDown()}. How ever it does not result in sending a stand down
     * command to the server.
     */
    public void targetLost() {
        if (ignoreNextTargetLost.getAndSet(false)) {
            log.debug("Expected target lost received from server. No action taken.");
        } else {
            log.debug("Target lost received from server. Stopping attack.");
            if (attackedChar == null) {
                unconfirmedChars.poll();
            } else {
                attackedChar = null;
            }
        }
    }

    /**
     * This function is called once the server is confirming that the attack starts.
     */
    public void confirmAttack() {
        attackedChar = unconfirmedChars.poll();
        log.debug("Attack confirmed received from server. Now attacking: {}", attackedChar);
    }


    /**
     * Attack the nearest monster, using euclidean distance on same Z level.
     */
    public void attackNearestMonster() {
        log.debug("Looking for nearest monster to atttack");
        List<Char> allKnownChars = World.getPeople().getAllCharacters();
        Char candidateChar = null;
        double candidateDistance = Double.POSITIVE_INFINITY;
        for (Char character : allKnownChars) {
            if (character.isMonster()) {
                double distance = ServerCoordinate.getDistance(character.getLocation(), World.getPlayer().getLocation());
                if ((character.getLocation().getZ() == World.getPlayer().getLocation().getZ()) && (distance < candidateDistance)) {
                    // found a closer monster
                    candidateChar = character;
                    candidateDistance = distance;
                    if (candidateDistance <= 1)
                        // good enough
                        break;
                }
            }
        }
        if (candidateChar != null) {
            setAttackTarget(candidateChar);
        }
    }

    /**
     * Set the character that is attacked from now in.
     *
     * @param character the character that is now attacked
     */
    public void setAttackTarget(@Nonnull Char character) {
        // Disable chat box to allow proper movement. Does not clear input.
        World.getGameGui().getChatGui().deactivateChatBox(false);
        if (isAttacking(character) || isGoingToAttack(character)) {
            return;
        }

        CharacterId characterId = character.getCharId();
        if (characterId == null) {
            log.error("Trying to attack a character without character ID.");
            standDown();
            return;
        }

        if (canBeAttacked(character)) {
            unconfirmedChars.offer(character);
            sendAttackToServer(characterId);
        } else {
            standDown();
        }
    }

    /**
     * Check if the character can be attacked.
     *
     * @param character the character to check
     * @return {@code true} in case the character is not the player and not a NPC.
     */
    @Contract(pure = true)
    public boolean canBeAttacked(@Nonnull Char character) {
        return !World.getPlayer().isPlayer(character.getCharId()) && !character.isNPC();
    }

    /**
     * Send a attack command to the server that initiates a fight with the character that ID is set in the parameter.
     *
     * @param id the ID of the character to fight
     */
    private static void sendAttackToServer(@Nonnull CharacterId id) {
        World.getNet().sendCommand(new AttackCmd(id));
    }
}

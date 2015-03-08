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
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
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
        return attackedChar != null;
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
     * Stop the current attack any report this to the server in case its needed. This does not directly stop the
     * attack. It just requests to stop the attack from the server.
     */
    public void standDown() {
        Char localAttackedChat = attackedChar;
        attackedChar = null;
        if (localAttackedChat != null) {
            ignoreNextTargetLost.set(true);
            World.getNet().sendCommand(new StandDownCmd());
        }
    }

    /**
     * This function does nearly the same as {@link #standDown()}. How ever it does not result in sending a stand down
     * command to the server.
     */
    public void targetLost() {
        if (!ignoreNextTargetLost.getAndSet(false)) {
            attackedChar = null;
        }
    }

    /**
     * Set the character that is attacked from now in.
     *
     * @param character the character that is now attacked
     */
    public void setAttackTarget(@Nonnull Char character) {
        if (isAttacking(character)) {
            return;
        }

        CharacterId characterId = character.getCharId();
        if (characterId == null) {
            log.error("Trying to attack a character without character ID.");
            standDown();
            return;
        }

        if (canBeAttacked(character)) {
            attackedChar = character;
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

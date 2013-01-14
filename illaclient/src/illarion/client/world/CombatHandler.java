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

import illarion.client.net.client.AttackCmd;
import illarion.client.net.client.StandDownCmd;
import illarion.common.types.CharacterId;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This class is used to store and set the current combat mode. It will forward all changes to the combat mode to the
 * other parts of the client, that need to be notified about it.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
public final class CombatHandler {
    /**
     * The singleton instance of this class.
     */
    private static final CombatHandler INSTANCE = new CombatHandler();

    /**
     * The character that is currently under attack.
     */
    @GuardedBy("this")
    private Char attackedChar;

    /**
     * Private constructor to ensure that only the singleton instance is created.
     */
    private CombatHandler() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static CombatHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Test if the player is currently attacking anyone.
     *
     * @return <code>true</code> in case anyone is attacked
     */
    public boolean isAttacking() {
        synchronized (this) {
            return attackedChar != null;
        }
    }

    /**
     * In case this character is attacked, stand down. In case its not attacked, attack it.
     *
     * @param character the character to start or stop attacking
     */
    public void toggleAttackOnCharacter(@Nonnull final Char character) {
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
     * @return <code>true</code> in case the character is the current target
     */
    public boolean isAttacking(@Nonnull final Char testChar) {
        synchronized (this) {
            return isAttacking() && testChar.equals(attackedChar);
        }
    }

    /**
     * Stop the current attack any report this to the server in case its needed. This does not directly stop the
     * attack. It just requests to stop the attack from the server.
     */
    public void standDown() {
        synchronized (this) {
            if (attackedChar != null) {
                World.getNet().sendCommand(new StandDownCmd());
                targetLost();
            }
        }
    }

    /**
     * This function does nearly the same as {@link #standDown()}. How ever it does not result in sending a stand down
     * command to the server.
     */
    public void targetLost() {
        synchronized (this) {
            if (attackedChar != null) {
                attackedChar.setAttackMarker(false);
                attackedChar = null;
            }
        }
        World.getMusicBox().stopFightingMusic();
    }

    /**
     * The logging instance of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(CombatHandler.class);

    /**
     * Set the character that is attacked from now in.
     *
     * @param character the character that is now attacked
     */
    public void setAttackTarget(@Nonnull final Char character) {
        synchronized (this) {
            if (character == attackedChar) {
                return;
            }

            standDown();

            final CharacterId characterId = character.getCharId();
            if (characterId == null) {
                LOGGER.error("Trying to attack a character without character ID.");
                return;
            }

            if (canBeAttacked(character)) {
                attackedChar = character;
                sendAttackToServer(characterId);
                character.setAttackMarker(true);
                World.getMusicBox().playFightingMusic();
            }
        }
    }

    /**
     * Check if the character can be attacked.
     *
     * @param character the character to check
     * @return {@code true} in case the character is not the player and not a NPC.
     */
    public boolean canBeAttacked(final Char character) {
        return !World.getPlayer().isPlayer(character.getCharId()) && !character.isNPC();
    }

    /**
     * Send a attack command to the server that initiates a fight with the character that ID is set in the parameter.
     *
     * @param id the ID of the character to fight
     */
    private static void sendAttackToServer(@Nonnull final CharacterId id) {
        World.getNet().sendCommand(new AttackCmd(id));
    }
}

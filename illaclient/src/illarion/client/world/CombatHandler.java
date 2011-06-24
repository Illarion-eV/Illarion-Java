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

import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.AttackCmd;

/**
 * This class is used to store and set the current combat mode. It will forward
 * all changes to the combat mode to the other parts of the client, that need to
 * be notified about it.
 * 
 * @serial exclude
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class CombatHandler {
    /**
     * The singleton instance of this class.
     */
    private static final CombatHandler INSTANCE = new CombatHandler();

    /**
     * The character that is currently under attack.
     */
    private Char attackedChar;

    /**
     * Stores the current combat mode.
     */
    private boolean combatActive;

    /**
     * Private constructor to ensure that only the singleton instance is
     * created.
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
        return (attackedChar != null);
    }

    /**
     * Test if a character is currently attacked.
     * 
     * @param testChar the char to check if he is the current target
     * @return <code>true</code> in case the character is the current target
     */
    public boolean isAttacking(final Char testChar) {
        return ((testChar != null) && (testChar == attackedChar));
    }

    /**
     * Get the current combat mode.
     * 
     * @return <code>true</code> in case the combat mode is currently active
     */
    public boolean isCombatMode() {
        return combatActive;
    }

    /**
     * Set the character that is attacked from now in.
     * 
     * @param character the character that is now attacked
     */
    public void setAttackTarget(final Char character) {
        if (character == attackedChar) {
            standDown();
            return;
        }
        standDown();

        if (character != null) {
            attackedChar = character;
            final AttackCmd cmd =
                (AttackCmd) CommandFactory.getInstance().getCommand(
                    CommandList.CMD_ATTACK);
            cmd.setTarget(character.getCharId());
            Game.getNet().sendCommand(cmd);
            character.setAttackMarker(true);
        }
    }

    /**
     * Set the new combat mode.
     * 
     * @param newMode <code>true</code> to enable the combat mode
     */
    public void setCombatMode(final boolean newMode) {
        if (combatActive && !newMode) {
            combatActive = false;
            standDown();
        } else if (!combatActive && newMode) {
            combatActive = true;
            Game.getMusicBox().playFightingMusic();
        }
    }

    /**
     * Stop the current attack any report this to the server in case its needed.
     * This does not directly stop the attack. It just requests to stop the
     * attack from the server.
     */
    public void standDown() {
        if (attackedChar != null) {
            Game.getNet().sendCommand(
                CommandFactory.getInstance().getCommand(
                    CommandList.CMD_STAND_DOWN));
            Game.getMusicBox().stopFightingMusic();
        }
    }

    /**
     * This function does nearly the same as {@link #standDown()}. How ever it
     * does not result in sending a stand down command to the server.
     */
    public void targetLost() {
        if (attackedChar != null) {
            attackedChar.setAttackMarker(false);
            attackedChar = null;
        }
    }

    /**
     * Toggle the combat mode.
     * 
     * @return <code>true</code> in case the combat mode is now active
     */
    public boolean toggleCombatMode() {
        setCombatMode(!combatActive);
        return combatActive;
    }
}

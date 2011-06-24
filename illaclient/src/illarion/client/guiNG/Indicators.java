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
package illarion.client.guiNG;

/**
 * This is a simple helper class for accessing the indicators on the GUI.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Indicators {
    /**
     * The object that can be used to lock the indicators for synchronization
     * issues.
     */
    public static final Object LOCK = new Object();

    /**
     * The food indicator.
     */
    private Indicator food;

    /**
     * The health indicator.
     */
    private Indicator health;

    /**
     * The mana indicator.
     */
    private Indicator mana;

    /**
     * Default level constructor to ensure that only the GUI creates a instance
     * of this class.
     */
    Indicators() {
        // nothing to do
    }

    /**
     * Get the food indicator of the GUI. This function freezes the requesting
     * thread until this bar is available. Just in case the server sends the
     * informations before the GUI is ready.
     * 
     * @return the food indicator of the GUI or <code>null</code> in case the
     *         GUI is not ready yet
     */
    public Indicator getFood() {
        return food;
    }

    /**
     * Get the health indicator of the GUI. This function freezes the requesting
     * thread until this bar is available. Just in case the server sends the
     * informations before the GUI is ready.
     * 
     * @return the health indicator of the GUI or <code>null</code> in case the
     *         GUI is not ready yet
     */
    public Indicator getHealth() {
        return health;
    }

    /**
     * Get the mana indicator of the GUI. This function freezes the requesting
     * thread until this bar is available. Just in case the server sends the
     * informations before the GUI is ready.
     * 
     * @return the mana indicator of the GUI or <code>null</code> in case the
     *         GUI is not ready yet
     */
    public Indicator getMana() {
        return mana;
    }

    /**
     * Register the food bar in this storage.
     * 
     * @param newFood the food bar that is stored in this storage
     */
    public void registerFood(final Indicator newFood) {
        food = newFood;
    }

    /**
     * Register the health bar in this storage.
     * 
     * @param newHealth the health bar that is stored in this storage
     */
    public void registerHealth(final Indicator newHealth) {
        health = newHealth;
    }

    /**
     * Register the mana bar in this storage.
     * 
     * @param newMana the mana bar that is stored in this storage
     */
    public void registerMana(final Indicator newMana) {
        mana = newMana;
    }
}

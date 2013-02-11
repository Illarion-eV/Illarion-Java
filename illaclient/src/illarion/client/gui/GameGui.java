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
package illarion.client.gui;

import de.lessvoid.nifty.screen.ScreenController;
import org.newdawn.slick.GameContainer;

import javax.annotation.Nonnull;

/**
 * This interface is the global accessor to the GUI of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("InterfaceNamingConvention")
public interface GameGui {
    /**
     * Get the book GUI control.
     *
     * @return the controller of the book display
     */
    @Nonnull
    BookGui getBookGui();

    /**
     * Get the GUI that controls the display of the chat window and the display of the chat on the screen.
     *
     * @return the chat GUI controller
     */
    @Nonnull
    ChatGui getChatGui();

    /**
     * Get the GUI controller that is used to display the input dialoges.
     *
     * @return the input dialog GUI control
     */
    @Nonnull
    DialogInputGui getDialogInputGui();

    /**
     * Get the GUI controller that is used to display message dialogs.
     *
     * @return the message dialog GUI control
     */
    @Nonnull
    DialogMessageGui getDialogMessageGui();

    /**
     * Get the GUI that controls the display of the inform messages.
     *
     * @return the inform GUI controller
     */
    @Nonnull
    InformGui getInformGui();

    /**
     * Get the main screen controller of the GUI.
     *
     * @return the GUI screen controller
     */
    @Nonnull
    ScreenController getScreenController();

    /**
     * Get the GUI that controls the skill display in the GUI.
     *
     * @return the skill GUI controller
     */
    @Nonnull
    SkillGui getSkillGui();

    /**
     * Check if the GUI is ready to work.
     *
     * @return {@code true} in case the GUI is ready
     */
    boolean isReady();

    /**
     * Get the GUI that controls the display of the inventory.
     *
     * @return the inventory gui controller
     */
    @Nonnull
    InventoryGui getInventoryGui();

    /**
     * Update the GUI. This should be called once during the update cycle.
     *
     * @param container the container of the game
     * @param delta     the time since the last update in milliseconds
     */
    void onUpdateGame(GameContainer container, int delta);
}

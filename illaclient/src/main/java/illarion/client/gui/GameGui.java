/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.gui;

import de.lessvoid.nifty.screen.ScreenController;
import org.illarion.engine.GameContainer;

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
     * Get the GUI that controls the display of item containers in the game.
     *
     * @return the item container gui controller
     */
    @Nonnull
    ContainerGui getContainerGui();

    /**
     * Get the GUI controller that is used to display the dialogs.
     *
     * @return the dialog GUI control
     */
    @Nonnull
    DialogGui getDialogGui();

    /**
     * Get the GUI controller that is used to display the crafting dialogs.
     *
     * @return the crafting dialog GUI control
     */
    @Nonnull
    DialogCraftingGui getDialogCraftingGui();

    /**
     * Get the GUI controller that is used to display the merchant dialogs.
     *
     * @return the crafting dialog GUI control
     */
    @Nonnull
    DialogMerchantGui getDialogMerchantGui();

    /**
     * Get the GUI controller that is used to display the input dialogs.
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
     * Get the controller that displays the elements that are directly related to the game game.
     *
     * @return the game map gui controller
     */
    @Nonnull
    GameMapGui getGameMapGui();

    /**
     * Get the GUI that controls the display of the inform messages.
     *
     * @return the inform GUI controller
     */
    @Nonnull
    InformGui getInformGui();

    /**
     * Get the GUI that controls the display of the inventory.
     *
     * @return the inventory gui controller
     */
    @Nonnull
    InventoryGui getInventoryGui();

    /**
     * Get the GUI that shows the status of the player. So the hit points, food points and mana points.
     *
     * @return the player character GUI controller
     */
    @Nonnull
    PlayerStatusGui getPlayerStatusGui();

    /**
     * Get the GUI that control the display of the quests in the GUI.
     *
     * @return the quest GUI controller
     */
    @Nonnull
    QuestGui getQuestGui();

    /**
     * Get the GUI that control the display of the documentation in the GUI.
     *
     * @return the documentation GUI controller
     */
    @Nonnull
    DocumentationGui getDocumentationGui();

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
     * Get the GUI that controls the mini map display.
     *
     * @return the mini map display control
     */
    @Nonnull
    MiniMapGui getMiniMapGui();

    @Nonnull
    CloseGameGui getCloseGameGui();

    /**
     * Check if the GUI is ready to work.
     *
     * @return {@code true} in case the GUI is ready
     */
    boolean isReady();

    /**
     * Update the GUI. This should be called once during the update cycle.
     *
     * @param container the container of the game
     * @param delta the time since the last update in milliseconds
     */
    void onUpdateGame(@Nonnull GameContainer container, int delta);
}

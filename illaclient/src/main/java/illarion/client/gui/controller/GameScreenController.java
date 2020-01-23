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
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.gui.*;
import illarion.client.gui.controller.game.*;
import org.illarion.engine.GameContainer;
import org.illarion.engine.input.Input;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is the global accessor to the GUI of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GameScreenController implements GameGui, ScreenController {
    /**
     * ALL child ScreenControllers, such as Skills, Inventory, etc.
     */
    @Nonnull
    private final Collection<ScreenController> childControllers;
    /**
     * The child ScreenControllers that are updatable
     */
    @Nonnull
    private final Collection<UpdatableHandler> childUpdateControllers;

    /**
     * These handlers are all of the GUIs that can be displayed during the game
     * Any new In-Game GUIs must be declared here
     */
    @Nonnull
    private final BookHandler bookHandler;
    @Nonnull
    private final DialogHandler dialogHandler;
    @Nonnull
    private final SkillsHandler skillsHandler;
    @Nonnull
    private final InformHandler informHandler;
    @Nonnull
    private final GUIChatHandler chatHandler;
    @Nonnull
    private final GUIInventoryHandler inventoryHandler;
    @Nonnull
    private final ContainerHandler containerHandler;
    @Nonnull
    private final GameMapHandler gameMapHandler;
    @Nonnull
    private final QuestHandler questHandler;
    @Nonnull
    private final DocumentationHandler documentationHandler;
    @Nonnull
    private final GameMiniMapHandler gameMiniMapHandler;
    @Nonnull
    private final CharStatusHandler charStatusHandler;
    @Nonnull
    private final CloseGameHandler closeGameHandler;

    /**
     * Indicates that the screen has been setup by calling bind(Nifty, Screen)
     */
    private boolean ready;

    /**
     * Initializes all of the child handlers, adds them to appropriate collections
     *
     * Any new In-Game GUIs need to be added to this method
     *
     * @param input The Engine's input system
     */
    public GameScreenController(@Nonnull Input input) {
        NumberSelectPopupHandler numberPopupHandler = new NumberSelectPopupHandler();
        TooltipHandler tooltipHandler = new TooltipHandler();

        childControllers = new ArrayList<>();
        childUpdateControllers = new ArrayList<>();

        chatHandler = new GUIChatHandler();
        bookHandler = new BookHandler();
        dialogHandler = new DialogHandler(input, numberPopupHandler, tooltipHandler);
        skillsHandler = new SkillsHandler();
        informHandler = new InformHandler();
        inventoryHandler = new GUIInventoryHandler(input, numberPopupHandler, tooltipHandler);
        containerHandler = new ContainerHandler(input, numberPopupHandler, tooltipHandler);
        gameMapHandler = new GameMapHandler(input, numberPopupHandler, tooltipHandler);
        gameMiniMapHandler = new GameMiniMapHandler();
        questHandler = new QuestHandler();
        documentationHandler = new DocumentationHandler();
        charStatusHandler = new CharStatusHandler();
        closeGameHandler = new CloseGameHandler();

        addHandler(numberPopupHandler);
        addHandler(tooltipHandler);
        addHandler(chatHandler);
        addHandler(bookHandler);
        addHandler(inventoryHandler);
        addHandler(dialogHandler);
        addHandler(containerHandler);
        addHandler(closeGameHandler);
        addHandler(charStatusHandler);
        addHandler(skillsHandler);
        addHandler(questHandler);
        addHandler(documentationHandler);

        addHandler(gameMapHandler);
        addHandler(gameMiniMapHandler);

        addHandler(informHandler);
    }

    /**
     * Adds the handler to either the childControllers and childUpdatableHandlers if appropriate
     * @param handler   the ScreenController to be added
     */
    private void addHandler(ScreenController handler) {
        childControllers.add(handler);
        if (handler instanceof UpdatableHandler) {
            childUpdateControllers.add((UpdatableHandler) handler);
        }
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public BookGui getBookGui() {
        return bookHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public ChatGui getChatGui() {
        return chatHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public ContainerGui getContainerGui() {
        return containerHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public DialogGui getDialogGui() {
        return dialogHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public DialogCraftingGui getDialogCraftingGui() {
        return dialogHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public DialogMerchantGui getDialogMerchantGui() {
        return dialogHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public DialogInputGui getDialogInputGui() {
        return dialogHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public DialogMessageGui getDialogMessageGui() {
        return dialogHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public DialogSelectionGui getDialogSelectionGui() {
        return dialogHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public GameMapGui getGameMapGui() {
        return gameMapHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public InformGui getInformGui() {
        return informHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public InventoryGui getInventoryGui() {
        return inventoryHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public PlayerStatusGui getPlayerStatusGui() {
        return charStatusHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public QuestGui getQuestGui() {
        return questHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public DocumentationGui getDocumentationGui() {
        return documentationHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public ScreenController getScreenController() {
        return this;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public SkillGui getSkillGui() {
        return skillsHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public MiniMapGui getMiniMapGui() {
        return gameMiniMapHandler;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public CloseGameGui getCloseGameGui() {
        return closeGameHandler;
    }

    @Override
    @Contract(pure = true)
    public boolean isReady() {
        return ready;
    }

    /**
     * Calls onEndScreen() for all child ScreenControllers
     * Cleans up the world
     * Saves the current configuration state
     */
    @Override
    public void onEndScreen() {
        childControllers.forEach(ScreenController::onEndScreen);
        IllaClient.getCfg().save();
    }

    /**
     * Starts up all child ScreenControllers
     */
    @Override
    public void onStartScreen() {
        childControllers.forEach(ScreenController::onStartScreen);
    }

    /**
     * This function is called once inside the game loop with the delta value of the current update loop. Inside this
     * functions changes to the actual representation of the GUI should be done.
     *
     * @param container the container that displays the game
     * @param delta the time since the last update call
     */
    @Override
    public void onUpdateGame(@Nonnull GameContainer container, int delta) {
        for (UpdatableHandler childController : childUpdateControllers) {
            childController.update(container, delta);
        }
    }

    /**
     * Calls bind() for all child ScreenControllers with the given arguments
     * Sets ready to {@code true} once all children are ready
     * @param nifty     The Nifty object for this instance of the game
     * @param screen    The Screen for this instance of the game
     */
    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        for (ScreenController childController : childControllers) {
            childController.bind(nifty, screen);
        }
        ready = true;
    }
}

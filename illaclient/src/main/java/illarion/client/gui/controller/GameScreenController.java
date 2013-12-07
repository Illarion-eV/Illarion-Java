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
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.gui.*;
import illarion.client.gui.controller.game.*;
import org.illarion.engine.GameContainer;
import org.illarion.engine.input.Input;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

public final class GameScreenController implements GameGui, ScreenController {
    @Nonnull
    private final Collection<ScreenController> childControllers;
    @Nonnull
    private final Collection<UpdatableHandler> childUpdateControllers;

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
    private final GameMiniMapHandler gameMiniMapHandler;

    private boolean ready;

    public GameScreenController(@Nonnull final Input input) {
        final NumberSelectPopupHandler numberPopupHandler = new NumberSelectPopupHandler();
        final TooltipHandler tooltipHandler = new TooltipHandler();

        childControllers = new ArrayList<ScreenController>();
        childUpdateControllers = new ArrayList<UpdatableHandler>();

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

        addHandler(numberPopupHandler);
        addHandler(tooltipHandler);
        addHandler(chatHandler);
        addHandler(bookHandler);
        addHandler(inventoryHandler);
        addHandler(dialogHandler);
        addHandler(containerHandler);
        addHandler(new CloseGameHandler());
        addHandler(new CharStatusHandler());
        addHandler(skillsHandler);
        addHandler(questHandler);

        addHandler(gameMapHandler);
        addHandler(gameMiniMapHandler);

        addHandler(informHandler);
    }

    private void addHandler(final ScreenController handler) {
        childControllers.add(handler);
        if (handler instanceof UpdatableHandler) {
            childUpdateControllers.add((UpdatableHandler) handler);
        }
    }

    @Nonnull
    @Override
    public BookGui getBookGui() {
        return bookHandler;
    }

    @Nonnull
    @Override
    public ChatGui getChatGui() {
        return chatHandler;
    }

    @Nonnull
    @Override
    public ContainerGui getContainerGui() {
        return containerHandler;
    }

    @Nonnull
    @Override
    public DialogCraftingGui getDialogCraftingGui() {
        return dialogHandler;
    }

    @Nonnull
    @Override
    public DialogInputGui getDialogInputGui() {
        return dialogHandler;
    }

    @Nonnull
    @Override
    public DialogMessageGui getDialogMessageGui() {
        return dialogHandler;
    }

    @Nonnull
    @Override
    public GameMapGui getGameMapGui() {
        return gameMapHandler;
    }

    @Nonnull
    @Override
    public InformGui getInformGui() {
        return informHandler;
    }

    @Nonnull
    @Override
    public InventoryGui getInventoryGui() {
        return inventoryHandler;
    }

    @Nonnull
    @Override
    public QuestGui getQuestGui() {
        return questHandler;
    }

    @Nonnull
    @Override
    public ScreenController getScreenController() {
        return this;
    }

    @Nonnull
    @Override
    public SkillGui getSkillGui() {
        return skillsHandler;
    }

    @Nonnull
    @Override
    public MiniMapGui getMiniMapGui() {
        return gameMiniMapHandler;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void onEndScreen() {
        for (final ScreenController childController : childControllers) {
            childController.onEndScreen();
        }
    }

    @Override
    public void onStartScreen() {
        for (final ScreenController childController : childControllers) {
            childController.onStartScreen();
        }
    }

    /**
     * This function is called once inside the game loop with the delta value of the current update loop. Inside this
     * functions changes to the actual representation of the GUI should be done.
     *
     * @param container the container that displays the game
     * @param delta     the time since the last update call
     */
    @Override
    public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
        for (final UpdatableHandler childController : childUpdateControllers) {
            childController.update(container, delta);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        for (final ScreenController childController : childControllers) {
            childController.bind(nifty, screen);
        }
        ready = true;
    }
}

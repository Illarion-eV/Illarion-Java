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
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

public final class GameScreenController implements GameGui, ScreenController {
    @Nonnull
    private final Collection<ScreenController> childControllers;
    @Nonnull
    private final Collection<UpdatableHandler> childUpdateControllers;

    private final BookHandler bookHandler;
    private final DialogHandler dialogHandler;
    private final SkillsHandler skillsHandler;
    private final InformHandler informHandler;
    private final GUIChatHandler chatHandler;

    private boolean ready;

    public GameScreenController(final Input input) {
        final NumberSelectPopupHandler numberPopupHandler = new NumberSelectPopupHandler();
        final TooltipHandler tooltipHandler = new TooltipHandler();

        childControllers = new ArrayList<ScreenController>();
        childUpdateControllers = new ArrayList<UpdatableHandler>();

        chatHandler = new GUIChatHandler();
        bookHandler = new BookHandler();
        dialogHandler = new DialogHandler(numberPopupHandler, tooltipHandler);
        skillsHandler = new SkillsHandler();
        informHandler = new InformHandler();

        addHandler(numberPopupHandler);
        addHandler(tooltipHandler);
        addHandler(chatHandler);
        addHandler(bookHandler);
        addHandler(new GUIInventoryHandler(input, numberPopupHandler, tooltipHandler));
        addHandler(dialogHandler);
        addHandler(new ContainerHandler(input, numberPopupHandler, tooltipHandler));
        addHandler(new CloseGameHandler());
        addHandler(new CharStatusHandler());
        addHandler(skillsHandler);

        addHandler(new GameMapHandler(numberPopupHandler, tooltipHandler));
        addHandler(new GameMiniMapHandler());

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
    public InformGui getInformGui() {
        return informHandler;
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

    @Override
    public boolean isReady() {
        return ready;
    }

    @Nonnull
    @Override
    public ChatGui getChatGui() {
        return chatHandler;
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
    public void onUpdateGame(final GameContainer container, final int delta) {
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

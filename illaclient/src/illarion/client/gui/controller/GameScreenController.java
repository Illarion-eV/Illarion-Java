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

import java.util.ArrayList;
import java.util.Collection;

public final class GameScreenController implements ScreenController {

    private Nifty parentNifty;

    private Collection<ScreenController> childControllers;
    private Collection<UpdatableHandler> childUpdateControllers;

    private boolean notifyResolutionChanged;

    public GameScreenController() {
        childControllers = new ArrayList<ScreenController>();
        childUpdateControllers = new ArrayList<UpdatableHandler>();

        addHandler(new GUIChatHandler());
        addHandler(new GUIInventoryHandler());
        addHandler(new CharListHandler());
        addHandler(new DialogHandler());
        addHandler(new ContainerHandler());

        addHandler(new GameMapClickHandler());
        addHandler(new GameMapDragHandler());

        addHandler(new InformHandler());
    }

    private void addHandler(final ScreenController handler) {
        childControllers.add(handler);
        if (handler instanceof UpdatableHandler) {
            childUpdateControllers.add((UpdatableHandler) handler);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        parentNifty = nifty;

        for (final ScreenController childController : childControllers) {
            childController.bind(nifty, screen);
        }
    }

    @Override
    public void onStartScreen() {
        if (notifyResolutionChanged) {
            parentNifty.resolutionChanged();
            notifyResolutionChanged = false;
        }

        for (final ScreenController childController : childControllers) {
            childController.onStartScreen();
        }
    }

    /**
     * This function is called once inside the game loop with the delta value of the current update loop. Inside this
     * functions changes to the actual representation of the GUI should be done.
     *
     * @param delta the time since the last update call
     */
    public void onUpdateGame(final int delta) {
        for (final UpdatableHandler childController : childUpdateControllers) {
            childController.update(delta);
        }
    }

    @Override
    public void onEndScreen() {
        for (final ScreenController childController : childControllers) {
            childController.onEndScreen();
        }
    }

    public void resolutionChanged() {
        notifyResolutionChanged = true;
    }
}

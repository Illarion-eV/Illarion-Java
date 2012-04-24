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

public class GameScreenController
        implements ScreenController {

    private Nifty parentNifty;

    private Collection<ScreenController> childControllers;

    private boolean notifyResolutionChanged;

    public GameScreenController() {
        childControllers = new ArrayList<ScreenController>();

        childControllers.add(new GUIChatHandler());
        childControllers.add(new GUIInventoryHandler());
        childControllers.add(new CharListHandler());
        childControllers.add(new DialogHandler());
        childControllers.add(new ContainerHandler());

        childControllers.add(new GameMapClickHandler());
        childControllers.add(new GameMapDoubleClickHandler());
        childControllers.add(new GameMapDragHandler());
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

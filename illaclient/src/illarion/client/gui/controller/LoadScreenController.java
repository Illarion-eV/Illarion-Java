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
import org.illarion.nifty.controls.Progress;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

public final class LoadScreenController
        implements ScreenController {

    private Nifty nifty;
    private Progress progress;

    private final StateBasedGame game;

    private boolean notifyResolutionChanged;

    public LoadScreenController(StateBasedGame game) {
        this.game = game;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        progress = screen.findNiftyControl("loading", Progress.class);
    }

    @Override
    public void onStartScreen() {

        if (notifyResolutionChanged) {
            nifty.resolutionChanged();
            notifyResolutionChanged = false;
        }
    }

    public void resolutionChanged() {
        notifyResolutionChanged = true;
    }

    private boolean loadingDoneCalled = false;

    public void loadingDone() {
        if (loadingDoneCalled) {
            return;
        }
        loadingDoneCalled = true;

        game.enterState(illarion.client.Game.STATE_PLAYING, new FadeOutTransition(), null);
    }

    public void setProgress(final float progressValue) {
        if (progress != null) {
            progress.setProgress(progressValue);
        }
    }

    @Override
    public void onEndScreen() {
        // nothing to do
    }
}

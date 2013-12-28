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
import illarion.client.Game;
import org.illarion.nifty.controls.Progress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class LoadScreenController
        implements ScreenController {

    @Nullable
    private Progress progress;

    @Nonnull
    private final Game game;

    public LoadScreenController(@Nonnull final Game game) {
        this.game = game;
    }

    @Override
    public void bind(final Nifty nifty, @Nonnull final Screen screen) {
        progress = screen.findNiftyControl("loading", Progress.class);
    }

    @Override
    public void onStartScreen() {
        loadingDoneCalled = false;
    }

    private boolean loadingDoneCalled;

    public void loadingDone() {
        if (loadingDoneCalled) {
            return;
        }
        loadingDoneCalled = true;

        game.enterState(Game.STATE_PLAYING);
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

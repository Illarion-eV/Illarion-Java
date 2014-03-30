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
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CreditsEndScreenController implements ScreenController, KeyInputHandler {
    private Nifty nifty;

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {
        nifty.gotoScreen("login");
    }

    @Override
    public void onEndScreen() {
        // nothing
    }

    @Override
    public boolean keyEvent(@Nonnull final NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.Escape) {
            nifty.gotoScreen("login");
            return true;
        }
        return false;
    }
}

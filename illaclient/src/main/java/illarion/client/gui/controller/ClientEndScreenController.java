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
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;

import javax.annotation.Nonnull;

/**
 * The controller for the last screen that is displayed before the client goes down.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ClientEndScreenController implements ScreenController, KeyInputHandler {
    @Override
    public void bind(final Nifty nifty, final Screen screen) {
    }

    @Override
    public void onStartScreen() {
        IllaClient.getCfg().save();
        IllaClient.exitGameContainer();
    }

    @Override
    public void onEndScreen() {
        // nothing
    }

    @Override
    public boolean keyEvent(@Nonnull final NiftyInputEvent inputEvent) {
        return false;
    }
}

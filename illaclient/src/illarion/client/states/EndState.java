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
package illarion.client.states;

import de.lessvoid.nifty.Nifty;
import illarion.client.Game;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;

/**
 * This state is activated during the shutdown of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class EndState implements GameState {

    @Override
    public void create(@Nonnull final Game game, @Nonnull final GameContainer container, @Nonnull final Nifty nifty) {
        Util.loadXML(nifty, "illarion/client/gui/xml/shutdown.xml");
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(@Nonnull final GameContainer container, final int width, final int height) {
    }

    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
    }

    @Override
    public void render(@Nonnull final GameContainer container) {
    }

    @Override
    public boolean isClosingGame() {
        return false;
    }

    @Override
    public void enterState(@Nonnull final GameContainer container, @Nonnull final Nifty nifty) {
        nifty.gotoScreen("clientEnd");
    }

    @Override
    public void leaveState(@Nonnull final GameContainer container) {
    }
}

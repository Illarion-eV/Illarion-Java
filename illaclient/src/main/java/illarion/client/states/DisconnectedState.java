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
package illarion.client.states;

import de.lessvoid.nifty.Nifty;
import illarion.client.Game;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;

/**
 * This state is triggered in case the connection is terminated by a error or by the server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DisconnectedState implements GameState {
    @Override
    public void create(@Nonnull Game game, @Nonnull GameContainer container, @Nonnull Nifty nifty) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void resize(@Nonnull GameContainer container, int width, int height) {

    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {

    }

    @Override
    public void render(@Nonnull GameContainer container) {

    }

    @Override
    public boolean isClosingGame() {
        return false;
    }

    @Override
    public void enterState(@Nonnull GameContainer container, @Nonnull Nifty nifty) {

    }

    @Override
    public void leaveState(@Nonnull GameContainer container) {

    }
}

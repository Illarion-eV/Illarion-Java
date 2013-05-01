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
import illarion.client.Login;
import illarion.client.input.InputReceiver;
import illarion.client.world.MapDimensions;
import illarion.client.world.World;
import illarion.client.world.events.CloseGameEvent;
import org.bushe.swing.event.EventBus;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;

/**
 * This state is active while the player is playing the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class PlayingState implements GameState {
    /**
     * The input receiver of the game.
     */
    @Nonnull
    private final InputReceiver receiver;

    public PlayingState(@Nonnull final InputReceiver inputReceiver) {
        receiver = inputReceiver;
    }

    @Override
    public void create(@Nonnull final Game game, @Nonnull final GameContainer container, @Nonnull final Nifty nifty) {
        World.initGui(container.getEngine());
        nifty.registerScreenController(World.getGameGui().getScreenController());

        Util.loadXML(nifty, "illarion/client/gui/xml/gamescreen.xml");
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(@Nonnull final GameContainer container, final int width, final int height) {
        MapDimensions.getInstance().reportScreenSize(width, height);
    }

    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
        if (World.getGameGui().isReady()) {
            World.getUpdateTaskManager().onUpdateGame(container, delta);
        }
        World.getGameGui().onUpdateGame(container, delta);
        World.getWeather().update(delta);
        World.getMapDisplay().update(container, delta);
        World.getAnimationManager().animate(delta);
        World.getMusicBox().update();
    }

    @Override
    public void render(@Nonnull final GameContainer container) {
        World.getMap().getMinimap().render(container);
        World.getMapDisplay().render(container);
    }

    @Override
    public boolean isClosingGame() {
        EventBus.publish(new CloseGameEvent());
        return false;
    }

    @Override
    public void enterState(@Nonnull final GameContainer container, @Nonnull final Nifty nifty) {
        nifty.gotoScreen("gamescreen");
        receiver.setEnabled(true);

        if (Login.getInstance().login()) {
            MapDimensions.getInstance().reportScreenSize(container.getWidth(), container.getHeight());
        }
    }

    @Override
    public void leaveState(@Nonnull final GameContainer container) {
        receiver.setEnabled(false);
    }
}

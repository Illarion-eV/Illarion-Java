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
import de.lessvoid.nifty.slick2d.NiftyOverlayBasicGameState;
import de.lessvoid.nifty.slick2d.input.SlickSlickInputSystem;
import illarion.client.Game;
import illarion.client.Login;
import illarion.client.gui.controller.GameScreenController;
import illarion.client.input.InputReceiver;
import illarion.client.util.Lang;
import illarion.client.world.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import javax.annotation.Nonnull;

/**
 * This state is active while the player is playing the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class PlayingState extends NiftyOverlayBasicGameState {
    private GameScreenController gameScreenController;

    /* (non-Javadoc)
    * @see org.newdawn.slick.state.BasicGameState#getID()
    */
    @Override
    public int getID() {
        return Game.STATE_PLAYING;
    }

    @Override
    protected void initGameAndGUI(final GameContainer container, final StateBasedGame game)
            throws SlickException {
        initNifty(container, game, new SlickSlickInputSystem(new InputReceiver(this)));
    }

    @Override
    protected void prepareNifty(@Nonnull final Nifty nifty, final StateBasedGame game) {
        nifty.setLocale(Lang.getInstance().getLocale());
        gameScreenController = new GameScreenController(game.getContainer().getInput());
        nifty.registerScreenController(gameScreenController);

        try {
            nifty.validateXml("illarion/client/gui/xml/gamescreen.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        nifty.addXml("illarion/client/gui/xml/gamescreen.xml");
    }

    @Override
    protected void renderGame(@Nonnull final GameContainer container, final StateBasedGame game, @Nonnull final Graphics g)
            throws SlickException {
        World.getMap().getMinimap().render();
        World.getMapDisplay().render(g, container);
        World.getWeather().render(g, container);
    }

    @Override
    protected void updateGame(@Nonnull final GameContainer container, final StateBasedGame game, final int delta)
            throws SlickException {
        World.getUpdateTaskManager().onUpdateGame(container, game, delta);
        gameScreenController.onUpdateGame(container, delta);
        World.getWeather().update(delta);
        World.getMapDisplay().update(container, delta);
        World.getAnimationManager().animate(delta);
        World.getMusicBox().update();
    }

    @Override
    protected void enterState(final GameContainer container, final StateBasedGame game)
            throws SlickException {
        getNifty().gotoScreen("gamescreen");

        Login.getInstance().login();
    }

    @Override
    protected void leaveState(final GameContainer container, final StateBasedGame game)
            throws SlickException {
        // TODO Auto-generated method stub

    }

}

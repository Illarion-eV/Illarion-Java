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
import de.lessvoid.nifty.slick2d.input.PlainSlickInputSystem;
import illarion.client.Game;
import illarion.client.gui.controller.LoadScreenController;
import illarion.client.loading.Loading;
import illarion.client.util.Lang;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This game state is active while the game loads. It takes care for showing the loading screen and to trigger the
 * actual loading.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LoadingState
        extends NiftyOverlayBasicGameState {

    private LoadScreenController controller;

    private final Logger log = Logger.getLogger(LoadingState.class.getName());

    @Override
    protected void prepareNifty(final Nifty nifty, final StateBasedGame game) {
        nifty.setLocale(Lang.getInstance().getLocale());
        controller = new LoadScreenController(game);
        nifty.registerScreenController(controller);

        try {
            nifty.validateXml("illarion/client/gui/xml/loading.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        nifty.addXml("illarion/client/gui/xml/loading.xml");
    }

    public void enterState(final GameContainer container, final StateBasedGame game)
            throws SlickException {
        Loading.enlistMissingComponents();
        getNifty().gotoScreen("loading");
    }

    @Override
    protected void initGameAndGUI(final GameContainer container, final StateBasedGame game)
            throws SlickException {
        initNifty(container, game, new PlainSlickInputSystem());
    }

    @Override
    protected void leaveState(final GameContainer container, final StateBasedGame game)
            throws SlickException {

    }

    @Override
    public int getID() {
        return Game.STATE_LOADING;
    }

    @Override
    protected void renderGame(final GameContainer container, final StateBasedGame game, final Graphics g)
            throws SlickException {

        g.clear();

        final int remaining = LoadingList.get().getRemainingResources();
        final int total = LoadingList.get().getTotalResources();

        if (remaining > 0) {
            controller.setProgress((float) (total - remaining) / total);
        } else {
            controller.loadingDone();
        }

        if (remaining == 0) {
            return;
        }

        SlickCallable.enterSafeBlock();
        try {
            LoadingList.get().getNext().load();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to load resource.", e);
        } finally {
            SlickCallable.leaveSafeBlock();
        }
    }

    @Override
    protected void updateGame(final GameContainer container, final StateBasedGame game, final int delta)
            throws SlickException {

    }
}

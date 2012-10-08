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
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.Game;
import illarion.client.Login;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class CharScreenController implements ScreenController {

    private Nifty nifty;

    private ListBox<String> listBox;

    private final StateBasedGame game;
    private Label statusLabel;

    public CharScreenController(StateBasedGame game) {
        this.game = game;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        listBox = (ListBox<String>) screen.findNiftyControl("myListBox", ListBox.class);
        fillMyListBox();
        statusLabel = screen.findNiftyControl("statusText", Label.class);
        statusLabel.setHeight(new SizeValue("20" + SizeValue.PIXEL));
        statusLabel.setWidth(new SizeValue("180" + SizeValue.PIXEL));
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {

    }

    private void fillMyListBox() {

        final Login login = Login.getInstance();
        for (int i = 0; i < login.getCharacterCount(); i++) {
            listBox.addItem(login.getCharacterName(i));
        }
    }

    public void play() {
        boolean found = Login.getInstance().selectCharacter(listBox.getFocusItemIndex());

        if (!found) {
            statusLabel.setText("No character selected");
            statusLabel.getElement().getParent().layoutElements();
            return;
        }

        game.enterState(Game.STATE_LOADING, new FadeOutTransition(), null);
    }

    public void logout() {
        statusLabel.setText("");
        nifty.gotoScreen("login");
    }
}

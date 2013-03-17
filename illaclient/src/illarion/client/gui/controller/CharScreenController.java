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
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.Game;
import illarion.client.Login;
import illarion.client.util.Lang;
import illarion.common.config.ConfigChangedEvent;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;

import javax.annotation.Nonnull;

public class CharScreenController implements ScreenController, KeyInputHandler {

    private Nifty nifty;
    private Screen screen;

    private ListBox<String> listBox;

    private final Game game;
    private Label statusLabel;
    private boolean showLanguageChangedPopup;

    /**
     * The generated popup that is shown in case the client language got changed.
     */
    private Element popupLanguageChange;

    public CharScreenController(Game game) {
        this.game = game;
        AnnotationProcessor.process(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        nifty.setLocale(Lang.getInstance().getLocale());
        listBox = (ListBox<String>) screen.findNiftyControl("myListBox", ListBox.class);
        statusLabel = screen.findNiftyControl("statusText", Label.class);
        statusLabel.setHeight(new SizeValue("20" + SizeValue.PIXEL));
        statusLabel.setWidth(new SizeValue("180" + SizeValue.PIXEL));

        listBox.getElement().addInputHandler(this);
        popupLanguageChange = nifty.createPopup("languageChanged");

        fillMyListBox();
    }

    @EventTopicSubscriber(topic = Lang.LOCALE_CFG)
    public void onConfigChanged(final String topic, final ConfigChangedEvent event) {
        showLanguageChangedPopup = true;
    }

    @Override
    public void onStartScreen() {
        if (showLanguageChangedPopup) {
            nifty.showPopup(screen, popupLanguageChange.getId(), null);
            if (Lang.getInstance().isGerman()) {
                popupLanguageChange.findElementByName("#english").hideWithoutEffect();
            } else {
                popupLanguageChange.findElementByName("#german").hideWithoutEffect();
            }
            nifty.closePopup(popupLanguageChange.getId());
        }
    }

    @Override
    public void onEndScreen() {

    }

    private void fillMyListBox() {
        for (final Login.CharEntry entry : Login.getInstance().getCharacterList()) {
            if (entry.getStatus() == 0) {
                listBox.addItem(entry.getName());
            }
        }
    }

    public void play() {
        if (listBox.getSelection().isEmpty()) {
            statusLabel.setText("No character selected");
            statusLabel.getElement().getParent().layoutElements();
            return;
        }

        Login.getInstance().setLoginCharacter(listBox.getSelection().get(0));
        game.enterState(Game.STATE_LOADING);
    }

    public void logout() {
        statusLabel.setText("");
        nifty.gotoScreen("login");
    }

    @Override
    public boolean keyEvent(final NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.Activate) {
            play();
            return true;
        }
        return false;
    }
}

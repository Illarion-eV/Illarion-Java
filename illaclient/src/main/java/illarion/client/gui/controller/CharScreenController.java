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
import javax.annotation.Nullable;

/**
 * This is the screen controller that takes care for the logic behind the character selection screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharScreenController implements ScreenController, KeyInputHandler {
    /**
     * The instance of the Nifty-GUI that is used.
     */
    @Nullable
    private Nifty nifty;

    /**
     * The screen this controller is bound to.
     */
    @Nullable
    private Screen screen;

    /**
     * The list box that stores the character entries.
     */
    @Nullable
    private ListBox<String> listBox;

    /**
     * The game instance that is used.
     */
    @Nonnull
    private final Game game;

    /**
     * The label that displays any problems to the player.
     */
    @Nullable
    private Label statusLabel;

    /**
     * This flag is set {@code true} in case the language of the client was changed and the player needs to be
     * informed that a restart of the client is required.
     */
    private boolean showLanguageChangedPopup;

    /**
     * The generated popup that is shown in case the client language got changed.
     */
    @Nullable
    private Element popupLanguageChange;

    /**
     * Create a instance of the character screen controller.
     *
     * @param game the reference to the game that is required by the controller
     */
    public CharScreenController(@Nonnull final Game game) {
        this.game = game;
        AnnotationProcessor.process(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {
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

    /**
     * This class is used to react on changes of the configuration.
     *
     * @param topic the event topic
     * @param event the actual event data
     */
    @EventTopicSubscriber(topic = Lang.LOCALE_CFG)
    public void onConfigChanged(final String topic, final ConfigChangedEvent event) {
        showLanguageChangedPopup = true;
    }

    @Override
    public void onStartScreen() {
        if (nifty == null) {
            throw new IllegalStateException("Instance of Nifty is not set. Controller was not properly bound.");
        }
        if (popupLanguageChange == null) {
            throw new IllegalStateException(
                    "Language change popup was not created. Controller was not properly bound.");
        }
        if (showLanguageChangedPopup) {
            nifty.showPopup(screen, popupLanguageChange.getId(), null);
            if (Lang.getInstance().isGerman()) {
                popupLanguageChange.findElementById("#english").hideWithoutEffect();
            } else {
                popupLanguageChange.findElementById("#german").hideWithoutEffect();
            }
            nifty.closePopup(popupLanguageChange.getId());
        }
    }

    @Override
    public void onEndScreen() {
        showLanguageChangedPopup = false;
    }

    public void fillMyListBox() {
        if (listBox != null) {
            listBox.clear();
            for (@Nonnull final Login.CharEntry entry : Login.getInstance().getCharacterList()) {
                if (entry.getStatus() == 0) {
                    listBox.addItem(entry.getName());
                }
            }
        }
    }

    public void play() {
        if ((listBox == null) || (statusLabel == null)) {
            throw new IllegalStateException("CharScreenController was not bound properly.");
        }
        if (listBox.getSelection().isEmpty()) {
            statusLabel.setText("No character selected");
            statusLabel.getElement().getParent().layoutElements();
            return;
        }

        Login.getInstance().setLoginCharacter(listBox.getSelection().get(0));
        game.enterState(Game.STATE_LOADING);
    }

    public void logout() {
        if ((nifty == null) || (statusLabel == null)) {
            throw new IllegalStateException("CharScreenController was not bound properly.");
        }
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

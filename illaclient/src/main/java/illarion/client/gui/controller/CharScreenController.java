/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import de.lessvoid.nifty.controls.Button;
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
import illarion.client.Servers;
import illarion.client.util.Lang;
import illarion.common.config.ConfigChangedEvent;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
     * The button to proceed to play the game.
     */
    @Nullable
    private Button playButton;

    /**
     * The list box that stores the character entries.
     */
    @Nullable
    private ListBox<String> listBox;

    public static final int CHARS_LOADING = 0;
    public static final int CHARS_NONE_FOUND = 1;
    public static final int CHARS_SELECT = 2;

    /**
     * Number indicating the state: LOADING, NONE_FOUND or SELECT.
     */
    private int currentState;

    /**
     * Text panel saying load is in progress.
     */
    @Nullable
    private Element loadingCharsPanel;

    /**
     * Text panel saying no character was loaded.
     */
    @Nullable
    private Element noCharacterPanel;

    /**
     * List box with the character list.
     */
    @Nullable
    private Element characterSelectPanel;

    /**
     * Array with the three possible panels to display (loading, no character or select).
     */
    private Element[] panels;

    /**
     * Lock to avoid multiple calls to reload characters.
     */
    private ReentrantLock lockLoadChars = new ReentrantLock();

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

    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(CharScreenController.class);

    /**
     * Create a instance of the character screen controller.
     *
     * @param game the reference to the game that is required by the controller
     */
    public CharScreenController(@Nonnull Game game) {
        this.game = game;
        AnnotationProcessor.process(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        nifty.setLocale(Lang.getInstance().getLocale());

        playButton = screen.findNiftyControl("playBtn", Button.class);
        loadingCharsPanel = screen.findElementById("loading-chars");
        noCharacterPanel = screen.findElementById("no-character-available");
        characterSelectPanel = screen.findElementById("character-list");

        panels = new Element[] { loadingCharsPanel, noCharacterPanel, characterSelectPanel };
        updateState(CHARS_LOADING);

        listBox = (ListBox<String>) screen.findNiftyControl("myListBox", ListBox.class);
        statusLabel = screen.findNiftyControl("statusText", Label.class);
        statusLabel.setHeight(SizeValue.px(20));
        statusLabel.setWidth(SizeValue.px(180));

        listBox.getElement().addInputHandler(this);
        popupLanguageChange = nifty.createPopup("languageChanged");

        fillCharsListBox();
    }

    private void updateState(int newState) {
        log.debug("updateState() - {}", newState);

        currentState = newState;

        for (int index = 0; index < panels.length; index++) {
            panels[index].setVisible(index == newState);
        }

        playButton.setEnabled(newState == CHARS_SELECT);
    }

    /**
     * This class is used to react on changes of the configuration.
     *
     * @param topic the event topic
     * @param event the actual event data
     */
    @EventTopicSubscriber(topic = Lang.LOCALE_CFG)
    public void onConfigChanged(String topic, ConfigChangedEvent event) {
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

    public void reloadCharacters() {
        if (!lockLoadChars.tryLock()) {
            return;
        }

        log.warn("reloadCharacters");
        updateState(CHARS_LOADING);

        Login.getInstance().requestCharacterList(_errorCode -> {
            log.debug("requestCharacterList - " + _errorCode);
            fillCharsListBox();
            lockLoadChars.unlock();
        });
    }

    public void fillCharsListBox() {
        log.debug("fillCharsListBox()");

        if (listBox == null) {
            return;
        }

        listBox.clear();

        List<Login.CharEntry> data = Login.getInstance().getCharacterList().stream().filter(entry -> entry.getStatus() == 0).collect(Collectors.toList());
        log.info("Character data downloaded - {}", data.stream().map(Login.CharEntry::getName).collect(Collectors.toList()));

        if (data.isEmpty()) {
            updateState(CHARS_NONE_FOUND);
        } else {
            data.forEach(entry -> listBox.addItem(entry.getName()));
            updateState(CHARS_SELECT);
        }
    }

    public void openEditor() {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(Servers.CHARACTER_EDIT_URL));
            } catch (IOException | URISyntaxException e1) {
                log.warn("Can't launch browser: ", e1);
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
        game.enterState(Game.STATE_PLAYING);
    }

    public void logout() {
        if ((nifty == null) || (statusLabel == null)) {
            throw new IllegalStateException("CharScreenController was not bound properly.");
        }
        statusLabel.setText("");
        nifty.gotoScreen("login");
    }

    @Override
    public boolean keyEvent(@Nonnull NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.Activate) {
            play();
            return true;
        }
        return false;
    }
}

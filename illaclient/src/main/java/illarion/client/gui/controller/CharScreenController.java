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
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.util.Lang;
import illarion.client.util.account.Credentials;
import illarion.client.util.account.response.AccountGetCharResponse;
import illarion.client.util.account.response.AccountGetCharsResponse;
import illarion.client.util.account.response.AccountGetResponse;
import illarion.common.config.ConfigChangedEvent;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

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

    private DropDown<String> serverSelect;

    /**
     * The list box that stores the character entries.
     */
    @Nullable
    private ListBox<String> characterList;

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
    @Nullable
    private Credentials credentials;
    @Nullable
    private AccountGetResponse accountData;

    /**
     * Create a instance of the character screen controller.
     */
    public CharScreenController() {
        AnnotationProcessor.process(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        serverSelect = screen.findNiftyControl("server", DropDown.class);

        nifty.setLocale(Lang.getInstance().getLocale());
        characterList = (ListBox<String>) screen.findNiftyControl("myListBox", ListBox.class);
        statusLabel = screen.findNiftyControl("statusText", Label.class);
        statusLabel.setHeight(SizeValue.px(20));
        statusLabel.setWidth(SizeValue.px(180));

        characterList.getElement().addInputHandler(this);
        popupLanguageChange = nifty.createPopup("languageChanged");

        /* Values for the server list are already set. */
        if (credentials != null && accountData != null) {
            populateServerList();
        }
    }

    @Override
    public void onStartScreen() {
        assert nifty != null: "The Nifty instance is null, binding seems to have failed.";
        assert popupLanguageChange != null: "The Nifty instance is null, binding seems to have failed.";

        nifty.subscribeAnnotations(this);

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
        assert nifty != null: "The Nifty instance is null, binding seems to have failed.";

        showLanguageChangedPopup = false;
        nifty.unsubscribeAnnotations(this);
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

    @NiftyEventSubscriber(pattern = "server")
    public void onServerChanged(String topic, DropDownSelectionChangedEvent event) {
        setSelectedServer(event.getSelectionItemIndex());
    }

    private void populateServerList() {
        assert accountData != null;
        assert serverSelect != null;

        serverSelect.clear();
        accountData.getChars().stream()
                .forEachOrdered(response -> serverSelect.addItem(response.getName()));

        Element serverSelectElement = serverSelect.getElement();
        assert serverSelectElement != null;
        if (serverSelect.getItems().size() == 1) {
            if (serverSelectElement.isVisible()) {
                serverSelectElement.hide();
            }
        } else {
            if (!serverSelectElement.isVisible()) {
                serverSelectElement.show();
            }
        }

        setDefaultSelectedServer();
    }

    private void setSelectedServer(int index) {
        assert accountData != null;
        assert serverSelect != null;
        assert characterList != null;
        assert index >= 0 && index < serverSelect.getItems().size();

        if (serverSelect.getSelectedIndex() != index) {
            serverSelect.selectItemByIndex(index);
        }

        AccountGetCharsResponse chars = accountData.getChars().get(index);

        characterList.clear();
        chars.getList().stream()
                .filter(chr -> chr.getStatus() == 0)
                .map(AccountGetCharResponse::getName)
                .forEach(characterList::addItem);
    }

    private void setDefaultSelectedServer() {
        assert accountData != null;
        assert serverSelect != null;
        assert serverSelect.getItems().size() == accountData.getChars().size();

        List<Date> latestUsed = new ArrayList<>(accountData.getChars().size());
        Comparator<AccountGetCharResponse> sortCharsComparator = new LastUsedFirstComparator();
        for (AccountGetCharsResponse chars : accountData.getChars()) {
            Optional<AccountGetCharResponse> lastUsedChar =
                    chars.getList().stream().sorted(sortCharsComparator).findFirst();
            if (lastUsedChar.isPresent()) {
                latestUsed.add(lastUsedChar.get().getLastSaveTime());
            } else {
                latestUsed.add(null);
            }
        }

        int serverIndex = 0;
        Date selectedDate = null;
        for (int i = 0; i < latestUsed.size(); i++) {
            Date current = latestUsed.get(i);
            if (current != null) {
                if (selectedDate == null || selectedDate.compareTo(current) < 0) {
                    selectedDate = current;
                    serverIndex = i;
                }
            }
        }

        setSelectedServer(serverIndex);
    }

    public void applyAccountData(@Nonnull Credentials credentials, @Nonnull AccountGetResponse accountData) {
        this.credentials = credentials;
        this.accountData = accountData;

        /* Checking if the controller is already bound. */
        if (nifty != null) {
            populateServerList();
        }
    }

    public void play() {
        assert nifty != null;
        assert accountData != null;
        assert credentials != null;
        assert characterList != null;
        assert statusLabel != null;

        if (characterList.getSelection().isEmpty()) {
            statusLabel.setText("No character selected");
            statusLabel.getElement().getParent().layoutElements();
            return;
        }

        int serverIndex = serverSelect.getSelectedIndex();
        AccountGetCharsResponse accountChars = accountData.getChars().get(serverIndex);


        Screen enteringScreen = nifty.getScreen("entering");
        assert enteringScreen != null;
        EnteringScreenController enteringSC = (EnteringScreenController) enteringScreen.getScreenController();

        enteringSC.setLoginInformation(credentials.getEndpoint(),
                accountChars.getId(),
                characterList.getSelection().get(0),
                credentials.getPassword());

        nifty.gotoScreen(enteringScreen.getScreenId());
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

    private static final class LastUsedFirstComparator implements Comparator<AccountGetCharResponse> {
        @Override
        public int compare(AccountGetCharResponse o1, AccountGetCharResponse o2) {
            return -1 * o1.getLastSaveTime().compareTo(o2.getLastSaveTime());
        }
    }
}

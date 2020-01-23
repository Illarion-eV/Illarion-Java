/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.graphics.AvatarClothManager.AvatarClothGroup;
import illarion.client.graphics.AvatarEntity;
import illarion.client.gui.EntityRenderImage;
import illarion.client.gui.controller.create.GenderScreenController;
import illarion.client.resources.CharacterFactory;
import illarion.client.resources.data.AvatarTemplate;
import illarion.client.util.Lang;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.Credentials;
import illarion.client.util.account.response.*;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.graphics.CharAnimations;
import illarion.common.types.AvatarId;
import illarion.common.types.CharacterId;
import illarion.common.types.Direction;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This is the screen controller that takes care for the logic behind the character selection screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharScreenController implements ScreenController, KeyInputHandler {
    @Nonnull
    private final AccountSystem accountSystem;
    @Nonnull
    private final GameContainer container;

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
    @Nullable
    private DropDown<String> serverSelect;
    /**
     * The list box that stores the character entries.
     */
    @Nullable
    private ListBox<CharacterEntry> characterList;
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
    @Nullable
    private Element characterPreviewImage;

    /**
     * Create a instance of the character screen controller.
     * @param accountSystem
     */
    public CharScreenController(@Nonnull GameContainer container, @Nonnull AccountSystem accountSystem) {
        this.container = container;
        this.accountSystem = accountSystem;
        AnnotationProcessor.process(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        serverSelect = screen.findNiftyControl("server", DropDown.class);

        nifty.setLocale(Lang.getInstance().getLocale());
        characterList = (ListBox<CharacterEntry>) screen.findNiftyControl("characterList", ListBox.class);
        statusLabel = screen.findNiftyControl("statusText", Label.class);
        statusLabel.setHeight(SizeValue.px(20));
        statusLabel.setWidth(SizeValue.px(180));

        characterList.getElement().addInputHandler(this);
        popupLanguageChange = nifty.createPopup("languageChanged");

        characterPreviewImage = screen.findElementById("characterImage");

        /* Values for the server list are already set. */
        if ((credentials != null) && (accountData != null)) {
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
    public void onServerChanged(@Nonnull String topic, @Nonnull DropDownSelectionChangedEvent<String> event) {
        setSelectedServer(event.getSelectionItemIndex());
    }

    @NiftyEventSubscriber(pattern = "characterList")
    public void onSelectedCharacterChanged(@Nonnull String topic,
                                           @Nonnull ListBoxSelectionChangedEvent<CharacterEntry> event) {
        List<CharacterEntry> selection = event.getSelection();
        if (selection.isEmpty()) {
            clearAvatar();
        } else {
            updateCharacterPreview(event.getSelection().get(0));
        }
    }

    private void updateCharacterPreview(@Nonnull CharacterEntry character) {
        assert nifty != null;

        ListenableFuture<CharacterGetResponse> response =
                accountSystem.getCharacterInformation(character.getServerId(), character.getCharacterId());

        Futures.addCallback(response, new FutureCallback<CharacterGetResponse>() {
            @Override
            public void onSuccess(@Nullable CharacterGetResponse result) {
                if (result == null) {
                    clearAvatar();
                } else {
                    buildAvatar(result);
                }
            }

            @Override
            public void onFailure(@Nonnull Throwable t) {
                clearAvatar();
            }
        }, new NiftyExecutor(nifty));
    }

    private void buildAvatar(@Nonnull CharacterGetResponse response) {
        assert nifty != null;
        assert characterPreviewImage != null;

        AvatarId id = new AvatarId(response.getRace(), response.getRaceType(), Direction.West, CharAnimations.STAND);

        AvatarTemplate template = CharacterFactory.getInstance().getTemplate(id.getAvatarId());
        AvatarEntity avatarEntity = new AvatarEntity(template, true);

        avatarEntity.setClothItem(AvatarClothGroup.Hair, response.getPaperDoll().getHairId());
        avatarEntity.setClothItem(AvatarClothGroup.Beard, response.getPaperDoll().getBeardId());
        avatarEntity.changeClothColor(AvatarClothGroup.Hair, response.getPaperDoll().getHairColour().getColour());
        avatarEntity.changeClothColor(AvatarClothGroup.Beard, response.getPaperDoll().getHairColour().getColour());
        avatarEntity.changeBaseColor(response.getPaperDoll().getSkinColour().getColour());

        for (CharacterItemResponse item : response.getItems()) {
            AvatarClothGroup group = AvatarClothGroup.getFromPositionNumber(item.getPosition());
            if (group == null) {
                continue;
            }
            if (item.getId() == 0) {
                avatarEntity.removeClothItem(group);
            } else {
                avatarEntity.setClothItem(group, item.getId());
            }
        }

        ImageRenderer renderer = characterPreviewImage.getRenderer(ImageRenderer.class);
        assert renderer != null;

        NiftyImage image = new NiftyImage(nifty.getRenderEngine(), new EntityRenderImage(container, avatarEntity));
        renderer.setImage(image);
        characterPreviewImage.setConstraintHeight(SizeValue.px(image.getHeight()));
        characterPreviewImage.setConstraintWidth(SizeValue.px(image.getWidth()));
        characterPreviewImage.getParent().layoutElements();
    }

    private void clearAvatar() {
        ImageRenderer renderer = characterPreviewImage.getRenderer(ImageRenderer.class);
        assert renderer != null;
        renderer.setImage(null);
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
        assert (index >= 0) && (index < serverSelect.getItems().size());

        if (serverSelect.getSelectedIndex() != index) {
            serverSelect.selectItemByIndex(index);
        }

        AccountGetCharsResponse chars = accountData.getChars().get(index);
        assert chars != null;

        characterList.clear();
        chars.getList().stream()
                .filter(chr -> chr.getStatus() == 0)
                .map(chr -> new CharacterEntry(chars.getId(), chr))
                .forEach(characterList::addItem);

        if (!characterList.getItems().isEmpty()) {
            characterList.selectItemByIndex(0);
            updateCharacterPreview(characterList.getSelection().get(0));
        } else {
            clearAvatar();
        }
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
                if ((selectedDate == null) || (selectedDate.compareTo(current) < 0)) {
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
                characterList.getSelection().get(0).getName(),
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

    public void createChar() {
        assert nifty != null;

        Screen nextScreen = nifty.getScreen("characterCreateGender");
        ScreenController controller = nextScreen.getScreenController();
        if (controller instanceof GenderScreenController) {
            int serverIndex = serverSelect.getSelectedIndex();
            String serverId = accountData.getChars().get(serverIndex).getId();
            ((GenderScreenController) controller).setServerId(serverId);
        }
        nifty.gotoScreen("characterCreateGender");
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
        public int compare(@Nullable AccountGetCharResponse o1, @Nullable AccountGetCharResponse o2) {
            if ((o1 == null) && (o2 == null)) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return -1 * o1.getLastSaveTime().compareTo(o2.getLastSaveTime());
        }
    }

    private static final class CharacterEntry {
        @Nonnull
        private final String serverId;
        @Nonnull
        private final CharacterId characterId;
        @Nonnull
        private final String name;

        private CharacterEntry(@Nonnull String serverId, @Nonnull CharacterId characterId, @Nonnull String name) {
            this.serverId = serverId;
            this.characterId = characterId;
            this.name = name;
        }

        private CharacterEntry(@Nonnull String serverId, @Nonnull AccountGetCharResponse response) {
            this(serverId, response.getCharId(), response.getName());
        }

        @Nonnull
        public String getServerId() {
            return serverId;
        }

        @Nonnull
        public CharacterId getCharacterId() {
            return characterId;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        @Override
        public String toString() {
            return name;
        }
    }
}

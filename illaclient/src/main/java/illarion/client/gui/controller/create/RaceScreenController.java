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
package illarion.client.gui.controller.create;

import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.graphics.AvatarEntity;
import illarion.client.gui.EntityRenderImage;
import illarion.client.resources.CharacterFactory;
import illarion.client.resources.data.AvatarTemplate;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.response.CharacterCreateGetResponse;
import illarion.client.util.account.response.RaceResponse;
import illarion.client.util.account.response.RaceTypeResponse;
import illarion.common.graphics.CharAnimations;
import illarion.common.types.AvatarId;
import illarion.common.types.Direction;
import org.illarion.engine.GameContainer;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class RaceScreenController implements ScreenController {
    @Nonnull
    private final AccountSystem accountSystem;
    @Nonnull
    private final GameContainer container;
    private Nifty nifty;
    private ListenableFuture<CharacterCreateGetResponse> characterCreateData;
    private int raceTypeId;
    @Nullable
    private String serverId;
    @Nullable
    private Element[] raceImages;

    public RaceScreenController(@Nonnull GameContainer container, @Nonnull AccountSystem accountSystem) {
        this.accountSystem = accountSystem;
        this.container = container;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;

        raceImages = new Element[6];
        raceImages[0] = screen.findElementById("humanImage");
        raceImages[1] = screen.findElementById("dwarfImage");
        raceImages[2] = screen.findElementById("halflingImage");
        raceImages[3] = screen.findElementById("elfImage");
        raceImages[4] = screen.findElementById("orcImage");
        raceImages[5] = screen.findElementById("lizardImage");
    }

    @Override
    public void onStartScreen() {
        assert nifty != null: "Binding is not done, Nifty is NULL";

        ListenableFuture<CharacterCreateGetResponse> response = getCharacterCreateData();
        ListenableFuture<EntityRenderImage[]> renderImages =
                Futures.transform(response, new CreateEntityImagesFunction(raceTypeId));
        Futures.addCallback(renderImages, new SendEntityImagesToNiftyCallback());

        nifty.subscribeAnnotations(this);
    }

    @Override
    public void onEndScreen() {
        assert nifty != null: "Binding is not done, Nifty is NULL";

        nifty.unsubscribeAnnotations(this);
    }

    public int getRaceTypeId() {
        return raceTypeId;
    }

    public void setRaceTypeId(int raceTypeId) {
        this.raceTypeId = raceTypeId;
    }

    @Nonnull
    public ListenableFuture<CharacterCreateGetResponse> getCharacterCreateData() {
        ListenableFuture<CharacterCreateGetResponse> data = characterCreateData;
        if (data == null) {
            if (serverId == null) {
                throw new IllegalStateException("The server ID has to be set before entering the race selection screen.");
            }

            data = accountSystem.getCharacterCreateInformation(serverId);
            characterCreateData = data;
            return data;
        }
        return data;
    }

    public void setCharacterCreateData(ListenableFuture<CharacterCreateGetResponse> characterCreateData) {
        this.characterCreateData = characterCreateData;
    }

    @Nullable
    public String getServerId() {
        return serverId;
    }

    public void setServerId(@Nullable String serverId) {
        this.serverId = serverId;
    }

    @NiftyEventSubscriber(pattern = "backBtn")
    public void onBackButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        assert nifty != null;

        nifty.gotoScreen("characterCreateGender");
    }

    @NiftyEventSubscriber(pattern = "cancelBtn")
    public void onCancelButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        assert nifty != null;

        nifty.gotoScreen("charSelect");
    }

    @NiftyEventSubscriber(pattern = "humanBtn")
    public void onHumanButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        gotoNextScreen(0);
    }

    @NiftyEventSubscriber(pattern = "dwarfBtn")
    public void onDwarfButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        gotoNextScreen(1);
    }

    @NiftyEventSubscriber(pattern = "halflingBtn")
    public void onHalflingButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        gotoNextScreen(2);
    }

    @NiftyEventSubscriber(pattern = "elfBtn")
    public void onElfButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        gotoNextScreen(3);
    }

    @NiftyEventSubscriber(pattern = "orcBtn")
    public void onOrcButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        gotoNextScreen(4);
    }

    @NiftyEventSubscriber(pattern = "lizardBtn")
    public void onLizardButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        gotoNextScreen(5);
    }

    private void gotoNextScreen(int raceId) {

    }

    private final class CreateEntityImagesFunction implements Function<CharacterCreateGetResponse, EntityRenderImage[]> {
        private final int typeId;

        CreateEntityImagesFunction(int typeId) {
            this.typeId = typeId;
        }

        @Nullable
        @Override
        @Contract("null -> null; !null -> !null")
        public EntityRenderImage[] apply(@Nullable CharacterCreateGetResponse input) {
            if (input == null) {
                return null;
            }
            assert raceImages != null;

            EntityRenderImage[] result = new EntityRenderImage[raceImages.length];

            for (@Nonnull RaceResponse race : input.getRaces()) {
                for (@Nonnull RaceTypeResponse raceType : race.getTypes()) {
                    if (raceType.getId() != typeId) {
                        continue;
                    }
                    AvatarId id = new AvatarId(race.getId(), raceType.getId(),
                                               (raceType.getId() == 0) ? Direction.South : Direction.West,
                                               CharAnimations.STAND);

                    AvatarTemplate template = CharacterFactory.getInstance().getTemplate(id.getAvatarId());
                    AvatarEntity avatarEntity = new AvatarEntity(template, true);

                    Util.applyRandomFromRaceType(avatarEntity, raceType);
                    Util.applyRandomStartPack(avatarEntity, input.getStartPacks());

                    result[race.getId()] = new EntityRenderImage(container, avatarEntity);
                }
            }

            return result;
        }
    }

    private final class SendEntityImagesToNiftyCallback implements FutureCallback<EntityRenderImage[]> {
        @Override
        public void onSuccess(@Nullable EntityRenderImage[] result) {
            assert nifty != null;

            if (result == null) {
                return;
            }
            assert raceImages != null;

            for (int i = 0; i < raceImages.length; i++) {
                EntityRenderImage image = result[i];
                if (image == null) {
                    continue;
                }
                Util.applyEntityImage(nifty, image, raceImages[i]);
            }
        }

        @Override
        public void onFailure(@Nonnull Throwable t) {

        }
    }
}

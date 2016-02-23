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
import illarion.client.gui.controller.NiftyExecutor;
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
public final class GenderScreenController implements ScreenController {
    @Nonnull
    public static final String NEXT_SCREEN_ID = "characterCreateRace";
    @Nonnull
    private final AccountSystem accountSystem;
    @Nonnull
    private final GameContainer container;

    @Nullable
    private Nifty nifty;
    @Nullable
    private String serverId;

    @Nullable
    private Element[] raceImages;

    public GenderScreenController(@Nonnull GameContainer container, @Nonnull AccountSystem accountSystem) {
        this.accountSystem = accountSystem;
        this.container = container;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;

        raceImages = new Element[12];
        raceImages[0] = screen.findElementById("humanMale");
        raceImages[1] = screen.findElementById("humanFemale");
        raceImages[2] = screen.findElementById("dwarfMale");
        raceImages[3] = screen.findElementById("dwarfFemale");
        raceImages[4] = screen.findElementById("halflingMale");
        raceImages[5] = screen.findElementById("halflingFemale");
        raceImages[6] = screen.findElementById("elfMale");
        raceImages[7] = screen.findElementById("elfFemale");
        raceImages[8] = screen.findElementById("orcMale");
        raceImages[9] = screen.findElementById("orcFemale");
        raceImages[10] = screen.findElementById("lizardMale");
        raceImages[11] = screen.findElementById("lizardFemale");
    }

    @Override
    public void onStartScreen() {
        assert nifty != null: "Binding is not done, Nifty is NULL";

        if (serverId == null) {
            throw new IllegalStateException("The server ID has to be set before entering the gender selection screen.");
        }

        ListenableFuture<CharacterCreateGetResponse> response = accountSystem.getCharacterCreateInformation(serverId);
        ListenableFuture<EntityRenderImage[]> renderImages = Futures.transform(response, new CreateEntityImagesFunction());
        Futures.addCallback(renderImages, new SendEntityImagesToNiftyCallback(), new NiftyExecutor(nifty));

        getNextScreenController().setCharacterCreateData(response);

        nifty.subscribeAnnotations(this);
    }

    @Override
    public void onEndScreen() {
        assert nifty != null: "Binding is not done, Nifty is NULL";

        nifty.unsubscribeAnnotations(this);
    }

    @Nullable
    public String getServerId() {
        return serverId;
    }

    public void setServerId(@Nullable String serverId) {
        this.serverId = serverId;
    }

    @NiftyEventSubscriber(pattern = "cancelBtn")
    public void onCancelButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        assert nifty != null;

        nifty.gotoScreen("charSelect");
    }

    @NiftyEventSubscriber(pattern = "maleBtn")
    public void onMaleButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        gotoNextScreen(0);
    }

    @NiftyEventSubscriber(pattern = "femaleBtn")
    public void onFemaleButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        gotoNextScreen(1);
    }

    @Nonnull
    private RaceScreenController getNextScreenController() {
        assert nifty != null;

        Screen raceScreen = nifty.getScreen(NEXT_SCREEN_ID);
        assert raceScreen != null;

        return (RaceScreenController) raceScreen.getScreenController();
    }

    private void gotoNextScreen(int raceTypeId) {
        assert nifty != null;

        RaceScreenController controller = getNextScreenController();

        controller.setServerId(serverId);
        controller.setRaceTypeId(raceTypeId);
        nifty.gotoScreen(NEXT_SCREEN_ID);
    }

    private final class CreateEntityImagesFunction implements Function<CharacterCreateGetResponse, EntityRenderImage[]> {
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
                    AvatarId id = new AvatarId(race.getId(), raceType.getId(),
                                               (raceType.getId() == 0) ? Direction.South : Direction.West,
                                               CharAnimations.STAND);

                    AvatarTemplate template = CharacterFactory.getInstance().getTemplate(id.getAvatarId());
                    AvatarEntity avatarEntity = new AvatarEntity(template, true);

                    Util.applyRandomFromRaceType(avatarEntity, raceType);
                    Util.applyRandomStartPack(avatarEntity, input.getStartPacks());

                    int arrayIndex = (race.getId() * 2) + raceType.getId();
                    result[arrayIndex] = new EntityRenderImage(container, avatarEntity);
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

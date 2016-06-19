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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.effects.impl.Hint;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.graphics.AvatarClothManager.AvatarClothGroup;
import illarion.client.graphics.AvatarEntity;
import illarion.client.gui.EntityRenderImage;
import illarion.client.resources.CharacterFactory;
import illarion.client.resources.data.AvatarTemplate;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.response.*;
import illarion.common.graphics.CharAnimations;
import illarion.common.types.AvatarId;
import illarion.common.types.Direction;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class JobsScreenController implements ScreenController {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(JobsScreenController.class);
    @Nonnull
    private final AccountSystem accountSystem;
    @Nonnull
    private final GameContainer container;
    @Nonnull
    private final Properties jobsConfig;
    @Nullable
    private ListenableFuture<CharacterCreateGetResponse> characterCreateData;
    private int cultureId;
    private int raceTypeId;
    private int raceId;
    @Nullable
    private String serverId;
    @Nullable
    private Nifty nifty;

    public JobsScreenController(@Nonnull GameContainer container, @Nonnull AccountSystem accountSystem) {
        this.accountSystem = accountSystem;
        this.container = container;

        jobsConfig = new Properties();
        try (InputStream in = JobsScreenController.class.getClassLoader().getResourceAsStream(
                "illarion/client/gui/controller/create/jobs.properties"
        )) {
            jobsConfig.load(in);
        } catch (IOException e) {
            log.error("Error while reading the configuration for the config screen.", e);
        }
    }

    @NiftyEventSubscriber(pattern = "backBtn")
    public void onBackButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        assert nifty != null;

        nifty.gotoScreen("characterCreateCulture");
    }

    @NiftyEventSubscriber(pattern = "cancelBtn")
    public void onCancelButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        assert nifty != null;

        nifty.gotoScreen("charSelect");
    }

    @Nullable
    public String getServerId() {
        return serverId;
    }

    public void setServerId(@Nullable String serverId) {
        this.serverId = serverId;
    }

    public int getRaceId() {
        return raceId;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public int getRaceTypeId() {
        return raceTypeId;
    }

    public void setRaceTypeId(int raceTypeId) {
        this.raceTypeId = raceTypeId;
    }

    public int getCultureId() {
        return cultureId;
    }

    public void setCultureId(int cultureId) {
        this.cultureId = cultureId;
    }

    @Nonnull
    public ListenableFuture<CharacterCreateGetResponse> getCharacterCreateData() {
        return Objects.requireNonNull(characterCreateData);
    }

    public void setCharacterCreateData(@Nonnull ListenableFuture<CharacterCreateGetResponse> characterCreateData) {
        this.characterCreateData = characterCreateData;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {
        assert nifty != null;
        nifty.subscribeAnnotations(this);

        Futures.addCallback(getCharacterCreateData(), new FutureCallback<CharacterCreateGetResponse>() {
            @Override
            public void onSuccess(@Nullable CharacterCreateGetResponse result) {
                if (result == null) { return; }

                setup(result);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    @Override
    public void onEndScreen() {
        assert nifty != null;
        nifty.unsubscribeAnnotations(this);
    }

    private void setup(@Nonnull CharacterCreateGetResponse createData) {
        assert nifty != null;
    }

    @Nonnull
    private RaceTypeResponse getRaceTypeResponse(@Nonnull CharacterCreateGetResponse data) {
        return data.getRaces().stream()
                   .filter(r -> r.getId() == raceId)
                   .map(RaceResponse::getTypes)
                   .flatMap(Collection::stream)
                   .filter(t -> t.getId() == raceTypeId)
                   .findAny().orElseThrow(IllegalStateException::new);
    }
}

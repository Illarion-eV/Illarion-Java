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

import com.google.common.util.concurrent.ListenableFuture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.effects.impl.Hint;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.response.CharacterCreateGetResponse;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CultureScreenController implements ScreenController {
    @Nonnull
    private final AccountSystem accountSystem;
    @Nonnull
    private final GameContainer container;
    private ListenableFuture<CharacterCreateGetResponse> characterCreateData;
    private int raceTypeId;
    private int raceId;
    @Nullable
    private String serverId;
    @Nullable
    private List<CultureOption> options;
    @Nullable
    private Nifty nifty;

    public CultureScreenController(@Nonnull GameContainer container, @Nonnull AccountSystem accountSystem) {
        this.accountSystem = accountSystem;
        this.container = container;
    }

    @NiftyEventSubscriber(pattern = "backToRaceBtn")
    public void onBackButtonClicked(@Nonnull String topic, @Nonnull ButtonClickedEvent event) {
        assert nifty != null;

        nifty.gotoScreen("characterCreateRace");
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
        options = new ArrayList<>(6);
        for (int i = 1; i <= 6; i++) {
            options.add(new CultureOption(screen, "culture" + Integer.toString(i)));
        }
    }

    @Override
    public void onStartScreen() {
        assert nifty != null;
        nifty.subscribeAnnotations(this);

        switch (raceId) {
            case 0: setupForHumans(); break;
            case 1: setupForDwarfs(); break;
            case 2: setupForHalfling(); break;
            case 3: setupForElf(); break;
            case 4: setupForOrc(); break;
            case 5: setupForLizard(); break;
        }
    }

    @Override
    public void onEndScreen() {
        assert nifty != null;
        nifty.unsubscribeAnnotations(this);
    }

    private void setupForHumans() {
        assert options != null;

        options.get(0).setup("human.norodaj");
        options.get(1).setup("albar");
        options.get(2).setup("salkamar");
        options.get(3).setup("gynk");
        options.get(4).setup("human.serinjah");
        options.get(5).setup("cityborn");
    }

    private void setupForDwarfs() {
        assert options != null;

        options.get(0).setup("dwarf.kingdoms");
        options.get(1).setup("gynk");
        options.get(2).setup("cityborn");
        options.get(3).setup("dwarf.clanborn");
        options.get(4).setup();
        options.get(5).setup();
    }

    private void setupForHalfling() {
        assert options != null;

        options.get(0).setup("halfling.settlements");
        options.get(1).setup("gynk");
        options.get(2).setup("halfling.shireborn");
        options.get(3).setup();
        options.get(4).setup();
        options.get(5).setup();
    }

    private void setupForElf() {
        assert options != null;

        options.get(0).setup("elf.settlements");
        options.get(1).setup("elf.colonists");
        options.get(2).setup("elf.clanborn");
        options.get(3).setup();
        options.get(4).setup();
        options.get(5).setup();
    }

    private void setupForOrc() {
        assert options != null;

        options.get(0).setup("orc.flame");
        options.get(1).setup("orc.tribal");
        options.get(2).setup("gynk");
        options.get(3).setup("cityborn");
        options.get(4).setup("elf.clanborn");
        options.get(5).setup();
    }

    private void setupForLizard() {
        assert options != null;

        options.get(0).setup("lizard.original");
        options.get(1).setup("gynk");
        options.get(2).setup("lizard.riverhatched");
        options.get(3).setup();
        options.get(4).setup();
        options.get(5).setup();
    }

    private static final class CultureOption {
        @Nonnull
        private final Element container;
        @Nonnull
        private final Button button;
        @Nonnull
        private final Element image;

        public CultureOption(@Nonnull Screen screen, @Nonnull String containerKey) {
            container = Objects.requireNonNull(screen.findElementById(containerKey));
            button = Objects.requireNonNull(container.findNiftyControl("#button", Button.class));
            image = Objects.requireNonNull(container.findElementById("#image"));
        }

        public void setup() {
            container.hide();
        }

        public void setup(@Nonnull String key) {
            button.setText("${charCreateCulture-bundle." + key + ".title}");
            Objects.requireNonNull(button.getElement())
                  .getEffects(EffectEventId.onHover, Hint.class)
                  .stream()
                  .forEach(effect -> effect.getParameters().setProperty(
                          "hintText", "${charCreateCulture-bundle." + key + ".description}") );
            if (!container.isVisible()) {
                container.show();
            }
        }
    }
}

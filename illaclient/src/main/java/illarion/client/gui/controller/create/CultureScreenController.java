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
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.response.CharacterCreateGetResponse;
import illarion.client.util.account.response.ColourResponse;
import illarion.client.util.account.response.RaceResponse;
import illarion.client.util.account.response.RaceTypeResponse;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CultureScreenController implements ScreenController {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(CultureScreenController.class);
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
    @Nonnull
    private final Properties cultureConfig;
    @Nonnull
    private final Random random;

    public CultureScreenController(@Nonnull GameContainer container, @Nonnull AccountSystem accountSystem) {
        this.accountSystem = accountSystem;
        this.container = container;

        cultureConfig = new Properties();
        try (InputStream in = CultureScreenController.class.getClassLoader().getResourceAsStream(
                "illarion/client/gui/controller/create/culture.properties"
        )) {
            cultureConfig.load(in);
        } catch (IOException e) {
            log.error("Error while reading the configuration for the config screen.", e);
        }

        random = new Random();
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
        assert options != null;

        RaceTypeResponse raceTypeResponse = getRaceTypeResponse(createData);

        for (int i = 0; i < 6; i++) {
            CultureOption option = Objects.requireNonNull(options.get(i));
            String textKey = getConfigEntry(i, "textKey");
            if (textKey == null) {
                option.setup();
            } else {
                option.setup(textKey);

                ColourResponse hairColour = selectColour(raceTypeResponse.getHairColours(), i, "hair.colour.value", "skin.colour.delta");
                ColourResponse skinColour = selectColour(raceTypeResponse.getHairColours(), i, "skin.colour.value", "skin.colour.delta");

            }
        }
    }

    private ColourResponse selectColour(@Nonnull List<ColourResponse> options, int option, @Nonnull String valueKey, @Nonnull String deltaKey) {
        if (options.isEmpty()) return null;
        if (options.size() == 1) return options.get(0);

        int[] normalValue = getConfigEntryIntArray(option, valueKey);
        double[] deltaValues = getConfigEntryDoubleArray(option, deltaKey);
        float[] normalHsb = java.awt.Color.RGBtoHSB(normalValue[0], normalValue[1], normalValue[2], null);

        List<ColourResponse> possibleColours = new ArrayList<>();
        float[] testColour = new float[3];
        for (@Nonnull ColourResponse colourOption : options) {
            java.awt.Color.RGBtoHSB(colourOption.getRed(), colourOption.getGreen(), colourOption.getBlue(), testColour);
            //double delta = getColourDiff(normalValue, testColour);

            double hueDelta = Math.abs(Math.atan2(Math.sin(testColour[0] - normalHsb[0]), Math.cos(testColour[0] - normalHsb[0])));

            float saturationDelta = Math.abs(normalHsb[1] - testColour[1]);
            float brightnessDelta = Math.abs(normalHsb[2] - testColour[2]);

            if (hueDelta < deltaValues[0] && saturationDelta < deltaValues[1] && brightnessDelta < deltaValues[2]) {
                possibleColours.add(colourOption);
            }
        }

        if (possibleColours.isEmpty()) {
            possibleColours = options;
        }

        if (possibleColours.size() == 1) return possibleColours.get(0);
        int selectedIndex = random.nextInt(possibleColours.size());
        return possibleColours.get(selectedIndex);
    }

    private double getConfigEntryDouble(int option, @Nonnull String key) {
        String entry = getConfigEntry(option, key);
        if (entry == null) return 0.0;

        return Double.parseDouble(entry);
    }

    @Nonnull
    private int[] getConfigEntryIntArray(int option, @Nonnull String key) {
        String entry = getConfigEntry(option, key);
        if (entry == null) return new int[] {0, 1};

        String[] parts = entry.split(",");
        return Arrays.asList(parts).stream().mapToInt(Integer::parseInt).toArray();
    }

    @Nonnull
    private double[] getConfigEntryDoubleArray(int option, @Nonnull String key) {
        String entry = getConfigEntry(option, key);
        if (entry == null) return new double[] {0.0, 1.0};

        String[] parts = entry.split(",");
        return Arrays.asList(parts).stream().mapToDouble(Double::parseDouble).toArray();
    }

    @Nonnull
    private float[] getConfigEntryFloatArray(int option, @Nonnull String key) {
        double[] entry = getConfigEntryDoubleArray(option, key);
        float[] result = new float[entry.length];
        for (int i = 0; i < entry.length; i++) {
            result[i] = (float) entry[i];
        }
        return result;
    }

    @Nullable
    private String getConfigEntry(int option, @Nonnull String key) {
        String raceTypeKey = "race." + raceId + ".type." + raceTypeId + ".option." + option + "." + key;
        String raceTypeResult = cultureConfig.getProperty(raceTypeKey, null);
        if (raceTypeResult != null) return raceTypeResult;

        String raceKey = "race." + raceId + ".option." + option + "." + key;
        String raceResult = cultureConfig.getProperty(raceKey, null);
        if (raceResult != null) return raceResult;

        String genericKey = "option." + option + "." + key;
        String genericResult = cultureConfig.getProperty(genericKey, null);
        if (genericResult != null) return genericResult;

        return null;
    }

    @Nonnull
    private RaceTypeResponse getRaceTypeResponse(@Nonnull CharacterCreateGetResponse data) {
        return data.getRaces().stream()
                   .filter(r -> r.getId() == raceId)
                   .map(RaceResponse::getTypes)
                   .flatMap(Collection::stream)
                   .filter(t -> t.getId() == raceTypeId)
                   .findAny().get();
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

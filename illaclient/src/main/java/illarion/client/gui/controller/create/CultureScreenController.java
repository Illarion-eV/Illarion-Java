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
public final class CultureScreenController implements ScreenController {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(CultureScreenController.class);
    @Nonnull
    private final AccountSystem accountSystem;
    @Nonnull
    private final GameContainer container;
    @Nonnull
    private final Properties cultureConfig;
    @Nonnull
    private final Random random;
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

        cultureConfig = new Properties();
        try (InputStream in = CultureScreenController.class.getClassLoader().getResourceAsStream(
                "illarion/client/gui/controller/create/culture.properties"
        )) {
            cultureConfig.load(in);
        } catch (IOException e) {
            log.error("Error while reading the configuration for the config screen.", e);
        }

        random = new Random();

        colorRangePattern = Pattern.compile("\\[#([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})\\s*,\\s*#([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})\\]", Pattern.CASE_INSENSITIVE);
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
        assert nifty != null;
        assert options != null;

        RaceTypeResponse raceTypeResponse = getRaceTypeResponse(createData);

        for (int i = 0; i < 6; i++) {
            CultureOption option = Objects.requireNonNull(options.get(i));
            String textKey = getConfigEntry(i, "textKey");
            if (textKey == null) {
                option.setup();
            } else {
                ColourResponse hairColour = selectColour(raceTypeResponse.getHairColours(), i, "hair.colour");
                ColourResponse skinColour = selectColour(raceTypeResponse.getSkinColours(), i, "skin.colour");

                IdNameResponse beardValue = selectIndexValue(raceTypeResponse.getBeards(), i, "beard.id");
                IdNameResponse hairValue = selectIndexValue(raceTypeResponse.getHairs(), i, "hair.id");

                AvatarId id = new AvatarId(raceId, raceTypeId, Direction.South, CharAnimations.STAND);

                AvatarTemplate template = CharacterFactory.getInstance().getTemplate(id.getAvatarId());
                AvatarEntity avatarEntity = new AvatarEntity(template, true);

                avatarEntity.setClothItem(AvatarClothGroup.Beard, (beardValue == null) ? 0 : beardValue.getId());
                avatarEntity.setClothItem(AvatarClothGroup.Hair, (hairValue == null) ? 0 : hairValue.getId());

                avatarEntity.changeBaseColor((skinColour != null) ? skinColour.getColour() : null);
                Color hairColorValue = (hairColour != null) ? hairColour.getColour() : null;
                avatarEntity.changeClothColor(AvatarClothGroup.Hair, hairColorValue);
                avatarEntity.changeClothColor(AvatarClothGroup.Beard, hairColorValue);
                Util.applyRandomStartPack(avatarEntity, createData.getStartPacks());

                EntityRenderImage entityRenderImage = new EntityRenderImage(container, avatarEntity);
                NiftyImage niftyImage = new NiftyImage(nifty.getRenderEngine(), entityRenderImage);

                option.setup(textKey, niftyImage);
            }
        }
    }

    @Nullable
    private IdNameResponse selectIndexValue(@Nonnull List<IdNameResponse> options, int option, @Nonnull String idKey) {
        if (options.isEmpty()) {
            return null;
        }
        if (options.size() == 1) {
            return options.get(0);
        }

        int[] validIndexValues = getConfigEntryIntArray(option, idKey);
        if ((validIndexValues != null) && (validIndexValues.length != 0)) {
            int[] possibilities = IntStream.concat(options.stream().mapToInt(IdNameResponse::getId), IntStream.of(0)).sorted().toArray();
            validIndexValues = IntStream.of(validIndexValues).filter(v -> Arrays.binarySearch(possibilities, v) >= 0).toArray();
        }

        if ((validIndexValues == null) || (validIndexValues.length == 0)) {
            validIndexValues = IntStream.concat(options.stream().mapToInt(IdNameResponse::getId), IntStream.of(0)).toArray();
        }

        int selectedValue = random.nextInt(validIndexValues.length);
        int selectedIndex = validIndexValues[selectedValue];

        if (selectedIndex == 0) {
            return null;
        }

        return options.stream().filter(o -> o.getId() == selectedIndex).findFirst().orElseGet(() -> options.get(0));
    }

    @Nonnull
    private final Pattern colorRangePattern;

    @Nullable
    private ColourResponse selectColour(@Nonnull List<ColourResponse> options, int option, @Nonnull String valueKey) {
        if (options.isEmpty()) {
            return null;
        }
        if (options.size() == 1) {
            return options.get(0);
        }

        List<ColourResponse> possibleColours = new ArrayList<>();

        StringBuilder keyBuilder = new StringBuilder(valueKey);
        int rangeIndex = -1;

        int firstColor[] = new int[3];
        int secondColor[] = new int[3];
        float firstHsbColor[] = null;
        float secondHsbColor[] = null;
        while (true) {
            rangeIndex++;
            keyBuilder.setLength(valueKey.length());
            keyBuilder.append('.').append(rangeIndex);

            String colorRangeDef = getConfigEntry(option, keyBuilder);
            if (colorRangeDef == null) {
                break;
            }

            Matcher matcher = colorRangePattern.matcher(colorRangeDef);
            if (!matcher.matches()) {
                continue;
            }
            if (matcher.groupCount() != 6) {
                continue;
            }

            for (int i = 0; i < 3; i++) {
                firstColor[i] = Integer.parseInt(matcher.group(i + 1), 16);
                secondColor[i] = Integer.parseInt(matcher.group(i + 4), 16);
            }

            firstHsbColor = java.awt.Color.RGBtoHSB(firstColor[0], firstColor[1], firstColor[2], firstHsbColor);
            secondHsbColor = java.awt.Color.RGBtoHSB(secondColor[0], secondColor[1], secondColor[2], secondHsbColor);

            boolean hueOuterRange = false;
            float hueRange[] = new float[] {Math.min(firstHsbColor[0], secondHsbColor[0]), Math.max(firstHsbColor[0], secondHsbColor[0])};
            if (hueRange[1] - hueRange[0] > 0.5f) {
                hueOuterRange = true;
            }
            float saturationRange[] = new float[] {Math.min(firstHsbColor[1], secondHsbColor[1]), Math.max(firstHsbColor[1], secondHsbColor[1])};
            float brightnessRange[] = new float[] {Math.min(firstHsbColor[2], secondHsbColor[2]), Math.max(firstHsbColor[2], secondHsbColor[2])};

            for (@Nonnull ColourResponse c : options) {
                firstHsbColor = java.awt.Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), firstHsbColor);

                if (hueOuterRange) {
                    if (firstHsbColor[0] < hueRange[1] && firstHsbColor[0] > hueRange[0]) {
                        continue;
                    }
                } else {
                    if (firstHsbColor[0] < hueRange[0] || firstHsbColor[0] > hueRange[1]) {
                        continue;
                    }
                }

                if (firstHsbColor[1] < saturationRange[0] || firstHsbColor[1] > saturationRange[1]) {
                    continue;
                }

                if (firstHsbColor[2] < brightnessRange[0] || firstHsbColor[2] > brightnessRange[1]) {
                    continue;
                }

                possibleColours.add(c);
            }
        }

        if (possibleColours.isEmpty()) {
            possibleColours = options;
        }

        if (possibleColours.size() == 1) {
            return possibleColours.get(0);
        }
        int selectedIndex = random.nextInt(possibleColours.size());
        return possibleColours.get(selectedIndex);
    }

    private double getConfigEntryDouble(int option, @Nonnull String key) {
        String entry = getConfigEntry(option, key);
        if (entry == null) {
            return 0.0;
        }

        return Double.parseDouble(entry);
    }

    @Nullable
    private int[] getConfigEntryIntArray(int option, @Nonnull String key) {
        String entry = getConfigEntry(option, key);
        if (entry == null) {
            return null;
        }

        String[] parts = entry.split(",");
        return Arrays.asList(parts).stream().mapToInt(Integer::parseInt).toArray();
    }

    @Nonnull
    private double[] getConfigEntryDoubleArray(int option, @Nonnull String key) {
        String entry = getConfigEntry(option, key);
        if (entry == null) {
            return new double[]{0.0, 1.0};
        }

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
    private String getConfigEntry(int option, @Nonnull CharSequence key) {
        String raceTypeKey = "race." + raceId + ".type." + raceTypeId + ".option." + option + '.' + key;
        String raceTypeResult = cultureConfig.getProperty(raceTypeKey, null);
        if (raceTypeResult != null) {
            return raceTypeResult;
        }

        String raceKey = "race." + raceId + ".option." + option + '.' + key;
        String raceResult = cultureConfig.getProperty(raceKey, null);
        if (raceResult != null) {
            return raceResult;
        }

        String genericKey = "option." + option + '.' + key;
        String genericResult = cultureConfig.getProperty(genericKey, null);
        if (genericResult != null) {
            return genericResult;
        }

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

        public void setup(@Nonnull String key, @Nonnull NiftyImage niftyImage) {
            button.setText("${charCreateCulture-bundle." + key + ".title}");
            Objects.requireNonNull(button.getElement())
                  .getEffects(EffectEventId.onHover, Hint.class)
                  .stream()
                  .forEach(effect -> effect.getParameters().setProperty(
                          "hintText", "${charCreateCulture-bundle." + key + ".description}") );
            if (!container.isVisible()) {
                container.show();
            }

            image.getRenderer(ImageRenderer.class).setImage(niftyImage);
        }
    }
}

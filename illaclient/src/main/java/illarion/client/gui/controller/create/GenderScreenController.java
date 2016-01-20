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
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.graphics.AvatarClothManager.AvatarClothGroup;
import illarion.client.graphics.AvatarEntity;
import illarion.client.gui.EntityRenderImage;
import illarion.client.gui.controller.NiftyExecutor;
import illarion.client.resources.CharacterFactory;
import illarion.client.resources.data.AvatarTemplate;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.response.*;
import illarion.common.graphics.CharAnimations;
import illarion.common.types.AvatarId;
import illarion.common.types.Direction;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GenderScreenController implements ScreenController {
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

    @Nonnull
    private static <T> T getRandom(@Nonnull List<T> list) {
        int index = new Random().nextInt(list.size());
        //noinspection ConstantConditions
        return list.get(index);
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
        Futures.addCallback(renderImages, new FutureCallback<EntityRenderImage[]>() {
            @Override
            public void onSuccess(@Nullable EntityRenderImage[] result) {
                if (result == null) {
                    return;
                }
                assert raceImages != null;

                for (int i = 0; i < raceImages.length; i++) {
                    EntityRenderImage image = result[i];
                    if (image == null) {
                        continue;
                    }
                    Element raceImageElement = raceImages[i];

                    ImageRenderer renderer = raceImageElement.getRenderer(ImageRenderer.class);
                    assert renderer != null;
                    NiftyImage niftyImage = new NiftyImage(nifty.getRenderEngine(), image);
                    renderer.setImage(niftyImage);
                    raceImageElement.setConstraintHeight(SizeValue.px(image.getHeight()));
                    raceImageElement.setConstraintWidth(SizeValue.px(image.getWidth()));
                    raceImageElement.getParent().layoutElements();
                    raceImageElement.getParent().show();
                }
            }

            @Override
            public void onFailure(@Nonnull Throwable t) {

            }
        }, new NiftyExecutor(nifty));

    }

    @Override
    public void onEndScreen() {

    }

    @Nullable
    public String getServerId() {
        return serverId;
    }

    public void setServerId(@Nullable String serverId) {
        this.serverId = serverId;
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

                    List<IdNameResponse> beards = raceType.getBeards();
                    if (!beards.isEmpty()) {
                        avatarEntity.setClothItem(AvatarClothGroup.Beard, getRandom(beards).getId());
                    }
                    List<IdNameResponse> hairs = raceType.getHairs();
                    if (!hairs.isEmpty()) {
                        avatarEntity.setClothItem(AvatarClothGroup.Hair, getRandom(hairs).getId());
                    }
                    List<ColourResponse> skinColours = raceType.getSkinColours();
                    if (!skinColours.isEmpty()) {
                        avatarEntity.changeBaseColor(getRandom(skinColours).getColour()) ;
                    }
                    List<ColourResponse> hairColours = raceType.getHairColours();
                    if (!hairColours.isEmpty()) {
                        Color hairColor = getRandom(hairColours).getColour();
                        avatarEntity.changeClothColor(AvatarClothGroup.Hair, hairColor);
                        avatarEntity.changeClothColor(AvatarClothGroup.Beard, hairColor);
                    }

                    List<StartPackResponse> startPacks = input.getStartPacks();
                    StartPackResponse startPack = getRandom(startPacks);

                    for (StartPackItemsResponse item : startPack.getItems()) {
                        AvatarClothGroup group = AvatarClothGroup.getFromPositionNumber(item.getPosition());
                        if (group == null) {
                            continue;
                        }
                        if (item.getItemId() != 0) {
                            avatarEntity.setClothItem(group, item.getItemId());
                        }
                    }

                    int arrayIndex = (race.getId() * 2) + raceType.getId();
                    result[arrayIndex] = new EntityRenderImage(container, avatarEntity);
                }
            }

            return result;
        }
    }
}

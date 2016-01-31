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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.graphics.AvatarClothManager.AvatarClothGroup;
import illarion.client.graphics.AvatarEntity;
import illarion.client.util.account.response.*;
import org.illarion.engine.graphic.Color;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class Util {
    private Util() {}

    static void applyRandomFromRaceType(@Nonnull AvatarEntity avatarEntity,
                                        @Nonnull RaceTypeResponse raceType) {
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
    }

    static void applyRandomStartPack(@Nonnull AvatarEntity avatarEntity, @Nonnull List<StartPackResponse> startPacks) {
        applyStartPack(avatarEntity, getRandom(startPacks));
    }

    static void applyStartPack(@Nonnull AvatarEntity avatarEntity, @Nonnull StartPackResponse startPack) {
        for (StartPackItemsResponse item : startPack.getItems()) {
            AvatarClothGroup group = AvatarClothGroup.getFromPositionNumber(item.getPosition());
            if (group == null) {
                continue;
            }
            if (item.getItemId() != 0) {
                avatarEntity.setClothItem(group, item.getItemId());
            }
        }
    }

    static void applyEntityImage(@Nonnull Nifty nifty, @Nonnull RenderImage image, @Nonnull Element targetElement) {
        ImageRenderer renderer = targetElement.getRenderer(ImageRenderer.class);
        assert renderer != null;
        NiftyImage niftyImage = new NiftyImage(nifty.getRenderEngine(), image);
        renderer.setImage(niftyImage);
        targetElement.setConstraintHeight(SizeValue.px(image.getHeight()));
        targetElement.setConstraintWidth(SizeValue.px(image.getWidth()));
        targetElement.getParent().layoutElements();
        targetElement.getParent().show();
    }

    @Nonnull
    private static <T> T getRandom(@Nonnull List<T> list) {
        int index = new Random().nextInt(list.size());
        //noinspection ConstantConditions
        return list.get(index);
    }
}

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
package illarion.client.graphics;

import illarion.client.resources.MiscImageFactory;
import illarion.client.resources.data.MiscImageTemplate;
import illarion.client.world.MapTile;
import illarion.common.graphics.Layer;
import illarion.common.types.DisplayCoordinate;
import illarion.common.types.ServerCoordinate;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.ImmutableColor;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

/**
 * This entity is a marker that shows where a quest starts.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class QuestMarker extends AbstractEntity<MiscImageTemplate> {
    public enum QuestMarkerAvailability {
        Available,
        AvailableSoon
    }

    public enum QuestMarkerType {
        Start,
        Target
    }

    /**
     * The tile this marker is displayed on.
     */
    @Nonnull
    private final MapTile parentTile;

    /**
     * The availability state of this quest marker.
     */
    @Nonnull
    private QuestMarkerAvailability availability;

    /**
     * This is the map used to store the color values for the different availability states.
     */
    @Nonnull
    private static final Map<QuestMarkerAvailability, Color> COLOR_MAP;

    /**
     * The offset that is currently applied to the display coordinates.
     */
    private int appliedOffset;

    static {
        COLOR_MAP = new EnumMap<>(QuestMarkerAvailability.class);
        COLOR_MAP.put(QuestMarkerAvailability.Available, new ImmutableColor(1.f, .75f, 0.f, 1.f));
        COLOR_MAP.put(QuestMarkerAvailability.AvailableSoon, new ImmutableColor(1.f, 1.f, 1.f, .8f));
    }

    /**
     * Create a new quest marker with the default image and with the reference to the tile its displayed on.
     *
     * @param type the type of quest marker that is supposed to be created
     * @param parentTile the parent tile
     */
    public QuestMarker(@Nonnull QuestMarkerType type, @Nonnull MapTile parentTile) {
        this(getTemplateForType(type), parentTile);
    }

    @Nonnull
    private static MiscImageTemplate getTemplateForType(@Nonnull QuestMarkerType type) {
        int templateId;
        switch (type) {
            case Start:
                templateId = MiscImageFactory.QUEST_MARKER_EXCLAMATION_MARK;
                break;
            case Target:
                templateId = MiscImageFactory.QUEST_MARKER_QUESTION_MARK;
                break;
            default:
                throw new IllegalArgumentException("type has illegal value");
        }
        return MiscImageFactory.getInstance().getTemplate(templateId);
    }

    /**
     * Create a new quest marker with the required image and with the reference to the tile its displayed on.
     *
     * @param template the image template
     * @param parentTile the parent tile
     */
    public QuestMarker(@Nonnull MiscImageTemplate template, @Nonnull MapTile parentTile) {
        super(template);
        this.parentTile = parentTile;

        setBaseColor(null);
        setFadingCorridorEffectEnabled(false);
        availability = QuestMarkerAvailability.AvailableSoon;
    }

    @Override
    public int getAlpha() {
        Tile parentTileGraphic = parentTile.getTile();
        if (parentTileGraphic == null) {
            return 0;
        }
        return parentTileGraphic.getAlpha();
    }

    @Override
    public void show() {
        updateScreenPosition(0);

        super.show();
    }

    /**
     * This function is used to update the location of the quest marker on the display.
     */
    private void updateScreenPosition(int delta) {
        appliedOffset = AnimationUtility.approach(appliedOffset, parentTile.getQuestMarkerElevation(), 0, 300, delta);
        ServerCoordinate loc = parentTile.getCoordinates();

        int displayX = loc.toDisplayX();
        int displayY = loc.toDisplayY() - appliedOffset;
        int displayLayer = loc.toDisplayLayer(Layer.Overlays);
        setScreenPos(new DisplayCoordinate(displayX, displayY, displayLayer));
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        if (appliedOffset != parentTile.getQuestMarkerElevation()) {
            updateScreenPosition(delta);
        }
        super.update(container, delta);
    }

    /**
     * Get the availability state of this quest marker.
     *
     * @return the availability state
     */
    @Nonnull
    public QuestMarkerAvailability getAvailability() {
        return availability;
    }

    @Nonnull
    @Override
    public Color getLocalLight() {
        return COLOR_MAP.get(availability);
    }

    /**
     * Set the availability state of this quest marker.
     *
     * @param availability the new availability state
     */
    public void setAvailability(@Nonnull QuestMarkerAvailability availability) {
        this.availability = availability;
    }
}

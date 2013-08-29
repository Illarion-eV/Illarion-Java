package illarion.client.graphics;

import illarion.client.resources.MiscImageFactory;
import illarion.client.resources.data.MiscImageTemplate;
import illarion.client.world.Char;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.graphics.Layers;
import illarion.common.types.Location;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.ImmutableColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    /**
     * The tile this marker is displayed on.
     */
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
        COLOR_MAP = new EnumMap<QuestMarkerAvailability, Color>(QuestMarkerAvailability.class);
        COLOR_MAP.put(QuestMarkerAvailability.Available, new ImmutableColor(1.f, .75f, 0.f, 1.f));
        COLOR_MAP.put(QuestMarkerAvailability.AvailableSoon, new ImmutableColor(1.f, 1.f, 1.f, .8f));
    }

    /**
     * Create a new quest marker with the default image and with the reference to the tile its displayed on.
     *
     * @param parentTile the parent tile
     */
    public QuestMarker(@Nonnull final MapTile parentTile) {
        this(MiscImageFactory.getInstance().getTemplate(MiscImageFactory.QUEST_MARKER_QUESTIONMARK), parentTile);
    }

    /**
     * Create a new quest marker with the required image and with the reference to the tile its displayed on.
     *
     * @param template the image template
     * @param parentTile the parent tile
     */
    public QuestMarker(@Nonnull final MiscImageTemplate template, @Nonnull final MapTile parentTile) {
        super(template);
        this.parentTile = parentTile;

        setBaseColor(null);
        setFadingCorridorEffectEnabled(false);
        availability = QuestMarkerAvailability.AvailableSoon;

        updateScreenPosition();
    }

    /**
     * This function is used to update the location of the quest marker on the display.
     */
    private void updateScreenPosition() {
        appliedOffset = parentTile.getQuestMarkerElevation();
        final Location loc = parentTile.getLocation();
        setScreenPos(loc.getDcX(), loc.getDcY() - appliedOffset, loc.getDcZ(), Layers.OVERLAYS);
    }

    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
        if (appliedOffset != parentTile.getQuestMarkerElevation()) {
            updateScreenPosition();
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
    public void setAvailability(@Nonnull final QuestMarkerAvailability availability) {
        this.availability = availability;
    }
}

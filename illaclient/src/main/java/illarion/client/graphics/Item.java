/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import illarion.client.input.CurrentMouseLocationEvent;
import illarion.client.input.DoubleClickOnMapEvent;
import illarion.client.input.PointOnMapEvent;
import illarion.client.input.PrimaryKeyMapDrag;
import illarion.client.resources.ItemFactory;
import illarion.client.resources.Resource;
import illarion.client.resources.data.ItemTemplate;
import illarion.client.util.Lang;
import illarion.client.util.LookAtTracker;
import illarion.client.world.MapTile;
import illarion.common.graphics.MapConstants;
import illarion.common.graphics.MapVariance;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Location;
import org.illarion.engine.input.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.SceneEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A item is a object that is on the game map or in the inventory or in any container showcase of the client.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@SuppressWarnings("ClassNamingConvention")
public final class Item extends AbstractEntity<ItemTemplate> implements Resource {
    /**
     * The frame animation object that is used in case the item contains a animation that needs to be played.
     */
    @Nullable
    private final FrameAnimation animation;

    /**
     * The amount of items that are represented by this item instance. So in case the number is larger then 1 this
     * item represents a stack of items of the same kind.
     */
    private ItemCount count;

    /**
     * The text tag is the rendered text that shows the count of the item next to it.
     */
    @Nullable
    private TextTag number;

    /**
     * The tile this item is located on.
     */
    @Nonnull
    private final MapTile parentTile;

    /**
     * This indicates of the number of the item shall be shown. This number shows how many items are on this stack.
     * Its only useful to show this in case the item actually is a stack, so {@link #count} is greater then 1 and the
     * item is the only one or the one at the top position on one location.
     */
    private boolean showNumber;

    /**
     * True in case the object contains variances instead of a frame animation for the different frames of the image.
     * So if this is set to {@code true} all the frames of this image are not handles as a animation,
     * they are used as variances, selected by the location of the item on the map.
     */
    private final boolean variants;

    /**
     * The default constructor of a new item.
     *
     * @param template   the template used to create the new item
     * @param parentTile the tile this item is located on
     */
    public Item(@Nonnull final ItemTemplate template, @Nonnull final MapTile parentTile) {
        super(template);

        // an animated item
        if ((template.getAnimationSpeed() > 0) && (template.getFrames() > 1)) {
            // start animation right away. All items of this type will share it
            animation = template.getSharedAnimation();
            variants = false;
        } else if (template.getFrames() > 1) {
            // a tile with variants
            variants = true;
            animation = null;
        } else {
            animation = null;
            variants = false;
        }

        this.parentTile = parentTile;

        setFadingCorridorEffectEnabled(template.isEffectedByFadingCorridor());
    }

    @Override
    protected Color getParentLight() {
        final Tile parentGraphicTile = parentTile.getTile();
        if (parentGraphicTile == null) {
            return null;
        }
        return parentGraphicTile.getLocalLight();
    }

    /**
     * Create a new item instance for a ID and a specified location. The location is used in case the item has
     * variances. The item is not set on the map tile of the location by default.
     *
     * @param itemID    the ID of the item that shall be created
     * @param locColumn the column on the map where the item shall be created
     * @param locRow    the row on the map where the item shall be created
     * @param parent    the tile this item is located on
     * @return the new item
     */
    @Nonnull
    public static Item create(@Nonnull final ItemId itemID, final int locColumn,
                              final int locRow, @Nonnull final MapTile parent) {
        final ItemTemplate template = ItemFactory.getInstance().getTemplate(itemID.getValue());
        final Item item = new Item(template, parent);
        // Set variant and scaling, this functions check on their own if this is allowed
        item.setVariant(locColumn, locRow);
        item.setScale(locColumn, locRow);
        return item;
    }

    /**
     * Create a new item instance for a ID and a specified location. The location is used in case the item has
     * variances. The item is not set on the map tile of the location by default.
     *
     * @param itemID the ID of the item that shall be created
     * @param loc    the location where the item shall be shown
     * @param parent the tile this item is located on
     * @return the new item
     */
    @Nonnull
    public static Item create(@Nonnull final ItemId itemID, @Nonnull final Location loc,
                              @Nonnull final MapTile parent) {
        return create(itemID, loc.getCol(), loc.getRow(), parent);
    }

    @Override
    public int getHighlight() {
        return showHighlight;
    }

    @Override
    public void render(@Nonnull final Graphics g) {
        super.render(g);

        if (showNumber && (number != null)) {
            number.render(g);
        }

        showHighlight = 0;
    }

    /**
     * Enable the display of numbers for stacked items.
     *
     * @param newShowNumber {@code true} to show the number at this item
     */
    public void enableNumbers(final boolean newShowNumber) {
        showNumber = newShowNumber && getTemplate().getItemInfo().isMovable();
    }

    /**
     * Get the count of the item. A count greater then 1 means that this item
     * represents a item stack with the returned amount of items of the same
     * kind on it.
     *
     * @return the number of items on the stack or 1 in case there is just one
     *         item
     */
    public ItemCount getCount() {
        return count;
    }

    /**
     * Get the ID of this item.
     *
     * @return the ID of this item
     */
    @Nonnull
    public ItemId getItemId() {
        return new ItemId(getTemplate().getId());
    }

    private int showHighlight;

    /**
     * The logging instance that takes care for the logging output of this class.
     */
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOGGER = LoggerFactory.getLogger(Item.class);

    @Override
    public boolean isEventProcessed(@Nonnull final GameContainer container, final int delta,
                                    @Nonnull final SceneEvent event) {
        if (getLocalLight().getAlpha() == 0) {
            return false;
        }

        if (event instanceof CurrentMouseLocationEvent) {
            final CurrentMouseLocationEvent moveEvent = (CurrentMouseLocationEvent) event;
            if (!isMouseInInteractionRect(moveEvent.getX(), moveEvent.getY())) {
                return false;
            }

            showHighlight = 1;
            if (parentTile.getInteractive().isInUseRange()) {
                showHighlight = 2;
            }
            return true;
        }

        if (event instanceof PointOnMapEvent) {
            final PointOnMapEvent moveEvent = (PointOnMapEvent) event;
            if (!isMouseInInteractionRect(moveEvent.getX(), moveEvent.getY())) {
                return false;
            }

            if (!LookAtTracker.isLookAtObject(parentTile)) {
                LookAtTracker.setLookAtObject(parentTile);
                parentTile.getInteractive().lookAt();
            }
            return true;
        }

        if (!parentTile.isAtPlayerLevel()) {
            return false;
        }

        if (event instanceof DoubleClickOnMapEvent) {
            final DoubleClickOnMapEvent moveEvent = (DoubleClickOnMapEvent) event;
            if (moveEvent.getKey() != Button.Left) {
                return false;
            }

            if (!isMouseInInteractionRect(moveEvent.getX(), moveEvent.getY())) {
                return false;
            }

            parentTile.getInteractive().use();
            return true;
        }

        if (event instanceof PrimaryKeyMapDrag) {
            final PrimaryKeyMapDrag primKeyDragEvent = (PrimaryKeyMapDrag) event;
            if (!isMouseInInteractionRect(primKeyDragEvent.getOldX(), primKeyDragEvent.getOldY())) {
                return false;
            }

            return primKeyDragEvent.startDraggingItemFromTile(parentTile);
        }

        return false;
    }

    /**
     * Set number of stacked items.
     *
     * @param newCount the number of items on this stack, in case its more then
     *                 one a text is displayed next to the item that shown how many
     *                 items are on the stack
     */
    public void setCount(final ItemCount newCount) {
        count = newCount;

        // write number to text for display
        if (count.getValue() > 1) {
            number = new TextTag(count.getShortText(Lang.getInstance().getLocale()), Color.YELLOW);
            number.setOffset((MapConstants.TILE_W / 2) - number.getHeight() - number.getWidth(),
                    -number.getHeight() / 2);
        } else {
            number = null;
        }
    }

    @Override
    public void show() {
        // add to display list
        super.show();
        if (animation != null) {
            animation.addTarget(this, true);
        }
    }

    @Override
    public void hide() {
        if (animation != null) {
            animation.removeTarget(this);
        }
        super.hide();
    }

    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
        super.update(container, delta);

        if (showNumber && (number != null)) {
            number.addToCamera(getDisplayX(), getDisplayY());
            number.update(container, delta);
        }
    }

    /**
     * Determine the graphical variant from a coordinate and set the needed
     * frame on this.
     *
     * @param locX the first part of the coordinate
     * @param locY the second part of the coordinate
     */
    private void setVariant(final int locX, final int locY) {
        if (variants) {
            setFrame(MapVariance.getItemFrameVariance(locX, locY, getTemplate().getFrames()));
        }
    }

    /**
     * Set an individual scale dependent on a location. The new scale value is
     * directly applied to the item.
     *
     * @param locX the first part of the coordinate
     * @param locY the second part of the coordinate
     */
    private void setScale(final int locX, final int locY) {
        if (getTemplate().getItemInfo().hasVariance()) {
            setScale(MapVariance.getItemScaleVariance(locX, locY, getTemplate().getItemInfo().getVariance()));
        }
    }
}

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

import illarion.client.IllaClient;
import illarion.client.input.*;
import illarion.client.resources.ItemFactory;
import illarion.client.resources.Resource;
import illarion.client.resources.data.ItemTemplate;
import illarion.client.util.Lang;
import illarion.client.util.LookAtTracker;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.client.world.interactive.InteractiveMapTile;
import illarion.client.world.movement.MouseTargetMovementHandler;
import illarion.client.world.movement.TargetMovementHandler;
import illarion.common.graphics.MapConstants;
import illarion.common.graphics.MapVariance;
import illarion.common.gui.AbstractMultiActionHelper;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Location;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.SceneEvent;
import org.illarion.engine.input.Button;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Nullable
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
     * @param template the template used to create the new item
     * @param parentTile the tile this item is located on
     */
    public Item(@Nonnull ItemTemplate template, @Nonnull MapTile parentTile) {
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
        Tile parentGraphicTile = parentTile.getTile();
        if (parentGraphicTile == null) {
            return null;
        }
        return parentGraphicTile.getLocalLight();
    }

    /**
     * Create a new item instance for a ID and a specified location. The location is used in case the item has
     * variances. The item is not set on the map tile of the location by default.
     *
     * @param itemID the ID of the item that shall be created
     * @param locColumn the column on the map where the item shall be created
     * @param locRow the row on the map where the item shall be created
     * @param parent the tile this item is located on
     * @return the new item
     */
    @Nonnull
    public static Item create(
            @Nonnull ItemId itemID, int locColumn, int locRow, @Nonnull MapTile parent) {
        ItemTemplate template = ItemFactory.getInstance().getTemplate(itemID.getValue());
        Item item = new Item(template, parent);
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
     * @param loc the location where the item shall be shown
     * @param parent the tile this item is located on
     * @return the new item
     */
    @Nonnull
    public static Item create(
            @Nonnull ItemId itemID, @Nonnull Location loc, @Nonnull MapTile parent) {
        return create(itemID, loc.getCol(), loc.getRow(), parent);
    }

    @Override
    @Contract(pure = true)
    public int getHighlight() {
        return showHighlight;
    }

    @Override
    public void render(@Nonnull Graphics g) {
        if (performRendering()) {
            super.render(g);

            if (showNumber && (number != null)) {
                number.render(g);
            }

        }
        showHighlight = 0;
    }

    /**
     * Enable the display of numbers for stacked items.
     *
     * @param newShowNumber {@code true} to show the number at this item
     */
    public void enableNumbers(boolean newShowNumber) {
        showNumber = newShowNumber && getTemplate().getItemInfo().isMovable();
    }

    /**
     * Get the count of the item. A count greater then 1 means that this item
     * represents a item stack with the returned amount of items of the same
     * kind on it.
     *
     * @return the number of items on the stack or 1 in case there is just one
     * item
     */
    @Nullable
    @Contract(pure = true)
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
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(Item.class);

    private boolean isEventProcessed(@Nonnull CurrentMouseLocationEvent event) {
        if (!isMouseInInteractionRect(event.getX(), event.getY())) {
            return false;
        }

        if (!event.isHighlightHandled()) {
            showHighlight = 1;
            if (parentTile.getInteractive().isInUseRange()) {
                showHighlight = 2;
            }
            event.setHighlightHandled(true);
        }
        if (!parentTile.isBlocked()) {
            MouseTargetMovementHandler handler = World.getPlayer().getMovementHandler().getTargetMouseMovementHandler();
            handler.walkTo(parentTile.getLocation(), 0);
            return true;
        }
        return false;
    }

    private boolean isEventProcessed(@Nonnull PointOnMapEvent event) {
        if (!isMouseInInteractionRect(event.getX(), event.getY())) {
            return false;
        }

        if (!LookAtTracker.isLookAtObject(this)) {
            LookAtTracker.setLookAtObject(this);
            parentTile.getInteractive().lookAt(this);
        }
        return true;
    }

    private boolean isEventProcessed(@Nonnull ClickOnMapEvent event) {
        if (event.getKey() != Button.Left) {
            return false;
        }

        if (!parentTile.isAtPlayerLevel()) {
            return false;
        }

        delayGoToItem.reset();

        log.debug("Single clicking item: {}", getItemId());

        TargetMovementHandler handler = World.getPlayer().getMovementHandler().getTargetMovementHandler();

        boolean blocked = parentTile.isBlocked();
        boolean useRange = parentTile.getInteractive().isInUseRange();

        if (useRange) {
            log.trace("Clicked item {} is inside of use range.", getItemId());
            if (blocked) {
                return true;
            } else {
                log.trace("Clicked item {} allows walking to. Delaying move to accept double click.", getItemId());
                delayGoToItem.setLocation(parentTile.getLocation());
                delayGoToItem.pulse();
            }
        } else {
            log.trace("Clicked item {} is outside of use range.", getItemId());
            if (blocked) {
                handler.walkTo(parentTile.getLocation(), 1);
            } else {
                handler.walkTo(parentTile.getLocation(), 0);
            }
        }
        handler.assumeControl();
        return true;
    }

    private boolean isEventProcessed(@Nonnull DoubleClickOnMapEvent event) {
        if (event.getKey() != Button.Left) {
            return false;
        }

        if (!parentTile.isAtPlayerLevel()) {
            return false;
        }

        delayGoToItem.reset();

        log.debug("Double clicking item: {}", getItemId());

        if (parentTile.getInteractive().isInUseRange()) {
            log.trace("Item {} is within use range. Using it!", getItemId());
            parentTile.getInteractive().use();
        } else {
            log.trace("Item {} is outside of the use range. Walking to it.", getItemId());
            final InteractiveMapTile interactiveMapTile = parentTile.getInteractive();
            TargetMovementHandler handler = World.getPlayer().getMovementHandler().getTargetMovementHandler();
            handler.walkTo(parentTile.getLocation(), 1);
            handler.setTargetReachedAction(new Runnable() {
                @Override
                public void run() {
                    interactiveMapTile.use();
                }
            });
            handler.assumeControl();
        }

        return true;
    }

    private boolean isEventProcessed(@Nonnull PrimaryKeyMapDrag event) {
        if (!isMouseInInteractionRect(event.getOldX(), event.getOldY()) || !parentTile.isAtPlayerLevel()) {
            return false;
        }
        return event.startDraggingItemFromTile(parentTile);

    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isEventProcessed(@Nonnull GameContainer container, int delta, @Nonnull SceneEvent event) {
        if (getAlpha() == 0) {
            return false;
        }

        if (event instanceof AbstractMouseLocationEvent) {
            AbstractMouseLocationEvent locationEvent = (AbstractMouseLocationEvent) event;
            if (isMouseInInteractionRect(locationEvent.getX(), locationEvent.getY())) {
                if (event instanceof CurrentMouseLocationEvent) {
                    return isEventProcessed((CurrentMouseLocationEvent) event);
                }

                if (event instanceof PointOnMapEvent) {
                    return isEventProcessed((PointOnMapEvent) event);
                }

                if (event instanceof ClickOnMapEvent) {
                    return isEventProcessed((ClickOnMapEvent) event);
                }

                if (event instanceof DoubleClickOnMapEvent) {
                    return isEventProcessed((DoubleClickOnMapEvent) event);
                }
            }
            if (event instanceof PrimaryKeyMapDrag) {
                return isEventProcessed((PrimaryKeyMapDrag) event);
            }
        }


        return false;
    }

    private static final class DelayGoToItemHandler extends AbstractMultiActionHelper {
        @Nullable
        private Location target;

        DelayGoToItemHandler() {
            super(IllaClient.getCfg().getInteger("doubleClickInterval"), 2);
        }

        void setLocation(@Nullable Location target) {
            this.target = target;
        }

        @Override
        public void executeAction(int count) {
            if ((count == 1) && (target != null)) {
                TargetMovementHandler handler = World.getPlayer().getMovementHandler().getTargetMovementHandler();
                handler.walkTo(target, 0);
                handler.assumeControl();
            }
        }
    }

    @Nonnull
    private static final DelayGoToItemHandler delayGoToItem = new DelayGoToItemHandler();

    @Override
    protected boolean isMouseInInteractionRect(int mouseX, int mouseY) {
        if (super.isMouseInInteractionRect(mouseX, mouseY)) {
            if (isCurrentlyEffectedByFadingCorridor()) {
                Tile tile = parentTile.getTile();
                return (tile != null) && tile.isMouseInInteractionRect(mouseX, mouseY);
            }
            return true;
        }
        return false;
    }

    /**
     * Set number of stacked items.
     *
     * @param newCount the number of items on this stack, in case its more then
     * one a text is displayed next to the item that shown how many
     * items are on the stack
     */
    public void setCount(@Nullable ItemCount newCount) {
        count = newCount;

        // write number to text for display
        if (ItemCount.isGreaterOne(count)) {
            number = new TextTag(count.getShortText(Lang.getInstance().getLocale()), Color.YELLOW);
            number.setOffset((MapConstants.TILE_W / 2) - number.getHeight() - number.getWidth(),
                             -number.getHeight() / 2);
        } else {
            number = null;
        }
    }

    @Nullable
    private ItemStack parentStack;

    public void show(@Nonnull ItemStack stack) {
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        parentStack = stack;
        if (animation != null) {
            animation.addTarget(this, true);
        }
    }

    @Override
    @Contract("->fail")
    public void show() {
        throw new IllegalStateException("Calling show directly is not permitted for items.");
    }

    @Override
    public void hide() {
        parentStack = null;
        if (animation != null) {
            animation.removeTarget(this);
        }
    }

    @Override
    @Contract(pure = true)
    public boolean isShown() {
        ItemStack localStack = parentStack;
        return (localStack != null) && localStack.isShown();
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        super.update(container, delta);

        if (showNumber && (number != null)) {
            number.addToCamera(getDisplayX(), getDisplayY());
            number.update(container, delta);
        }
    }

    @Override
    public int getTargetAlpha() {
        Tile tileOfItem = parentTile.getTile();
        int alphaOfTile = (tileOfItem == null) ? Color.MAX_INT_VALUE : tileOfItem.getTargetAlpha();
        return Math.min(super.getTargetAlpha(), alphaOfTile);
    }

    /**
     * Determine the graphical variant from a coordinate and set the needed
     * frame on this.
     *
     * @param locX the first part of the coordinate
     * @param locY the second part of the coordinate
     */
    private void setVariant(int locX, int locY) {
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
    private void setScale(int locX, int locY) {
        if (getTemplate().getItemInfo().hasVariance()) {
            setScale(MapVariance.getItemScaleVariance(locX, locY, getTemplate().getItemInfo().getVariance()));
        }
    }
}

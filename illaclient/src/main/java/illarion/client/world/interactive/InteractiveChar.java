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
package illarion.client.world.interactive;

import illarion.client.graphics.Avatar;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.net.client.UseMapCmd;
import illarion.client.world.Char;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.types.ItemCount;
import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * This class represents the interactive variant of a character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class InteractiveChar implements Draggable, DropTarget, Usable {
    /**
     * The character this interactive reference points to.
     */
    @Nonnull
    private final Char parentChar;

    /**
     * Create a new interactive reference to a character.
     *
     * @param parent the character this interactive reference points to
     */
    public InteractiveChar(@Nonnull Char parent) {
        parentChar = parent;
    }

    /**
     * Drag one character to another character. Does nothing currently.
     */
    @Override
    public void dragTo(@Nonnull InteractiveChar targetChar, @Nonnull ItemCount count) {
        // nothing
    }

    /**
     * Dragging the character into the inventory does nothing at all.
     */
    @Override
    public void dragTo(@Nonnull InteractiveInventorySlot targetSlot, @Nonnull ItemCount count) {
        // nothing
    }

    /**
     * Drag the character to another spot on the map. This causes pushing the
     * character.
     */
    @Override
    public void dragTo(@Nonnull InteractiveMapTile targetTile, @Nonnull ItemCount count) {
        // nothing
    }

    @Override
    public void dragTo(@Nonnull InteractiveContainerSlot targetSlot, @Nonnull ItemCount count) {
        // nothing
    }

    /**
     * Check if the avatar of the character is located at the specified screen coordinates.
     *
     * @param screenX the screen X coordinate
     * @param screenY the screen Y coordinate
     * @return {@code true} in case the character is at the screen location
     */
    public boolean isCharOnScreenLoc(int screenX, int screenY) {
        MapDisplayManager displayManager = World.getMapDisplay();

        return isCharOnDisplayLoc(displayManager.getWorldX(screenX), displayManager.getWorldY(screenY));
    }

    /**
     * Check if the avatar of the character is located at the specified display coordinates.
     *
     * @param displayX the display X coordinate
     * @param displayY the display Y coordinate
     * @return {@code true} in case the character is at the display location
     */
    public boolean isCharOnDisplayLoc(int displayX, int displayY) {
        @Nullable Avatar avatar = parentChar.getAvatar();
        return (avatar != null) && avatar.getDisplayRect().isInside(displayX, displayY);
    }

    /**
     * Get the location of the character on the map.
     *
     * @return the location of the character on the map
     */
    @Nonnull
    public ServerCoordinate getLocation() {
        return parentChar.getLocation();
    }

    /**
     * Get the interactive tile this character is standing on.
     *
     * @return the interactive tile of this character
     */
    @Nullable
    public InteractiveMapTile getInteractiveTile() {
        @Nullable MapTile parentTile = getMapTile();
        if (parentTile == null) {
            return null;
        }
        return parentTile.getInteractive();
    }

    /**
     * Get the map tile this character is standing on.
     *
     * @return the tile this character is standing on
     */
    @Nullable
    public MapTile getMapTile() {
        return World.getMap().getMapAt(parentChar.getLocation());
    }

    /**
     * Get the display level. This can be used to determine the order of multiple objects in the render list.
     *
     * @return the display level
     */
    public int getDisplayLevel() {
        @Nullable Avatar avatar = parentChar.getAvatar();
        if (avatar == null) {
            return Integer.MAX_VALUE;
        }
        return avatar.getOrder();
    }

    /**
     * Perform a use operation on this Avatar.
     */
    @Override
    public void use() {
        if (!isInUseRange()) {
            return;
        }

        World.getNet().sendCommand(new UseMapCmd(getLocation()));
    }

    /**
     * Check if the Avatar is inside the valid using range of the player character.
     *
     * @return {@code true} in case the character is allowed to use anything on this tile or the tile itself
     */
    @Override
    public boolean isInUseRange() {
        @Nonnull ServerCoordinate playerLocation = World.getPlayer().getLocation();
        return (playerLocation.getZ() == getLocation().getZ()) &&
                (playerLocation.getStepDistance(getLocation()) <= getUseRange());
    }

    @Override
    public int getUseRange() {
        return 2;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Interactive " + parentChar;
    }
}

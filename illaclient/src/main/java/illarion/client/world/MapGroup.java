/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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
package illarion.client.world;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to organise the maps into groups. This is done to show and hide whole groups of maps.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MapGroup {
    /**
     * In case this flag is turned {@code true} the entire map group is hidden. This value has no effect at all in
     * case the {@link #parent} is not set to {@code null}.
     */
    private boolean hidden;

    /**
     * The parent group that will overwrite the local hidden value. This is used to connect multiple map groups.
     */
    @Nullable
    private MapGroup parent;

    /**
     * This list contains a list of groups that will overwrite the hidden state of the group. In case one of the
     * groups in this list is hidden, this group will be assumed hidden as well.
     */
    @Nullable
    private List<MapGroup> overwritingGroups;

    /**
     * This list stores the children of this map group.
     */
    @Nullable
    private List<MapGroup> children;

    /**
     * Get the root group. This could either be this group or a parent group that has not further parent.
     *
     * @return the root group
     */
    @Nonnull
    public MapGroup getRootGroup() {
        MapGroup currentGroup = this;
        while (true) {
            assert currentGroup != null;
            if (currentGroup.parent == null) {
                return currentGroup;
            }
            currentGroup = currentGroup.parent;
        }
    }

    /**
     * Check if this map group is currently hidden.
     *
     * @return {@code in case the map group is hidden}
     */
    public boolean isHidden() {
        if (getRootGroup().isOverwritingGroupHidden()) {
            return true;
        }
        return hidden;
    }

    /**
     * Check if one of the overwriting groups of this map group is flagged as hidden.
     *
     * @return {@code true} in case one of the overwriting groups is hidden
     */
    private boolean isOverwritingGroupHidden() {
        @Nullable final List<MapGroup> lclList = overwritingGroups;
        if (lclList != null) {
            for (final MapGroup group : lclList) {
                if (group.isHidden()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRootGroup() {
        return parent == null;
    }

    /**
     * Set the hidden flag of this map group.
     *
     * @param hidden the hidden flag
     */
    public void setHidden(final boolean hidden) {
        MapGroup other = this;
        while (true) {
            assert other != null;
            if (other.parent == null) {
                other.hidden = hidden;
                other.sendHiddenToChildren();
                return;
            }
            other = other.parent;
        }
    }

    /**
     * Send the hidden flag to all children.
     */
    private void sendHiddenToChildren() {
        if (children == null) {
            return;
        }

        for (MapGroup aChildren : children) {
            aChildren.hidden = hidden;
        }
    }

    /**
     * Apply a parent to this map group.
     *
     * @param parent the parent of this group
     */
    public void setParent(@Nonnull final MapGroup parent) {
        if (parent.parent != null) {
            throw new IllegalArgumentException("Set a parent group that is not a root group is not allowed.");
        }
        if (this.parent != null) {
            throw new IllegalArgumentException("Setting a parent to a group that already has a parent is not allows");
        }
        this.parent = parent;
        parent.addChild(this);

        if (children != null) {
            for (MapGroup aChildren : children) {
                parent.addChild(aChildren);
            }
            children = null;
        }

        if (overwritingGroups != null) {
            for (final MapGroup group : overwritingGroups) {
                parent.addOverwritingGroup(group);
            }
            overwritingGroups = null;
        }
    }

    /**
     * Add a child to the list of children of this map group.
     *
     * @param child the child to add
     */
    private void addChild(@Nonnull final MapGroup child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(child)) {
            children.add(child);
        }
    }

    /**
     * Add a group to the list of overwriting groups.
     *
     * @param group the group to add to the list of overwriting groups
     */
    public void addOverwritingGroup(@Nonnull final MapGroup group) {
        if (parent != null) {
            throw new IllegalStateException("Adding overwriting groups no non-root groups is not allowed.");
        }
        if (overwritingGroups == null) {
            overwritingGroups = new ArrayList<>();
        }
        if (!overwritingGroups.contains(group)) {
            overwritingGroups.add(group);
        }
    }
}

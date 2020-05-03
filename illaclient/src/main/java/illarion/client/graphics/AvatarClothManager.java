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

import illarion.client.resources.ClothFactory;
import illarion.client.resources.ItemFactory;
import illarion.client.resources.data.AvatarClothTemplate;
import illarion.client.resources.data.ItemTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The Cloth manager stores all known clothes and their locations for each known
 * avatar. And allows accessing the cloth definitions.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AvatarClothManager {
    /**
     * The index of the group of clothes that contains the beards types this
     * avatar could wear.
     */
    public static final int GROUP_BEARD; // NO_UCD

    /**
     * The index of the groups of all chest parts, such as shirts, armors and so
     * on this avatar can wear.
     */
    public static final int GROUP_CHEST; // NO_UCD

    /**
     * The index of the groups of all coats a avatar can wear. Such as coats,
     * capes, dresses.
     */
    public static final int GROUP_COAT; // NO_UCD

    /**
     * The amount of groups that exist to sort the paper dolling objects in.
     */
    public static final int GROUP_COUNT; // NO_UCD

    /**
     * The index of the group of items the avatar can wear in the right hand.
     * Tools weapons and similar things could be listed there.
     */
    public static final int GROUP_FIRST_HAND; // NO_UCD

    /**
     * The index of the group of clothes that contains the hair types this
     * avatar could wear.
     */
    public static final int GROUP_HAIR; // NO_UCD

    /**
     * The index of the groups of all hats this avatar can wear.
     */
    public static final int GROUP_HAT; // NO_UCD

    /**
     * The index of the group of items the avatar can wear in the left hand.
     * Tools weapons and similar things could be listed there.
     */
    public static final int GROUP_SECOND_HAND; // NO_UCD

    /**
     * The index of the group of shoes and boots a avatar can wear.
     */
    public static final int GROUP_SHOES; // NO_UCD

    /**
     * The index of the group of trousers a avatar can wear.
     */
    public static final int GROUP_TROUSERS; // NO_UCD

    static {
        int cnt = 0;
        GROUP_HAIR = cnt++;
        GROUP_BEARD = cnt++;
        GROUP_HAT = cnt++;
        GROUP_CHEST = cnt++;
        GROUP_COAT = cnt++;
        GROUP_FIRST_HAND = cnt++;
        GROUP_SECOND_HAND = cnt++;
        GROUP_TROUSERS = cnt++;
        GROUP_SHOES = cnt++;

        GROUP_COUNT = cnt;
    }

    /**
     * The storage for the known clothes of each group.
     */
    @Nonnull
    private final ClothFactory[] existingClothes;

    /**
     * Constructor for this class. Each avatar needs one instance of this class
     * to get the paper dolling working.
     */
    public AvatarClothManager() {
        existingClothes = new ClothFactory[GROUP_COUNT];
    }

    /**
     * Add a item to the storage. It has to be sorted into a group here. Its
     * only possible to define a itemID once per group. But a single item can be
     * defined in multiple groups.
     *
     * @param group the group the item shall be assigned to
     * @param cloth the definition of the cloth itself
     */
    public void addCloth(int group, @Nonnull AvatarClothTemplate cloth) {
        if ((group < 0) || (group >= GROUP_COUNT)) {
            throw new IllegalArgumentException("Group needs to be between 0 and " + GROUP_COUNT);
        }
        if (existingClothes[group] == null) {
            existingClothes[group] = new ClothFactory();
            existingClothes[group].init();
        }
        existingClothes[group].storeResource(cloth);
    }

    /**
     * Check if a cloth item is defined on a specified location.
     *
     * @param group the location of the cloth
     * @param itemID the item id of the item that shall be checked
     * @return {@code true} in case the cloth item is defined for that location
     */
    public boolean doesClothExists(int group, int itemID) {
        if ((group < 0) || (group >= GROUP_COUNT)) {
            throw new IllegalArgumentException("Group needs to be between 0 and " + GROUP_COUNT);
        }
        if (existingClothes[group] == null) {
            return false;
        }
        int refID;

        if ((group == GROUP_HAIR) || (group == GROUP_BEARD)) {
            refID = itemID;
        } else {
            ItemTemplate refItem = ItemFactory.getInstance().getTemplate(itemID);
            refID = refItem.getPaperdollingId();
        }
        return (refID != 0) && existingClothes[group].hasTemplate(refID);
    }

    /**
     * Cleanup all internal data for the actual client runtime. This method is
     * called after the loading is done to optimize the internal storage.
     */
    public void finish() {
        for (ClothFactory factory : existingClothes) {
            if (factory != null) {
                factory.loadingFinished();
            }
        }
    }

    /**
     * Get a item from the storage.
     *
     * @param group the group the requested item is assigned to
     * @param itemID the item id of the requested item
     * @param parentAvatar the avatar the cloth will belong to
     * @return the item that was found regarding the parameters or {@code null} in case the requested item was not
     * defined in this storage
     */
    @Nullable
    public AvatarCloth getCloth(int group, int itemID, @Nonnull Avatar parentAvatar) {
        if ((group < 0) || (group >= GROUP_COUNT)) {
            throw new IllegalArgumentException("Group needs to be between 0 and " + GROUP_COUNT);
        }
        if (existingClothes[group] == null) {
            return null;
        }
        int refID;
        @Nullable ItemTemplate refItem;

        if ((group == GROUP_HAIR) || (group == GROUP_BEARD)) {
            refID = itemID;
            refItem = null;
        } else {
            refItem = ItemFactory.getInstance().getTemplate(itemID);
            refID = refItem.getPaperdollingId();
        }
        AvatarClothTemplate template;
        try {
            template = existingClothes[group].getTemplate(refID);
        } catch (@Nonnull IllegalStateException ex) {
            return null;
        }
        if (template.getId() == 0) {
            return null;
        }
        AvatarCloth ret = new AvatarCloth(template, parentAvatar);
        if (refItem != null) {
            ret.setBaseColor(refItem.getPaperdollingColor());
        }
        return ret;
    }

    @Nonnull
    public static String toString(int slot) {
        if (slot == GROUP_BEARD) {
            return "Beard";
        } else if (slot == GROUP_CHEST) {
            return "Chest";
        } else if (slot == GROUP_COAT) {
            return "Coat";
        } else if (slot == GROUP_FIRST_HAND) {
            return "First Hand";
        } else if (slot == GROUP_HAIR) {
            return "Hair";
        } else if (slot == GROUP_HAT) {
            return "Hat";
        } else if (slot == GROUP_SECOND_HAND) {
            return "Second Hand";
        } else if (slot == GROUP_SHOES) {
            return "Shoes";
        } else if (slot == GROUP_TROUSERS) {
            return "Hair";
        } else {
            return "Unknown Cloth";
        }
    }
}

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
package illarion.client.graphics;

import illarion.client.resources.ClothFactory;
import illarion.client.resources.ItemFactory;
import illarion.client.resources.data.AvatarClothTemplate;
import illarion.client.resources.data.ItemTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * The Cloth manager stores all known clothes and their locations for each known avatar. And allows accessing the cloth
 * definitions.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AvatarClothManager {
    /**
     * The storage for the known clothes of each group.
     */
    @Nonnull
    private final Map<AvatarClothGroup, ClothFactory> existingClothes;

    /**
     * Constructor for this class. Each avatar needs one instance of this class to get the paper dolling working.
     */
    public AvatarClothManager() {
        existingClothes = new EnumMap<>(AvatarClothGroup.class);
    }

    @Nonnull
    public static String toString(@Nonnull AvatarClothGroup group) {
        return "AvatarClothManager(" + group.name() + ')';
    }

    /**
     * Add a item to the storage. It has to be sorted into a group here. Its only possible to define a itemID once per
     * group. But a single item can be defined in multiple groups.
     *
     * @param group the group the item shall be assigned to
     * @param cloth the definition of the cloth itself
     */
    public void addCloth(@Nonnull AvatarClothGroup group, @Nonnull AvatarClothTemplate cloth) {
        existingClothes.compute(group, (g, factory) -> {
            if (factory == null) {
                factory = new ClothFactory();
                factory.init();
            }
            factory.storeResource(cloth);
            return factory;
        });
    }

    /**
     * Check if a cloth item is defined on a specified location.
     *
     * @param group the location of the cloth
     * @param itemID the item id of the item that shall be checked
     * @return {@code true} in case the cloth item is defined for that location
     */
    public boolean doesClothExists(@Nonnull AvatarClothGroup group, int itemID) {
        ClothFactory factory = existingClothes.get(group);
        if (factory == null) {
            return false;
        }
        int refID;

        if ((group == AvatarClothGroup.Hair) || (group == AvatarClothGroup.Beard)) {
            refID = itemID;
        } else {
            ItemTemplate refItem = ItemFactory.getInstance().getTemplate(itemID);
            refID = refItem.getPaperdollingId();
        }
        return (refID != 0) && factory.hasTemplate(refID);
    }

    /**
     * Cleanup all internal data for the actual client runtime. This method is called after the loading is done to
     * optimize the internal storage.
     */
    public void finish() {
        existingClothes.forEach((g, factory) -> factory.loadingFinished());
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
    public AvatarCloth getCloth(@Nonnull AvatarClothGroup group, int itemID, @Nonnull AbstractEntity<?> parentAvatar) {
        ClothFactory factory = existingClothes.get(group);
        if (factory == null) {
            return null;
        }

        int refID;
        @Nullable ItemTemplate refItem;

        if ((group == AvatarClothGroup.Hair) || (group == AvatarClothGroup.Beard)) {
            refID = itemID;
            refItem = null;
        } else {
            refItem = ItemFactory.getInstance().getTemplate(itemID);
            refID = refItem.getPaperdollingId();
        }
        AvatarClothTemplate template;
        try {
            template = factory.getTemplate(refID);
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

    public enum AvatarClothGroup {
        /**
         * The index of the group of clothes that contains the beards types this avatar could wear.
         */
        Beard(1),

        /**
         * The index of the groups of all chest parts, such as shirts, armors and so on this avatar can wear.
         */
        Chest(3),

        /**
         * The index of the groups of all coats a avatar can wear. Such as coats, capes, dresses.
         */
        Coat(4),

        /**
         * The index of the group of items the avatar can wear in the right hand. Tools weapons and similar things could
         * be listed there.
         */
        FirstHand(5),

        /**
         * The index of the group of clothes that contains the hair types this avatar could wear.
         */
        Hair(0),

        /**
         * The index of the groups of all hats this avatar can wear.
         */
        Hat(2),

        /**
         * The index of the group of items the avatar can wear in the left hand. Tools weapons and similar things could
         * be listed there.
         */
        SecondHand(6),

        /**
         * The index of the group of shoes and boots a avatar can wear.
         */
        Shoes(8),

        /**
         * The index of the group of trousers a avatar can wear.
         */
        Trousers(7);
        private final int groupId;

        AvatarClothGroup(int id) {
            groupId = id;
        }

        public int getGroupId() {
            return groupId;
        }
    }
}

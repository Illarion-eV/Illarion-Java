/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.world.items;

import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Money;

import javax.annotation.Nonnull;

/**
 * This class is able to store a single item that is sold by a NPC merchant.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public class MerchantItem {
    /**
     * This enumerator contains all possible item types.
     */
    public enum MerchantItemType {
        /**
         * This item is a item that is sold by the NPC.
         */
        SellingItem,

        /**
         * This item is a item that is bought by the NPC for a good price.
         */
        BuyingPrimaryItem,

        /**
         * This item is a item that is bought by the NPC for a poor price.
         */
        BuyingSecondaryItem
    }

    /**
     * The index of the item inside the list of items as it was transferred.
     */
    private final int index;

    /**
     * The ID of the item.
     */
    private final ItemId itemId;

    /**
     * The name that is supposed to be displayed along with the item.
     */
    private final String name;

    /**
     * The price of the item in copper coins.
     */
    private final Money price;

    /**
     * The type of this item.
     */
    private final MerchantItemType type;

    /**
     * The amount of items sold at once.
     */
    private final ItemCount bundleSize;

    /**
     * Create a new instance of that merchant item.
     *
     * @param itemIndex the index of the item in the list of items that was send by the server
     * @param itemType the type of the item
     * @param id the ID of the item
     * @param itemName the name of the item
     * @param itemPrice the price of the item in copper coins
     * @param amount the amount of items sold at once
     */
    public MerchantItem(
            int itemIndex,
            MerchantItemType itemType,
            ItemId id,
            String itemName,
            long itemPrice,
            ItemCount amount) {
        index = itemIndex;
        type = itemType;
        itemId = id;
        name = itemName;
        price = new Money(itemPrice);
        bundleSize = amount;
    }

    /**
     * Create a new instance of that merchant item.
     *
     * @param itemIndex the index of the item in the list of items that was send by the server
     * @param itemType the type of the item
     * @param id the ID of the item
     * @param itemName the name of the item
     * @param itemPrice the price of the item in copper coins
     */
    public MerchantItem(
            int itemIndex,
            MerchantItemType itemType,
            ItemId id,
            String itemName,
            long itemPrice) {
        this(itemIndex, itemType, id, itemName, itemPrice, ItemCount.getInstance(1));
    }

    /**
     * Copy constructor that creates a duplicate of the item.
     *
     * @param org the original item to be copied
     */
    public MerchantItem(@Nonnull MerchantItem org) {
        index = org.index;
        type = org.type;
        itemId = org.itemId;
        name = org.name;
        price = org.price;
        bundleSize = org.bundleSize;
    }

    /**
     * The index of the merchant item in the list as it was transferred from the server.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * The ID of the item that is supposed to be traded.
     *
     * @return the item ID
     */
    public ItemId getItemId() {
        return itemId;
    }

    /**
     * The name of the item that was transferred and is supposed to be displayed.
     *
     * @return the name of the item
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Get the price of the item in copper coins.
     *
     * @return the price of the item
     */
    @Nonnull
    public Money getPrice() {
        return price;
    }

    /**
     * Get the type of the item.
     *
     * @return the type of the item
     */
    public MerchantItemType getType() {
        return type;
    }

    /**
     * Get the size of a bundle that is bought from the NPC.
     *
     * @return the bundle size of this item
     */
    @Nonnull
    public ItemCount getBundleSize() {
        return bundleSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof MerchantItem) {
            MerchantItem item = (MerchantItem) obj;
            if ((item.getIndex() == getIndex()) && (item.getBundleSize() == item.getBundleSize()) &&
                    (item.getItemId() == getItemId()) && item.getPrice().equals(getPrice()) &&
                    (item.getType() == item.getType())) {
                return true;
            }
        }
        return false;
    }
}

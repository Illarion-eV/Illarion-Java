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
package illarion.client.gui.util;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.render.NiftyImage;
import illarion.client.gui.EntitySlickRenderImage;
import illarion.client.resources.ItemFactory;
import illarion.client.world.items.MerchantItem;
import org.illarion.nifty.controls.MerchantListEntry;

import javax.annotation.Nonnull;

/**
 * This implementation of the merchant item is very similar to the original merchant item. It just adds a few entries
 * of data that are needed so the item can be displayed properly in the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class NiftyMerchantItem extends MerchantItem implements MerchantListEntry {
    /**
     * The image that represents this merchant item.
     */
    @Nonnull
    private final NiftyImage itemImage;

    /**
     * Create a new instance of that merchant item.
     * <p/>
     * This constructor performs changes to the Nifty-GUI. Do not call it outside the regular update loop of the GUI.
     *
     * @param nifty the instance of the Nifty-GUI used to create the objects for the GUI
     * @param org the original merchant item that contains the actual data
     */
    public NiftyMerchantItem(@Nonnull Nifty nifty, @Nonnull MerchantItem org) {
        super(org);

        itemImage = new NiftyImage(nifty.getRenderEngine(), new EntitySlickRenderImage(
                ItemFactory.getInstance().getTemplate(org.getItemId().getValue())));
    }

    /**
     * Get the image that is meant to display this merchant item in the list.
     *
     * @return the Nifty image
     */
    @Nonnull
    @Override
    public NiftyImage getItemImage() {
        return itemImage;
    }

    @Nonnull
    @Override
    public EntryType getEntryType() {
        switch (getType()) {
            case BuyingPrimaryItem: return EntryType.BuyPrimary;
            case BuyingSecondaryItem: return EntryType.BuySecondary;
            case SellingItem: return EntryType.Selling;
        }
        throw new IllegalStateException("Unknown merchant entry type: " + getType());
    }
}

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
package illarion.client.gui.util;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.render.NiftyImage;
import illarion.client.gui.EntitySlickRenderImage;
import illarion.client.resources.ItemFactory;
import illarion.client.world.items.CraftingIngredientItem;
import illarion.client.world.items.CraftingItem;
import illarion.common.types.ItemId;
import org.illarion.nifty.controls.CraftingItemEntry;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Karing
 * Date: 06.10.12
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
public class NiftyCraftingItem extends CraftingItem implements CraftingItemEntry {
    private final NiftyImage craftImage;
    private final NiftyImage[] ingredientImages;
    private final int itemIndex;

    /**
     * Constructor that applies all required values.
     *
     * @param group          the group this item belongs to
     * @param itemId         the ID of the item that is crafted
     * @param name           the name of the item that is crafted
     * @param buildTime      the time required to craft this item
     * @param buildStackSize the amount of items crafted at once
     * @param ingredients    the ingredients required to build this
     */
    public NiftyCraftingItem(final Nifty nifty, final int group, final int index, final ItemId itemId,
                             final String name,
                             final int buildTime, final int buildStackSize, final CraftingIngredientItem[] ingredients) {
        super(group, itemId, name, buildTime, buildStackSize, ingredients);

        itemIndex = index;

        craftImage = new NiftyImage(nifty.getRenderEngine(),
                new EntitySlickRenderImage(ItemFactory.getInstance().getPrototype(itemId)));

        ingredientImages = new NiftyImage[ingredients.length];
        for (int i = 0; i < ingredientImages.length; i++) {
            ingredientImages[i] = new NiftyImage(nifty.getRenderEngine(),
                    new EntitySlickRenderImage(ItemFactory.getInstance().getPrototype(ingredients[i].getItemId())));
        }
    }

    public NiftyCraftingItem(final Nifty nifty, final int index, final CraftingItem org) {
        super(org);

        craftImage = new NiftyImage(nifty.getRenderEngine(),
                new EntitySlickRenderImage(ItemFactory.getInstance().getPrototype(getItemId())));

        itemIndex = index;

        ingredientImages = new NiftyImage[getIngredientCount()];
        for (int i = 0; i < ingredientImages.length; i++) {
            ingredientImages[i] = new NiftyImage(nifty.getRenderEngine(),
                    new EntitySlickRenderImage(ItemFactory.getInstance().getPrototype(getIngredientItemId(i))));
        }
    }

    @Override
    public ItemId getIngredientItemId(final int index) {
        return getIngredient(index).getItemId();
    }

    @Override
    public int getIngredientAmount(final int index) {
        return getIngredient(index).getCount();
    }

    @Override
    public NiftyImage getImage() {
        return craftImage;
    }

    /**
     * Get the index of this item.
     *
     * @return the index of the item
     */
    @Override
    public int getItemIndex() {
        return itemIndex;
    }

    /**
     * Get the crafting time in seconds.
     *
     * @return the crafting time in seconds
     */
    @Override
    public double getCraftTime() {
        return (float) getBuildTime() / 10.f;
    }

    @Override
    public NiftyImage getIngredientImage(final int index) {
        return ingredientImages[index];
    }

    /**
     * Get the text displayed in the tree.
     *
     * @return the text to display in the tree
     */
    @Override
    public String getTreeLabel() {
        return getName();
    }
}

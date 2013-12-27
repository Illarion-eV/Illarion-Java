/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls.dialog.merchant;

import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.common.types.ItemCount;
import illarion.common.types.Money;
import org.illarion.nifty.controls.MerchantListEntry;

import javax.annotation.Nonnull;

/**
 * This converter is used to display the merchant items in the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("UnusedDeclaration")
public final class MerchantItemListViewConverter implements ListBox.ListBoxViewConverter<MerchantListEntry> {
    @Override
    public void display(@Nonnull final Element listBoxItem, @Nonnull final MerchantListEntry item) {
        final Element itemImage = listBoxItem.findElementById("#imageDisplay");
        final NiftyImage itemPicture = item.getItemImage();
        itemImage.getRenderer(ImageRenderer.class).setImage(itemPicture);

        int imageHeight = itemPicture.getHeight();
        int imageWidth = itemPicture.getWidth();

        if (imageHeight > 46) {
            imageWidth = (int) ((float) imageWidth * (46.f / imageHeight));
            imageHeight = 46;
        }

        if (imageWidth > 76) {
            imageHeight = (int) ((float) imageHeight * (76.f / imageWidth));
            imageWidth = 76;
        }

        itemImage.setConstraintHeight(SizeValue.px(imageHeight));
        itemImage.setConstraintWidth(SizeValue.px(imageWidth));

        final Element title = listBoxItem.findElementById("#itemTitle");
        title.getRenderer(TextRenderer.class).setText(item.getName());

        final Money money = item.getPrice();
        final int gold = money.getGold();
        final int silver = money.getSilver();
        final int copper = money.getCopper();

        applyMoneyValues(gold, listBoxItem.findElementById("#moneyGoldCount"),
                listBoxItem.findElementById("#moneyGoldImage"));
        applyMoneyValues(silver, listBoxItem.findElementById("#moneySilverCount"),
                listBoxItem.findElementById("#moneySilverImage"));
        applyMoneyValues(copper, listBoxItem.findElementById("#moneyCopperCount"),
                listBoxItem.findElementById("#moneyCopperImage"));

        final Element bundleSizeDisplay = listBoxItem.findElementById("#bundleSizeDisplay");
        if (ItemCount.isGreaterOne(item.getBundleSize())) {
            bundleSizeDisplay.setVisible(true);
            bundleSizeDisplay.getRenderer(TextRenderer.class).setText(Integer.toString(item.getBundleSize().getValue()));
        } else {
            bundleSizeDisplay.setVisible(false);
        }

        listBoxItem.layoutElements();

        listBoxItem.getNiftyControl(DialogMerchantEntryControl.class).setIndex(item.getIndex());
    }

    /**
     * This function is used to apply the money data to the displayed entries.
     *
     * @param money        the money value
     * @param textDisplay  the text display for this part of the money
     * @param imageDisplay the image display for this part of money
     */
    private static void applyMoneyValues(final int money, @Nonnull final Element textDisplay, @Nonnull final Element imageDisplay) {
        if (money > 0) {
            textDisplay.getRenderer(TextRenderer.class).setText(Integer.toString(money));
            textDisplay.showWithoutEffects();
            imageDisplay.showWithoutEffects();

            textDisplay.getLayoutPart().getBoxConstraints().setWidth(SizeValue.px(34));
            imageDisplay.getLayoutPart().getBoxConstraints().setWidth(SizeValue.px(16));
            imageDisplay.setMarginRight(SizeValue.px(2));
        } else {
            textDisplay.hideWithoutEffect();
            imageDisplay.hideWithoutEffect();
            textDisplay.getLayoutPart().getBoxConstraints().setWidth(SizeValue.px(0));
            imageDisplay.getLayoutPart().getBoxConstraints().setWidth(SizeValue.px(0));
            imageDisplay.setMarginRight(SizeValue.px(0));
        }
    }

    @Override
    public int getWidth(@Nonnull final Element element, final MerchantListEntry item) {
        return element.getWidth();
    }
}

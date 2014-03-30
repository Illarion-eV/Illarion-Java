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
package org.illarion.nifty.controls.dialog.select;

import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.tools.SizeValue;
import org.illarion.nifty.controls.SelectListEntry;

import javax.annotation.Nonnull;

/**
 * This converter is used to display the select items in the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SelectItemViewConverter implements ListBox.ListBoxViewConverter<SelectListEntry> {
    @Override
    public void display(@Nonnull final Element listBoxItem, @Nonnull final SelectListEntry item) {
        final Element itemImage = listBoxItem.findElementById("#imageDisplay");
        final Element itemImageContainer = itemImage.getParent();
        final NiftyImage itemPicture = item.getItemImage();
        if (itemPicture == null) {
            itemImageContainer.hideWithoutEffect();
            itemImage.hideWithoutEffect();
            itemImageContainer.setConstraintHeight(SizeValue.px(0));
            itemImageContainer.setConstraintWidth(SizeValue.px(0));
            itemImage.setConstraintHeight(SizeValue.px(0));
            itemImage.setConstraintWidth(SizeValue.px(0));
        } else {
            itemImageContainer.showWithoutEffects();
            itemImage.showWithoutEffects();

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

            itemImageContainer.setConstraintHeight(SizeValue.px(46));
            itemImageContainer.setConstraintWidth(SizeValue.px(76));
            itemImage.setConstraintHeight(SizeValue.px(imageHeight));
            itemImage.setConstraintWidth(SizeValue.px(imageWidth));
        }

        final Element title = listBoxItem.findElementById("#itemTitle");
        title.getRenderer(TextRenderer.class).setText(item.getName());

        listBoxItem.layoutElements();
        //noinspection deprecation
        listBoxItem.getNiftyControl(DialogSelectEntryControl.class).setIndex(item.getIndex());
    }

    @Override
    public int getWidth(@Nonnull final Element element, @Nonnull final SelectListEntry item) {
        return element.getWidth();
    }
}

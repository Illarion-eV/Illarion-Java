/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
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
package org.illarion.nifty.controls.dialog.select;

import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.tools.SizeValue;
import org.illarion.nifty.controls.SelectListEntry;

/**
 * This converter is used to display the select items in the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SelectItemViewConverter implements ListBox.ListBoxViewConverter<SelectListEntry> {
    @Override
    public void display(final Element listBoxItem, final SelectListEntry item) {
        final Element itemImage = listBoxItem.findElementByName("#imageDisplay");
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

        final Element title = listBoxItem.findElementByName("#itemTitle");
        title.getRenderer(TextRenderer.class).setText(item.getName());

        listBoxItem.layoutElements();
        listBoxItem.getNiftyControl(DialogSelectEntryControl.class).setIndex(item.getIndex());
    }

    @Override
    public int getWidth(final Element element, final SelectListEntry item) {
        return element.getWidth();
    }
}

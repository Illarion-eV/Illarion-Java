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
package illarion.client.gui.util;

import de.lessvoid.nifty.controls.DropDown.DropDownViewConverter;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Stefano Bonicatti &lt;smjert@gmail.com&gt;
 */

public class DropDownItemViewConverter<T extends DropDownItem<?>> implements DropDownViewConverter<T> {

    @Override
    public void display(@Nonnull Element itemElement, @Nullable T item) {
        TextRenderer renderer = itemElement.getRenderer(TextRenderer.class);

        if ((renderer != null) && (item != null)) {
            renderer.setText(itemElement.getNifty().specialValuesReplace(item.getValue()));
        }
    }

    @Override
    public int getWidth(@Nonnull Element itemElement, @Nullable T item) {
        TextRenderer renderer = itemElement.getRenderer(TextRenderer.class);

        if ((renderer != null) && (item != null)) {
            return renderer.getFont().getWidth(itemElement.getNifty().specialValuesReplace(item.getValue()));
        } else {
            return 0;
        }
    }
}

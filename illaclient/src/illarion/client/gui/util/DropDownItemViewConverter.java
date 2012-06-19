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

import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 *
 * @author Stefano Bonicatti &lt;smjert@gmail.com&gt;
 */


public class DropDownItemViewConverter<T extends DropDownItem> implements DropDown.DropDownViewConverter<T> {

    @Override
    public void display(Element itemElement, T item) {
        TextRenderer renderer = itemElement.getRenderer(TextRenderer.class);

        if(renderer != null && item != null)
            renderer.setText(item.getValue());
    }

    @Override
    public int getWidth(Element itemElement, T item) {
        TextRenderer renderer = itemElement.getRenderer(TextRenderer.class);

        int width = 0;
        if(renderer != null && item != null)
        {
            width = itemElement.getRenderer(TextRenderer.class).getFont().getWidth(item.getValue());
            return width;
        }
        else
            return 0;
    }
}

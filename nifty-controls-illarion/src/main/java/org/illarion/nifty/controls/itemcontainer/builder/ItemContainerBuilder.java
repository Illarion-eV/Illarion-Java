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
package org.illarion.nifty.controls.itemcontainer.builder;

import de.lessvoid.nifty.builder.ControlBuilder;

public class ItemContainerBuilder extends ControlBuilder {
    public ItemContainerBuilder(final String id, final String title) {
        super(id, "itemcontainer");

        set("title", title);
        set("closeable", Boolean.TRUE.toString());
        set("hideOnClose", Boolean.FALSE.toString());
    }

    public void slotBackground(final String image) {
        set("slotBackground", image);
    }

    public void slots(final int slots) {
        set("slots", Integer.toString(slots));
    }

    public void slotHeight(final int height) {
        set("slotHeight", Integer.toString(height));
    }

    public void slotWidth(final int width) {
        set("slotWidth", Integer.toString(width));
    }

    public void slotDim(final int height, final int width) {
        slotHeight(height);
        slotWidth(width);
    }

    public void containerId(final int id) {
        set("containerId", Integer.toString(id));
    }
}

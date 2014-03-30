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
package org.illarion.nifty.controls.itemcontainer.builder;

import de.lessvoid.nifty.builder.ControlBuilder;

import javax.annotation.Nonnull;

public class ItemContainerBuilder extends ControlBuilder {
    public ItemContainerBuilder(@Nonnull final String id, final String title) {
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

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
package org.illarion.nifty.controls.itemcontainer.builder;

import de.lessvoid.nifty.builder.ControlBuilder;

import javax.annotation.Nonnull;

public final class ItemContainerBuilder extends ControlBuilder {
    public ItemContainerBuilder(@Nonnull String id, @Nonnull String title) {
        super(id, "itemcontainer");

        set("title", title);
        set("closeable", Boolean.toString(true));
        hideOnClose(false);
    }

    public void slotBackground(@Nonnull String image) {
        set("slotBackground", image);
    }

    public void slots(int slots) {
        set("slots", Integer.toString(slots));
    }

    public void slotHeight(int height) {
        set("slotHeight", Integer.toString(height));
    }

    public void slotWidth(int width) {
        set("slotWidth", Integer.toString(width));
    }

    public void hideOnClose(boolean value) {
        set("hideOnClose", Boolean.toString(value));
    }

    public void slotDim(int height, int width) {
        slotHeight(height);
        slotWidth(width);
    }
}

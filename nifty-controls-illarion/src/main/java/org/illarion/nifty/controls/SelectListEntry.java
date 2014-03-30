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
package org.illarion.nifty.controls;

import de.lessvoid.nifty.render.NiftyImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This interfaces defines a entry in the select dialog that contains the selectable entry.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface SelectListEntry {
    /**
     * Get the image that is supposed to be displayed in the entry.
     *
     * @return the nifty image to display or {@code null} in case no image is supposed to be displayed
     */
    @Nullable
    NiftyImage getItemImage();

    /**
     * Get the name of the item.
     *
     * @return the name of the item
     */
    @Nonnull
    String getName();

    /**
     * The index of the select item in the list as it was transferred from the server.
     *
     * @return the index
     */
    int getIndex();
}

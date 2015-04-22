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
package illarion.client.resources;

import illarion.client.resources.data.OverlayTemplate;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

/**
 * The overlay factory loads and stores all graphical representations of the
 * overlays that create the map of Illarion.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class OverlayFactory extends AbstractTemplateFactory<OverlayTemplate> {
    /**
     * The singleton instance of this class.
     */
    @Nonnull
    private static final OverlayFactory INSTANCE = new OverlayFactory();

    /**
     * Get the singleton instance of this factory.
     *
     * @return the singleton instance of this factory.
     */
    @Nonnull
    @Contract(pure = true)
    public static OverlayFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The private constructor to ensure that no instance but the singleton
     * instance is created.
     */
    private OverlayFactory() {
    }

    /**
     * The initialization function to prepare the class for loading the
     * resources.
     */
    @Override
    public void init() {
    }
}

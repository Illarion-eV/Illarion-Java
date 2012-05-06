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
package illarion.client.resources;

import illarion.client.graphics.Overlay;
import illarion.common.util.RecycleFactory;

/**
 * The overlay factory loads and stores all graphical representations of the
 * overlays that create the map of Illarion.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class OverlayFactory extends RecycleFactory<Overlay> implements
        ResourceFactory<Overlay> {
    /**
     * The singleton instance of this class.
     */
    private static final OverlayFactory INSTANCE = new OverlayFactory();

    /**
     * Get the singleton instance of this factory.
     *
     * @return the singleton instance of this factory.
     */
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
     * Activate the winter mode and apply the mapping that will turn the map
     * into a winter look and feel.
     */
    public final void activateWinter() {
        forceMap(11, 10);
    }

    /**
     * The initialization function to prepare the class for loading the
     * resources.
     */
    @Override
    public void init() {
    }

    /**
     * Finish the loading sequence for this factory and prepare it for normal
     * operation.
     */
    @Override
    public void loadingFinished() {
        finish();
    }

    /**
     * Store a new resource in this factory.
     */
    @Override
    public void storeResource(final Overlay resource) {
        register(resource);
    }
}

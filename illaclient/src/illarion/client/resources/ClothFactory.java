/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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

import illarion.client.graphics.AvatarCloth;
import illarion.common.util.RecycleFactory;

import javax.annotation.Nonnull;

/**
 * The factory that handles all clothes of one group in relationship to the item
 * IDs they are assigned to. If needed it creates further instances of the
 * avatar cloth instances to ensure that all can be drawn properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ClothFactory extends RecycleFactory<AvatarCloth> implements
        ResourceFactory<AvatarCloth> {
    /**
     * Default constructor for the avatar cloth factory. This creates also the
     * default cloth instance that is returned in case the requested item was
     * not created.
     */
    public ClothFactory() {
        super();
    }

    /**
     * Initialize the factory.
     */
    @Override
    public void init() {
        // nothing
    }

    /**
     * Finish the loading and prepare the factory for normal operation.
     */
    @Override
    public void loadingFinished() {
        finish();
    }

    /**
     * Add a new cloth to this factory.
     *
     * @param cloth the cloth instance that shall be added
     */
    void registerCloth(final AvatarCloth cloth) {
    }

    /**
     * Store a element in this factory.
     */
    @Override
    public void storeResource(@Nonnull final AvatarCloth resource) {
        resource.setFactory(this);
        register(resource);
    }
}

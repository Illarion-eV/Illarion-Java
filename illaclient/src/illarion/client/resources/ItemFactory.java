/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.resources;

import illarion.client.graphics.Item;
import illarion.common.util.RecycleFactory;

/**
 * The Item factory loads creates and stores all instances of the item class
 * that are around in the client.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class ItemFactory extends RecycleFactory<Item> implements
    ResourceFactory<Item> {
    /**
     * The singleton instance of this class.
     */
    private static final ItemFactory INSTANCE = new ItemFactory();

    /**
     * Get the singleton instance of this factory.
     * 
     * @return the singleton instance of this factory
     */
    public static ItemFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Construct the item factory.
     */
    private ItemFactory() {
        super();
    }

    /**
     * The initialization function.
     */
    @Override
    @SuppressWarnings("nls")
    public void init() {
        // nothing to do
    }

    /**
     * Prepare the factory after loading for normal operation.
     */
    @Override
    public void loadingFinished() {
        mapDefault(0, 1);

        finish();
    }

    /**
     * Store the resource in the factory.
     */
    @Override
    public void storeResource(final Item resource) {
        register(resource);
    }
}

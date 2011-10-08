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

import illarion.client.graphics.Avatar;
import illarion.common.util.RecycleFactory;

/**
 * The avatar factory loads and stores all graphical representations of
 * characters.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class CharacterFactory extends RecycleFactory<Avatar> implements
    ResourceFactory<Avatar> {
    /**
     * The ID of the avatar that is loaded by default in case the requested
     * avatar was not found.
     */
    private static final int DEFAULT_ID = 10450;

    /**
     * The singleton instance of this class.
     */
    private static final CharacterFactory INSTANCE = new CharacterFactory();

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of the avatar factory
     */
    public static CharacterFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Constructor for the avatar factory. This sets up all storage tables that
     * are needed to store the instances of the avatars created by this function
     * and it starts loading the avatar table.
     */
    private CharacterFactory() {
        super();
    }

    /**
     * The initialization function.
     */
    @Override
    @SuppressWarnings("nls")
    public void init() {
    }

    /**
     * Prepare the factory for normal operation after the loading is done.
     */
    @Override
    public void loadingFinished() {
        mapDefault(DEFAULT_ID, 1);

        finish();
    }

    /**
     * Store a resource in this factory.
     * 
     * @param resource the resource to store
     */
    @Override
    public void storeResource(final Avatar resource) {
        register(resource);
    }
}

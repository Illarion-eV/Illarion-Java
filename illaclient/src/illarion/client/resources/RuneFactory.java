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

import illarion.client.graphics.Rune;
import illarion.common.util.RecycleFactory;

/**
 * The rune factory loads and stores all graphical representations of the runes
 * the player can use to cast.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public class RuneFactory extends RecycleFactory<Rune> implements
    ResourceFactory<Rune> {
    /**
     * The singleton instance of this class.
     */
    private static final RuneFactory INSTANCE = new RuneFactory();

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static RuneFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The private constructor to ensure that only one instance is created.
     */
    private RuneFactory() {
        super();
    }

    /**
     * The initialization function prepares the factory to receive its values.
     */
    @Override
    @SuppressWarnings("nls")
    public void init() {
    }

    /**
     * Finish the loading sequence and prepare the factory for normal operation.
     */
    @Override
    public void loadingFinished() {
        finish();
    }

    /**
     * Store one new resource in this factory.
     * 
     * @param resource the resource to store
     */
    @Override
    public void storeResource(final Rune resource) {
        register(resource);
    }
}

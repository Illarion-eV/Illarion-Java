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
package illarion.client.tableloader;

import illarion.client.graphics.Rune;
import illarion.client.resources.ResourceFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * This class is used to load the overlay definitions from the resource table
 * that was created using the configuration tool. The class will create the
 * required overlay objects and send them to the overlay factory that takes care
 * for distributing those objects.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class RuneLoader extends ResourceLoader<Rune> implements
    TableLoaderSink {
    /**
     * The index of the column that stores the ID of the rune inside the
     * resource table.
     */
    private static final int TB_ID = 0;

    /**
     * The index of the column that stores the name of the rune inside the
     * resource table.
     */
    private static final int TB_NAME = 1;

    /**
     * Trigger the loading sequence for this loader.
     */
    @Override
    public void load() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<Rune> factory = getTargetFactory();

        factory.init();
        new TableLoader("Runes", this);
        factory.loadingFinished();
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int id = loader.getInt(TB_ID);
        final Rune rune = new Rune(id, loader.getString(TB_NAME));
        getTargetFactory().storeResource(rune);

        return true;
    }

}

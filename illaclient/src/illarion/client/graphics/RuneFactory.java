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
package illarion.client.graphics;

import illarion.common.util.RecycleFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * Created: 20.08.2005 22:41:23
 */
public class RuneFactory extends RecycleFactory<Rune> implements
    TableLoaderSink {
    private static final RuneFactory instance = new RuneFactory();

    private static final int TB_ID = 0;
    private static final int TB_NAME = 1;

    private RuneFactory() {
        super();
    }

    public static RuneFactory getInstance() {
        return instance;
    }

    /**
     * The initialisation function prepares all prototyped that are needed to
     * work with this function.
     */
    @SuppressWarnings("nls")
    public void init() {
        new TableLoader("Runes", this);
        // mapDefault(12, 1);
        finish();
    }

    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int id = loader.getInt(TB_ID);
        final Rune rune = new Rune(id, loader.getString(TB_NAME));
        register(rune);

        return true;
    }
}

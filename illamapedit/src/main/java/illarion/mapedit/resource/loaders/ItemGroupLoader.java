/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.resource.loaders;

import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.common.util.TableLoaderMapGroups;
import illarion.common.util.TableLoaderSink;
import illarion.mapedit.Lang;
import illarion.mapedit.resource.Resource;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Tim
 */
public class ItemGroupLoader implements TableLoaderSink<TableLoaderMapGroups>, Resource {
    private static final ItemGroupLoader INSTANCE = new ItemGroupLoader();
    private final boolean isGerman = Lang.getInstance().isGerman();
    private final TIntObjectHashMap<String> groups = new TIntObjectHashMap<>();

    @Nonnull
    public static ItemGroupLoader getInstance() {
        return INSTANCE;
    }

    @Override
    public void load() throws IOException {
        new TableLoaderMapGroups(this);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Item Groups";
    }

    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderMapGroups loader) {
        if (isGerman) {
            groups.put(loader.getId(), loader.getNameGerman());
        } else {
            groups.put(loader.getId(), loader.getNameEnglish());
        }
        return true;
    }

    public String getGroupName(final int id) {
        return groups.get(id);
    }
}

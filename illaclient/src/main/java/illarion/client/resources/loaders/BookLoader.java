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
package illarion.client.resources.loaders;

import illarion.client.resources.ResourceFactory;
import illarion.client.util.IdWrapper;
import illarion.common.util.TableLoaderBooks;
import illarion.common.util.TableLoaderSink;

import javax.annotation.Nonnull;

/**
 * This loader is used to load the data required for the books.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookLoader extends AbstractResourceLoader<IdWrapper<String>> implements TableLoaderSink<TableLoaderBooks> {
    @Nonnull
    @Override
    public ResourceFactory<IdWrapper<String>> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<IdWrapper<String>> factory = getTargetFactory();

        factory.init();
        new TableLoaderBooks(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderBooks loader) {
        final int id = loader.getBookId();
        final String bookFile = loader.getBookFile();

        getTargetFactory().storeResource(new IdWrapper<String>(id, bookFile));

        return true;
    }
}

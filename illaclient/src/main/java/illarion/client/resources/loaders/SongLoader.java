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
import illarion.common.util.TableLoaderMusic;
import illarion.common.util.TableLoaderSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class is used to load the song definitions from the resource table that was created using the configuration
 * tool. The class will create the required song objects and send them to the song factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SongLoader extends AbstractResourceLoader<IdWrapper<String>>
        implements TableLoaderSink<TableLoaderMusic> {
    /**
     * The logger that is used to report error messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemLoader.class);

    /**
     * Trigger the loading sequence for this loader.
     */
    @Nonnull
    @Override
    public ResourceFactory<IdWrapper<String>> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<IdWrapper<String>> factory = getTargetFactory();

        factory.init();
        new TableLoaderMusic(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderMusic loader) {
        final int clipID = loader.getSongId();
        final String filename = loader.getSongFile();

        try {
            getTargetFactory().storeResource(new IdWrapper<>(clipID, filename));
        } catch (@Nonnull final IllegalStateException ex) {
            LOGGER.error("Failed adding song to internal factory. ID: " + clipID + " - Filename: " + filename);
        }

        return true;
    }
}

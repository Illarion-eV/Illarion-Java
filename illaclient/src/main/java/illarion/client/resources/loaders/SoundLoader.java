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
import illarion.common.util.TableLoaderSink;
import illarion.common.util.TableLoaderSound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class is used to load the sound definitions from the resource table that was created using the configuration
 * tool. The class will create the required sound objects and send them to the sound factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SoundLoader extends AbstractResourceLoader<IdWrapper<String>>
        implements TableLoaderSink<TableLoaderSound> {
    /**
     * The index in the table record of the sound id.
     */
    private static final int TB_ID = 0;

    /**
     * The index in the table record of the sound filename.
     */
    private static final int TB_NAME = 1;

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
        new TableLoaderSound(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderSound loader) {
        final int clipID = loader.getInt(TB_ID);
        final String filename = loader.getString(TB_NAME);

        try {
            getTargetFactory().storeResource(new IdWrapper<>(clipID, filename));
        } catch (@Nonnull final IllegalStateException ex) {
            LOGGER.error("Failed adding sound to internal factory. ID: " + clipID + " - Filename: " + filename);
        }

        return true;
    }
}

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

import illarion.client.graphics.Overlay;
import illarion.client.resources.ResourceFactory;
import illarion.common.util.TableLoaderOverlay;
import illarion.common.util.TableLoaderSink;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * This class is used to load the overlay definitions from the resource table
 * that was created using the configuration tool. The class will create the
 * required overlay objects and send them to the overlay factory that takes care
 * for distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class OverlayLoader extends AbstractResourceLoader<Overlay> implements
        TableLoaderSink<TableLoaderOverlay> {
    /**
     * The logger that is used to report error messages.
     */
    private static final Logger logger = Logger.getLogger(ItemLoader.class);

    /**
     * Trigger the loading sequence for this loader.
     */
    @Override
    public ResourceFactory<Overlay> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<Overlay> factory = getTargetFactory();

        factory.init();
        new TableLoaderOverlay(this);
        factory.loadingFinished();

        return factory;
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderOverlay loader) {
        final int id = loader.getTileId();
        final String name = loader.getOverlayFile();
        final Overlay overlay = new Overlay(id, name);

        try {
            getTargetFactory().storeResource(overlay);
            overlay.activate(id);
        } catch (@Nonnull final IllegalStateException ex) {
            logger.error("Failed adding overlay to internal factory. ID: "
                    + Integer.toString(id) + " - Filename: " + name);
        }

        return true;
    }

}

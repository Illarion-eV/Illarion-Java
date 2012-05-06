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
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;
import org.apache.log4j.Logger;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.SoundStore;

/**
 * This class is used to load the song definitions from the resource table that
 * was created using the configuration tool. The class will create the required
 * song objects and send them to the song factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SongLoader extends AbstractResourceLoader<IdWrapper<Music>> implements
        TableLoaderSink {
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
    private final Logger logger = Logger.getLogger(ItemLoader.class);

    private static final String SONG_DIR = "data/music/";

    /**
     * Trigger the loading sequence for this loader.
     */
    @Override
    public ResourceFactory<IdWrapper<Music>> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<IdWrapper<Music>> factory = getTargetFactory();

        factory.init();
        SoundStore.get().setDeferredLoading(true);
        new TableLoader("Songs", this);
        factory.loadingFinished();

        return factory;
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int clipID = loader.getInt(TB_ID);
        final String filename = loader.getString(TB_NAME);

        try {
            getTargetFactory().storeResource(new IdWrapper<Music>(clipID, new Music(SONG_DIR + filename, true)));
        } catch (final IllegalStateException ex) {
            logger.error("Failed adding song to internal factory. ID: "
                    + Integer.toString(clipID) + " - Filename: " + filename);
        } catch (SlickException e) {
            logger.error("Failed adding song to internal factory. ID: "
                    + Integer.toString(clipID) + " - Filename: " + filename);
        }

        return true;
    }

}

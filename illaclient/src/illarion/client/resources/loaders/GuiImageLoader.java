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

import illarion.client.graphics.GuiImage;
import illarion.client.graphics.Sprite;
import illarion.client.graphics.SpriteBuffer;
import illarion.client.resources.ResourceFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;
import org.apache.log4j.Logger;

/**
 * This class is used to load the GUI image definitions from the resource table
 * that was created using the configuration tool. The class will create the
 * required GUI image objects and send them to the GUI image factory that takes
 * care for distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GuiImageLoader extends AbstractResourceLoader<GuiImage> implements TableLoaderSink {
    /**
     * The path inside the resources where the GUI images are stored.
     */
    private static final String PATH = "data/gui/";

    /**
     * The index of the column in the resource table that stores the frame count
     * of this GUI image.
     */
    private static final int TB_FRAME = 2;

    /**
     * The index of the column in the resource table that stores the file name
     * of this GUI image.
     */
    private static final int TB_NAME = 1;

    /**
     * The index of the column in the resource table that stores the x-offset
     * value of this GUI image.
     */
    private static final int TB_OFFX = 3;

    /**
     * The index of the column in the resource table that stores the y-offset
     * value of this GUI image.
     */
    private static final int TB_OFFY = 4;

    /**
     * The logger that is used to report error messages.
     */
    private final Logger logger = Logger.getLogger(ItemLoader.class);

    /**
     * Trigger the loading sequence for this loader.
     */
    @Override
    public ResourceFactory<GuiImage> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<GuiImage> factory = getTargetFactory();

        factory.init();
        new TableLoader("Gui", this);
        factory.loadingFinished();

        return factory;
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final String name = loader.getString(TB_NAME);

        try {
            getTargetFactory().storeResource(
                    new GuiImage(name, SpriteBuffer.getInstance().getSprite(PATH,
                            name, loader.getInt(TB_FRAME), loader.getInt(TB_OFFX),
                            loader.getInt(TB_OFFY), Sprite.HAlign.left,
                            Sprite.VAlign.top, true, false)));
        } catch (final IllegalStateException ex) {
            logger.error("Failed adding GUI image to internal factory. Filename: " + name);
        }

        return true;
    }

}

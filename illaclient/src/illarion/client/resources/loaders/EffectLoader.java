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
package illarion.client.resources.loaders;

import illarion.client.graphics.Effect;
import illarion.client.resources.ResourceFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

import org.apache.log4j.Logger;

/**
 * This class is used to load the effect definitions from the resource table
 * that was created using the configuration tool. The class will create the
 * required effect objects and send them to the effect factory that takes care
 * for distributing those objects.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class EffectLoader extends ResourceLoader<Effect> implements
    TableLoaderSink {
    /**
     * The table index of the column that stores the amount of frames of the
     * effect animation.
     */
    private static final int TB_FRAME = 2;

    /**
     * The table index of the column that stores the effect ID.
     */
    private static final int TB_ID = 0;

    /**
     * The table index of the column that stores the encoded light value that is
     * emitted by the effect.
     */
    private static final int TB_LIGHT = 7;

    /**
     * The table index of the column that stores the base filename of the effect
     * graphics.
     */
    private static final int TB_NAME = 1;

    /**
     * The table index of the column that stores the x offset of the effect
     * graphic.
     */
    private static final int TB_OFFX = 3;

    /**
     * The table index of the column that stores the y offset of the effect
     * graphic.
     */
    private static final int TB_OFFY = 4;

    /**
     * The table index of the column that stores the speed of the effect
     * animation.
     */
    private static final int TB_SPEED = 5;

    /**
     * The logger that is used to report error messages.
     */
    private final Logger logger = Logger.getLogger(ItemLoader.class);

    /**
     * Trigger the loading sequence for this loader.
     */
    @Override
    public void load() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<Effect> factory = getTargetFactory();

        factory.init();
        new TableLoader("Effects", this);
        factory.loadingFinished();
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int effectID = loader.getInt(TB_ID);
        final String fileName = loader.getString(TB_NAME);
        final int frameCount = loader.getInt(TB_FRAME);
        final int offsetX = loader.getInt(TB_OFFX);
        final int offsetY = loader.getInt(TB_OFFY);
        final int animSpeed = loader.getInt(TB_SPEED);
        final int light = loader.getInt(TB_LIGHT);

        final Effect effect =
            new Effect(effectID, fileName, frameCount, offsetX, offsetY,
                animSpeed, light);
        try {
            getTargetFactory().storeResource(effect);
            effect.activate(effectID);
        } catch (final IllegalStateException ex) {
            logger.error("Failed adding effect to internal factory. ID: "
                + Integer.toString(effectID) + " - Filename: " + fileName);
        }

        return true;
    }

}

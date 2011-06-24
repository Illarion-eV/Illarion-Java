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
 * The effect factory creates and stores the effect objects and keeps them for
 * reuse.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.95
 */
public final class EffectFactory extends RecycleFactory<Effect> implements
    TableLoaderSink {
    /**
     * The ID of the effect that is shown in case the requested effect is not
     * defined.
     */
    private static final int DEFAULT_EFFECT = 12;

    /**
     * The singleton instance of the effect factory.
     */
    private static final EffectFactory INSTANCE = new EffectFactory();

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
     * The constructor of the effect factory that triggers preparing the effects
     * and loading the definition table files.
     */
    private EffectFactory() {
        super();
    }

    /**
     * The the singleton instance of this effect factory.
     * 
     * @return the singleton instance for the effect factory
     */
    public static EffectFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The initialisation function prepares all prototyped that are needed to
     * work with this function.
     */
    @SuppressWarnings("nls")
    public void init() {
        new TableLoader("Effects", this);
        mapDefault(DEFAULT_EFFECT, 1);
        finish();
    }

    /**
     * Process one line of the definition table that contains the parameters for
     * the effects.
     * 
     * @param line the line that is currently processed
     * @param loader the table loader the loads the table file
     * @return true to go on reading the file, false to stop reading the file
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
        register(effect);
        effect.activate(effectID);

        return true;
    }
}

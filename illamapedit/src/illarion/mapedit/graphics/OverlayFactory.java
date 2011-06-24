/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.graphics;

import illarion.common.util.RecycleFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * The factory that takes care of all loaded overlay graphics.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class OverlayFactory extends RecycleFactory<Overlay> implements
    TableLoaderSink {
    /**
     * The singleton instance of this class.
     */
    private static final OverlayFactory INSTANCE = new OverlayFactory();

    /**
     * The table index of the overlay ID values.
     */
    private static final int TB_ID = 0;

    /**
     * The table index of the layer of the overlay graphic.
     */
    private static final int TB_LAYER = 3;

    /**
     * The table index of the graphic name for this overlay.
     */
    private static final int TB_NAME = 1;

    /**
     * Private constructor to avoid any instances but the singleton instance.
     */
    private OverlayFactory() {
        super();
    }

    /**
     * Get the singleton instance of this factory.
     * 
     * @return the singleton instance of this factory
     */
    public static OverlayFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The init function preapares all prototyped that are needed to work with
     * this function.
     */
    @SuppressWarnings("nls")
    public void init() {
        new TableLoader("Overlays", this);
        finish();
    }

    /**
     * This method takes care of processing one line in the table file. It reads
     * the data and prepares the required objects regarding the fetched data.
     * 
     * @param line the line number of the currently processed line
     * @param loader the table loader that acts as source of the data
     * @return <code>true</code> in all cases to have the table loader reading
     *         the table to the very end
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final Overlay overlay =
            new Overlay(loader.getInt(TB_ID), loader.getString(TB_NAME),
                loader.getInt(TB_LAYER));
        register(overlay);

        return true;
    }
}

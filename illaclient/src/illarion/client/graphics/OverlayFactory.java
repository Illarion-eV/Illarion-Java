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

import illarion.client.Debug;
import illarion.client.IllaClient;

import illarion.common.util.RecycleFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * Created: 20.08.2005 22:41:23
 */
public class OverlayFactory extends RecycleFactory<Overlay> implements
    TableLoaderSink {
    private static final OverlayFactory instance = new OverlayFactory();

    private static final int TB_ID = 0;
    private static final int TB_NAME = 1;
    private static final int TB_PRELOAD = 2;

    private OverlayFactory() {
        super();
    }

    public static OverlayFactory getInstance() {
        return instance;
    }

    public final void activateWinter() {
        forceMap(11, 10);
    }

    /**
     * The init function preapares all prototyped that are needed to work with
     * this function.
     */
    public void init() {
        new TableLoader("Overlays", this);
        finish();
    }

    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final Overlay overlay =
            new Overlay(loader.getInt(TB_ID), loader.getString(TB_NAME));
        register(overlay);
        if (!IllaClient.isDebug(Debug.preloadOff)
            && loader.getBoolean(TB_PRELOAD)) {
            overlay.activate(overlay.getId());
        }

        return true;
    }
}

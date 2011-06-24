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
package illarion.client.guiNG.init;

import illarion.client.guiNG.GUI;
import illarion.client.guiNG.MapOverview;
import illarion.client.guiNG.elements.ScrollArea;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.elements.Window;

/**
 * This initialization script is used to register the inventory properly in the
 * GUI so it can be used as it should.
 * 
 * @author Natal Venetz
 * @since 1.22
 * @version 1.22
 */
public final class MapOverviewInit implements WidgetInit {
    /**
     * The serialization UID of this initialization script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The journal that is initilizied with this function.
     */
    private final MapOverview mapOverview;

    /**
     * The window of the inventory that is initilizied with this function.
     */
    private final Window mapOverviewWindow;

    /**
     * The scrollarea that is initilizied with this function.
     */
    private final ScrollArea mapScrollArea;

    /**
     * Constructor for this initialization script that sets the instances that
     * are required for the initialization.
     * 
     * @param invent the inventory that is initialized
     * @param invent the ScrollArea that is initialized
     * @param window the window of the journal that is registered to the GUI
     */
    public MapOverviewInit(final MapOverview mapOverV,
        final ScrollArea mapScrollA, final Window window) {
        mapOverview = mapOverV;
        mapScrollArea = mapScrollA;
        mapOverviewWindow = window;
    }

    /**
     * This function is called when initialization starts.
     */
    @Override
    public void initWidget(final Widget widget) {
        GUI.getInstance().registerOverviewMap(mapOverview, mapScrollArea,
            mapOverviewWindow);
    }

}

/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.tools;

import illarion.mapedit.data.Map;
import illarion.mapedit.events.MapClickedEvent;
import illarion.mapedit.events.RepaintRequestEvent;
import illarion.mapedit.events.TileSelectedEvent;
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.util.Disposable;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

/**
 * @author Tim
 */
public final class ToolManager implements Disposable {
    private static final Logger LOGGER = Logger.getLogger(ToolManager.class);


    private final AbstractTool[] actualTool = new AbstractTool[3];
    private final Map map;

    private TileImg selectedTile;

    public ToolManager(final Map map) {
        AnnotationProcessor.process(this);
        this.map = map;
        setTool(new SingleTileTool(), 0);
    }

    public void setTool(final AbstractTool tool, final int button) {
        if (tool != null)
            tool.registerManager(this);
        actualTool[button] = tool;
    }

    public Map getMap() {
        return map;
    }

    TileImg getSelectedTile() {
        return selectedTile;
    }

    @Override
    public void dispose() {
        AnnotationProcessor.unprocess(this);
    }

    @EventSubscriber(eventClass = MapClickedEvent.class)
    public void clickedAt(final MapClickedEvent e) {
        final int button = e.getButton() - 1;
        if ((actualTool != null) && (actualTool.length > button) && (actualTool[button] != null)) {
            actualTool[button].clickedAt(e.getX(), e.getY());
            EventBus.publish(new RepaintRequestEvent());
        }
    }

    @EventSubscriber(eventClass = TileSelectedEvent.class)
    public void onTileSelected(TileSelectedEvent e) {
        selectedTile = e.getTileImg();
        LOGGER.debug("Selected: " + e.getTileImg());
    }
}

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

import illarion.mapedit.events.ItemSelectedEvent;
import illarion.mapedit.events.TileSelectedEvent;
import illarion.mapedit.events.ToolSelectedEvent;
import illarion.mapedit.events.map.MapClickedEvent;
import illarion.mapedit.events.map.MapDragFinishedEvent;
import illarion.mapedit.events.map.MapDraggedEvent;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.gui.GuiController;
import illarion.mapedit.history.HistoryManager;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.util.Disposable;
import illarion.mapedit.util.MouseButton;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

/**
 * @author Tim
 */
public final class ToolManager implements Disposable {
    private static final Logger LOGGER = Logger.getLogger(ToolManager.class);

    private final GuiController controller;
    private final RendererManager renderer;

    private AbstractTool actualTool;
    private TileImg selectedTile;
    private ItemImg selectedItem;

    public ToolManager(final GuiController controller, final RendererManager renderer) {
        this.controller = controller;
        this.renderer = renderer;
        AnnotationProcessor.process(this);
        setTool(new SingleTileTool());
    }

    public void setTool(final AbstractTool tool) {
        if (tool != null) {
            tool.registerManager(this);
        }
        actualTool = tool;
    }

    TileImg getSelectedTile() {
        return selectedTile;
    }

    ItemImg getSelectedItem() {
        return selectedItem;
    }

    RendererManager getRenderer() {
        return renderer;
    }

    @Override
    public void dispose() {
        AnnotationProcessor.unprocess(this);
    }

    public GuiController getController() {
        return controller;
    }


    @EventSubscriber
    public void clickedAt(final MapClickedEvent e) {
        if ((actualTool != null) && (e.getButton() == MouseButton.LeftButton)) {
            actualTool.clickedAt(e.getX(), e.getY(), e.getMap());
            EventBus.publish(new RepaintRequestEvent());
        }
    }

    @EventSubscriber
    public void onMapDragged(final MapDraggedEvent e) {
        if (e.getButton() == MouseButton.LeftButton) {
            actualTool.clickedAt(e.getX(), e.getY(), e.getMap());
            EventBus.publish(new RepaintRequestEvent());
        }
    }

    @EventSubscriber
    public void onMapDragFinished(final MapDragFinishedEvent e) {

    }

    @EventSubscriber
    public void onTileSelected(final TileSelectedEvent e) {
        selectedTile = e.getTileImg();
    }


    @EventSubscriber
    public void onItemSelected(final ItemSelectedEvent e) {
        selectedItem = e.getItemImg();
    }

    @EventSubscriber
    public void onSelectTool(final ToolSelectedEvent e) {
        setTool(e.getTool());
    }

    public HistoryManager getHistory() {
        return controller.getHistoryManager();
    }
}

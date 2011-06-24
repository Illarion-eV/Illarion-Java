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
package illarion.mapedit.input;

import illarion.mapedit.MapEditor;
import illarion.mapedit.map.Map;
import illarion.mapedit.map.MapStorage;

import illarion.common.util.Location;

import illarion.input.MouseEvent;
import illarion.input.MouseEventReceiver;

/**
 * This class is used to receive the mouse events and forward them correctly to
 * the rest of the map editor.
 * 
 * @author Martin Karing
 * @since 0.99
 */
final class MouseHandler implements MouseEventReceiver {
    /**
     * Handle the incoming mouse event in case its needed.
     */
    @Override
    public void handleMouseEvent(final MouseEvent event) {
        final int eventType = event.getEvent();

        if ((event.getPosX() == -1) || (event.getPosY() == -1)) {
            SelectionManager.getInstance().setOutside();

            if (eventType == MouseEvent.EVENT_DRAG_END) {
                SelectionManager.getInstance().endAreaSelect();
            }
            return;
        }

        switch (eventType) {
            case MouseEvent.EVENT_LOCATION:
                updateLocation(event);
                break;
            case MouseEvent.EVENT_DRAG_START:
                SelectionManager.getInstance().startAreaSelect();
                updateLocation(event);
                break;
            case MouseEvent.EVENT_DRAG_END:
                SelectionManager.getInstance().executeToolOnSelection(
                    event.getKey());
                SelectionManager.getInstance().endAreaSelect();
                updateLocation(event);
                break;
            case MouseEvent.EVENT_KEY_CLICK:
                updateLocation(event);
                SelectionManager.getInstance().executeToolOnSelection(
                    event.getKey());
                break;
        }
    }

    private Location displayLocToMapLoc(final int displayX, final int displayY) {
        final Map selectedMap = MapStorage.getInstance().getSelectedMap();
        if (selectedMap == null) {
            return null;
        }
        final float zoom = MapEditor.getDisplay().getZoom();
        final int worldX =
            (int) (displayX / zoom) + MapEditor.getDisplay().getOffsetX();
        final int worldY =
            (int) (displayY / zoom) + MapEditor.getDisplay().getOffsetY();

        final Location helpLoc = Location.getInstance();
        helpLoc.setDC(worldX, worldY);
        final int level = selectedMap.getOrigin().getScZ();
        if (level != 0) {
            helpLoc.setSC(helpLoc.getScX() - (level * 3), helpLoc.getScY()
                + (level * 3), level);
        }
        return helpLoc;
    }

    private void updateLocation(final MouseEvent event) {
        final Location mapLoc =
            displayLocToMapLoc(event.getPosX(), event.getPosY());
        if (mapLoc == null) {
            return;
        }
        SelectionManager.getInstance().setCurrentLocation(mapLoc);
        mapLoc.recycle();
    }
}

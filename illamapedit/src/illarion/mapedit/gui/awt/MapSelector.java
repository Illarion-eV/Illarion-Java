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
package illarion.mapedit.gui.awt;

import java.awt.List;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import illarion.mapedit.map.Map;
import illarion.mapedit.map.MapStorage;

/**
 * The map selector shows the currently loaded maps and allows to select one for
 * using it.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class MapSelector extends List implements KeyListener, ItemListener {
    /**
     * The serialization UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct the map selector and prepare everything to handle it properly.
     */
    public MapSelector() {
        super(8, false);

        addKeyListener(this);
        addItemListener(this);
    }

    /**
     * Add a map to the map selector.
     * 
     * @param mapName the name of the map to add
     */
    public void addMap(final String mapName) {
        add(mapName);
        if (getItemCount() == 1) {
            select(0);
            dispatchEvent(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
                null, ItemEvent.SELECTED));
        }
    }

    /**
     * Delete a map from the list.
     * 
     * @param mapName the name of the map to delete
     */
    public void deleteMap(final String mapName) {
        remove(mapName);
    }

    /**
     * Triggered in case a different item is selected on the map selector.
     * 
     * @param e the item event that holds the data of the item selection change
     */
    @Override
    public void itemStateChanged(final ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            final String changeMap = e.getItem().toString();
            final Map currentSelMap =
                MapStorage.getInstance().getSelectedMap();
            if (currentSelMap == null) {
                return;
            }
            if (changeMap.equals(currentSelMap.getMapName())) {
                MapStorage.getInstance().setSelectedMap(null);
            }
        } else {
            final String changeMap =
                e.getItemSelectable().getSelectedObjects()[0].toString();
            MapStorage.getInstance().setSelectedMap(changeMap);
        }
    }

    /**
     * Triggered on pressing any key. In case the key is delete the map is
     * removed from the map selector and unload from the storage.
     * 
     * @param e the key event storing the data of the event
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            MapStorage.getInstance().unloadMap(getSelectedItem());
        }
    }

    /**
     * Triggered when a button is released. Not needed here.
     * 
     * @param e the key event
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        // not needed
    }

    /**
     * Triggered when a letter is typed. Not needed here.
     * 
     * @param e the key event
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        // not needed
    }

}

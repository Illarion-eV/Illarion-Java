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
package illarion.mapedit.events;

import illarion.mapedit.data.Map;

import java.util.List;

/**
 * @author Tim
 */
public class UpdateMapListEvent {

    private final List<Map> maps;
    private final int selectedIndex;

    public UpdateMapListEvent(final List<Map> maps, final int selectedIndex) {
        this.maps = maps;
        this.selectedIndex = selectedIndex;
    }

    public List<Map> getMaps() {
        return maps;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}

/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.mapedit.gui.util;

import illarion.mapedit.data.Map;

import javax.swing.*;
import java.util.List;

/**
 * @author Tim
 */
public class MapComboBoxModel extends AbstractListModel implements ComboBoxModel {

    private List<Map> objects;

    private Object selected;

    public MapComboBoxModel(final List<Map> objects) {
        this.objects = objects;
    }

    public void updateList(List<Map> l) {
        objects = l;
        fireContentsChanged(this, 0, objects.size());
    }

    @Override
    public int getSize() {
        if ((objects == null) || (objects.size() == 0)) {
            return 0;
        }
        return objects.size();
    }

    @Override
    public String getElementAt(final int index) {

        return objects.get(index).getName();
    }

    @Override
    public void setSelectedItem(final Object anItem) {
        selected = anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }
}

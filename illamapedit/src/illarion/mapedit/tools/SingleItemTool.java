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

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapItem;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.tools.panel.SingleItemPanel;
import illarion.mapedit.util.Vector2i;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.swing.*;

/**
 * @author Tim
 */
public class SingleItemTool extends AbstractTool {

    public final SingleItemPanel panel;

    public SingleItemTool() {
        panel = new SingleItemPanel();
    }

    @Override
    public void clickedAt(final int x, final int y) {
        final Map m = getManager().getMap();
        Vector2i pos = new Vector2i(x, y);
        if (!m.contains(x, y)) {
            return;
        }
        final ItemImg item = getManager().getSelectedItem();
        if (item != null) {
            m.getTileAt(x, y).getMapItems().add(new MapItem(item.getItemId(), "", 0));
        }
    }


    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.SingleItemTool");
    }

    @Override
    public ResizableIcon getToolIcon() {
        return null;
    }

    @Override
    public JPanel getSettingsPanel() {
        return panel;
    }

}

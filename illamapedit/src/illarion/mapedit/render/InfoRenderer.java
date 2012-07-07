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
package illarion.mapedit.render;

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.gui.MapPanel;

import java.awt.*;

/**
 * This renderer renders some useful information's: eg. the size and the position.
 *
 * @author Tim
 */
public class InfoRenderer extends AbstractMapRenderer {
    private static final String NL = System.getProperty("line.separator");
    private final String size;
    private final String pos;

    public InfoRenderer(final MapPanel mapPanel) {
        super(mapPanel);
        size = Lang.getMsg("map.info.Size");
        pos = Lang.getMsg("map.info.Pos");
    }

    @Override
    public void renderMap(final Graphics2D g) {
        Map m = getMap();
        String s1 = String.format("%1$s (%2$d, %3$d, %4$d)", pos, m.getX(), m.getY(),
                m.getL());
        String s2 = String.format("%1$s (%2$d, %3$d)", size, m.getW(), m.getH());
        g.drawString(s1, 10, 20);
        g.drawString(s2, 10, 35);
    }

    @Override
    protected int getRenderPriority() {
        return 100;
    }
}

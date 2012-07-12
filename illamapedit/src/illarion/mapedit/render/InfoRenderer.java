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
import illarion.mapedit.resource.loaders.TileLoader;

import java.awt.*;

/**
 * This renderer renders some useful information's: eg. the size and the position.
 *
 * @author Tim
 */
public class InfoRenderer extends AbstractMapRenderer {
    private static final Font FONT = new Font("Arial", Font.BOLD, 14);
    private static final int START_Y = 20;
    private static final int STEP_Y = 15;
    private final MapPanel panel;
    private final String size;
    private final String pos;
    private final String zoom;
    private final String trans;

    public InfoRenderer(final MapPanel mapPanel) {
        super(mapPanel);
        panel = mapPanel;
        size = Lang.getMsg("map.info.Size");
        pos = Lang.getMsg("map.info.Pos");
        zoom = Lang.getMsg("map.info.Zoom");
        trans = Lang.getMsg("map.info.Trans");
    }

    @Override
    public void renderMap(final Graphics2D g) {
        g.setFont(FONT);
        g.setColor(Color.WHITE);
        final Map m = getMap();
        final int mapX = panel.getMouseMapPosX();
        final int mapY = panel.getMouseMapPosY();

        String[] lines = new String[5];
        lines[0] = String.format("%1$s (%2$d, %3$d, %4$d)", pos, m.getX(), m.getY(),
                m.getZ());
        lines[1] = String.format("%1$s (%2$d, %3$d)", size, m.getW(), m.getH());
        lines[2] = String.format("%1$s %2$f", zoom, getZoom());
        lines[3] = String.format("%1$s (%2$d, %3$d)", trans, getTranslateX(), getTranslateY());
        if (m.contains(mapX, mapY)) {
            final String tile = TileLoader.getInstance().getTileFromId(m.getTileAt(mapX, mapY).getId()).getDescription();
            lines[4] = String.format("(%d,%d) - %s", mapX, mapY, tile);
        } else {
            lines[4] = "";
        }
        int y = START_Y;
        for (String s : lines) {
            g.drawString(s, 10, y);
            y += STEP_Y;
        }

    }

    @Override
    protected int getRenderPriority() {
        return 100;
    }
}

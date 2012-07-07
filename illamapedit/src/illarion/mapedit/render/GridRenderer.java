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

import illarion.common.util.FastMath;
import illarion.mapedit.data.Map;
import illarion.mapedit.gui.MapPanel;

import java.awt.*;

/**
 * @author Tim
 */
public class GridRenderer extends AbstractMapRenderer {

    public GridRenderer(final MapPanel panel) {
        super(panel);
    }

    @Override
    public void renderMap(final Graphics2D g) {
        final float zoom = getZoom();
        final Map map = getMap();
        final int w = map.getW();
        final int h = map.getH();
        final int wm = map.getW() / 2;
        final int hm = map.getH() / 2;
        final float tileSideLength = FastMath.sqrt(FastMath.sqr(w / 2) + FastMath.sqr(h / 2));
        final int tx = getTranslateX();
        final int ty = getTranslateY();

        g.setColor(Color.WHITE);
        for (int i = 0; i < (w + 1); ++i) {
            final int wid = (int) ((i * getTileWidth()) / 2f);
            final int hei = (int) ((i * getTileHeight()) / 2f);
            g.drawLine(tx + wid, ty + (int) (hm * getTileHeight()) + hei, tx + (int) (wm * getTileWidth()) + wid, ty + hei);
        }
        for (int i = 0; i < (h + 1); ++i) {
            final int wid = (int) ((i * getTileWidth()) / 2f);
            final int hei = (int) ((i * getTileHeight()) / 2f);
            g.drawLine(tx + wid, (ty + (int) (hm * getTileHeight())) - hei, tx + (int) (wm * getTileWidth()) + wid,
                    ty + (int) ((h * getTileHeight()) - hei));
        }
    }

    @Override
    protected int getRenderPriority() {
        return 0;
    }
}

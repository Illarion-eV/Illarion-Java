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
package illarion.mapedit.gui;

import illarion.mapedit.data.Map;
import illarion.mapedit.render.RendererManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Tim
 */
public class MapPanel extends JPanel {

    private final RendererManager rendererManager;
    private final Rectangle dirty;
    private Map map;

    public MapPanel() {
        super();
        rendererManager = RendererManager.getInstance();
        dirty = new Rectangle(getWidth(), getHeight());
    }

    @Override
    public void paintComponent(Graphics gt) {
        Graphics2D g = (Graphics2D) gt;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        rendererManager.render(g);
        dirty.x = 0;
        dirty.y = 0;
        dirty.width = 0;
        dirty.height = 0;
    }

    public Rectangle getDirtyRegion() {
        return null;
    }

    public void addRegionDirty(final int x, final int y, final int w, final int h) {
        dirty.union(new Rectangle(x, y, w, h));
    }

    public void setMap(final Map map) {

    }
}

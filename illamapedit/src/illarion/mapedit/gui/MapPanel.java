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

import illarion.mapedit.render.AbstractMapRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Tim
 */
public class MapPanel extends JPanel {

    private RepaintManager repaintManager;
    private AbstractMapRenderer renderer;

    public MapPanel() {
        super();
        repaintManager = RepaintManager.currentManager(this);
        System.out.println(repaintManager);
        repaintManager.addDirtyRegion(this, 0, 0, 10, 10);
    }

    @Override
    public void paintComponent(Graphics gt) {
        Graphics2D g = (Graphics2D) gt;
        if (renderer != null){
            renderer.renderMap((Graphics2D) g);
        }
    }

    public Rectangle getDirtyRegion() {
        return repaintManager.getDirtyRegion(this);
    }
}

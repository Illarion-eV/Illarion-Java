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
import java.awt.event.*;

/**
 * @author Tim
 */
public class MapPanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {
    private static final MapPanel INSTANCE = new MapPanel();
    private final RendererManager rendererManager;
    private final Rectangle dirty;
    private Map map;
    private boolean canDrag;
    private int clickX, clickY;

    private MapPanel() {
        super();
        rendererManager = RendererManager.getInstance();
        dirty = new Rectangle(getWidth(), getHeight());
        rendererManager.initRenderers(this);
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics gt) {
        Graphics2D g = (Graphics2D) gt;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (map != null && rendererManager != null)
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
        this.map = map;
        repaint();
    }

    public Map getMap() {
        return map;
    }

    public static MapPanel getInstance() {
        return INSTANCE;
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        if (map != null) {
            rendererManager.changeZoom(e.getWheelRotation() / 2f);
            repaint();
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (canDrag && map != null) {
            rendererManager.changeTranslation(e.getX() - clickX, e.getY() - clickY);
            clickX = e.getX();
            clickY = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {

    }

    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (canDrag && map != null) {
            clickX = e.getX();
            clickY = e.getY();
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        canDrag = true;
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        canDrag = false;
    }
}

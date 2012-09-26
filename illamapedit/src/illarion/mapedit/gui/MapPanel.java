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

import illarion.mapedit.Utils;
import illarion.mapedit.data.Map;
import illarion.mapedit.events.MapClickedEvent;
import illarion.mapedit.events.MapDragFinishedEvent;
import illarion.mapedit.events.MapDraggedEvent;
import illarion.mapedit.events.RepaintRequestEvent;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.tools.ToolManager;
import illarion.mapedit.util.MouseButton;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The map panel is the area, on which the map is rendered.
 *
 * @author Tim
 */
public class MapPanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {
    private static final MapPanel INSTANCE = new MapPanel();
    private final RendererManager rendererManager;
    private final Rectangle dirty;
    private Map mapData;
    private boolean canDrag;
    private boolean isDragging;
    private int clickX;
    private int clickY;
    private int downClickX;
    private int downClickY;
    private int mouseMapPosX;
    private int mouseMapPosY;
    private ToolManager toolManager;

    private MapPanel() {
        rendererManager = new RendererManager(this);
        dirty = new Rectangle(getWidth(), getHeight());
        rendererManager.initRenderers(this);
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        AnnotationProcessor.process(this);
    }

    @Override
    public void paintComponent(final Graphics gt) {
        final Graphics2D g = (Graphics2D) gt;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        if ((mapData != null) && (rendererManager != null)) {
            rendererManager.render(g);
        }
        dirty.x = 0;
        dirty.y = 0;
        dirty.width = 0;
        dirty.height = 0;
    }

    public void setMap(final Map map) {
        mapData = map;
        if (toolManager != null) {
            toolManager.dispose();
        }
        toolManager = new ToolManager(map, rendererManager);
        EventBus.publish(new RepaintRequestEvent());
    }

    public Map getMap() {
        return mapData;
    }

    public static MapPanel getInstance() {
        return INSTANCE;
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        if (mapData != null) {
            if (e.getWheelRotation() < 0) {
                rendererManager.zoomIn();
            } else if (e.getWheelRotation() > 0) {
                rendererManager.zoomOut();
            }
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {

        if (!canDrag || (mapData == null)) {
            return;
        }

        final MouseButton btn = MouseButton.fromAwt(e.getModifiers());

        if (btn == MouseButton.RightButton) {
            rendererManager.changeTranslation(e.getX() - clickX, e.getY() - clickY);
        } else {
            isDragging = true;
            if (btn == MouseButton.LeftButton) {
                EventBus.publish(new MapDraggedEvent(clickX, clickY, e.getX(), e.getY(), btn));
            }
        }
        EventBus.publish(new RepaintRequestEvent());
        clickX = e.getX();
        clickY = e.getY();
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        final int transX = rendererManager.getTranslationX();
        final int transY = rendererManager.getTranslationY();
        mouseMapPosX = Utils.getMapXFormDisp(e.getX(), e.getY(), transX, transY, rendererManager.getZoom());
        mouseMapPosY = Utils.getMapYFormDisp(e.getX(), e.getY(), transX, transY, rendererManager.getZoom());
        EventBus.publish(new RepaintRequestEvent());
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (toolManager == null) {
            return;
        }
        final int x = Utils.getMapXFormDisp(e.getX(), e.getY(), rendererManager.getTranslationX(),
                rendererManager.getTranslationY(), rendererManager.getZoom());
        final int y = Utils.getMapYFormDisp(e.getX(), e.getY(), rendererManager.getTranslationX(),
                rendererManager.getTranslationY(), rendererManager.getZoom());
        EventBus.publish(new MapClickedEvent(x, y, MouseButton.fromAwt(e.getModifiers())));
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (canDrag && (mapData != null)) {
            clickX = e.getX();
            clickY = e.getY();
            downClickX = e.getX();
            downClickY = e.getY();
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (isDragging) {
            final int x1 = Utils.getMapXFormDisp(downClickX, downClickY, rendererManager.getTranslationX(),
                    rendererManager.getTranslationY(), rendererManager.getZoom());
            final int y1 = Utils.getMapYFormDisp(downClickY, downClickY, rendererManager.getTranslationX(),
                    rendererManager.getTranslationY(), rendererManager.getZoom());
            final int x2 = Utils.getMapXFormDisp(e.getX(), e.getY(), rendererManager.getTranslationX(),
                    rendererManager.getTranslationY(), rendererManager.getZoom());
            final int y2 = Utils.getMapYFormDisp(e.getX(), e.getY(), rendererManager.getTranslationX(),
                    rendererManager.getTranslationY(), rendererManager.getZoom());

            if (x1 != x2 && y1 != y2) {
                EventBus.publish(new MapDragFinishedEvent(x1, y1, x2, y2));
            }
        }
        isDragging = false;
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        canDrag = true;
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        canDrag = false;
    }

    public int getMouseMapPosX() {
        return mouseMapPosX;
    }

    public int getMouseMapPosY() {
        return mouseMapPosY;
    }

    @EventSubscriber(eventClass = RepaintRequestEvent.class)
    public void onRepaintRequest(final RepaintRequestEvent e) {
        e.doRepaint(this);
    }

    @Override
    public void repaint() {
        super.repaint();
    }
}

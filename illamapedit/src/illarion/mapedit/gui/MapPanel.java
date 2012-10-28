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
import illarion.mapedit.events.map.MapClickedEvent;
import illarion.mapedit.events.map.MapDragFinishedEvent;
import illarion.mapedit.events.map.MapDraggedEvent;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.tools.ToolManager;
import illarion.mapedit.util.MouseButton;
import illarion.mapedit.util.SwingLocation;
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
public class MapPanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener, ComponentListener {
    private final RendererManager rendererManager;
    private final Rectangle dirty;
    private boolean canDrag;
    private boolean isDragging;
    private int clickX;
    private int clickY;
    private int downClickX;
    private int downClickY;
    private ToolManager toolManager;
    private final GuiController controller;

    public MapPanel(final GuiController controller) {
        this.controller = controller;
        rendererManager = new RendererManager();
        toolManager = new ToolManager(controller, rendererManager);
        dirty = new Rectangle(getWidth(), getHeight());
        rendererManager.initRenderers();
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        addComponentListener(this);
        AnnotationProcessor.process(this);

    }

    @Override
    public void paintComponent(final Graphics gt) {
        final Graphics2D g = (Graphics2D) gt;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        for (Map m : controller.getMaps()) {
            rendererManager.render(m, getVisibleRect(), g);
        }

        dirty.x = 0;
        dirty.y = 0;
        dirty.width = 0;
        dirty.height = 0;
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        if (controller.isMapLoaded()) {
            if (e.getWheelRotation() < 0) {
                rendererManager.zoomIn();
            } else if (e.getWheelRotation() > 0) {
                rendererManager.zoomOut();
            }
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {

        if (!canDrag || !controller.isMapLoaded()) {
            return;
        }

        final MouseButton btn = MouseButton.fromAwt(e.getModifiers());

        if (btn == MouseButton.RightButton) {
            rendererManager.changeTranslation(e.getX() - clickX, e.getY() - clickY);
        } else {
            isDragging = true;
            if (btn == MouseButton.LeftButton) {
                EventBus.publish(new MapDraggedEvent(clickX, clickY, e.getX(), e.getY(), btn,
                        controller.getSelected()));
            }
        }
        EventBus.publish(new RepaintRequestEvent());
        clickX = e.getX();
        clickY = e.getY();
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (toolManager == null) {
            return;
        }
        //FIXME: Fix layer value not included!!
        final int x = SwingLocation.mapCoordinateX(e.getX(), e.getY(), controller.getSelected().getZ(), rendererManager.getTranslationX(),
                rendererManager.getTranslationY(), rendererManager.getZoom());
        final int y = SwingLocation.mapCoordinateY(e.getX(), e.getY(), controller.getSelected().getZ(), rendererManager.getTranslationX(),
                rendererManager.getTranslationY(), rendererManager.getZoom());

        if (controller.getSelected() != null) {
            EventBus.publish(new MapClickedEvent(x, y, MouseButton.fromAwt(e.getModifiers()),
                    controller.getSelected()));
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (canDrag && controller.isMapLoaded()) {
            clickX = e.getX();
            clickY = e.getY();
            downClickX = e.getX();
            downClickY = e.getY();
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (isDragging) {
            final int x1 = SwingLocation.mapCoordinateX(downClickX, downClickY, rendererManager.getTranslationX(),
                    rendererManager.getTranslationY(), controller.getSelected().getZ(), rendererManager.getZoom());
            final int y1 = SwingLocation.mapCoordinateY(downClickY, downClickY, rendererManager.getTranslationX(),
                    rendererManager.getTranslationY(), controller.getSelected().getZ(), rendererManager.getZoom());
            final int x2 = SwingLocation.mapCoordinateX(e.getX(), e.getY(), rendererManager.getTranslationX(),
                    rendererManager.getTranslationY(), controller.getSelected().getZ(), rendererManager.getZoom());
            final int y2 = SwingLocation.mapCoordinateY(e.getX(), e.getY(), rendererManager.getTranslationX(),
                    rendererManager.getTranslationY(), controller.getSelected().getZ(), rendererManager.getZoom());

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

    public RendererManager getRenderManager() {
        return rendererManager;
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        rendererManager.setPanelViewport(getVisibleRect());
    }

    @Override
    public void componentMoved(final ComponentEvent e) {

    }

    @Override
    public void componentShown(final ComponentEvent e) {
        rendererManager.setPanelViewport(getVisibleRect());
    }

    @Override
    public void componentHidden(final ComponentEvent e) {

    }

    @EventSubscriber
    public void onRepaintRequest(final RepaintRequestEvent e) {
        e.doRepaint(this);
    }
}

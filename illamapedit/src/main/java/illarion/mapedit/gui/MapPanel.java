/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.mapedit.gui;

import illarion.mapedit.data.Map;
import illarion.mapedit.events.map.*;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.tools.ToolManager;
import illarion.mapedit.util.MouseButton;
import illarion.mapedit.util.SwingLocation;
import illarion.mapedit.util.Vector2i;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The map panel is the area, on which the map is rendered.
 *
 * @author Tim
 */
public class MapPanel extends JPanel
        implements MouseWheelListener, MouseMotionListener, MouseListener, ComponentListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapPanel.class);
    @Nonnull
    private final RendererManager rendererManager;
    @Nonnull
    private final Rectangle dirty;
    private boolean canDrag;
    private boolean isDragging;
    private int clickX;
    private int clickY;
    private int downClickX;
    private int downClickY;
    @Nonnull
    private final ToolManager toolManager;
    private final GuiController controller;

    public MapPanel(GuiController controller) {
        this.controller = controller;
        rendererManager = new RendererManager();
        toolManager = new ToolManager(controller);
        dirty = new Rectangle(getWidth(), getHeight());
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        addComponentListener(this);
        AnnotationProcessor.process(this);
    }

    @Override
    public void paintComponent(Graphics gt) {
        Graphics2D g = (Graphics2D) gt;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        List<Map> maps = new ArrayList<>(controller.getMaps());

        Collections.sort(maps, (map1, map2) -> Integer.compare(map1.getZ(), map2.getZ()));
        maps.stream().filter(Map::isVisible).forEach(map -> rendererManager.render(map, getVisibleRect(), g));

        dirty.x = 0;
        dirty.y = 0;
        dirty.width = 0;
        dirty.height = 0;
    }

    @Override
    public void mouseWheelMoved(@Nonnull MouseWheelEvent e) {
        if (controller.isMapLoaded()) {
            if (e.getWheelRotation() < 0) {
                rendererManager.zoomIn(new Vector2i(e.getX(), e.getY()));
            } else if (e.getWheelRotation() > 0) {
                rendererManager.zoomOut(new Vector2i(e.getX(), e.getY()));
            }
        }
    }

    @Override
    public void mouseDragged(@Nonnull MouseEvent e) {
        publishMapPosition(e);
        Map selected = controller.getSelected();
        if (!canDrag || !controller.isMapLoaded() || (selected == null)) {
            return;
        }

        MouseButton btn = MouseButton.fromAwt(e.getModifiers());

        if (btn == MouseButton.RightButton) {
            rendererManager.changeTranslation(e.getX() - clickX, e.getY() - clickY);
        } else {
            isDragging = true;
            if (btn == MouseButton.LeftButton) {
                int x = getMapCoordinateX(clickX, clickY, 0);
                int y = getMapCoordinateY(clickX, clickY, 0);
                int startX = getMapCoordinateX(downClickX, downClickY, 0);
                int startY = getMapCoordinateY(downClickX, downClickY, 0);
                if (selected.contains(x, y)) {
                    EventBus.publish(new MapDraggedEvent(x, y, startX, startY, btn, selected));
                }
            }
        }
        EventBus.publish(new RepaintRequestEvent());
        clickX = e.getX();
        clickY = e.getY();
    }

    private void publishMapPosition(@Nonnull MouseEvent e) {
        Map selectedMap = controller.getSelected();
        if (selectedMap == null) {
            return;
        }

        int mapX = getMapCoordinateX(e.getX(), e.getY(), selectedMap.getX());
        int mapY = getMapCoordinateY(e.getX(), e.getY(), selectedMap.getY());
        int worldX = selectedMap.getX() + mapX;
        int worldY = selectedMap.getY() + mapY;
        int worldZ = selectedMap.getZ();
        EventBus.publish(new MapPositionEvent(mapX, mapY, worldX, worldY, worldZ));
    }

    @Override
    public void mouseMoved(@Nonnull MouseEvent e) {
        publishMapPosition(e);
    }

    @Override
    public void mouseClicked(@Nonnull MouseEvent e) {
        Map selected = controller.getSelected();
        if (selected == null) {
            return;
        }

        int x = getMapCoordinateX(e.getX(), e.getY(), selected.getX());
        int y = getMapCoordinateY(e.getX(), e.getY(), selected.getY());
        if (selected.contains(x, y)) {
            EventBus.publish(new MapClickedEvent(x, y, MouseButton.fromAwt(e.getModifiers()), selected));
        }
    }

    @Override
    public void mousePressed(@Nonnull MouseEvent e) {
        if (canDrag && controller.isMapLoaded()) {
            clickX = e.getX();
            clickY = e.getY();
            downClickX = e.getX();
            downClickY = e.getY();
        }
    }

    @Override
    public void mouseReleased(@Nonnull MouseEvent e) {
        if (isDragging) {
            Map selected = controller.getSelected();
            if (selected != null) {
                int x1 = getMapCoordinateX(downClickX, downClickY, selected.getX());
                int y1 = getMapCoordinateY(downClickX, downClickY, selected.getY());
                int x2 = getMapCoordinateX(e.getX(), e.getY(), selected.getX());
                int y2 = getMapCoordinateY(e.getX(), e.getY(), selected.getY());
                if ((x1 != x2) || (y1 != y2)) {
                    EventBus.publish(new MapDragFinishedEvent(x1, y1, x2, y2, selected));
                }
            }
        }
        isDragging = false;
    }

    private int getMapCoordinateX(int x, int y, int offset) {
        return SwingLocation.mapCoordinateX(x, y, rendererManager.getTranslationX(), rendererManager.getTranslationY(),
                                            rendererManager.getZoom()) - offset;
    }

    private int getMapCoordinateY(int x, int y, int offset) {
        return SwingLocation.mapCoordinateY(x, y, rendererManager.getTranslationX(), rendererManager.getTranslationY(),
                                            rendererManager.getZoom()) - offset;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        canDrag = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        canDrag = false;
    }

    @Nonnull
    public RendererManager getRenderManager() {
        return rendererManager;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        rendererManager.setPanelViewport(getVisibleRect());
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {
        rendererManager.setPanelViewport(getVisibleRect());
    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @EventSubscriber
    public void onRepaintRequest(@Nonnull RepaintRequestEvent e) {
        e.doRepaint(this);
    }
}

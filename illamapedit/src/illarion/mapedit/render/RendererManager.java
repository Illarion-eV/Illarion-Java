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

import illarion.mapedit.data.Map;
import illarion.mapedit.events.map.RendererToggleEvent;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.events.map.ZoomEvent;
import illarion.mapedit.util.Vector2i;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.List;

/**
 * This class manages all renderers and enables, and disables them.
 *
 * @author Tim
 */
public class RendererManager {
    private static final Logger LOGGER = Logger.getLogger(RendererManager.class);
    private static final int DEFAULT_TILE_HEIGHT = 16;
    private static final int DEFAULT_TILE_WIDTH = 32;
    public static final float DEFAULT_ZOOM = 1f;
    private static final float MIN_ZOOM = 0.27f;
    public static final float ZOOM_STEP = .1f;

    private final List<AbstractMapRenderer> renderers;

    /**
     * The actual selection. If nothing is selected, the rectangle equals {@code (0, 0, 0, 0)}
     */
    private final Rectangle selection = new Rectangle();
    private float zoom = DEFAULT_ZOOM;
    private int translationX;
    private int translationY;
    private int defaultTranslationX;
    private int defaultTranslationY;
    private float zoomPointX;
    private float zoomPointY;
    private int actualLevel;
    private Rectangle panelViewport;
    private illarion.common.util.Rectangle mapViewport;


    public RendererManager() {
        renderers = new FastList<AbstractMapRenderer>();
        AnnotationProcessor.process(this);
    }

    //TODO: Move this
    public void initRenderers() {
        renderers.add(new TileRenderer(this));
        renderers.add(new ItemRenderer(this));
        Collections.sort(renderers);
    }

    public void addRenderer(final AbstractMapRenderer r) {
        renderers.add(r);
        Collections.sort(renderers);
        EventBus.publish(new RepaintRequestEvent());
    }

    public void removeRenderer(final AbstractMapRenderer r) {
        renderers.remove(r);
        EventBus.publish(new RepaintRequestEvent());
    }

    public void render(final Map map, final Rectangle viewport, final Graphics2D g) {
        final AffineTransform t = g.getTransform();
        g.translate(translationX - (zoomPointX * 2), translationY - (zoomPointY * 2));
        g.scale(getZoom(), getZoom());
        g.translate((zoomPointX * 2) / zoom, (zoomPointY * 2) / zoom);
        for (final AbstractMapRenderer r : renderers) {
            r.renderMap(map, viewport, actualLevel, g);
        }
        g.setTransform(t);
    }

    public static float getTileHeight() {
        return DEFAULT_TILE_HEIGHT;
    }

    public static float getTileWidth() {
        return DEFAULT_TILE_WIDTH;
    }

    public void setZoom(final float zoom) {
        LOGGER.debug("SetZoom(" + zoom + ");");
        this.zoom = zoom;
        //TODO: Include zoomPoint in event.
        Vector2i zoomPoint = new Vector2i(panelViewport.width / 2, panelViewport.height / 2);

        //TODO: Rename these variables
        zoomPointX = zoomPoint.getX();
        zoomPointY = zoomPoint.getY() /** zoom*/;


        calculateMapViewport();

        EventBus.publish(new RepaintRequestEvent());
    }

    private void calculateMapViewport() {
        mapViewport.set(mapViewport.getX(), mapViewport.getY(), (int) (panelViewport.getWidth() / zoom),
                (int) (panelViewport.getHeight() / zoom));
    }

    public float getZoom() {
        return zoom;
    }

    public int getTranslationX() {
        return translationX;
    }

    public void setTranslationX(final int translationX) {
        this.translationX = translationX;
    }

    public int getTranslationY() {
        return translationY;
    }

    public void setTranslationY(final int translationY) {
        this.translationY = translationY;
    }

    public void zoomIn() {
        if (zoom < 1) {
            setZoom(zoom + ZOOM_STEP);
        }
    }

    public void zoomOut() {
        if (zoom > 0) {
            setZoom(zoom - ZOOM_STEP);
        }
    }

    public void changeZoom(final float amount) {
        setZoom(zoom + amount);
    }

    public void changeTranslation(final int x, final int y) {
        setTranslationX(translationX + x);
        setTranslationY(translationY + y);
    }

    public float getMinZoom() {
        return MIN_ZOOM;
    }

    public void setPanelViewport(Rectangle panelViewport) {
        this.panelViewport = panelViewport;
        if (mapViewport == null) {
            mapViewport = new illarion.common.util.Rectangle();
        }
        this.mapViewport.set(translationX, translationY, panelViewport.width, panelViewport.height);
        calculateMapViewport();
    }

    /**
     * Sets a new rectangle as selection.
     *
     * @param rect the new rectangle
     */
    public void setSelection(final Rectangle rect) {
        selection.setBounds(rect);
    }

    public Rectangle getSelection() {
        return selection;
    }

    public void setDefaultTranslationY(final int defaultTranslationY) {
        this.defaultTranslationY = defaultTranslationY;
    }

    public void setDefaultTranslationX(final int defaultTranslationX) {
        this.defaultTranslationX = defaultTranslationX;
    }

    @EventSubscriber
    public void onRendererToggle(final RendererToggleEvent e) {
        for (final AbstractMapRenderer r : renderers) {
            if (r.getClass().equals(e.getRendererClass())) {
                removeRenderer(r);
                return;
            }
        }
        addRenderer(e.getRenderer(this));
    }

    @EventSubscriber
    public void onZoom(final ZoomEvent e) {
        if (e.isOriginal()) {
            setZoom(DEFAULT_ZOOM);
            setTranslationX(defaultTranslationX);
            setTranslationY(defaultTranslationY);
        } else {
            changeZoom(e.getValue());
        }
    }

    public void setSelectedLevel(final int level) {
        this.actualLevel = level;
    }
}

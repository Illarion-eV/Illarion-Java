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
package illarion.mapedit.render;

import illarion.mapedit.data.Map;
import illarion.mapedit.events.MapScrollEvent;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.events.map.ZoomEvent;
import illarion.mapedit.util.Vector2i;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class manages all renderers and enables, and disables them.
 *
 * @author Tim
 */
public class RendererManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RendererManager.class);
    private static final int DEFAULT_TILE_HEIGHT = 16;
    private static final int DEFAULT_TILE_WIDTH = 32;
    public static final float DEFAULT_ZOOM = 1f;
    private static final float MIN_ZOOM = 0.27f;
    public static final float ZOOM_STEP = .1f;

    @Nonnull
    private final List<AbstractMapRenderer> renderers;

    private float zoom = DEFAULT_ZOOM;
    private int translationX;
    private int translationY;
    private int defaultTranslationX;
    private int defaultTranslationY;
    private int actualLevel;
    private Rectangle panelViewport;

    public RendererManager() {
        renderers = new ArrayList<>();
        AnnotationProcessor.process(this);
    }

    public void addRenderer(AbstractMapRenderer r) {
        renderers.add(r);
        Collections.sort(renderers);
        EventBus.publish(new RepaintRequestEvent());
    }

    public void removeRenderer(AbstractMapRenderer r) {
        renderers.remove(r);
        EventBus.publish(new RepaintRequestEvent());
    }

    public void render(Map map, @Nonnull Rectangle viewport, @Nonnull Graphics2D g) {
        Rectangle renderViewport = new Rectangle((int) (viewport.x - (getTileWidth() * getZoom())),
                                                       (int) (viewport.y - (getTileHeight() * getZoom())),
                                                       (int) (viewport.width + (2 * getTileWidth() * getZoom())),
                                                       (int) (viewport.height + (2 * getTileHeight() * getZoom())));
        AffineTransform t = g.getTransform();
        g.translate(translationX, translationY);
        g.scale(getZoom(), getZoom());
        for (AbstractMapRenderer r : renderers) {
            r.renderMap(map, renderViewport, actualLevel, g);
        }
        g.setTransform(t);
    }

    public static float getTileHeight() {
        return DEFAULT_TILE_HEIGHT;
    }

    public static float getTileWidth() {
        return DEFAULT_TILE_WIDTH;
    }

    public void setZoom(float zoom, @Nullable Vector2i zoomPoint) {
        if (zoomPoint == null) {
            setZoom(zoom);
            return;
        }
        if ((zoom < .1) || (zoom > 1)) {
            return;
        }
        int viewportWidth = panelViewport.width;
        int viewportHeight = panelViewport.height;

        float relativeX = zoomPoint.getX() / (float) viewportWidth;
        float relativeY = zoomPoint.getY() / (float) viewportHeight;

        float oldViewportWidth = viewportWidth / this.zoom;
        float oldViewportHeight = viewportHeight / this.zoom;

        float newViewportWidth = viewportWidth / zoom;
        float newViewportHeight = viewportHeight / zoom;

        int fixX = Math.round((newViewportWidth - oldViewportWidth) * relativeX);
        int fixY = Math.round((newViewportHeight - oldViewportHeight) * relativeY);

        translationX /= this.zoom;
        translationY /= this.zoom;

        translationX += fixX;
        translationY += fixY;

        translationX *= zoom;
        translationY *= zoom;

        this.zoom = zoom;
        EventBus.publish(new RepaintRequestEvent());
    }

    public void setZoom(float zoom) {
        if ((zoom < .1) || (zoom > 1)) {
            return;
        }
        Vector2i zoomPoint = new Vector2i(panelViewport.width / 2, panelViewport.height / 2);
        setZoom(zoom, zoomPoint);
    }

    public float getZoom() {
        return zoom;
    }

    public int getTranslationX() {
        return translationX;
    }

    public void setTranslationX(int translationX) {
        this.translationX = translationX;
    }

    public int getTranslationY() {
        return translationY;
    }

    public void setTranslationY(int translationY) {
        this.translationY = translationY;
    }

    public void zoomIn(Vector2i pos) {
        if (zoom < 1) {
            setZoom(zoom + ZOOM_STEP, pos);
        }
    }

    public void zoomOut(Vector2i pos) {
        if (zoom > 0) {
            setZoom(zoom - ZOOM_STEP, pos);
        }
    }

    public void changeZoom(float amount, Vector2i pos) {
        setZoom(zoom + amount, pos);
    }

    public void changeTranslation(int x, int y) {
        setTranslationX(translationX + x);
        setTranslationY(translationY + y);
    }

    public float getMinZoom() {
        return MIN_ZOOM;
    }

    public void setPanelViewport(@Nullable Rectangle panelViewport) {
        if (panelViewport == null) {
            LOGGER.warn("SetPanelViewport: panelViewport is null");
            return;
        }
        if (this.panelViewport == null) {
            this.panelViewport = new Rectangle();
        }
        this.panelViewport.setRect(panelViewport.x, panelViewport.y, panelViewport.width, panelViewport.height);
    }

    public void setDefaultTranslationY(int defaultTranslationY) {
        this.defaultTranslationY = defaultTranslationY;
    }

    public void setDefaultTranslationX(int defaultTranslationX) {
        this.defaultTranslationX = defaultTranslationX;
    }

    public void setSelectedLevel(int level) {
        actualLevel = level;
    }

    @EventSubscriber
    public void onZoom(@Nonnull ZoomEvent e) {
        if (e.isOriginal()) {
            setZoom(DEFAULT_ZOOM);
            setTranslationX(defaultTranslationX);
            setTranslationY(defaultTranslationY);
        } else {
            changeZoom(e.getValue(), e.getPos());
        }
    }

    @EventSubscriber
    public void onScroll(@Nonnull MapScrollEvent e) {
        changeTranslation(e.getX(), e.getY());
        EventBus.publish(new RepaintRequestEvent());
    }
}

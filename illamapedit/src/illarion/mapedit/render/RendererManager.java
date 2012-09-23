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

import illarion.common.util.Rectangle;
import illarion.mapedit.Utils;
import illarion.mapedit.events.RendererToggleEvent;
import illarion.mapedit.events.RepaintRequestEvent;
import illarion.mapedit.events.ZoomEvent;
import illarion.mapedit.gui.MapPanel;
import javolution.util.FastList;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * This class manages all renderers and enables, and disables them.
 *
 * @author Tim
 */
public class RendererManager {
    private static final int DEFAULT_TILE_HEIGHT = 16;
    private static final int DEFAULT_TILE_WIDTH = 32;
    public static final float DEFAULT_ZOOM = 1f;
    private static final float MIN_ZOOM = 0.27f;
    public static final float ZOOM_STEP = .1f;

    private final List<AbstractMapRenderer> renderers;

    /**
     * The actual selection. If nothing is selected, the rectangle equals {@code (0, 0, 0, 0)}
     */
    private final illarion.common.util.Rectangle selection = new Rectangle();
    private float zoom = DEFAULT_ZOOM;
    private int translationX;
    private int translationY;
    private float tileHeight = DEFAULT_TILE_HEIGHT;
    private float tileWidth = DEFAULT_TILE_WIDTH;
    private final MapPanel mapPanel;


    public RendererManager(final MapPanel mapPanel) {
        renderers = new FastList<AbstractMapRenderer>();
        AnnotationProcessor.process(this);
        this.mapPanel = mapPanel;
    }

    public void initRenderers(final MapPanel panel) {
        renderers.add(new InfoRenderer(this));
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

    public void render(final Graphics2D g) {
        for (final AbstractMapRenderer r : renderers) {
            r.renderMap(g);
        }
    }

    public float getTileHeight() {
        return DEFAULT_TILE_HEIGHT;
    }

    public float getTileWidth() {
        return DEFAULT_TILE_WIDTH;
    }

    public void setZoom(final float zoom) {
        final float oldZoom = this.zoom;
        final float oldTileWith = tileWidth;
        final float oldTileHeight = tileHeight;

        tileWidth = DEFAULT_TILE_WIDTH * zoom;
        tileHeight = DEFAULT_TILE_HEIGHT * zoom;
        this.zoom = zoom;

        //TODO:  Fix this ;-S
        final int oldX = Utils.getMapXFormDisp(0, 0, getTranslationX(), getTranslationY(), oldZoom);
        final int oldY = Utils.getMapYFormDisp(0, 0, getTranslationX(), getTranslationY(), oldZoom);

        final int newX = Utils.getMapXFormDisp(0, 0, getTranslationX(), getTranslationY(), zoom);
        final int newY = Utils.getMapYFormDisp(0, 0, getTranslationX(), getTranslationY(), zoom);

        changeTranslation((int) ((newX - oldX) * oldTileWith), (int) ((newY - oldY) * oldTileHeight));

        EventBus.publish(new RepaintRequestEvent());
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
            setZoom(zoom + (zoom * ZOOM_STEP));
        }
    }

    public void zoomOut() {
        setZoom(zoom - (zoom * ZOOM_STEP));

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

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    /**
     * Sets a new rectangle as selection.
     *
     * @param rect the new rectangle
     */
    public void setSelection(illarion.common.util.Rectangle rect) {
        selection.set(rect);
    }

    public Rectangle getSelection() {
        return selection;
    }

    @EventSubscriber(eventClass = RendererToggleEvent.class)
    public void onRendererToggle(final RendererToggleEvent e) {
        for (final AbstractMapRenderer r : renderers) {
            if (r.getClass().equals(e.getRendererClass())) {
                removeRenderer(r);
                return;
            }
        }
        addRenderer(e.getRenderer(this));
    }

    @EventSubscriber(eventClass = ZoomEvent.class)
    public void onZoom(final ZoomEvent e) {
        if (e.isOriginal()) {
            setZoom(DEFAULT_ZOOM);
        } else {
            changeZoom(e.getValue());
        }
    }
}

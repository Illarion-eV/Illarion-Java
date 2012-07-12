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

import illarion.mapedit.events.MapDragedEvent;
import illarion.mapedit.events.RepaintRequestEvent;
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
    private static final RendererManager INSTANCE = new RendererManager();
    private static final int DEFAULT_TILE_HEIGHT = 16;
    private static final int DEFAULT_TILE_WIDTH = 32;
    public static final float DEFAULT_ZOOM = 1f;
    private static final float MIN_ZOOM = 0.27f;
    public static final float ZOOM_STEP = .1f;

    private final List<AbstractMapRenderer> renderers;

    private float zoom = DEFAULT_ZOOM;
    private int translationX;
    private int translationY;
    private float tileHeight = DEFAULT_TILE_HEIGHT;
    private float tileWidth = DEFAULT_TILE_WIDTH;

    private RendererManager() {
        renderers = new FastList<AbstractMapRenderer>();
        AnnotationProcessor.process(this);
    }

    public void initRenderers(final MapPanel panel) {
        renderers.add(new InfoRenderer(panel));
        renderers.add(new TileRenderer(panel));
        renderers.add(new ItemRenderer(panel));
        Collections.sort(renderers);
    }

    public void addRenderer(final AbstractMapRenderer r) {
        renderers.add(r);
        Collections.sort(renderers);
        EventBus.publish(new RepaintRequestEvent());
    }

    public void removeRenderer(final AbstractMapRenderer r) {
        renderers.remove(r);
    }

    public void render(final Graphics2D g) {
        for (final AbstractMapRenderer r : renderers) {
            r.renderMap(g);
        }
    }

    public static RendererManager getInstance() {
        return INSTANCE;
    }

    public float getTileHeight() {
        return tileHeight;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public void setZoom(final float zoom) {
        this.zoom = zoom;
        tileWidth = DEFAULT_TILE_WIDTH * zoom;
        tileHeight = DEFAULT_TILE_HEIGHT * zoom;
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
        setZoom(zoom + (zoom * ZOOM_STEP));
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

    @EventSubscriber(eventClass = MapDragedEvent.class)
    public void onMapDragged(final MapDragedEvent e) {
        changeTranslation(e.getOffsetX(), e.getOffsetY());
        EventBus.publish(new RepaintRequestEvent());
    }
}

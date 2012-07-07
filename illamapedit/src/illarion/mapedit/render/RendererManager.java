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

import illarion.mapedit.gui.MapPanel;
import javolution.util.FastList;

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
    public static final float DEFAULT_ZOOM = 1f;
    private static final float MIN_ZOOM = 0.05f;

    private final List<AbstractMapRenderer> renderers;

    private float zoom = DEFAULT_ZOOM;
    private int translationX = 0;
    private int translationY = 0;
    private float tileHeight = 16;
    private float tileWidth = 32;

    private RendererManager() {
        renderers = new FastList<AbstractMapRenderer>();
    }

    public void initRenderers(MapPanel panel) {
        renderers.add(new InfoRenderer(panel));
        renderers.add(new TileRenderer(panel));
    }

    public void addRenderer(AbstractMapRenderer r) {
        renderers.add(r);
        Collections.sort(renderers);
    }

    public void removeRenderer(AbstractMapRenderer r) {
        renderers.remove(r);
    }

    public void render(Graphics2D g) {
        for (AbstractMapRenderer r : renderers) {
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

    public void setZoom(float zoom) {
        this.zoom = zoom;
        tileHeight = zoom / 2f;
        tileWidth = zoom;
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
        setZoom(zoom + zoom * .1f);
    }

    public void zoomOut() {
        setZoom(zoom - zoom * .1f);

    }

    public void changeZoom(float amount) {
        setZoom(zoom + amount);
    }

    public void changeTranslation(final int x, final int y) {
        setTranslationX(translationX + x);
        setTranslationY(translationY + y);
    }
}

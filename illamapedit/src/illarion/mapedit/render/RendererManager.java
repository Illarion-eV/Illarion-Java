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

import javolution.util.FastList;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Tim
 */
public class RendererManager {
    private static final RendererManager INSTANCE = new RendererManager();

    private final List<AbstractMapRenderer> renderers;

    private RendererManager() {
        renderers = new FastList<AbstractMapRenderer>();
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
}

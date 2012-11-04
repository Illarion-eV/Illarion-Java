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

import java.awt.*;


/**
 * @author Tim
 */
public class SelectionRenderer extends AbstractMapRenderer {

    public SelectionRenderer(RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(final Map map, final Rectangle viewport, final int level, final Graphics2D g) {

    }

    @Override
    protected int getRenderPriority() {
        return 0;
    }
}

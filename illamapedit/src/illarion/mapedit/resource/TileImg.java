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
package illarion.mapedit.resource;

import illarion.common.graphics.TileInfo;

import java.awt.*;

/**
 * @author Tim
 */
public class TileImg {

    private final int id;
    private final String name;
    private final int frameCount;
    private final int animationSpeed;
    private final TileInfo info;
    private final Image[] img;

    public TileImg(final int id, final String name, final int frameCount, final int animationSpeed,
                   final TileInfo info, final Image[] img) {
        this.id = id;
        this.name = name;
        this.frameCount = frameCount;
        this.animationSpeed = animationSpeed;
        this.info = info;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public TileInfo getInfo() {
        return info;
    }

    public Image[] getImg() {
        return img;
    }
}

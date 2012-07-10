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

import javolution.text.TextBuilder;

import java.awt.*;

/**
 * @author Tim
 */
public class ItemImg {


    private final int itemId;
    private final String resourceName;
    private final int offsetX;
    private final int offsetY;
    private final int frameCount;
    private final int animationSpeed;
    private final int itemMode;
    private final int itemLight;
    private final int face;
    private Image[] imgs;

    public int getItemId() {
        return itemId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public int getItemMode() {
        return itemMode;
    }

    public int getItemLight() {
        return itemLight;
    }

    public int getFace() {
        return face;
    }

    public ItemImg(final int itemId, final String resourceName,
                   final int offsetX, final int offsetY, final int frameCount,
                   final int animationSpeed, final int itemMode,
                   final int itemLight, final int face, final Image[] imgs) {

        this.itemId = itemId;
        this.resourceName = resourceName;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.frameCount = frameCount;
        this.animationSpeed = animationSpeed;
        this.itemMode = itemMode;
        this.itemLight = itemLight;
        this.face = face;
        this.imgs = new Image[imgs.length];
        System.arraycopy(imgs, 0, this.imgs, 0, imgs.length);
    }

    @Override
    public String toString() {
        TextBuilder b = TextBuilder.newInstance();
        b.append("ItemImg{").append("itemId=").append(itemId).append(", resourceName='").append(resourceName);
        b.append('\'').append(", offsetX=").append(offsetX).append(", offsetY=").append(offsetY);
        b.append(", frameCount=").append(frameCount).append(", animationSpeed=").append(animationSpeed);
        b.append(", itemMode=").append(itemMode).append(", itemLight=").append(itemLight);
        b.append(", face=").append(face).append('}');

        try {
            return b.toString();
        } finally {
            TextBuilder.recycle(b);
        }
    }
}

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

import illarion.common.graphics.ItemInfo;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * @author Tim
 */
public class ItemImg {

    private final ItemInfo info;
    private final int itemId;
    private final String resourceName;
    private final int offsetX;
    private final int offsetY;
    private final int frameCount;
    private final int animationSpeed;
    private final int itemMode;
    @Nonnull
    private final Image[] imgs;
    private final int editorGroup;

    public int getItemId() {
        return itemId;
    }

    public boolean isObstacle() {
        return !info.isObstacle();
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

    /**
     * Get the surface level of the item. So the offset how much a item that lies on this item has to move up to
     * appear to lie on this item.
     *
     * @return the amount of pixels the next item offset has to move up
     */
    public int getHeight() {
        return info.getLevel();
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public int getItemMode() {
        return itemMode;
    }

    public ItemImg(final int itemId, final String resourceName,
                   final int editorGroup, final int offsetX, final int offsetY, final int frameCount,
                   final int animationSpeed, final int itemMode,
                   @Nonnull final Image[] imgs, final ItemInfo info) {

        this.itemId = itemId;
        this.resourceName = resourceName;
        this.editorGroup = editorGroup;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.frameCount = frameCount;
        this.animationSpeed = animationSpeed;
        this.itemMode = itemMode;


        this.info = info;
        this.imgs = new Image[imgs.length];

        System.arraycopy(imgs, 0, this.imgs, 0, imgs.length);
    }

    @Nonnull
    public Image[] getImgs() {
        return imgs;
    }

    public int getEditorGroup() {
        return editorGroup;
    }
}

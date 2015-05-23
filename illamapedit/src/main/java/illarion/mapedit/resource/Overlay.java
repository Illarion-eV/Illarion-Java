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
package illarion.mapedit.resource;

import java.awt.*;

/**
 * @author Tim
 */
public class Overlay {
    private static final int BASE_MASK = 0x001F;
    private static final int OVERLAY_MASK = 0x03E0;
    private static final int SHAPE_MASK = 0xFC00;

    private final int tileID;
    private final String fileName;
    private final int layer;
    private final Image[] imgs;

    public Overlay(int tileID, String fileName, int layer, Image... imgs) {
        this.tileID = tileID;
        this.fileName = fileName;
        this.layer = layer;
        this.imgs = imgs;
    }

    public int getTileID() {
        return tileID;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLayer() {
        return layer;
    }

    public Image[] getImgs() {
        return imgs;
    }

    public static int generateTileId(int baseId, int overlayId, int shapeId) {
        return (baseId & BASE_MASK) | ((overlayId << 5) & OVERLAY_MASK) | ((shapeId << 10) & SHAPE_MASK);
    }
}

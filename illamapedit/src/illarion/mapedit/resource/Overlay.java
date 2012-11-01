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

/**
 * @author Tim
 */
public class Overlay {
    private final int tileID;
    private final String fileName;
    private final String layer;

    public Overlay(final int tileID, final String fileName, final String layer) {
        this.tileID = tileID;
        this.fileName = fileName;
        this.layer = layer;
    }

    public int getTileID() {
        return tileID;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLayer() {
        return layer;
    }
}

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
package illarion.mapedit.data;

/**
 * This object represents a tile with a coordinate, an id and a music id.
 *
 * @author Tim
 */
public class Tile {
    /**
     * The x coordinate.
     */
    private final int x;
    /**
     * The y coordinate.
     */
    private final int y;
    /**
     * The tile id.
     */
    private final int id;
    /**
     * The music id.
     */
    private final int musicID;

    /**
     * Creates a new tile with the coordinates, the id and the music id.
     *
     * @param x
     * @param y
     * @param id
     * @param musicID
     */
    public Tile(final int x, final int y, final int id, final int musicID) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.musicID = musicID;
    }

    /**
     * Creates a copy of the other tile.
     *
     * @param old
     */
    public Tile(final Tile old) {
        x = old.x;
        y = old.y;
        id = old.id;
        musicID = old.musicID;
    }

    /**
     * Returns the x coordinate.
     *
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     *
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the tile id.
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the music id.
     *
     * @return
     */
    public int getMusicID() {
        return musicID;
    }

    /**
     * Loads a tile from a line of a *.tiles.txt file with the format <br/>
     * {@code [X];[Y];[TileID];[MusikID];0 }
     *
     * @param line
     * @return
     */
    public static Tile fromString(final String line) {
        final String[] sections = line.split(";");
        if (sections.length != 5) {
            throw new IllegalArgumentException("Item can only hava 5 sections: " + line);
        }
        return new Tile(
                Integer.parseInt(sections[0]),
                Integer.parseInt(sections[1]),
                Integer.parseInt(sections[2]),
                Integer.parseInt(sections[3])
        );

    }
}

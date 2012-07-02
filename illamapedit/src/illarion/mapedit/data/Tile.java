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
 * @author Tim
 */
public class Tile {

    private final int x;
    private final int y;
    private final int id;
    private final int musicID;

    public Tile(final int x, final int y, final int id, final int musicID) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.musicID = musicID;
    }

    public Tile(final Tile old) {
        x = old.x;
        y = old.y;
        id = old.id;
        musicID = old.musicID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public int getMusicID() {
        return musicID;
    }

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

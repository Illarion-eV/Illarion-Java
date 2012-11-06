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
package illarion.mapedit.gui.docu;

import java.awt.*;

/**
 * @author Tim
 */
public class DocuPage {

    private final String name;
    private final String text;
    private final Image img;

    public DocuPage(final String name, final String text, final Image img) {

        this.name = name;
        this.text = text;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public Image getImg() {
        return img;
    }
}

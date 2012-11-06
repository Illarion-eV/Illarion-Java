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

import illarion.mapedit.Lang;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author Tim
 */
public class DocuReader implements Iterable<DocuPage>, Iterator<DocuPage> {

    private static final Logger LOGGER = Logger.getLogger(DocuReader.class);
    private static final Pattern DELIMITER = Pattern.compile(";");
    private final boolean isGerman;
    private final BufferedReader is;

    private String[] nextLine;

    public DocuReader() {
        isGerman = Lang.getInstance().getLocale().equals(Locale.GERMAN);
        is = new BufferedReader(new InputStreamReader(DocuReader.class.getResourceAsStream("/docu/docu.txt")));
    }

    @Override
    public boolean hasNext() {
        String line = null;
        do {
            try {
                line = is.readLine().trim();
            } catch (IOException e) {
                LOGGER.warn("IO Error while reading docu", e);
            }
        } while ((line != null) && (line.startsWith("#") || line.isEmpty()));
        if (line == null) {
            return false;
        }
        nextLine = DELIMITER.split(line);
        return (nextLine.length == 4) || (nextLine.length == 5);
    }

    @Override
    public DocuPage next() {
        final String name;
        final String text;
        final Image img;
        if (isGerman) {
            name = nextLine[1];
            text = nextLine[3];
        } else {
            name = nextLine[0];
            text = nextLine[2];
        }
        if (nextLine.length == 5) {
            img = load(nextLine[4]);
        } else {
            img = null;
        }
        return new DocuPage(name, text, img);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removing a DocuEntry is not supportet.");
    }


    @Override
    public Iterator<DocuPage> iterator() {
        return this;
    }

    private Image load(final String path) {
        try {
            return ImageIO.read(DocuReader.class.getResourceAsStream(path));
        } catch (IOException e) {
            return null;
        }
    }
}

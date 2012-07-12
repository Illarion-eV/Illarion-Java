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
package illarion.mapedit.crash.exceptions;

import java.io.IOException;

/**
 * @author Tim
 */
public class FormatCorruptedException extends IOException {
    private final String file;
    private final String line;
    private final int lineNr;
    private final String format;

    public FormatCorruptedException(final String file, final String line, final int lineNr, final String format) {
        super(String.format("Format corrupted in file %s at line %d. Expected:[%s] Found:[%s]",
                file, lineNr, format, line));
        this.file = file;
        this.line = line;
        this.lineNr = lineNr;
        this.format = format;
    }

    public String getFile() {
        return file;
    }

    public String getLine() {
        return line;
    }

    public int getLineNr() {
        return lineNr;
    }

    public String getFormat() {
        return format;
    }
}

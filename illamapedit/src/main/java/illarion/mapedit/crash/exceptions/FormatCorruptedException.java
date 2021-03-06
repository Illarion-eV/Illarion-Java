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
package illarion.mapedit.crash.exceptions;

import java.io.IOException;

/**
 * This will be thrown if a file is corrupted.
 *
 * @author Tim
 */
public class FormatCorruptedException extends IOException {
    /**
     * The path of the file with the corrupted format.
     */
    private final String file;
    /**
     * The line, that contains the error.
     */
    private final String line;
    /**
     * The nr of the line that contains the error.
     */
    private final int lineNr;
    /**
     * The expected content of the line.
     */
    private final String format;

    /**
     * Creates a new FormatCorruptedException.
     *
     * @param file the path of the file
     * @param line the content of the line, containing the error
     * @param lineNr the number of the line
     * @param format the expected content of the line
     */
    public FormatCorruptedException(String file, String line, int lineNr, String format) {
        super(String.format("Format corrupted in file %s at line %d. Expected:[%s] Found:[%s]", file, lineNr, format,
                            line));
        this.file = file;
        this.line = line;
        this.lineNr = lineNr;
        this.format = format;
    }

    /**
     * @return the filepath
     */
    public String getFile() {
        return file;
    }

    /**
     * @return the line content
     */
    public String getLine() {
        return line;
    }

    /**
     * @return the line number
     */
    public int getLineNr() {
        return lineNr;
    }

    /**
     * @return the expected content of the line
     */
    public String getFormat() {
        return format;
    }
}

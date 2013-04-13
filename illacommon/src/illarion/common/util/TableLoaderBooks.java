/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import javax.annotation.Nonnull;

/**
 * This implementation of the table loader is used to load the books table.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class TableLoaderBooks extends TableLoader {
    /**
     * The index in the table record of the book id.
     */
    private static final int TB_ID = 0;

    /**
     * The index in the table record of the book filename.
     */
    private static final int TB_NAME = 1;

    /**
     * Create a new book table loader that loads the music data from the default file.
     *
     * @param callback the callback sink that receives the data
     * @param <T>      the type of the table loader used
     */
    public <T extends TableLoader> TableLoaderBooks(@Nonnull final TableLoaderSink<T> callback) {
        super("Books", callback);
    }

    /**
     * Get the ID of the book.
     *
     * @return the ID of the book
     */
    public int getBookId() {
        return getInt(TB_ID);
    }

    /**
     * The name of the file that contains books contents.
     *
     * @return the book file
     */
    public String getBookFile() {
        return getString(TB_NAME);
    }
}

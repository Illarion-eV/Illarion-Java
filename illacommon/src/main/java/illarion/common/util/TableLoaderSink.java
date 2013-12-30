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

/**
 * Interface for callback functions of the table loader. Classes that implement this interface are usable as
 * callback function of a table loader and so they are able to read the data the table loader reads.
 *
 * @param <T> the specific implementation of the table loader that is used with this sink
 * @author Nop
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public interface TableLoaderSink<T extends TableLoader> {
    /**
     * This function is called for every line and allows reading the tokens that are read in the line by the table
     * loader. It does not matter what tokens are read by the function then or how often. After this function quits the
     * table loader goes on reading the next line until everything is read.
     *
     * @param line   the numeric index of the last read line
     * @param loader the instance of table loader that is reading the line
     * @return {@code true} in case the table loader shall go on reading the table, {@code false} for cancel the reading
     *         operations and quit the tableloader
     */
    boolean processRecord(int line, T loader);
}

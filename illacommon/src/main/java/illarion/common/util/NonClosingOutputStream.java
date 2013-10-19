/*
 * This file is part of the common.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The common is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The common is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the common.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.io.FilterOutputStream;
import java.io.OutputStream;

/**
 * This class is a little bit of a hack. Its a filter stream that prevents the target stream from being closed. Other
 * than that, it forwards everything to the target stream.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class NonClosingOutputStream extends FilterOutputStream {

    public NonClosingOutputStream(final OutputStream out) {
        super(out);
    }

    @Override
    public void close() {
        // nothing
    }
}

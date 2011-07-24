/*
 * This file is part of the Illarion Download Manager.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Download Manager is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Download Manager is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Download Manager. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.tasks.unpack;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * As there are some entirely stupid implementations in java some terrible
 * classes are needed to work around those. This class is one of those. In this
 * case its a input stream that does nothing but catching close requests and
 * discard them
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class NonClosingInputStream extends FilterInputStream {
    /**
     * The default constructor to this filter input stream.
     * 
     * @param inStream the input stream that is filtered
     */
    public NonClosingInputStream(final InputStream inStream) {
        super(inStream);
    }

    /**
     * This close function does not close the stream. Isn't it great?
     */
    @Override
    public void close() {
        // do nothing! Hahaha, you can't close me!
    }
}

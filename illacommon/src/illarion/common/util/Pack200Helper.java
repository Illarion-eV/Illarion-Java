/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Pack200;

/**
 * This is a helper class for all classes that work with the pack200 packer. Its
 * sole purpose is to provide packer and unpacker with common settings.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Pack200Helper {
    /**
     * This constant is used as code attribute value to set how the local
     * variable table is handled.
     */
    @SuppressWarnings("nls")
    private static final String CODE_ATTRIB_LOCAL_VAR_TABLE =
        "LocalVariableTable";

    /**
     * This is the map of properties that is added to every new created packer
     * and unpacker.
     */
    private static final Map<String, String> PROPS;

    static {
        PROPS = new HashMap<String, String>();
        PROPS.put(Pack200.Packer.KEEP_FILE_ORDER, Pack200.Packer.FALSE);
        PROPS.put(Pack200.Packer.MODIFICATION_TIME, Pack200.Packer.LATEST);
        PROPS.put(Pack200.Packer.EFFORT, Integer.toString(7));
        PROPS.put(Pack200.Packer.DEFLATE_HINT, Pack200.Packer.TRUE);
        PROPS.put(Pack200.Packer.CODE_ATTRIBUTE_PFX
            .concat(CODE_ATTRIB_LOCAL_VAR_TABLE), Pack200.Packer.STRIP);
        PROPS.put(Pack200.Unpacker.DEFLATE_HINT, Pack200.Packer.TRUE);
    }

    /**
     * This private constructor does nothing but blocking the possibility to
     * create new instances of this class.
     */
    private Pack200Helper() {
        // nothing to do
    }

    /**
     * This function returns a newly created packer with the common settings
     * that is ready to be used.
     * 
     * @return the new packer
     */
    public static Pack200.Packer getPacker() {
        final Pack200.Packer packer = Pack200.newPacker();
        packer.properties().putAll(PROPS);
        return packer;
    }

    /**
     * This class creates a new unpacker with the common settings that is ready
     * to be used.
     * 
     * @return the new unpacker
     */
    public static Pack200.Unpacker getUnpacker() {
        final Pack200.Unpacker unpacker = Pack200.newUnpacker();
        unpacker.properties().putAll(PROPS);
        return unpacker;
    }
}

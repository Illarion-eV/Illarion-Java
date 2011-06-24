/*
 * This file is part of the Illarion Input Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Input Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Input Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Input Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.input;

/**
 * A list of the engines that this input port offers to ensure a valid engine is
 * selected.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public enum Engines {
    /**
     * Constant for the native JAVA system using AWT.
     */
    @SuppressWarnings("nls")
    java("java", "JAVA"),

    /**
     * Constant for the LWJGL 2D Graphic engine.
     */
    @SuppressWarnings("nls")
    lwjgl("lwjgl", "LWJGL"),

    /**
     * Constant for the JOGL system using NEWT.
     */
    @SuppressWarnings("nls")
    newt("newt", "NEWT");

    /**
     * Stores the name of the class folder that is searched for the classes that
     * are implemented with this Engine. The classes are expected in
     * "illarion.graphics." + classFolder
     */
    private final String classFolder;

    /**
     * The suffix of the class names that identify this implementation. The
     * class names have the class names of their interface and the content of
     * this variable as suffix.
     */
    private final String classSuffix;

    /**
     * Basic constructor of the Engines definition.
     * 
     * @param folder the class folder of this implementation, see
     *            {@link #classFolder}
     * @param suffix the class name suffix of the implementation, see
     *            {@link #classSuffix}
     */
    Engines(final String folder, final String suffix) {
        classFolder = folder;
        classSuffix = suffix;
    }

    /**
     * Get the class folder name of this implementation.
     * 
     * @return the string name of the class folder of this implementation.
     * @see #classFolder
     */
    String getFolder() {
        return classFolder;
    }

    /**
     * Get the class suffix of this implementation.
     * 
     * @return the string of the class suffix of this implementation.
     * @see #classSuffix
     */
    String getSuffix() {
        return classSuffix;
    }
}

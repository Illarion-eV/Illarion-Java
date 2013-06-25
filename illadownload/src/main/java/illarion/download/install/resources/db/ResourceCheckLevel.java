/*
 * This file is part of the Illarion Download Utility.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Download Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Download Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Download Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.install.resources.db;

/**
 * This enumerator contains the different levels of possible checks of the
 * resources.
 *
 * @author Martin Karing
 * @version 1.00
 * @since 1.00
 */
public enum ResourceCheckLevel {
    /**
     * A more detailed check method. In addition to the simple check is only compares the last change time of the
     * file with the one stored in the database.
     */
    detailedCheck,

    /**
     * The full detailed check. Its the slowest one and in addition of the etailed check it also calculates and
     * compares the checksum of the file.
     */
    fullCheck,

    /**
     * The simplest check to perform. It only checks if the file exists and nothing beyond.
     */
    simpleCheck;
}

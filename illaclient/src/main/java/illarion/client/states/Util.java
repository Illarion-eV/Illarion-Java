/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.states;

import de.lessvoid.nifty.Nifty;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * This utility class is used to load the game state stuff.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class Util {
    /**
     * The logger that is used for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Util.class);

    /**
     * Load the XML file after validating its contents.
     *
     * @param nifty   the instance of Nifty the files are supposed to be applied to
     * @param xmlFile the XML file that is supposed to be load
     */
    public static void loadXML(@Nonnull final Nifty nifty, @Nonnull final String xmlFile) {
        try {
            nifty.validateXml(xmlFile);
        } catch (Exception e) {
            LOGGER.error("Validation of the XML file \"" + xmlFile + "\" failed.", e);
        }
        nifty.addXml(xmlFile);
    }
}

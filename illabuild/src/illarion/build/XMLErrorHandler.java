/*
 * This file is part of the Illarion Build Utility.
 * 
 * The Illarion Build Utility is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Build Utility. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLErrorHandler implements ErrorHandler {

    @Override
    public void error(final SAXParseException exception) throws SAXException {
        System.out.println("XML Error: " + exception.toString());
    }

    @Override
    public void fatalError(final SAXParseException exception)
        throws SAXException {
        System.out.println("XML Fatal Error: " + exception.toString());
    }

    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        System.out.println("XML Warning: " + exception.toString());
    }

}

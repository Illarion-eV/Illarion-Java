/*
 * This file is part of the Illarion Common Library.
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

/**
 * No resource exception is a runtime exception that is thrown in case the the
 * table loader fails at loading a resource.
 * 
 * @author Nop
 */
public final class NoResourceException extends RuntimeException {
    /**
     * The serialisation unique ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default constructor for this exception. It forwards the message of
     * the exception to the super class runtime exception.
     * 
     * @param message the exception message
     */
    public NoResourceException(final String message) {
        super(message);
    }
}

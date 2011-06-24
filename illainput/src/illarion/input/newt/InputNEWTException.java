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
package illarion.input.newt;

import illarion.input.InputException;

/**
 * A exception that is triggered in case the NEWT port causes any problem.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
final class InputNEWTException extends InputException {
    /**
     * The serialization UID of this JOGL exception
     */
    private static final long serialVersionUID = 1L;

    /**
     * The empty constructor used to throw such a exception without any
     * explanation.
     */
    public InputNEWTException() {
        super();
    }

    /**
     * Constructor with a message that should contain informations for the
     * reason for this exception.
     * 
     * @param message the reason for this exception
     */
    public InputNEWTException(final String message) {
        super(message);
    }

    /**
     * Constructor with a message that should contain informations for the
     * reason for this exception and a exception that caused this one.
     * 
     * @param message the message describing the reason for the exception
     * @param cause the cause for this exception
     */
    public InputNEWTException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a cause for this exception.
     * 
     * @param cause the cause for this exception
     */
    public InputNEWTException(final Throwable cause) {
        super(cause);
    }
}

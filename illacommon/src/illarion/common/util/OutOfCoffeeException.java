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

/**
 * This exception is thrown in case java stops working for any creepy reason
 * nobody understands anyway.
 * <p>
 * This exception has no real purpose, but its funny.
 * </p>
 * 
 * @author Martin Karing
 * @since 1.22
 */
public class OutOfCoffeeException extends RuntimeException {
    /**
     * The serialization unique ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default constructor for this exception. It forwards the message of
     * the exception to the super class runtime exception.
     */
    public OutOfCoffeeException() {
        super();
    }

    /**
     * The default constructor for this exception. It forwards the message of
     * the exception to the super class runtime exception.
     * 
     * @param message the exception message
     */
    public OutOfCoffeeException(final String message) {
        super(message);
    }

    /**
     * The default constructor for this exception. It forwards the message of
     * the exception to the super class runtime exception.
     * 
     * @param message the exception message
     * @param cause the reason for the exception
     */
    public OutOfCoffeeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The default constructor for this exception. It forwards the message of
     * the exception to the super class runtime exception.
     * 
     * @param cause the reason for the exception
     */
    public OutOfCoffeeException(final Throwable cause) {
        super(cause);
    }
}

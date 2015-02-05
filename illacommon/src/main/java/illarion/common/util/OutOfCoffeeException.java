/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.common.util;

/**
 * This exception is thrown in case java stops working for any creepy reason nobody understands anyway.
 * <p>
 * This exception has no real purpose, but its funny.
 * </p>
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("UnusedDeclaration")
public class OutOfCoffeeException extends RuntimeException {
    /**
     * The serialization unique ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default constructor for this exception. It forwards the message of the exception to the super class
     * runtime exception.
     */
    public OutOfCoffeeException() {
    }

    /**
     * The default constructor for this exception. It forwards the message of the exception to the super class
     * runtime exception.
     *
     * @param message the exception message
     */
    public OutOfCoffeeException(String message) {
        super(message);
    }

    /**
     * The default constructor for this exception. It forwards the message of the exception to the super class
     * runtime exception.
     *
     * @param message the exception message
     * @param cause the reason for the exception
     */
    public OutOfCoffeeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * The default constructor for this exception. It forwards the message of the exception to the super class
     * runtime exception.
     *
     * @param cause the reason for the exception
     */
    public OutOfCoffeeException(Throwable cause) {
        super(cause);
    }
}

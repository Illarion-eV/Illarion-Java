/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics;

/**
 * This class is used to define any exception that occurs during graphic
 * rendering actions.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public class GraphicsException extends RuntimeException {
    /**
     * The serialization UID of this exception.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty constructor that does not show any reason for this problem.
     */
    public GraphicsException() {
        super();
    }

    /**
     * Constructor that is able to hand over a readable message that describes
     * the reason for the crash.
     * 
     * @param message the message to show along with the exception
     */
    public GraphicsException(final String message) {
        super(message);
    }

    /**
     * This constructor is able to hand over a cause for this exception and a
     * message describing the problem.
     * 
     * @param message the message that describes the problem
     * @param cause the cause of the exception
     */
    public GraphicsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor that is able to hand over a reason for this exception.
     * 
     * @param cause the cause of this exception
     */
    public GraphicsException(final Throwable cause) {
        super(cause);
    }
}

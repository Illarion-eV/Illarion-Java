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
package illarion.graphics.jogl;

import javax.media.opengl.GLProfile;

import illarion.graphics.GraphicsException;

/**
 * A exception that is triggered in case the JOGL port causes any problem.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class GraphicsJOGLException extends GraphicsException {
    /**
     * The introduction for the profile name that caused the problem.
     */
    @SuppressWarnings("nls")
    private static final String PROFILE_INTRO = "Current profile: ";

    /**
     * The divider between the profile addition and the message.
     */
    @SuppressWarnings("nls")
    private static final String PROFILE_MSG_DIV = " - ";

    /**
     * The serialization UID of this JOGL exception
     */
    private static final long serialVersionUID = 1L;

    /**
     * The empty constructor used to throw such a exception without any
     * explanation.
     */
    public GraphicsJOGLException() {
        super();
    }

    /**
     * Constructor with the information about the OpenGL profile that caused the
     * problem.
     * 
     * @param profile the OpenGL profile that caused the problem
     */
    public GraphicsJOGLException(final GLProfile profile) {
        super(PROFILE_INTRO + profile.getImplName());
    }

    /**
     * Constructor with a profile that is the reason for this problem and a
     * exception that caused this exception.
     * 
     * @param profile the OpenGL profile that caused the problem
     * @param cause the cause for this exception
     */
    public GraphicsJOGLException(final GLProfile profile, final Throwable cause) {
        super(PROFILE_INTRO + profile.getImplName(), cause);
    }

    /**
     * Constructor with a message that should contain informations for the
     * reason for this exception.
     * 
     * @param message the reason for this exception
     */
    public GraphicsJOGLException(final String message) {
        super(message);
    }

    /**
     * Constructor with a message that should contain informations for the
     * reason for this exception and with the profile that caused the problem.
     * 
     * @param message the message describing the reason for the exception
     * @param profile the OpenGL profile that caused the problem
     */
    public GraphicsJOGLException(final String message, final GLProfile profile) {
        super(message + PROFILE_MSG_DIV + PROFILE_INTRO
            + profile.getImplName());
    }

    /**
     * Full Constructor with a message a profile and a cause for this exception.
     * 
     * @param message the message describing the reason for the exception
     * @param profile the OpenGL profile that caused the problem
     * @param cause the cause for this exception
     */
    public GraphicsJOGLException(final String message,
        final GLProfile profile, final Throwable cause) {
        super(message + PROFILE_MSG_DIV + PROFILE_INTRO
            + profile.getImplName(), cause);
    }

    /**
     * Constructor with a message that should contain informations for the
     * reason for this exception and a exception that caused this one.
     * 
     * @param message the message describing the reason for the exception
     * @param cause the cause for this exception
     */
    public GraphicsJOGLException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a cause for this exception.
     * 
     * @param cause the cause for this exception
     */
    public GraphicsJOGLException(final Throwable cause) {
        super(cause);
    }
}

/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.crash;

import javax.annotation.Nonnull;

/**
 * This is the default crash handler that is called in case anything crashes
 * that did not got a special crash handler. Since a call of this crash handler
 * means that its unknown what exactly crashed there is no way in restarting the
 * crashed part. So in this case the client will be shut down and a error
 * message displayed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DefaultCrashHandler extends AbstractCrashHandler {
    /**
     * The singleton instance of this crash handler to avoid to many instances
     * of this one.
     */
    private static final DefaultCrashHandler INSTANCE = new DefaultCrashHandler();

    /**
     * The private constructor that is used to avoid the creation of any other
     * instances but the singleton instance.
     */
    private DefaultCrashHandler() {
        super();
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static DefaultCrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Get the message that describes the problem human readable.
     *
     * @return the error message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    protected String getCrashMessage() {
        return "crash.default";
    }

    /**
     * Crash the client right away, since there is no specific thing to do here.
     *
     * @return <code>true</code> in case a restart of the connection is needed
     */
    @Override
    protected boolean restart() {
        super.crashClient();

        return false;
    }
}

/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.crash;

/**
 * This is the default crash handler that is called in case anything crashes
 * that did not got a special crash handler. Since a call of this crash handler
 * means that its unknown what exactly crashed there is no way in restarting the
 * crashed part. So in this case the editor will be shut down and a error
 * message displayed.
 * 
 * @author Martin Karing
 * @since 1.02
 * @version 1.02
 */
public final class DefaultCrashHandler extends AbstractCrashHandler {
    /**
     * The singleton instance of this crash handler to avoid to many instances
     * of this one.
     */
    private static final DefaultCrashHandler INSTANCE =
        new DefaultCrashHandler();

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
    public static DefaultCrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Get the message that describes the problem human readable.
     * 
     * @return the error message
     */
    @SuppressWarnings("nls")
    @Override
    protected String getCrashMessage() {
        return "crash.default";
    }

    /**
     * Crash the client right away, since there is no specific thing to do here.
     */
    @Override
    protected void restart() {
        super.crashEditor();
    }
}

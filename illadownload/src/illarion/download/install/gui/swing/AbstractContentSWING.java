/*
 * This file is part of the Illarion Download Utility.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Download Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Download Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Download Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.install.gui.swing;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * This class defines a class that is able to display content in the base gui.
 *
 * @author Martin Karing
 * @version 1.22
 * @since 1.22
 */
public abstract class AbstractContentSWING {
    /**
     * This lock is used to synchronize the access to the continue blocking
     * system.
     */
    private final Object continueLock = new Object();

    /**
     * This flag is turned true once its fine to go on with the installation.
     */
    private boolean readyToContinue = false;

    /**
     * Fill the button pane of the GUI.
     *
     * @param base        the base of the GUI
     * @param buttonPanel the panel that holds the buttons of the GUI and needs
     *                    to be filled
     */
    public abstract void fillButtons(BaseSWING base, JPanel buttonPanel);

    /**
     * Fill the content pane of the GUI.
     *
     * @param base         the base of the GUI
     * @param contentPanel the panel that is supposed to be filled
     */
    public abstract void fillContent(BaseSWING base, JPanel contentPanel);

    /**
     * This function once the content is ready to be displayed for final
     * operations.
     *
     * @param base the base of the GUI
     */
    public abstract void prepareDisplay(BaseSWING base);

    /**
     * This function is used to block the execution of what ever until its fine
     * by this installation step to go on.
     */
    public final void waitForContinue() {
        synchronized (continueLock) {
            while (!readyToContinue) {
                try {
                    continueLock.wait();
                } catch (@Nonnull final InterruptedException e) {
                    return;
                }
            }
        }
    }

    /**
     * This function is used to report that its now fine to go on with the
     * installation.
     */
    protected final void reportContinue() {
        synchronized (continueLock) {
            readyToContinue = true;
            continueLock.notify();
        }
    }
}

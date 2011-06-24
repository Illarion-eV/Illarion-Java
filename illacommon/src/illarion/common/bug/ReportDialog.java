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
package illarion.common.bug;

import illarion.common.util.MessageSource;

/**
 * This interface holds the general specifications of a crash report display.
 * The special implementations for AWT and SWING have this interface is common
 * so they can be controlled properly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
interface ReportDialog {
    /**
     * The result value in case the "send always" button was clicked. This means
     * the report is supposed to be send now and every time in the future
     * without asking the user again.
     */
    int SEND_ALWAYS = 1;

    /**
     * The result value in case the "Send never" button was clicked. This means
     * the report is supposed to be discarded now and every time in future
     * without asking the user again.
     */
    int SEND_NEVER = 3;

    /**
     * The result value in case the "Don't send" button was clicked. This means
     * the report is supposed to be discarded now and the next time the user
     * shall be asked again.
     */
    int SEND_NOT = 2;

    /**
     * Result value in case the "send once" button was clicked. This means the
     * the crash report is to be send now and the next time the user shall be
     * asked again.
     */
    int SEND_ONCE = 0;

    /**
     * Get the result of the dialog.
     * 
     * @return one of the possible result values
     */
    int getResult();

    /**
     * Set the data that was collected about the crash.
     * 
     * @param data the data about the crash
     */
    void setCrashData(CrashData data);

    /**
     * Set the source of the messages and texts that are displayed on the
     * dialog.
     * 
     * @param source the message source
     */
    void setMessageSource(MessageSource source);

    /**
     * Show the dialog. Calling this will block the current thread until the
     * dialog closes.
     */
    void showDialog();
}

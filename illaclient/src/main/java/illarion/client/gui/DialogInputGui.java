/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui;

/**
 * This interface defines the access to the GUI used to display input dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface DialogInputGui {
    /**
     * Show a input dialog on the GUI.
     *
     * @param dialogId  the ID of the input dialog
     * @param title     the title of the input dialog
     * @param message   the message that is displayed in the dialog
     * @param maxLength the amount of characters that are maximal allowed in this dialog
     * @param multiLine {@code true} in case the input of multiple lines should be allowed
     */
    void showInputDialog(int dialogId, String title, String message, int maxLength, boolean multiLine);
}

/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG.init;

import illarion.client.guiNG.GUI;
import illarion.client.guiNG.Journal;
import illarion.client.guiNG.elements.Widget;
import illarion.client.guiNG.elements.Window;

/**
 * This initialization script is used to register the journal properly in the
 * GUI so it can be used as it should.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class JournalInit implements WidgetInit {
    /**
     * The serialization UID of this initialization script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The journal that is initilizied with this function.
     */
    private final Journal journal;

    /**
     * The window of the journal that is initilizied with this function.
     */
    private final Window journalWindow;

    /**
     * Constructor for this initialization script that sets the instances that
     * are required for the initialization.
     * 
     * @param journ the journal that is initializied
     * @param window the window of the journal that is registered to the GUI
     */
    public JournalInit(final Journal journ, final Window window) {
        journal = journ;
        journalWindow = window;
    }

    /**
     * This function is called when initialization starts.
     */
    @Override
    public void initWidget(final Widget widget) {
        GUI.getInstance().registerJournal(journal, journalWindow);
    }

}

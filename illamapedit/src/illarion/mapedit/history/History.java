/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.history;

import javolution.util.FastList;

import illarion.mapedit.MapEditor;

/**
 * The history class stores the chain of all history entries that are connected
 * to this history and allows undoing and redoing this actions.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class History {
    /**
     * The singleton instance of the history class that is only used in case
     * there is a global history.
     */
    private static History instance = null;

    /**
     * The list of history entries that can be done again.
     */
    private final FastList<HistoryEntry> redoList;

    /**
     * The list of history entries that can be undone.
     */
    private final FastList<HistoryEntry> undoList;

    /**
     * Private constructor to prepare the history to work and not avoid any
     * instances created outside the {@link #getInstance()} method.
     */
    private History() {
        undoList = new FastList<HistoryEntry>();
        redoList = new FastList<HistoryEntry>();
    }

    /**
     * Get a instance of the history class. In case the configuration is set to
     * use a global history always the same history is returned. Else new
     * instances are created at every call.
     * 
     * @return the singleton instance of this class or a new instance, depending
     *         on the configuration settings
     */
    @SuppressWarnings("nls")
    public static History getInstance() {
        if (MapEditor.getConfig().getBoolean("globalHist")) {
            if (instance == null) {
                instance = new History();
            }
            return instance;
        }
        return new History();
    }

    /**
     * Add a entry to the history list. This causes that the redo list is
     * cleared and in case the undo list is getting too long the oldest entry is
     * removed.
     * 
     * @param entry the entry to add to this list
     */
    @SuppressWarnings("nls")
    public void addEntry(final HistoryEntry entry) {
        undoList.addLast(entry);
        redoList.clear();
        final int maxLength =
            MapEditor.getConfig().getInteger("historyLength");
        while (undoList.size() > maxLength) {
            undoList.removeFirst();
        }
        //MapEditor.getMainFrame().getMenubar().validateHistory();
    }

    /**
     * Check if this history contains events that can be done again.
     * 
     * @return <code>true</code> in case there are events that can be done again
     */
    public boolean canRedo() {
        return !redoList.isEmpty();
    }

    /**
     * Check if this history contains events that can be undone.
     * 
     * @return <code>true</code> in case there are events that can be undone
     */
    public boolean canUndo() {
        return !undoList.isEmpty();
    }

    /**
     * Do the last entry that was undone again.
     */
    public void redo() {
        if (canRedo()) {
            final HistoryEntry entry = redoList.removeLast();
            entry.redo();
            undoList.addLast(entry);
            //MapEditor.getMainFrame().getMenubar().validateHistory();
        }
    }

    /**
     * Undo the last entry that was added to this history.
     */
    public void undo() {
        if (canUndo()) {
            final HistoryEntry entry = undoList.removeLast();
            entry.undo();
            redoList.addLast(entry);
            //MapEditor.getMainFrame().getMenubar().validateHistory();
        }
    }
}

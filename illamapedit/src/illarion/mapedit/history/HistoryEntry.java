/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.history;

import javolution.util.FastList;

/**
 * One entry of the history. This entry contains multiple actions that can be
 * undone or redone at once.
 *
 * @author Martin Karing
 * @since 0.99
 */
public final class HistoryEntry {
    /**
     * The list of actions connected to this entry.
     */
    private final FastList<HistoryAction> actions;

    /**
     * Constructor to prepare the entry to work.
     */
    public HistoryEntry() {
        actions = new FastList<HistoryAction>();
    }

    /**
     * Add a action to the list of actions stored in this entry.
     *
     * @param act the action to add
     */
    public void addAction(final HistoryAction act) {
        actions.addLast(act);
    }

    /**
     * Check if this history entry contains any actions.
     *
     * @return {@code true} if this history entry contains actions
     */
    public boolean containsActions() {
        return !actions.isEmpty();
    }

    /**
     * Redo the actions stored in this entry.
     */
    public void redo() {
        for (final HistoryAction act : actions) {
            act.redo();
        }
    }

    /**
     * Undo the actions stored in this entry.
     */
    public void undo() {
        for (final HistoryAction act : actions) {
            act.undo();
        }
    }
}

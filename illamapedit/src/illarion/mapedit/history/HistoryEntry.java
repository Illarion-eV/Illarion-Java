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
    private final FastList<AbstractHistoryAction> actions;

    /**
     * Constructor to prepare the entry to work.
     */
    public HistoryEntry() {
        actions = new FastList<AbstractHistoryAction>();
    }

    /**
     * Add a action to the list of actions stored in this entry.
     * 
     * @param act the action to add
     */
    public void addAction(final AbstractHistoryAction act) {
        actions.addLast(act);
    }

    /**
     * Check if this history entry contains any actions.
     * 
     * @return <code>true</code> if this history entry contains actions
     */
    public boolean containsActions() {
        return !actions.isEmpty();
    }

    /**
     * Redo the actions stored in this entry in adding order.
     */
    public void redo() {
        FastList.Node<AbstractHistoryAction> currNode =
            actions.head().getNext();
        AbstractHistoryAction act = currNode.getValue();
        while (act != null) {
            act.redo();
            currNode = currNode.getNext();
            act = currNode.getValue();
        }
    }

    /**
     * Undo the actions stored in this entry in reverse adding order.
     */
    public void undo() {
        FastList.Node<AbstractHistoryAction> currNode =
            actions.tail().getPrevious();
        AbstractHistoryAction act = currNode.getValue();
        while (act != null) {
            act.undo();
            currNode = currNode.getPrevious();
            act = currNode.getValue();
        }
    }
}

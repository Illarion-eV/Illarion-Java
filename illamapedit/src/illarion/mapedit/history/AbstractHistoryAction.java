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

import gnu.trove.list.TLinkable;

/**
 * A history action is a action that can be part of a history entry. It contains
 * one change of the map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public abstract class AbstractHistoryAction implements
    TLinkable<AbstractHistoryAction> {
    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The next node in the linked list.
     */
    private AbstractHistoryAction nextListNode;

    /**
     * The previous node in the linked list.
     */
    private AbstractHistoryAction prevListNode;

    @Override
    public final AbstractHistoryAction getNext() {
        return nextListNode;
    }

    @Override
    public final AbstractHistoryAction getPrevious() {
        return prevListNode;
    }

    /**
     * Perform the action again.
     */
    public abstract void redo();

    @Override
    public final void setNext(final AbstractHistoryAction next) {
        nextListNode = next;
    }

    @Override
    public final void setPrevious(final AbstractHistoryAction prev) {
        prevListNode = prev;
    }

    /**
     * Undo the action.
     */
    public abstract void undo();
}

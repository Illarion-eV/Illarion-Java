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

import illarion.mapedit.events.HistoryEvent;
import illarion.mapedit.events.map.RepaintRequestEvent;
import javolution.util.FastList;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

/**
 * The HistoryManager class stores the chain of all history entries that are connected
 * to this history and allows undoing and redoing this actions.
 * TODO: Non-Singleton
 * TODO: USE IT!!
 *
 * @author Martin Karing, Tim
 * @since 0.99
 */
public class HistoryManager {
    private static final int MAX_HISTORY_LENGHT = 100;
    /**
     * The list of history entries that can be done again.
     */
    private final FastList<HistoryAction> redoList;

    /**
     * The list of history entries that can be undone.
     */
    private final FastList<HistoryAction> undoList;

    public HistoryManager() {
        AnnotationProcessor.process(this);
        undoList = new FastList<HistoryAction>();
        redoList = new FastList<HistoryAction>();
    }

    /**
     * Add a entry to the history list. This causes that the redo list is
     * cleared and in case the undo list is getting too long the oldest entry is
     * removed.
     *
     * @param entry the entry to add to this list
     */
    @SuppressWarnings("nls")
    public void addEntry(final HistoryAction entry) {
        undoList.addLast(entry);
        redoList.clear();
        while (undoList.size() > MAX_HISTORY_LENGHT) {
            undoList.removeFirst();
        }
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
            final HistoryAction entry = redoList.removeLast();
            entry.redo();
            undoList.addLast(entry);
        }
    }

    /**
     * Undo the last entry that was added to this history.
     */
    public void undo() {
        if (canUndo()) {
            final HistoryAction entry = undoList.removeLast();
            entry.undo();
            redoList.addLast(entry);
        }
    }

    @EventSubscriber
    public void onHistoryEvent(final HistoryEvent e) {
        if (e.isUndo()) {
            undo();
        } else {
            redo();
        }
        EventBus.publish(new RepaintRequestEvent());
    }
}

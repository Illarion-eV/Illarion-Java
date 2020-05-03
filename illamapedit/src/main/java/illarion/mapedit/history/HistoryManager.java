/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.mapedit.history;

import illarion.mapedit.events.HistoryEvent;
import illarion.mapedit.events.HistoryPasteCutEvent;
import illarion.mapedit.events.map.RepaintRequestEvent;
import javolution.util.FastTable;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Deque;

/**
 * The HistoryManager class stores the chain of all history entries that are connected
 * to this history and allows undoing and redoing this actions.
 *
 * @author Martin Karing, Tim
 * @since 0.99
 */
public class HistoryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryManager.class);
    private static final int MAX_HISTORY_LENGHT = 100;
    /**
     * The list of history entries that can be done again.
     */
    @Nonnull
    private final Deque<HistoryAction> redoList;

    /**
     * The list of history entries that can be undone.
     */
    @Nonnull
    private final Deque<HistoryAction> undoList;

    public HistoryManager() {
        AnnotationProcessor.process(this);
        undoList = new FastTable<>();
        redoList = new FastTable<>();
    }

    /**
     * Add a entry to the history list. This causes that the redo list is
     * cleared and in case the undo list is getting too long the oldest entry is
     * removed.
     *
     * @param entry the entry to add to this list
     */
    public void addEntry(HistoryAction entry) {
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
            HistoryAction entry = redoList.removeLast();
            entry.redo();
            undoList.addLast(entry);
        }
    }

    /**
     * Undo the last entry that was added to this history.
     */
    public void undo() {
        if (canUndo()) {
            HistoryAction entry = undoList.removeLast();
            entry.undo();
            redoList.addLast(entry);
        }
    }

    @EventSubscriber
    public void onHistoryEvent(@Nonnull HistoryEvent e) {
        if (e.isUndo()) {
            undo();
        } else {
            redo();
        }
        EventBus.publish(new RepaintRequestEvent());
    }

    @EventSubscriber
    public void onHistoryCutPasteEvent(@Nonnull HistoryPasteCutEvent e) {
        addEntry(e.getAction());
    }
}

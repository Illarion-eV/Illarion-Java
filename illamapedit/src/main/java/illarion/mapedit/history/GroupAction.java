/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import javolution.util.FastTable;

import javax.annotation.Nonnull;

/**
 * @author Tim
 */
public class GroupAction extends HistoryAction {
    @Nonnull
    private final FastTable<HistoryAction> actions;

    public GroupAction() {
        super(null);
        actions = new FastTable<HistoryAction>();
    }

    public void addAction(final HistoryAction action) {
        actions.add(action);
    }

    @Override
    public void redo() {
        for (HistoryAction a : actions) {
            a.redo();
        }
    }

    @Override
    public void undo() {
        for (HistoryAction a : actions) {
            a.undo();
        }
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }
}

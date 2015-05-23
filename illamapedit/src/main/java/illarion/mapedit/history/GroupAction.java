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

import javolution.util.FastTable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Tim
 */
public class GroupAction extends HistoryAction {
    @Nonnull
    private final List<HistoryAction> actions;

    public GroupAction() {
        super(null);
        actions = new FastTable<>();
    }

    public void addAction(HistoryAction action) {
        actions.add(action);
    }

    @Override
    public void redo() {
        actions.forEach(HistoryAction::redo);
    }

    @Override
    public void undo() {
        actions.forEach(HistoryAction::undo);
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }
}

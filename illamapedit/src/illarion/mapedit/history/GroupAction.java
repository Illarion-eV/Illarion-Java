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
 * @author Tim
 */
public class GroupAction extends HistoryAction {
    private final FastList<HistoryAction> actions;

    public GroupAction() {
        super(null);
        actions = new FastList<HistoryAction>();
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
}

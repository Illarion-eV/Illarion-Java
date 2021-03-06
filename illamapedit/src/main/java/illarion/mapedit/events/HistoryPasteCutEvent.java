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
package illarion.mapedit.events;

import illarion.mapedit.history.GroupAction;
import illarion.mapedit.history.HistoryAction;

/**
 * @author Fredrik K
 */
public class HistoryPasteCutEvent {
    private final HistoryAction action;

    public HistoryPasteCutEvent(GroupAction action) {
        this.action = action;
    }

    public HistoryAction getAction() {
        return action;
    }
}

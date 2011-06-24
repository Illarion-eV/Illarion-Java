/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.util.LinkedList;

/**
 * This class is able to save stoppable objects and stop them all at once.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class StoppableStorage {
    /**
     * The singleton instance of StoppableStorage.
     */
    private static final StoppableStorage INSTANCE = new StoppableStorage();

    /**
     * The list of stoppable instances stored in this class.
     */
    private final LinkedList<Stoppable> list = new LinkedList<Stoppable>();

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static StoppableStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Add a new stoppable to the list in this class. When calling shutdown this
     * class will be stopped as well.
     * 
     * @param newStoppable the new stoppable instance that will be killed if
     *            requested
     */
    public void add(final Stoppable newStoppable) {
        list.add(newStoppable);
    }

    /**
     * Remove a class from this handler. This does just remove the last and not
     * stop it.
     * 
     * @param newStoppable the class to remove from this handler
     */
    public void remove(final Stoppable newStoppable) {
        if (list.contains(newStoppable)) {
            list.remove(newStoppable);
        }
    }

    /**
     * Shut down all until now saved instances.
     */
    public void shutdown() {
        while (!list.isEmpty()) {
            list.removeLast().saveShutdown();
        }
    }
}

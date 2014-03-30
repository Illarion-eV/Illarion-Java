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
package illarion.common.util;

import javax.annotation.Nonnull;
import java.util.LinkedList;

/**
 * This class is able to save stoppable objects and stop them all at once.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class StoppableStorage {
    /**
     * The singleton instance of StoppableStorage.
     */
    private static final StoppableStorage INSTANCE = new StoppableStorage();

    /**
     * The list of stoppable instances stored in this class.
     */
    private final LinkedList<Stoppable> list = new LinkedList<>();

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static StoppableStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Add a new stoppable to the list in this class. When calling shutdown this
     * class will be stopped as well.
     *
     * @param newStoppable the new stoppable instance that will be killed if
     * requested
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

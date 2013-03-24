/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.backend.shared;

import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.SceneElement;
import org.illarion.engine.graphic.SceneEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This is the abstract implementation of a scene that takes care for the sorting and storing of the scene elements
 * as this is the same for all the implementations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractScene implements Scene {
    /**
     * This sorted set contains the elements of the scene in their natural order.
     */
    @Nonnull
    private final TreeSet<SceneElement> sceneElements;

    /**
     * This is the queue of events that are published during the updates.
     */
    @Nonnull
    private final Queue<SceneEvent> eventQueue;

    /**
     * Create a new scene and setup the internal structures.
     */
    protected AbstractScene() {
        sceneElements = new TreeSet<SceneElement>(new Comparator<SceneElement>() {
            @Override
            public int compare(final SceneElement o1, final SceneElement o2) {
                return o2.getOrder() - o1.getOrder();
            }
        });
        eventQueue = new ConcurrentLinkedDeque<SceneEvent>();
    }

    @Override
    public final void addElement(@Nonnull final SceneElement element) {
        sceneElements.add(element);
    }

    @Override
    public final void updateElementLocation(@Nonnull final SceneElement element) {
        sceneElements.remove(element);
        sceneElements.add(element);
    }

    @Override
    public final void removeElement(@Nonnull final SceneElement element) {
        sceneElements.remove(element);
    }

    /**
     * This function performs the actual calling of the update functions for all scene elements.
     *
     * @param container the game container that is forwarded to the scene elements
     * @param delta     the time since the last update that is reported to the elements
     */
    protected final void updateScene(@Nonnull final GameContainer container, final int delta) {
        for (@Nonnull final SceneElement element : sceneElements) {
            element.update(container, delta);
        }
    }

    /**
     * This function performs the actual render operation for all elements of the scene.
     *
     * @param graphics the graphics instance that is used to render the game
     */
    protected final void renderScene(@Nonnull final Graphics graphics) {
        for (@Nonnull final SceneElement element : sceneElements) {
            element.render(graphics);
        }
    }

    /**
     * This function processes all the events that were queued up until now.
     *
     * @param container the game container that is reported to the scene elements
     * @param delta     the time since the last update that is reported to the scene elements
     */
    protected final void processEvents(@Nonnull final GameContainer container, final int delta) {
        @Nullable SceneEvent event = eventQueue.poll();
        while (event != null) {
            @Nonnull final Iterator<SceneElement> itr = sceneElements.descendingIterator();
            while (itr.hasNext()) {
                if (itr.next().isEventProcessed(container, delta, event)) {
                    break;
                }
            }
            event = eventQueue.poll();
        }
    }

    @Override
    public final void publishEvent(@Nonnull final SceneEvent event) {
        eventQueue.offer(event);
    }
}

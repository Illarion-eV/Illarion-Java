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
import org.illarion.engine.graphic.effects.SceneEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This is the abstract implementation of a scene that takes care for the sorting and storing of the scene elements
 * as this is the same for all the implementations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractScene<T extends SceneEffect> implements Scene {
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
     * The list of effects applied to this scene.
     */
    @Nonnull
    private final List<T> sceneEffects;

    /**
     * This is the snapshot array that is taken and filled shortly before the update calls. Is then used to render
     * and update the scene.
     */
    @Nonnull
    private SceneElement[] workingArray = new SceneElement[0];

    private int workingArraySize;

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
        sceneEffects = new ArrayList<T>();
    }

    @Override
    public final void addElement(@Nonnull final SceneElement element) {
        synchronized (sceneElements) {
            sceneElements.add(element);
        }
    }

    @Override
    public final void updateElementLocation(@Nonnull final SceneElement element) {
        synchronized (sceneElements) {
            sceneElements.remove(element);
            sceneElements.add(element);
        }
    }

    @Override
    public final void removeElement(@Nonnull final SceneElement element) {
        synchronized (sceneElements) {
            sceneElements.remove(element);
        }
    }

    /**
     * This function performs the actual calling of the update functions for all scene elements.
     *
     * @param container the game container that is forwarded to the scene elements
     * @param delta     the time since the last update that is reported to the elements
     */
    protected final void updateScene(@Nonnull final GameContainer container, final int delta) {
        Arrays.fill(workingArray, null);
        synchronized (sceneElements) {
            workingArray = sceneElements.toArray(workingArray);
            workingArraySize = sceneElements.size();
        }

        @Nullable SceneEvent event = eventQueue.poll();
        while (event != null) {
            for (int i = workingArraySize - 1; i >= 0; i--) {
                final SceneElement element = workingArray[i];
                if (element.isEventProcessed(container, delta, event)) {
                    break;
                }
            }
            event = eventQueue.poll();
        }

        for (int i = 0; i < workingArraySize; i++) {
            final SceneElement element = workingArray[i];
            element.update(container, delta);
        }
    }

    /**
     * This function performs the actual render operation for all elements of the scene.
     *
     * @param graphics the graphics instance that is used to render the game
     */
    protected final void renderScene(@Nonnull final Graphics graphics) {
        for (int i = 0; i < workingArraySize; i++) {
            final SceneElement element = workingArray[i];
            element.render(graphics);
        }
    }

    @Override
    public final void publishEvent(@Nonnull final SceneEvent event) {
        eventQueue.offer(event);
    }

    @Override
    public void addEffect(@Nonnull final SceneEffect effect) {
        try {
            @SuppressWarnings("unchecked") final T sceneEffect = (T) effect;
            if (!sceneEffects.contains(sceneEffect)) {
                sceneEffects.add(sceneEffect);
            }
        } catch (@Nonnull final ClassCastException e) {
            // illegal type
        }
    }

    @Override
    public void removeEffect(@Nonnull final SceneEffect effect) {
        try {
            @SuppressWarnings("unchecked") final T sceneEffect = (T) effect;
            sceneEffects.remove(sceneEffect);
        } catch (@Nonnull final ClassCastException e) {
            // illegal type
        }
    }

    @Override
    public void clearEffects() {
        sceneEffects.clear();
    }

    /**
     * Get a scene effect applied to a specific image.
     *
     * @param index the index of the effect
     * @return the scene effect
     */
    protected final T getEffect(final int index) {
        return sceneEffects.get(index);
    }

    /**
     * Get the amount of scene effects allowed.
     *
     * @return the scene effects
     */
    protected final int getEffectCount() {
        return sceneEffects.size();
    }
}

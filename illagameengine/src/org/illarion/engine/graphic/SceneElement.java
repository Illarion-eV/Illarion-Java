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
package org.illarion.engine.graphic;

import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;

/**
 * This is one element that is displayed on a graphics scene.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface SceneElement {
    /**
     * This value is used to sort the elements in the scene and used to determine the order its rendered in.
     *
     * @return the order value of this element
     */
    int getOrder();

    /**
     * This function is called to render the element.
     *
     * @param graphics the graphics instance that is supposed to be used to render the element
     */
    void render(@Nonnull Graphics graphics);

    /**
     * This function is called when the element is supposed to perform a update.
     *
     * @param container the game container this scene is assigned to
     * @param delta     the time since the last update
     */
    void update(@Nonnull GameContainer container, int delta);

    /**
     * This function is called for events that are send to the scene.
     *
     * @param container the game container this scene is assigned to
     * @param delta     the time since the last update
     * @param event     the event
     * @return {@code true} in case this element handled the event
     */
    boolean isEventProcessed(@Nonnull GameContainer container, int delta, @Nonnull SceneEvent event);
}

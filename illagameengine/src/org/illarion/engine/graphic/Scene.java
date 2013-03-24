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
 * This class represents a 2D scene that is rendered to the screen. This should be used to render the games graphics.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Scene {
    /**
     * Add a element to this scene.
     *
     * @param element the element to add
     */
    void addElement(@Nonnull SceneElement element);

    /**
     * This needs to be called for a existing element of the scene. This function has to ensure that the render order
     * for this element is correct. This function should be called in case the element changes its location in the
     * scene.
     *
     * @param element the element to check
     */
    void updateElementLocation(@Nonnull SceneElement element);

    /**
     * Remove a element from the scene.
     *
     * @param element the element to remove
     */
    void removeElement(@Nonnull SceneElement element);

    /**
     * Update the scene. This will call the {@link SceneElement#update(GameContainer, int)}
     * function of all the elements of the scene.
     *
     * @param container the container of the game
     * @param delta     the time since the last update
     */
    void update(@Nonnull GameContainer container, int delta);

    /**
     * This function is called to render the scene. It does so by calling the {@link SceneElement#render(Graphics)}
     * function.
     *
     * @param graphics the graphics instance that is supposed to be used to render the element
     * @param offsetX  the x coordinate of the offset that is applied to all rendered elements
     * @param offsetY  the y coordinate of the offset that is applied to all rendered elements
     */
    void render(@Nonnull Graphics graphics, int offsetX, int offsetY);

    /**
     * This function publishes events to the scene. The actual publishing is done during the call of the
     * {@link #update(GameContainer, int)} function. This method is thread save.
     *
     * @param event the event to publish
     */
    void publishEvent(@Nonnull SceneEvent event);
}

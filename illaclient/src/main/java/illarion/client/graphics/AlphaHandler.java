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
package illarion.client.graphics;

/**
 * Interface for components that have fade out and hiding abilities on the game
 * map.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface AlphaHandler {
    /**
     * Add a alpha change listener to the class that reports that receives any
     * changes to the alpha value of this entity.
     *
     * @param listener the listener that receives the data
     */
    void addAlphaChangeListener(AlphaChangeListener listener);

    /**
     * Get the currently set alpha value.
     *
     * @return the current alpha value of this component
     */
    int getAlpha();

    /**
     * Set the alpha value of this component to a new value
     *
     * @param newAlpha the new value of the alpha of this component
     */
    void setAlpha(int newAlpha);

    /**
     * Set the alpha target for a fading effect. At every update the color will
     * approach this alpha target value.
     *
     * @param newAlpha the new alpha target for a fading effect
     */
    void setAlphaTarget(int newAlpha);
}

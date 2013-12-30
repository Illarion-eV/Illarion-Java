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
package org.illarion.engine.graphic.effects;

/**
 * This is the scene effect that shows the fog over the scene.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface FogEffect extends SceneEffect {
    /**
     * Set the density of the fog.
     *
     * @param density the fog density, values are capped from {@code 0.f} to {@code 1.f}
     */
    void setDensity(float density);
}

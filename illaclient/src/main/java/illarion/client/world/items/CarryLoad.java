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
package illarion.client.world.items;

/**
 * This class stores and maintains the current carry load and provides some methods to easily handle the load values.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CarryLoad {
    private int maximumLoad;
    private int currentLoad;

    /**
     * Update the current and the maximum load values. This function is supposed to be triggered by the server in
     * case the load values change due to item movement or attribute change.
     *
     * @param current the new current load value
     * @param maximum the maximum load value
     */
    public void updateLoad(int current, int maximum) {
        maximumLoad = maximum;
        currentLoad = current;
    }

    public double getLoadFactor() {
        if (maximumLoad == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return currentLoad / (double) maximumLoad;
    }

    public boolean isRunningPossible() {
        return getLoadFactor() <= 0.75;
    }

    public boolean isWalkingPossible() {
        return currentLoad <= maximumLoad;
    }
}

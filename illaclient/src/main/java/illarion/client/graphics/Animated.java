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
 * Interface for a general animation target for the animation handler.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface Animated {
    /**
     * This function is called once the animation is started.
     */
    void animationStarted();

    /**
     * This function is called to report that the animation is finished or not.
     *
     * @param finished true in case the animation finished, false if not
     */
    void animationFinished(boolean finished);
}

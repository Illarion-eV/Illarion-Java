/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

/**
 * Interface for movement based animation targets.
 * 
 * @author Nop
 * @since 0.95
 */
public interface AnimatedMove extends Animated {
    /**
     * Update the position for a move animation.
     * 
     * @param posX the x coordinate of the new movement position
     * @param posY the y coordinate of the new movement position
     * @param posZ the z coordinate of the new movement position
     */
    void setPosition(int posX, int posY, int posZ);
}

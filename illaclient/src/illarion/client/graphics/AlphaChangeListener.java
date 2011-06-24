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
 * This interface holds the listener that is notified in case the alpha value of
 * a interface changes.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface AlphaChangeListener {
    /**
     * This function is called to set the change of a alpha value.
     * 
     * @param from the old value of the alpha
     * @param to the new value of the alpha
     */
    void alphaChanged(int from, int to);
}

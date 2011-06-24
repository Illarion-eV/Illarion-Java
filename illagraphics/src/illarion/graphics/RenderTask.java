/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics;

/**
 * A task that is executed during the render actions.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface RenderTask {
    /**
     * Perform the render action of this task.
     * 
     * @param delta the time in milliseconds since the last render action
     * @return <code>true</code> in case the task shall be rendered again in the
     *         next loop, false if its supposed to be removed
     */
    boolean render(final int delta);
}

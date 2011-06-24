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
package illarion.graphics.jogl;

import illarion.graphics.RenderTask;
import illarion.graphics.generic.AbstractRenderManager;

/**
 * This render manager is supposed to ensure that all render tasks are rendered
 * correctly. This one holds the lock on the main render loop.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public class RenderManagerJOGL extends AbstractRenderManager {
    /**
     * The frames rendered since the last measure.
     */
    private int countFrames = 0;

    /**
     * The time counted since the last frames per second were calculated.
     */
    private int countTime = 0;

    /**
     * The time of the last call of the render function.
     */
    private long lastCall = System.currentTimeMillis();

    /**
     * The last archived frames per second.
     */
    private int lastFPS = 60;

    /**
     * Constructor that setups the render thread correctly.
     */
    public RenderManagerJOGL() {
        super();
    }

    /**
     * Add a task to the list of tasks that are rendered at each loop.
     * 
     * @param task the task to add
     */
    @Override
    public void addTask(final RenderTask task) {
        if (!isRenderStarted()) {
            task.render(0);
        } else {
            super.addTask(task);
        }
    }

    /**
     * Draw all graphics
     */
    public void draw() {
        final long thisCall = System.currentTimeMillis();
        final int delta = (int) (thisCall - lastCall);
        lastCall = thisCall;
        countTime += delta;
        countFrames++;
        if (countTime > 1000) {
            countTime = countTime % 1000;
            lastFPS = countFrames;
            countFrames = 0;
        }

        draw(delta);
    }

    /**
     * Get the frames per second count archived by the render loop.
     * 
     * @return the reached frames per second
     */
    @Override
    public int getRealFPS() {
        return lastFPS;
    }
}

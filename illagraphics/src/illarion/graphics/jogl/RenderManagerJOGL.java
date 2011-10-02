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

import java.util.List;

import javolution.util.FastTable;

import org.apache.log4j.Logger;

import illarion.graphics.Graphics;
import illarion.graphics.RenderManager;
import illarion.graphics.RenderTask;

/**
 * This render manager is supposed to ensure that all render tasks are rendered
 * correctly. This one holds the lock on the main render loop.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class RenderManagerJOGL implements RenderManager {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(RenderManagerJOGL.class);

    /**
     * Flag to determine if the regular render cycle was started already.
     */
    private boolean renderStarted = false;

    /**
     * The list of tasks that need to be updated at every run.
     */
    private final List<RenderTask> taskList;

    /**
     * Constructor that setups the render thread correctly.
     */
    public RenderManagerJOGL() {
        taskList = new FastTable<RenderTask>();
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
            synchronized (taskList) {
                taskList.add(task);
            }
        }
    }

    /**
     * Draw all graphics.
     * 
     * @param delta the time since the last render operation
     */
    @SuppressWarnings("nls")
    private void draw(final int delta) {
        this.delta = delta;
        
        try {
            synchronized (taskList) {
                int count = taskList.size();
                int curr = 0;
                while (curr < count) {
                    if (!taskList.get(curr).render(delta)) {
                        taskList.remove(curr);
                        --count;
                    } else {
                        ++curr;
                    }
                }
            }
        } catch (final NullPointerException ex) {
            LOGGER.warn("Render Thread catched NullPointerException");
            LOGGER.debug("Exception:", ex);
        }

        SpriteJOGL.resetDrawCount();
    }

    /**
     * Check if the rendering got started already.
     * 
     * @return <code>true</code> in case it was reported that the rendering
     *         started
     */
    private boolean isRenderStarted() {
        return renderStarted;
    }

    /**
     * Set that the rendering started. After this the RenderTasks will be
     * executed during the regular render cycles.
     */
    public void renderStarted() {
        renderStarted = true;
    }
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
     * The current delta value.
     */
    private int delta;

    /**
     * Get the frames per second count archived by the render loop.
     * 
     * @return the reached frames per second
     */
    @Override
    public int getRealFPS() {
        return lastFPS;
    }

    @Override
    public int getCurrentDelta() {
        return delta;
    }
}

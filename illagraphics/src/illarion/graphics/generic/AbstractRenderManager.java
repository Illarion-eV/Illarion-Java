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
package illarion.graphics.generic;

import java.util.List;

import javolution.util.FastTable;

import org.apache.log4j.Logger;

import illarion.graphics.RenderManager;
import illarion.graphics.RenderTask;

/**
 * Generic render manager implementation that implements the parts of the render
 * manager that is shared by all library specific implementations.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public abstract class AbstractRenderManager implements RenderManager {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(AbstractRenderManager.class);

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
    protected AbstractRenderManager() {
        taskList = new FastTable<RenderTask>();
    }

    /**
     * Add a task to the list of tasks that are rendered at each loop.
     * 
     * @param task the task to add
     */
    @Override
    public void addTask(final RenderTask task) {
        synchronized (taskList) {
            taskList.add(task);
        }
    }

    /**
     * Set that the rendering started. After this the RenderTasks will be
     * executed during the regular render cycles.
     */
    public final void renderStarted() {
        renderStarted = true;
    }

    /**
     * Draw all graphics.
     * 
     * @param delta the time since the last render operation
     */
    @SuppressWarnings("nls")
    protected final void draw(final int delta) {
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

        AbstractSprite.resetDrawCount();
    }

    /**
     * Check if the rendering got started already.
     * 
     * @return <code>true</code> in case it was reported that the rendering
     *         started
     */
    protected final boolean isRenderStarted() {
        return renderStarted;
    }
}

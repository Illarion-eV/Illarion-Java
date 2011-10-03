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
package illarion.graphics.lwjgl;

import java.util.List;

import org.apache.log4j.Logger;

import javolution.util.FastTable;

import illarion.common.util.Stoppable;
import illarion.common.util.StoppableStorage;
import illarion.graphics.Graphics;
import illarion.graphics.RenderManager;
import illarion.graphics.RenderTask;
import illarion.graphics.jogl.RenderManagerJOGL;
import illarion.graphics.jogl.SpriteJOGL;

/**
 * This render manager is supposed to ensure that all render tasks are rendered
 * correctly. This one holds the lock on the main render loop.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public class RenderManagerLWJGL  implements RenderManager, 
    Runnable, Stoppable {
    /**
     * The render display the render manager updates.
     */
    private RenderDisplayLWJGL display;

    /**
     * The last recorded time call of the drawing method.
     */
    private long lastCall = System.currentTimeMillis();

    /**
     * This variable holds the reference to the thread that runs the render
     * loop.
     */
    private Thread renderThread;

    /**
     * Tag to check of the thread is running correctly.
     */
    private boolean running;

    /**
     * The frames per seconds value that was set last time.
     */
    private int setFPS = -2;

    /**
     * The timer that is used to set the looping time of the render loop
     * correctly.
     */
    private final TimerLWJGL timer;
    
    /**
     * The list of render tasks that need to be handled by this render manager.
     */
    private final List<RenderTask> taskList;

    /**
     * Constructor that setups the render thread correctly.
     */
    public RenderManagerLWJGL() {
        super();
        running = false;
        timer = new TimerLWJGL();
        taskList = new FastTable<RenderTask>();
    }

    /**
     * Add a task to the list of tasks that are rendered at each loop.
     * 
     * @param task the task to add
     */
    @Override
    public void addTask(final RenderTask task) {
        taskList.add(task);
        if (!isRenderStarted()) {
            Graphics.getInstance().getRenderDisplay().getRenderArea()
                .repaint();
        }
    }
    
    /**
     * Flag to determine if the regular render cycle was started already.
     */
    private boolean renderStarted = false;

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
     * Bind a display to the render manager that is updated at teach render
     * loop.
     * 
     * @param boundDisplay the render display that is from now on bound to the
     *            render manager
     */
    public void bind(final RenderDisplayLWJGL boundDisplay) {
        display = boundDisplay;
    }

    /**
     * Draw all graphics.
     */
    private void draw() {
        final long thisCall = System.currentTimeMillis();
        final int delta = (int) (thisCall - lastCall);
        lastCall = thisCall;

        draw(delta);

        if (display != null) {
            display.update();
        }
    }
    
    /**
     * The current delta value.
     */
    private int delta;
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(RenderManagerLWJGL.class);

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

        SpriteLWJGL.resetDrawCount();
    }

    /**
     * Get the frames per second the main loop is able to archive.
     * 
     * @return the frames per second the main loop manages to display
     */
    @Override
    public int getRealFPS() {
        return timer.getFrameRate();
    }

    /**
     * Manual painting function. This function triggers the drawing only in case
     * the frames per second are set to -1. Only in this case the updates will
     * run correctly.
     */
    public void paint() {
        if (setFPS == -1) {
            draw();
        }
    }

    /**
     * Main loop of the thread that actually triggers the render actions of the
     * different tasks.
     */
    @Override
    public void run() {
        while (running) {
            draw();

            timer.sync();
            timer.update();
        }
        if (display != null) {
            display.stopRendering();
        }
    }

    /**
     * Stop the execution of this thread safely at the next loop.
     */
    @Override
    public void saveShutdown() {
        running = false;
    }

    /**
     * Set the target Frames per Second of the render loop.
     * 
     * @param newFPS target frames per second of the render loop
     */
    @SuppressWarnings("nls")
    public void setTargetFPS(final int newFPS) {
        if (newFPS == setFPS) {
            return;
        }
        if ((newFPS == -1) && (setFPS != -2)) {
            throw new IllegalArgumentException(
                "Manual Trigger mode can only be set initial");
        }
        renderStarted();
        setFPS = newFPS;
        if (newFPS == -1) {
            return;
        }
        timer.setFPS(newFPS);
        start();
    }

    /**
     * Start the thread and set the internal constants correctly to keep it
     * running.
     */
    public synchronized void start() {
        if ((renderThread == null) || !renderThread.isAlive()) {
            running = true;
            StoppableStorage.getInstance().add(this);
            renderThread = new Thread(this, "Render Thread"); //$NON-NLS-1$
            renderThread.setDaemon(true);
            renderThread.start();
        }
    }

    @Override
    public int getCurrentDelta() {
        return delta;
    }
}

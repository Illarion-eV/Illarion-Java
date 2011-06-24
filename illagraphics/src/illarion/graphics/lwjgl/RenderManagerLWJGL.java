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

import illarion.common.util.Stoppable;
import illarion.common.util.StoppableStorage;

import illarion.graphics.Graphics;
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
public class RenderManagerLWJGL extends AbstractRenderManager implements
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
     * Constructor that setups the render thread correctly.
     */
    public RenderManagerLWJGL() {
        super();
        running = false;
        timer = new TimerLWJGL();
    }

    /**
     * Add a task to the list of tasks that are rendered at each loop.
     * 
     * @param task the task to add
     */
    @Override
    public void addTask(final RenderTask task) {
        super.addTask(task);
        if (!isRenderStarted()) {
            Graphics.getInstance().getRenderDisplay().getRenderArea()
                .repaint();
        }
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

    /**
     * Draw all graphics.
     */
    private void draw() {
        final long thisCall = System.currentTimeMillis();
        final int delta = (int) (thisCall - lastCall);
        lastCall = thisCall;

        super.draw(delta);

        if (display != null) {
            display.update();
        }
    }
}

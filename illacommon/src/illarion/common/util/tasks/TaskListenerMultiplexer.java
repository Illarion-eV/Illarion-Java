/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util.tasks;

/**
 * This class is used to allow multiple task listeners to be used at one task.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class TaskListenerMultiplexer implements TaskListener {
    /**
     * The first listener that is called by this multiplexer.
     */
    private final TaskListener listener1;

    /**
     * The second listener that is called by this multiplexer.
     */
    private final TaskListener listener2;
    
    /**
     * Create a new multiplexer and sign the two listeners that are stored in
     * this multiplexer.
     * 
     * @param l1 the first listener
     * @param l2 the second listener
     */
    public TaskListenerMultiplexer(final TaskListener l1, final TaskListener l2) {
        listener1 = l1;
        listener2 = l2;
    }

    /**
     * {@inheritDoc}
     * This function will return <code>true</code> in case even one of
     * the listeners returns <code>true</code>.
     */
    @Override
    public boolean cancelTask() {
        return (listener1.cancelTask() | listener2.cancelTask());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void taskCanceled() {
        listener1.taskCanceled();
        listener2.taskCanceled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void taskFinished() {
        listener1.taskFinished();
        listener2.taskFinished();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void taskProgress(final int done, final int total) {
        listener1.taskProgress(done, total);
        listener2.taskProgress(done, total);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void taskStarted() {
        listener1.taskStarted();
        listener2.taskStarted();
    }
}

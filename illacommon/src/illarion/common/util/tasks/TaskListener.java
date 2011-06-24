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
 * This is the listener to the task processing system. This task allows to
 * report updates upon the different tasks that are handled by the task
 * processing system.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface TaskListener {
    /**
     * This is a feedback function that allows to interrupt the execution of the
     * task.
     * 
     * @return <code>true</code> to cancel this task
     */
    boolean cancelTask();

    /**
     * This function is called once the processing of the task is canceled.
     * Either by a error or by this listener using the {@link #cancelTask()}
     * function.
     */
    void taskCanceled();

    /**
     * This function is called once the processing of the task comes to a
     * successful end.
     */
    void taskFinished();

    /**
     * This function is called to update the progress of the task.
     * 
     * @param done the amount of task steps done already
     * @param total the total amount of steps this task has to do
     */
    void taskProgress(int done, int total);

    /**
     * This function is called once the processing of a task starts.
     */
    void taskStarted();
}

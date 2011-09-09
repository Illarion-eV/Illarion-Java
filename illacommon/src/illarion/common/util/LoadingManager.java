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
package illarion.common.util;

/**
 * This manager is used to keep track of the loading sequence of the client.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class LoadingManager {
    /**
     * This interface is used to register the monitor that is informed about
     * changes of the loading state.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    public interface LoadingMonitor {
        /**
         * This function is called upon changes of the state of the update.
         */
        void updateState(float state);
    }
    
    /**
     * The internal singleton instance of the loading manager.
     */
    private static final LoadingManager INSTANCE = new LoadingManager();
    
    /**
     * Private constructor to ensure that no other part of the application creates a instance of this class.
     */
    private LoadingManager() {
        currentCount = 0;
        totalCount = 1;
    }
    
    /**
     * Get the singleton instance of that class.
     * 
     * @return the singleton instance
     */
    public static final LoadingManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * The monitor that is informed about changes of the loading state.
     */
    private LoadingMonitor monitor;
    
    /**
     * The total amount of states the progress is split to.
     */
    private int totalCount;
    
    /**
     * The current progress.
     */
    private volatile int currentCount;
    
    /**
     * Set the monitor that is informed about the progress of the updates from
     * now on.
     * 
     * @param loadingMonitor the monitor that is informed from now on
     */
    public void setMonitor(final LoadingMonitor loadingMonitor) {
        monitor = loadingMonitor;
        sendState();
    }
    
    /**
     * Send the current state to the monitor.
     */
    private void sendState() {
        if (monitor == null) {
            return;
        }
        if (currentCount >= totalCount) {
            currentCount = totalCount;
        }
        monitor.updateState(((float) currentCount) / ((float) totalCount));
    }
    
    /**
     * Set the total count.
     * 
     * @param count the total count
     */
    public void setTotalCount(final int count) {
        totalCount = count;
        sendState();
    }
    
    /**
     * Set the new current count.
     * 
     * @param count the current count
     */
    private void setCurrentCount(final int count) {
        currentCount = count;
        sendState();
    }
    
    /**
     * Increase the current count by one.
     */
    public void increaseCurrentCount() {
        if (currentCount + 1 >= totalCount) {
            totalCount = currentCount + 2;
        }
        setCurrentCount(currentCount+1);
    }
    
    /**
     * Set the current count to be finished.
     */
    public void setFinished() {
        setCurrentCount(totalCount);
    }
    
}

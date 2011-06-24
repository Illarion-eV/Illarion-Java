/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui.swing;

import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import javolution.util.FastList;

import illarion.common.util.FastMath;

/**
 * This class is a monitor that takes care for watching changes of the zooming
 * state and publishes updates in case its needed.
 * 
 * @author Martin Karing
 * @since 1.02
 * @version 1.02
 */
public final class ZoomManager {
    /**
     * This interface defines the listeners for this zoom manager.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    public static interface Listener {
        /**
         * This method is called to inform the listener about a change of the
         * Zoom values.
         * 
         * @param oldState the old zoom value
         * @param newState the new zoom value
         */
        void zoomChanged(final int oldState, final int newState);
    }

    /**
     * This class is used as publisher that is send into the SWING event queue
     * and reports updates of the zooming value once they happen.
     * <p>
     * This task requests the value of the zoom upon call. So in case multiple
     * class are planned within a short time, its possible that the first one
     * does the work of all the following tasks and the following ones just run
     * idle.
     * </p>
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class PublishEventTask implements Runnable {
        /**
         * This is a reference to the list in the parent class that holds all
         * listeners. When the report happens a copy of this list is made in
         * order to ensure that this list does not change during the publishing.
         */
        private final List<Listener> listenerList;

        /**
         * The parent of this class that supplies the current zoom values.
         */
        private final ZoomManager parent;

        /**
         * This public constructor is needed to allow the parent class is create
         * a instance and to ensure that this class gets the references to the
         * variables needed for proper working.
         * 
         * @param parentManager the manager that is the parent of this class
         * @param listenersList the list that holds the listeners that receive
         *            the event message
         */
        public PublishEventTask(final ZoomManager parentManager,
            final List<Listener> listenersList) {
            listenerList = listenersList;
            parent = parentManager;
        }

        @Override
        public void run() {
            final int oldZoomVal = parent.getOldZoomValue();
            final int newZoomVal = parent.getTargetZoomValue();

            if (oldZoomVal == newZoomVal) {
                // values fit already, nothing to do
            }

            final FastList<Listener> workingList = FastList.newInstance();
            workingList.addAll(listenerList);

            parent.reportZoomUpdatePublished(newZoomVal);
            final Iterator<Listener> itr = workingList.iterator();
            while (itr.hasNext()) {
                itr.next().zoomChanged(oldZoomVal, newZoomVal);
            }

            FastList.recycle(workingList);
        }
    }

    /**
     * The maximal zoom in percent.
     */
    public static final int MAX_ZOOM = 100;

    /**
     * The minimal zoom in percent.
     */
    public static final int MIN_ZOOM = 10;

    /**
     * This is the singleton instance of this class.
     */
    private static final ZoomManager INSTANCE = new ZoomManager();

    /**
     * The list of listeners that need to be informed about changes of the
     * zooming value.
     */
    private final List<Listener> listeners;

    /**
     * The last known zoom value.
     */
    private int oldZoom;

    /**
     * The task that is used to publish then zoom change events.
     */
    private final PublishEventTask publishTask;

    /**
     * The zoom value that is supposed to be approached.
     */
    private int targetZoomValue;

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance are created.
     */
    private ZoomManager() {
        listeners = new FastList<Listener>();
        publishTask = new PublishEventTask(this, listeners);
        reset();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static ZoomManager getInstance() {
        return INSTANCE;
    }

    /**
     * Add a listener to the listeners of this class.
     * 
     * @param newListner the new listener
     */
    public void addListener(final Listener newListner) {
        if (listeners.contains(newListner)) {
            return;
        }
        listeners.add(newListner);
    }

    /**
     * Request to change the zoom value by a delta value.
     * 
     * @param delta the delta the zoom is changed by
     */
    public void changeZoom(final int delta) {
        if (delta == 0) {
            return;
        }

        requestNewZoom(FastMath.clamp(targetZoomValue + delta, MIN_ZOOM,
            MAX_ZOOM));
    }

    /**
     * Remove a listener from the listeners of this class.
     * 
     * @param oldListener the old listner of this class
     */
    public void removeListener(final Listener oldListener) {
        if (!listeners.contains(oldListener)) {
            return;
        }
        listeners.remove(oldListener);
    }

    /**
     * This function is used to request a new zooming value.
     * 
     * @param newZoomVal the new zooming value requested
     * @throws IllegalArgumentException in case the parameter is larger then
     *             {@link #MAX_ZOOM} or smaller then {@link #MIN_ZOOM}
     */
    public void requestNewZoom(final int newZoomVal) {
        if (newZoomVal > MAX_ZOOM) {
            throw new IllegalArgumentException("Parameter too large"); //$NON-NLS-1$
        }
        if (newZoomVal < MIN_ZOOM) {
            throw new IllegalArgumentException("Parameter too small"); //$NON-NLS-1$
        }

        if (newZoomVal != targetZoomValue) {
            targetZoomValue = newZoomVal;
            SwingUtilities.invokeLater(publishTask);
        }
    }

    /**
     * This function is used to reset the internal state of this class. This
     * should be done in case the application performs any case of relaunch.
     */
    public void reset() {
        oldZoom = MAX_ZOOM;
        targetZoomValue = MAX_ZOOM;
        listeners.clear();
    }

    /**
     * This internal function is used to fetch the last reported zoom value.
     * 
     * @return the last reported zoom value
     */
    protected int getOldZoomValue() {
        return oldZoom;
    }

    /**
     * The last zoom value that was requested by the GUI is returned by this
     * function.
     * 
     * @return the last zoom value requested
     */
    protected int getTargetZoomValue() {
        return targetZoomValue;
    }

    /**
     * This internal function is used to inform this manger that a update of the
     * zoom values is now published.
     * 
     * @param newValue the new zoom value that is published
     */
    protected void reportZoomUpdatePublished(final int newValue) {
        oldZoom = newValue;
    }
}

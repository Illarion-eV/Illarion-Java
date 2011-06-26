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
package illarion.mapedit.database;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import illarion.mapedit.MapEditor;

import illarion.common.config.Config;
import illarion.common.config.ConfigChangeListener;

import javolution.util.FastTable;

/**
 * This class keeps track on the currently active database and forwards the
 * information on this database to the other parts of the application.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class MapDatabaseManager implements ConfigChangeListener {
    /**
     * This listener interface is used to register the listeners to this
     * manager. Those listeners get informed in case the activated database
     * changes.
     * 
     * @author Martin Karing
     * @since 1.01
     * @version 1.01
     */
    public static interface Listener {
        /**
         * This function is called for the listener in case the map database
         * that is marked as active changes.
         * 
         * @param newDb the database that is active from now on.
         */
        void reportNewDatabase(final MapDatabase newDb);
    }

    /**
     * This is the singleton instance of this class.
     */
    private static final MapDatabaseManager INSTANCE =
        new MapDatabaseManager();

    /**
     * The currently active map database.
     */
    private MapDatabase currentDb;

    /**
     * The list of listeners that need to be informed once the database that is
     * marked active changes.
     */
    private List<Listener> listeners;
    
    /**
     * The key in the configuration system that is used to store the map directory.
     */
    public static final String CFG_KEY_MAP_DIR = "mapDir"; //$NON-NLS-1$

    /**
     * Private constructor to ensure that no instances but the singleton
     * instance are created.
     */
    private MapDatabaseManager() {
        MapEditor.getConfig().addListener(CFG_KEY_MAP_DIR, this);
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static MapDatabaseManager getInstance() {
        return INSTANCE;
    }

    /**
     * Add a listener to the manager that will be informed from now on in case a
     * new database is load.
     * 
     * @param newListener the listener to be informed
     * @throws IllegalArgumentException in case the listener argument to add is
     *             <code>null</code>
     */
    public void addListener(final Listener newListener) {
        if (newListener == null) {
            throw new IllegalArgumentException(
                "Listener to add must not be null"); //$NON-NLS-1$
        }
        synchronized (this) {
            if (listeners == null) {
                listeners = FastTable.newInstance();
            } else if (listeners.contains(newListener)) {
                return;
            }
            listeners.add(newListener);
            newListener.reportNewDatabase(currentDb);
        }
    }

    /**
     * Get the database that is currently flagged as the active one.
     * 
     * @return the active database
     */
    public MapDatabase getCurrentDatabase() {
        return currentDb;
    }

    /**
     * Remove a listener from this database manager. Listener won't receive any
     * notifications anymore.
     * 
     * @param remListener the listener to remove
     * @throws IllegalArgumentException in case the listener argument to remove
     *             is <code>null</code>
     */
    public void removeListener(final Listener remListener) {
        if (remListener == null) {
            throw new IllegalArgumentException(
                "Listener to remove must not be null"); //$NON-NLS-1$
        }
        synchronized (this) {
            if (listeners == null) {
                return;
            } else if (listeners.remove(remListener) && listeners.isEmpty()) {
                FastTable.recycle((FastTable<?>) listeners);
                listeners = null;
            }
        }
    }

    /**
     * Activate a new map database.
     * 
     * @param newDb the new map database
     */
    void activateDb(final MapDatabase newDb) {
        if (currentDb == newDb) {
            return;
        }
        
        if (currentDb != null) {
            currentDb.save();
        }
        currentDb = newDb;

        final FastTable<Listener> workingTable = FastTable.newInstance();
        synchronized (this) {
            if (listeners != null) {
                workingTable.addAll(listeners);
            }
        }
        if (!workingTable.isEmpty()) {
            final Iterator<Listener> itr = workingTable.iterator();
            while (itr.hasNext()) {
                itr.next().reportNewDatabase(newDb);
            }
        }
        FastTable.recycle(workingTable);
    }

    /**
     * In case the selected configuration entry of the map directory changed,
     * load the new database.
     */
    @Override
    public void configChanged(final Config cfg, final String key) {
        if (key.equals(CFG_KEY_MAP_DIR)) {
            final File mapDir = cfg.getFile(CFG_KEY_MAP_DIR);
            MapDatabase.loadDatabase(mapDir).activate();
        }
    }
}

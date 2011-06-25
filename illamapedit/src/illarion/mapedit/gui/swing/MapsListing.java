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

import java.awt.BorderLayout;

import javax.swing.JPanel;

import illarion.mapedit.database.MapDatabase;
import illarion.mapedit.database.MapDatabaseManager;

/**
 * Stored in this panel there will be the listing of all maps found in the
 * selected map directory.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class MapsListing extends JPanel implements MapDatabaseManager.Listener {
    /**
     * The serialization UID of the map listing widget.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The database that stores the stuff that is displayed on the screen.
     */
    private MapDatabase db;

    /**
     * Create the map listing object.
     */
    public MapsListing() {
        super(new BorderLayout());
        
        MapDatabaseManager.getInstance().addListener(this);
    }

    /**
     * In case the change of the database is reported, store the new database
     * in this instance.
     */
    @Override
    public void reportNewDatabase(MapDatabase newDb) {
        db = newDb;
    }
}

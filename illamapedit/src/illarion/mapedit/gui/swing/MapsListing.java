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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTreeTable;

import illarion.mapedit.database.MapDatabase;
import illarion.mapedit.database.MapDatabaseManager;

import illarion.common.util.tasks.TaskCancelException;

/**
 * Stored in this panel there will be the listing of all maps found in the
 * selected map directory.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class MapsListing extends JPanel implements
    MapDatabaseManager.Listener {
    /**
     * This listener is used to implement the function of the reload button.
     * 
     * @author Martin Karing
     * @since 1.01
     * @version 1.01
     */
    private static final class ReloadButtonListener implements ActionListener {
        /**
         * The parent of this listener.
         */
        private final MapsListing parent;

        /**
         * The public constructor that is used to define the parent instance of
         * this listener.
         * 
         * @param p the parent of this listener
         */
        public ReloadButtonListener(final MapsListing p) {
            parent = p;
        }

        /**
         * This function is called once the button is clicked and will cause a
         * reload event to be triggered.
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            if (e.getID() == ActionEvent.ACTION_PERFORMED) {
                parent.reloadDatabase();
            }
        }

    }

    /**
     * This helper class is used to trigger the reload the current database.
     * 
     * @author Martin Karing
     * @since 1.01
     * @version 1.01
     */
    private static final class ReloadDatabaseRunner implements Runnable {
        /**
         * The database to be reloaded.
         */
        private final MapDatabase db;

        /**
         * Create a instance of this runner and set the database that is
         * triggered for a reload.
         * 
         * @param database the database that is supposed to be reloaded
         */
        public ReloadDatabaseRunner(final MapDatabase database) {
            db = database;
        }

        /**
         * This function is called in order to execute the reload.
         */
        @Override
        public void run() {
            try {
                db.refreshFull(null);
            } catch (final TaskCancelException e) {
                // nothing
            }
        }
    }

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
        initialize();

        MapDatabaseManager.getInstance().addListener(this);
    }

    /**
     * In case the change of the database is reported, store the new database in
     * this instance.
     */
    @Override
    public void reportNewDatabase(final MapDatabase newDb) {
        db = newDb;
        model.loadDatabase(newDb);
    }

    /**
     * A call of this function causes a reload of the database.
     */
    void reloadDatabase() {
        if (db != null) {
            SwingUtilities.invokeLater(new ReloadDatabaseRunner(db));
        }
    }
    
    /**
     * The tree table model that is used to display the map tree.
     */
    private MapsListingModel model;

    /**
     * This function causes the view on this element to be prepared properly.
     */
    private void initialize() {
        final JPanel buttonPanel =
            new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        add(buttonPanel, BorderLayout.NORTH);

        final JButton reloadButton =
            new JButton(Utils.getIconFromResource("reload.png")); //$NON-NLS-1$
        reloadButton.addActionListener(new ReloadButtonListener(this));
        buttonPanel.add(reloadButton);
        
        model = new MapsListingModel();
        final JXTreeTable mapListing = new JXTreeTable(model);
        add(new JScrollPane(mapListing), BorderLayout.CENTER);
    }
}

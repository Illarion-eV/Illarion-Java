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
package illarion.mapedit.gui.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import illarion.mapedit.MapEditor;
import illarion.mapedit.map.MapStorage;

/**
 * This dialog is supposed to be displayed in case there are unsaved changes and
 * the editor is going to exit.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class SaveChangedDialog extends Dialog {
    /**
     * The serialization UID of this dialog
     */
    private static final long serialVersionUID = 1L;

    /**
     * This flag stores of the client is supposed to exit after this dialog or
     * not.
     */
    private boolean exit;

    /**
     * The list that displays all currently unsaved maps.
     */
    private final List unsavedMaps;

    /**
     * Create the dialog and prepare it to be displayed.
     */
    @SuppressWarnings("nls")
    public SaveChangedDialog() {
        super(MapEditor.getMainFrame(), "Unsaved changes", true);

        final Panel content = new Panel(new BorderLayout(5, 5));

        final Label message =
            new Label("There are unsaved changes to one or"
                + " more load maps. What do you want to do?");
        content.add(message, BorderLayout.NORTH);

        unsavedMaps = new List(5, false);
        content.add(unsavedMaps, BorderLayout.CENTER);

        final Panel buttons =
            new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        content.add(buttons, BorderLayout.SOUTH);

        final Button exitSave = new Button("Save maps and exit");
        final Button exitDiscard = new Button("Discard changes and exit");
        final Button cancel = new Button("Cancel");

        exitSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MapStorage.getInstance().saveAllMaps();
                setExit(true);
                setVisible(false);
            }
        });

        exitDiscard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setExit(true);
                setVisible(false);
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setExit(false);
                setVisible(false);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                setExit(false);
                setVisible(false);
            }
        });

        buttons.add(exitSave);
        buttons.add(exitDiscard);
        buttons.add(cancel);

        add(content);

        pack();
        validate();

        setLocation(100, 100);

        final Dimension prefSize = getPreferredSize();
        prefSize.width = 500;
        setPreferredSize(prefSize);
    }

    /**
     * Check if the editor is supposed to exit after showing this dialog.
     * 
     * @return <code>true</code> if the editor is supposed to exit
     */
    public boolean isExit() {
        return exit;
    }

    /**
     * Set the names of the unsaved maps that are supposed to be saved.
     * 
     * @param mapNames the names of the maps that are yet not saved
     */
    public void setUnsavedMaps(final String[] mapNames) {
        unsavedMaps.removeAll();
        for (final String mapName : mapNames) {
            unsavedMaps.add(mapName);
        }
    }

    /**
     * Set the new value of the exit flag.
     * 
     * @param newExit the new value of the exit flag
     */
    void setExit(final boolean newExit) {
        exit = newExit;
    }
}

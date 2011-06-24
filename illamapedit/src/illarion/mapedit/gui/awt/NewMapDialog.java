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
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import illarion.mapedit.MapEditor;
import illarion.mapedit.map.MapStorage;

/**
 * This dialog is used to create a new map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class NewMapDialog extends Dialog {
    /**
     * The serialization UID of the dialog.
     */
    private static final long serialVersionUID = 1L;

    private final TextField mapHeight;
    private final TextField mapName;
    private final TextField mapOriginX;
    private final TextField mapOriginY;
    private final TextField mapOriginZ;
    private final TextField mapWidth;

    /**
     * Prepare the dialog for creating a new map.
     */
    @SuppressWarnings("nls")
    public NewMapDialog() {
        super(MapEditor.getMainFrame(), "Create new map", true);

        final Panel content = new Panel(new BorderLayout(5, 5));
        final Panel settings = new Panel(new GridLayout(0, 2));

        content.add(settings, BorderLayout.CENTER);

        settings.add(new Label("Name of the map:"));
        mapName = new TextField(20);
        settings.add(mapName);

        settings.add(new Label("Map Origin X Coordinate:"));
        mapOriginX = new TextField(20);
        settings.add(mapOriginX);

        settings.add(new Label("Map Origin Y Coordinate:"));
        mapOriginY = new TextField(20);
        settings.add(mapOriginY);

        settings.add(new Label("Map Origin Z Coordinate:"));
        mapOriginZ = new TextField(20);
        settings.add(mapOriginZ);

        settings.add(new Label("Map Width:"));
        mapWidth = new TextField(20);
        settings.add(mapWidth);

        settings.add(new Label("Map Height:"));
        mapHeight = new TextField(20);
        settings.add(mapHeight);

        final Panel buttons =
            new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        final Button okButton = new Button("Create map");
        final Button cancelButton = new Button("Cancel");

        buttons.add(okButton);
        buttons.add(cancelButton);

        content.add(buttons, BorderLayout.SOUTH);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                createMap();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                setVisible(false);
            }
        });

        add(content);

        pack();
        validate();

        setLocation(100, 100);

        final Dimension prefSize = getPreferredSize();
        prefSize.width = 300;
        setPreferredSize(prefSize);
    }

    @SuppressWarnings("nls")
    void createMap() {
        String error = null;
        try {
            error =
                MapStorage.getInstance().createMap(mapName.getText(),
                    Integer.parseInt(mapOriginX.getText()),
                    Integer.parseInt(mapOriginY.getText()),
                    Integer.parseInt(mapOriginZ.getText()),
                    Integer.parseInt(mapWidth.getText()),
                    Integer.parseInt(mapHeight.getText()));
        } catch (final NumberFormatException ex) {
            error = "Origin coordinates and dimensions need to be numbers";
        } catch (final Exception ex) {
            error = "Invalid values insert";
        }
        if (error == null) {
            setVisible(false);
        } else {
            final Dialog errorDiag = new Dialog(this, "Error", true);
            final Panel content = new Panel(new BorderLayout(5, 5));
            errorDiag.add(content);

            final Panel messages = new Panel(new GridLayout(0, 1));
            messages.add(new Label("Error while creating map:"));
            messages.add(new Label(error));
            content.add(messages, BorderLayout.CENTER);
            final Button closeButton = new Button("OK");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    errorDiag.setVisible(false);
                }
            });
            final Panel buttonPanel =
                new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.add(closeButton);
            content.add(buttonPanel, BorderLayout.SOUTH);
            errorDiag.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent e) {
                    errorDiag.setVisible(false);
                }
            });

            errorDiag.pack();
            errorDiag.validate();

            final Point loc = getLocation();
            errorDiag.setLocation(loc.x + 20, loc.y + 20);

            errorDiag.setVisible(true);
            errorDiag.dispose();
        }
    }
}

/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui;

import illarion.common.config.Config;
import illarion.mapedit.Lang;
import illarion.mapedit.MapEditor;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author Tim
 */
public class MapDialogs {
    private static final int UNSIGNED_MAX = 100000;
    private static final int SIGNED_MAX = 10000;
    private static final FilenameFilter FILTER_TILES = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {
            return name.endsWith(MapIO.EXT_TILE);
        }
    };
    private static File saveDir;
    private static final Config config = MapEditor.getConfig();

    private MapDialogs() {

    }

    public static Map showNewMapDialog(final JFrame owner) {
        final JDialog dialog = new JDialog(owner, Lang.getMsg("gui.newmap"));
        dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));
        dialog.setResizable(false);
        dialog.setModal(true);
        final JSpinner width = new JSpinner(new SpinnerNumberModel(100, 0, UNSIGNED_MAX, 1));
        final JSpinner height = new JSpinner(new SpinnerNumberModel(100, 0, UNSIGNED_MAX, 1));
        final JSpinner x = new JSpinner(new SpinnerNumberModel(0, -SIGNED_MAX, SIGNED_MAX, 1));
        final JSpinner y = new JSpinner(new SpinnerNumberModel(0, -SIGNED_MAX, SIGNED_MAX, 1));
        final JSpinner l = new JSpinner(new SpinnerNumberModel(0, -SIGNED_MAX, SIGNED_MAX, 1));
        final JTextField name = new JTextField(1);
        final JButton btn = new JButton(Lang.getMsg("gui.newmap.Ok"));
        saveDir = null;
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JFileChooser ch = new JFileChooser(MapEditor.getConfig().getFile("mapLastOpenDir"));
                ch.setDialogType(JFileChooser.OPEN_DIALOG);
                ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (ch.showOpenDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION) {
                    dialog.setVisible(false);
                    return;
                }
                saveDir = ch.getSelectedFile();
                dialog.setVisible(false);
            }
        });

        dialog.add(new JLabel(Lang.getMsg("gui.newmap.Width")));
        dialog.add(width);
        dialog.add(new JLabel(Lang.getMsg("gui.newmap.Height")));
        dialog.add(height);
        dialog.add(new JLabel(Lang.getMsg("gui.newmap.X")));
        dialog.add(x);
        dialog.add(new JLabel(Lang.getMsg("gui.newmap.Y")));
        dialog.add(y);
        dialog.add(new JLabel(Lang.getMsg("gui.newmap.Z")));
        dialog.add(l);
        dialog.add(new JLabel(Lang.getMsg("gui.newmap.Name")));
        dialog.add(name);
        dialog.add(btn);
        dialog.doLayout();
        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();

        if (saveDir != null) {
            return new Map(name.getText(), saveDir.getPath(), (Integer) width.getValue(), (Integer) height.getValue(),
                    (Integer) x.getValue(), (Integer) y.getValue(), (Integer) l.getValue());
        }
        return null;
    }

    public static Map showOpenMapDialog(final JFrame owner) throws IOException {
        final JFileChooser ch = new JFileChooser();
        if (config.getFile("mapLastOpenDir") != null) {
            ch.setCurrentDirectory(config.getFile("mapLastOpenDir"));
        }
        ch.setDialogType(JFileChooser.OPEN_DIALOG);
        ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (ch.showOpenDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        final File dir = ch.getSelectedFile();
        config.set("mapLastOpenDir", dir);
        final String[] maps = dir.list(FILTER_TILES);
        for (int i = 0; i < maps.length; ++i) {
            maps[i] = maps[i].substring(0, maps[i].length() - MapIO.EXT_TILE.length());
        }
        final JDialog dialog = new JDialog(owner, Lang.getMsg("gui.chooser"));
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        final JList<String> list = new JList<String>(maps);
        final JButton btn = new JButton(Lang.getMsg("gui.chooser.Ok"));

        btn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (list.getSelectedValue() != null) {
                    dialog.setVisible(false);
                }
            }
        });

        dialog.add(new JScrollPane(list), BorderLayout.CENTER);
        dialog.add(btn, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();
        if (list.getSelectedValue() == null) {
            return null;
        }
        return MapIO.loadMap(dir.getPath(), list.getSelectedValue());
    }

    public static void showSaveDialog() {

    }
}

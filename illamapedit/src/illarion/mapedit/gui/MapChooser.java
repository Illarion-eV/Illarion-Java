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
import illarion.common.config.ConfigChangeListener;
import illarion.mapedit.Lang;
import illarion.mapedit.MapEditor;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapDimensions;

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
public class MapChooser {

    public interface SelectedListener {
        void selected(String s);
    }

    private class MapSelector extends JDialog {


        private final JList<String> list;

        public MapSelector(String[] maps) {
            super(MainFrame.getInstance(), Lang.getMsg("gui.chooser"));
            setModal(true);
            setLayout(new BorderLayout());

            list = new JList<String>(maps);
            JButton btn = new JButton(Lang.getMsg("gui.chooser.Ok"));

            btn.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (list.getSelectedValue() != null) {
                        setVisible(false);
                    }
                }
            });

            add(new JScrollPane(list), BorderLayout.CENTER);
            add(btn, BorderLayout.SOUTH);
            pack();
        }

        public String select() {
            setVisible(true);
            dispose();
            return list.getSelectedValue();
        }
    }

    private class NewMapInfoDialog extends JDialog {

        private final JSpinner width;
        private final JSpinner height;
        private final JSpinner x;
        private final JSpinner y;
        private final JSpinner l;
        private final JTextField name;
        private File saveDir;

        public NewMapInfoDialog() {
            super(MainFrame.getInstance(), Lang.getMsg("gui.newmap"));
            getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
            setResizable(false);
            setModal(true);
            width = new JSpinner(new SpinnerNumberModel(100, 0, 100000, 1));
            height = new JSpinner(new SpinnerNumberModel(100, 0, 100000, 1));
            x = new JSpinner(new SpinnerNumberModel(0, -10000, 10000, 1));
            y = new JSpinner(new SpinnerNumberModel(0, -10000, 10000, 1));
            l = new JSpinner(new SpinnerNumberModel(0, -10000, 10000, 1));
            name = new JTextField(1);
            final JButton btn = new JButton(Lang.getMsg("gui.newmap.Ok"));
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final JFileChooser ch = new JFileChooser(MapEditor.getConfig().getFile("mapLastOpenDir"));
                    ch.setDialogType(JFileChooser.OPEN_DIALOG);
                    ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    if (ch.showOpenDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION) {
                        setVisible(false);
                        return;
                    }
                    saveDir = ch.getSelectedFile();
                    setVisible(false);
                }
            });

            Dimension d = width.getPreferredSize();
            d.width = 100;
            width.setPreferredSize(d);
            height.setPreferredSize(d);
            x.setPreferredSize(d);
            y.setPreferredSize(d);
            l.setPreferredSize(d);
            add(new JLabel(Lang.getMsg("gui.newmap.Width")));
            add(width);
            add(new JLabel(Lang.getMsg("gui.newmap.Height")));
            add(height);
            add(new JLabel(Lang.getMsg("gui.newmap.X")));
            add(x);
            add(new JLabel(Lang.getMsg("gui.newmap.Y")));
            add(y);
            add(new JLabel(Lang.getMsg("gui.newmap.Z")));
            add(l);
            add(new JLabel(Lang.getMsg("gui.newmap.Name")));
            add(name);
            add(btn);
            doLayout();
            pack();
        }

        public Map showDialog() {
            setVisible(true);
            dispose();
            if (saveDir == null) {
                return null;
            }
            return new Map(name.getText(), saveDir.getPath(), new MapDimensions((Integer) x.getValue(), (Integer) y.getValue(),
                    (Integer) l.getValue(),
                    (Integer) width.getValue(), (Integer) height.getValue())
            );
        }
    }

    private static File lastDir;
    private static final MapChooser INSTANCE = new MapChooser();

    private MapChooser() {
        lastDir = MapEditor.getConfig().getFile("mapLastOpenDir");
        MapEditor.getConfig().addListener("mapLastOpenDir", new ConfigChangeListener() {
            @Override
            public void configChanged(final Config cfg, final String key) {
                lastDir = cfg.getFile("mapLastOpenDir");
            }
        });
    }

    public static MapChooser getInstance() {
        return INSTANCE;
    }

    public Map loadMap() throws IOException {
        File t = chooseDirectory();
        if (!isDirOk(t)) {
            return null;
        }
        String[] files = getMaps(t);
        if (files == null || files.length == 0)
            return null;
        String mapS = new MapSelector(files).select();
        System.out.println(mapS);
        Map m = Map.fromBasePath(t.getPath(), mapS);
        return m;
    }

    private String[] getMaps(final File dir) {
        if (!isDirOk(dir)) {
            return new String[0];
        }
        String[] maps = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".tiles.txt");
            }
        });
        if (maps == null) {
            return new String[0];
        }
        for (int i = 0; i < maps.length; ++i) {
            maps[i] = maps[i].substring(0, maps[i].length() - 10);
        }
        return maps;
    }

    private File chooseDirectory() {
        JFileChooser ch = new JFileChooser();
        if (isDirOk(lastDir)) {
            ch.setCurrentDirectory(lastDir);
        } else {
            ch.setCurrentDirectory(MapEditor.getConfig().getFile("mapLastOpenDir"));
        }
        ch.setDialogType(JFileChooser.OPEN_DIALOG);
        ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (ch.showOpenDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        if (!lastDir.equals(ch.getCurrentDirectory())) {
            lastDir = ch.getCurrentDirectory();
            MapEditor.getConfig().set("mapLastOpenDir", lastDir);
        }
        return ch.getSelectedFile();
    }

    private static boolean isDirOk(final File f) {
        return (f != null) && f.exists() && f.isDirectory();
    }

    public Map newMap() {
        return new NewMapInfoDialog().showDialog();
    }
}

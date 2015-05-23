/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.mapedit.gui;

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;

import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Tim
 */
public final class MapDialogs {
    private static final int UNSIGNED_MAX = 100000;
    private static final int SIGNED_MAX = 10000;

    @Nullable
    private static Path saveDir;

    private MapDialogs() {

    }

    @Nullable
    public static Map showNewMapDialog(JFrame owner) {
        JDialog dialog = new JDialog(owner, Lang.getMsg("gui.newmap"));
        dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        JSpinner width = new JSpinner(new SpinnerNumberModel(100, 0, UNSIGNED_MAX, 1));
        JSpinner height = new JSpinner(new SpinnerNumberModel(100, 0, UNSIGNED_MAX, 1));
        JSpinner x = new JSpinner(new SpinnerNumberModel(0, -SIGNED_MAX, SIGNED_MAX, 1));
        JSpinner y = new JSpinner(new SpinnerNumberModel(0, -SIGNED_MAX, SIGNED_MAX, 1));
        JSpinner l = new JSpinner(new SpinnerNumberModel(0, -SIGNED_MAX, SIGNED_MAX, 1));
        JTextField name = new JTextField(1);
        JButton btn = new JButton(Lang.getMsg("gui.newmap.Ok"));
        saveDir = null;
        btn.addActionListener(e -> {
            JFileChooser ch = new JFileChooser(MapEditorConfig.getInstance().getMapFolder().toFile());
            ch.setDialogType(JFileChooser.OPEN_DIALOG);
            ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (ch.showOpenDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION) {
                dialog.setVisible(false);
                return;
            }
            File selectedFile = ch.getSelectedFile();
            saveDir = selectedFile == null ? null : selectedFile.toPath();
            dialog.setVisible(false);
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
            return new Map(name.getText(), saveDir, (Integer) width.getValue(), (Integer) height.getValue(),
                           (Integer) x.getValue(), (Integer) y.getValue(), (Integer) l.getValue());
        }
        return null;
    }

    @Nullable
    public static Path showSetFolderDialog() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        if (MapEditorConfig.getInstance().getMapFolder() != null) {
            fileChooser.setCurrentDirectory(MapEditorConfig.getInstance().getMapFolder().toFile());
        }
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile().toPath();
    }

    public static boolean isShowSaveDialog() {
        int answer = JOptionPane
                .showConfirmDialog(null, Lang.getMsg("gui.info.unsaved"), Lang.getMsg("gui.info.unsaved.Title"),
                                   JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return answer == JOptionPane.YES_OPTION;
    }
}

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
package illarion.easyquest.gui;

import illarion.common.util.CopyrightHeader;
import illarion.easyquest.Lang;
import illarion.easyquest.QuestIO;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class Utils {

    @Nullable
    private static final CopyrightHeader COPYRIGHT_HEADER = new CopyrightHeader(80, "<!--", "-->", null, null);

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    @Nullable
    public static ResizableIcon getResizableIconFromResource(
            String resource) {
        Image image;
        try {
            image = ImageIO.read(Utils.class.getClassLoader().getResource(resource));
        } catch (@Nonnull IOException e) {
            LOGGER.error("Failed to read image: \"" + resource + '"');
            return null;
        }
        int height = image.getHeight(null);
        int width = image.getWidth(null);
        return ImageWrapperResizableIcon.getIcon(image, new Dimension(width, height));
    }

    public static void saveEasyQuest(@Nonnull Editor editor) {
        Path file = editor.getQuestFile();
        if (file == null) {
            selectAndSaveEasyQuest(editor);
            return;
        }

        try {
            QuestIO.saveGraphModel(editor.getGraph().getModel(), file);
            editor.saved();
        } catch (IOException e) {
            LOGGER.error("Saving file failed.", e);
        }
    }

    static void exportEasyQuest(@Nonnull Editor editor) {
        if (editor.validQuest()) {
            JFileChooser dirDiag = new JFileChooser();
            dirDiag.setDialogTitle("Exportieren");
            dirDiag.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirDiag.setAcceptAllFileFilterUsed(false);
            dirDiag.setCurrentDirectory(Config.getInstance().getExportFolder().toFile());
            int fileReturn = dirDiag.showSaveDialog(MainFrame.getInstance());
            if (fileReturn == JFileChooser.APPROVE_OPTION) {
                File targetDir = dirDiag.getSelectedFile();
                try {
                    QuestIO.exportQuest(editor.getGraph().getModel(), targetDir.toPath());
                } catch (IOException e) {
                    LOGGER.error("Exporting the quest failed.", e);
                }
            }
        }
    }

    public static void selectAndOpenQuest() {
        JFileChooser fileDiag = new JFileChooser();
        fileDiag.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(@Nonnull File f) {
                return !f.isFile() || f.getName().endsWith(".quest"); //$NON-NLS-1$
            }

            @Override
            public String getDescription() {
                return Lang.getMsg(Utils.class, "easyQuestFileType"); //$NON-NLS-1$
            }
        });
        fileDiag.setAcceptAllFileFilterUsed(false);
        fileDiag.setCurrentDirectory(Config.getInstance().getEasyQuestFolder().toFile());

        int fileReturn = fileDiag.showOpenDialog(MainFrame.getInstance());
        if (fileReturn == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileDiag.getSelectedFile();
            openQuest(selectedFile.toPath());
        }
    }

    static void openQuest(@Nonnull Path file) {
        int editorIndex = MainFrame.getInstance().alreadyOpen(file);
        if (editorIndex > -1) {
            MainFrame.getInstance().setCurrentEditorTab(editorIndex);
            return;
        }

        Editor editor = MainFrame.getInstance().addNewQuest(file);
        editor.setQuestFile(file);

        MainFrame.getInstance().setCurrentTabTitle(file.getFileName().toString());
        Config.getInstance().addLastOpenedFile(file);
    }

    static void selectAndSaveEasyQuest(@Nonnull Editor editor) {
        JFileChooser fileDiag = new JFileChooser();
        fileDiag.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(@Nonnull File f) {
                return !f.isFile() || f.getName().endsWith(".quest"); //$NON-NLS-1$
            }

            @Override
            public String getDescription() {
                return Lang.getMsg(Utils.class, "easyQuestFileType"); //$NON-NLS-1$
            }
        });
        fileDiag.setAcceptAllFileFilterUsed(false);
        fileDiag.setCurrentDirectory(Config.getInstance().getEasyQuestFolder().toFile());
        Path questFile = editor.getQuestFile();
        fileDiag.setSelectedFile(questFile == null ? null : questFile.toFile());
        int fileReturn = fileDiag.showSaveDialog(MainFrame.getInstance());
        if (fileReturn == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileDiag.getSelectedFile();
            if (!targetFile.getName().endsWith(".quest")) {
                targetFile = new File(targetFile.getParent(), targetFile.getName() + ".quest");
            }
            Path targetPath = targetFile.toPath();
            editor.setQuestFile(targetPath);
            try {
                QuestIO.saveGraphModel(editor.getGraph().getModel(), targetPath);
                MainFrame.getInstance().setTabTitle(editor, targetFile.getName());
                editor.saved();
            } catch (IOException e) {
                LOGGER.error("Failed to save file.", e);
            }
        }
    }
}

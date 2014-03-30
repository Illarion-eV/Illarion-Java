/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.download.gui.controller;

import illarion.common.util.DirectoryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractDirSelectionController extends AbstractController {

    @FXML
    public TextField selectedDirectory;

    @FXML
    public RadioButton optionAbsolute;

    @FXML
    public RadioButton optionRelative;

    @Nonnull
    private final DirectoryManager.Directory dir;

    protected AbstractDirSelectionController(@Nonnull DirectoryManager.Directory directory) {
        dir = directory;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final DirectoryManager dm = DirectoryManager.getInstance();
        if (dm.isDirectorySet(dir)) {
            //noinspection ConstantConditions
            selectedDirectory.setText(dm.getDirectory(dir).toAbsolutePath().toString());
        }

        if (dm.isRelativeDirectoryPossible()) {
            optionAbsolute.setSelected(!dm.isDirectoryRelative(dir));
            optionRelative.setSelected(dm.isDirectoryRelative(dir));
        } else {
            optionAbsolute.setSelected(true);
            optionRelative.setDisable(true);
        }
    }

    @FXML
    public void browse(@Nonnull final ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();

        final DirectoryManager dm = DirectoryManager.getInstance();
        if (dm.isDirectorySet(dir)) {
            directoryChooser.setInitialDirectory(dm.getDirectory(dir).toAbsolutePath().toFile());
        } else {
            final File subDir = new File(System.getProperty("user.home"), "Illarion");
            directoryChooser.setInitialDirectory(new File(subDir, dir.getDefaultDir()));
        }
        while (!directoryChooser.getInitialDirectory().exists()) {
            directoryChooser.setInitialDirectory(directoryChooser.getInitialDirectory().getParentFile());
        }

        @Nullable final File selectedDirectory = directoryChooser.showDialog(getModel().getStage());
        if (selectedDirectory != null) {
            this.selectedDirectory.setText(selectedDirectory.getAbsolutePath());
            optionAbsolute.setSelected(true);
        }
    }

    @FXML
    public void nextStep(@Nonnull final ActionEvent actionEvent) {
        final DirectoryManager dm = DirectoryManager.getInstance();
        if (optionRelative.isSelected() && dm.isRelativeDirectoryPossible()) {
            dm.setDirectoryRelative(dir);
        } else {
            dm.setDirectory(dir, Paths.get(selectedDirectory.getText()));
        }
        if (dm.isDirectorySet(dir)) {
            dm.save();
            try {
                getModel().getStoryboard().nextScene();
            } catch (@Nonnull final IOException e) {
                // nothing
            }
        } else {
            selectedDirectory.setText("");
        }
    }
}

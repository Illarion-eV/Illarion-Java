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

import illarion.download.cleanup.Cleaner;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class UninstallController extends AbstractController {
    @FXML
    public RadioButton keepPlayerOption;
    @FXML
    public RadioButton removeAllOption;
    @FXML
    public Button buttonCancel;
    @FXML
    public Button buttonUninstall;

    @Override
    public void initialize(@Nonnull final URL url, @Nonnull final ResourceBundle resourceBundle) {

    }

    @FXML
    public void uninstall(@Nonnull final ActionEvent actionEvent) {
        buttonCancel.setDisable(true);
        buttonUninstall.setDisable(true);

        final Cleaner.Mode uninstallMode;
        if (removeAllOption.isSelected()) {
            uninstallMode = Cleaner.Mode.RemoveEverything;
        } else {
            uninstallMode = Cleaner.Mode.RemoveBinaries;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cleaner cleaner = new Cleaner(uninstallMode);
                cleaner.clean();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        getModel().getStage().close();
                    }
                });
            }
        }).run();
    }

    @FXML
    public void cancel(@Nonnull final ActionEvent actionEvent) {
        try {
            getModel().getStoryboard().showNormal();
        } catch (@Nonnull final IOException ignored) {
        }
    }
}

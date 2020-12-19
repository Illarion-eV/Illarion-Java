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
package illarion.download.gui.controller;

import illarion.common.config.Config;
import illarion.download.gui.model.GuiModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Andreas Grob &lt;vilarion@illarion.org&gt;
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ChannelSelectionController extends AbstractController {
    @FXML
    public ComboBox<String> targetClient;
    @FXML
    public ComboBox<String> targetMapEditor;

    @Override
    public void initialize(URL location, @Nonnull ResourceBundle resources) {
        ObservableList<String> targets = FXCollections
                .observableArrayList(resources.getString("optionRelease"),
                        resources.getString("optionSnapshot"));
        targetClient.setItems(targets);
        targetMapEditor.setItems(targets);
    }

    @Override
    public void setModel(@Nonnull GuiModel model) {
        super.setModel(model);

        Config cfg = getModel().getConfig();

        targetClient.setValue(targetClient.getItems().get(cfg.getInteger("channelClient")));
        targetMapEditor.setValue(targetMapEditor.getItems().get(cfg.getInteger("channelMapEditor")));
    }

    @FXML
    public void nextStep(@Nonnull ActionEvent actionEvent) {
        Config cfg = getModel().getConfig();
        cfg.set("channelClient", targetClient.getItems().indexOf(targetClient.getValue()));
        cfg.set("channelMapEditor", targetMapEditor.getItems().indexOf(targetMapEditor.getValue()));

        cfg.save();

        try {
            getModel().getStoryboard().showNormal();
        } catch (@Nonnull IOException e) {
            // nothing
        }
    }
}

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

import illarion.download.gui.model.GuiModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.annotation.Nonnull;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ApplicationController extends AbstractController {
    @FXML
    public AnchorPane footer;

    @FXML
    public AnchorPane content;

    @FXML
    public AnchorPane rootPane;

    private double initialX;
    private double initialY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setModel(@Nonnull final GuiModel model) {
        super.setModel(model);
        if (getModel().getHostServices().getWebContext() == null) {
            rootPane.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(@Nonnull MouseEvent me) {
                    if (me.getButton() != MouseButton.MIDDLE) {
                        initialX = me.getSceneX();
                        initialY = me.getSceneY();
                    }
                }
            });

            rootPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(@Nonnull MouseEvent me) {
                    if (me.getButton() != MouseButton.MIDDLE) {
                        getModel().getStage().setX(me.getScreenX() - initialX);
                        getModel().getStage().setY(me.getScreenY() - initialY);
                    }
                }
            });
        }
    }

    @FXML
    public void close(@Nonnull final ActionEvent event) {
        getModel().getStage().close();
    }

    @FXML
    public void minimize(@Nonnull final ActionEvent event) {
        getModel().getStage().setIconified(true);
    }
}

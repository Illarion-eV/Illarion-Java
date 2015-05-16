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
package illarion.download.gui.view;

import illarion.download.gui.model.GuiModel;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ApplicationView extends AnchorPane implements SceneUpdater {
    public ApplicationView(@Nonnull GuiModel model) throws IOException {
        boolean isApplet;
        try {
            isApplet = model.getHostServices().getWebContext() != null;
        } catch (Exception e) {
            isApplet = false;
        }

        Parent root = Util.loadFXML("applicationFrame.fxml", model, Util.loadResourceBundle("applicationFrame"));

        if (isApplet) {
            root.getStyleClass().add("applet");
            root.lookup("#header").setVisible(false);
        } else {
            root.getStyleClass().add("application");
        }

        getChildren().add(root);
        maximizeOnAnchorPane(root);
    }

    @Nonnull
    protected final Pane getContentPane() {
        return (Pane) lookup("#content");
    }

    @Nonnull
    protected final Pane getFooterPane() {
        return (Pane) lookup("#footer");
    }

    protected final void setContent(@Nonnull Node content, @Nonnull Node footer) {
        getContentPane().getChildren().add(content);
        getFooterPane().getChildren().add(footer);

        maximizeOnAnchorPane(content);
        maximizeOnAnchorPane(footer);
    }

    private static void maximizeOnAnchorPane(@Nonnull Node node) {
        setRightAnchor(node, 0.0);
        setLeftAnchor(node, 0.0);
        setTopAnchor(node, 0.0);
        setBottomAnchor(node, 0.0);
    }

    @Override
    public void updateScene(@Nonnull Scene scene) {
        String stylesheet = Util.getCssReference("applicationFrame");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet);
        } else {
            System.out.println("Failed to locate stylesheet: applicationFrame");
        }
    }
}

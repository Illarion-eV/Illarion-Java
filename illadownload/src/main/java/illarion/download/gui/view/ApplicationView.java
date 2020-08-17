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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ApplicationView extends AnchorPane implements SceneUpdater {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(ApplicationView.class);

    ApplicationView(@Nonnull GuiModel model) throws IOException {
        Parent root = Util.loadFXML("applicationFrame.fxml", model, Util.loadResourceBundle("applicationFrame"));

        root.getStyleClass().add("application");

        getChildren().add(root);
        maximizeOnAnchorPane(root);
    }

    @Nonnull
    private Node lookupRequiredNode(@Nonnull String selector) {
        Node node = lookup(selector);
        if (node == null) {
            throw new IllegalArgumentException("Selector did not mark a existing node.");
        }
        return node;
    }

    @Nonnull
    private Pane getContentPane() {
        return (Pane) lookupRequiredNode("#content");
    }

    @Nonnull
    private Pane getFooterPane() {
        return (Pane) lookupRequiredNode("#footer");
    }

    final void setContent(@Nonnull Node content, @Nonnull Node footer) {
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
            log.error("Failed to locate stylesheet: applicationFrame");
        }
    }
}

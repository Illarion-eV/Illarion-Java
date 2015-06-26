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

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MainView extends ApplicationView {
    public MainView(@Nonnull GuiModel model) throws IOException {
        super(model);

        Node viewContents = Util.loadFXML("mainView.fxml", model, Util.loadResourceBundle("mainView"));

        Node content = viewContents.lookup("#content");
        Node footer = viewContents.lookup("#footer");
        if ((content == null) || (footer == null)) {
            throw new IllegalStateException("Failed to locate contents and footer in source FXML.");
        }
        setContent(content, footer);
    }
}

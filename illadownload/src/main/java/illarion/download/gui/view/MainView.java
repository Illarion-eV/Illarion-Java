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
package illarion.download.gui.view;

import illarion.download.gui.model.GuiModel;
import javafx.scene.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MainView extends ApplicationView {
    public MainView(@Nonnull final GuiModel model) throws IOException {
        super(model);

        final Node viewContents = Util.loadFXML("mainView.fxml", model, Util.loadResourceBundle("mainView"));

        setContent(viewContents.lookup("#content"), viewContents.lookup("#footer"));
    }
}

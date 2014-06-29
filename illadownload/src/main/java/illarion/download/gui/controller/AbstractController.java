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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the abstract implementation for a controller. It implements the storage for the model reference.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("NullableProblems")
public abstract class AbstractController implements Controller {
    /**
     * The stored reference to the GUI model.
     */
    @Nullable
    private GuiModel model;

    protected AbstractController() {
    }

    @Override
    public void setModel(@Nonnull GuiModel model) {
        this.model = model;
    }

    @Nonnull
    public GuiModel getModel() {
        if (model == null) {
            throw new IllegalStateException("GUIModel was not set yet.");
        }
        return model;
    }
}

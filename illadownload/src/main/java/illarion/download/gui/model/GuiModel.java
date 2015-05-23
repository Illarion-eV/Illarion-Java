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
package illarion.download.gui.model;

import illarion.common.config.Config;
import illarion.download.gui.Storyboard;
import javafx.application.HostServices;
import javafx.stage.Stage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GuiModel {
    @Nonnull
    private final Stage stage;

    @Nonnull
    private final HostServices hostServices;

    @Nonnull
    private final Storyboard storyboard;

    @Nullable
    private Config config;

    public GuiModel(
            @Nonnull Stage stage,
            @Nonnull HostServices hostServices,
            @Nonnull Storyboard storyboard) {
        this.stage = stage;
        this.hostServices = hostServices;
        this.storyboard = storyboard;
    }

    @Nonnull
    public Stage getStage() {
        return stage;
    }

    @Nonnull
    public HostServices getHostServices() {
        return hostServices;
    }

    @Nonnull
    public Storyboard getStoryboard() {
        return storyboard;
    }

    public void setConfig(@Nullable Config config) {
        this.config = config;
    }

    @Nullable
    public Config getConfig() {
        return config;
    }
}

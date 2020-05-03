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
import illarion.common.config.ConfigSystem;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.download.gui.Storyboard;
import javafx.application.HostServices;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

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

    @Nonnull
    private final Config config;

    public GuiModel(
            @Nonnull Stage stage,
            @Nonnull HostServices hostServices,
            @Nonnull Storyboard storyboard) {
        this.stage = stage;
        this.hostServices = hostServices;
        this.storyboard = storyboard;
        config = loadConfig();
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

    @Nonnull
    public Config getConfig() {
        return config;
    }

    @Nonnull
    private static Config loadConfig() {
        DirectoryManager dm = DirectoryManager.getInstance();
        ConfigSystem cfg = new ConfigSystem(dm.resolveFile(Directory.User, "download.xcfgz"));
        cfg.setDefault("channelClient", 0);
        cfg.setDefault("channelEasyNpc", 1);
        cfg.setDefault("channelEasyQuest", 1);
        cfg.setDefault("channelMapEditor", 1);
        cfg.setDefault("launchAggressive", false);
        cfg.setDefault("stayOpenAfterLaunch", true);
        cfg.setDefault("verifyArtifactChecksum", false);
        cfg.set("launchAggressive", false);
        return cfg;
    }
}

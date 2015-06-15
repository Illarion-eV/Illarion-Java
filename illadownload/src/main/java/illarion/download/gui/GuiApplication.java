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
package illarion.download.gui;

import illarion.common.config.ConfigSystem;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.download.gui.model.GuiModel;
import illarion.download.gui.view.ChannelSelectView;
import illarion.download.gui.view.MainView;
import illarion.download.gui.view.SceneUpdater;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GuiApplication extends Application implements Storyboard {
    private static final double SCENE_WIDTH = 620.0;
    private static final double SCENE_HEIGHT = 410.0;
    @Nonnull
    private final ConfigSystem cfg;
    private GuiModel model;
    @Nullable
    private Stage stage;

    public GuiApplication() {
        cfg = loadConfig();
    }

    @Nonnull
    private static ConfigSystem loadConfig() {
        DirectoryManager dm = DirectoryManager.getInstance();
        ConfigSystem cfg = new ConfigSystem(dm.resolveFile(Directory.User, "download.xcfgz"));
        cfg.setDefault("channelClient", 0);
        cfg.setDefault("channelEasyNpc", 1);
        cfg.setDefault("channelEasyQuest", 1);
        cfg.setDefault("channelMapEditor", 1);
        return cfg;
    }

    @Override
    public void start(@Nonnull Stage primaryStage) throws Exception {
        model = new GuiModel(primaryStage, getHostServices(), this);
        model.setConfig(cfg);

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        stage = primaryStage;

        primaryStage.getIcons().add(new Image("illarion_download256.png"));

        showNormal();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void setScene(@Nonnull Parent sceneContent) {
        if (stage == null) {
            return;
        }

        Scene scene = new Scene(sceneContent, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(null);
        if (sceneContent instanceof SceneUpdater) {
            ((SceneUpdater) sceneContent).updateScene(scene);
        }
        stage.setScene(scene);
    }

    @Override
    public void showOptions() throws IOException {
        if (model == null) {
            throw new IllegalStateException("Model is not set.");
        }

        setScene(new ChannelSelectView(model));
    }

    @Override
    public void showNormal() throws IOException {
        if (model == null) {
            throw new IllegalStateException("Model is not set.");
        }

        setScene(new MainView(model));
    }
}

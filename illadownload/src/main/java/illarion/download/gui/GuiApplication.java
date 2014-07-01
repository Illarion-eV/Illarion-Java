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
package illarion.download.gui;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import illarion.common.config.ConfigSystem;
import illarion.common.util.DirectoryManager;
import illarion.download.gui.model.GuiModel;
import illarion.download.gui.view.ChannelSelectView;
import illarion.download.gui.view.MainView;
import illarion.download.gui.view.SceneUpdater;
import illarion.download.gui.view.UninstallView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GuiApplication extends Application implements Storyboard {
    private static final double SCENE_WIDTH = 620.0;
    private static final double SCENE_HEIGHT = 410.0;

    private int currentScene = -1;
    private static final int SCENE_SELECT_DATA = 0;
    private static final int SCENE_SELECT_USER = 1;
    private static final int SCENE_MAIN = 2;

    private GuiModel model;

    @Nullable
    private Stage stage;

    @Nullable
    private ConfigSystem cfg;

    @Override
    public void start(@Nonnull Stage stage) throws Exception {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        stage.initStyle(StageStyle.TRANSPARENT);
        model = new GuiModel(stage, getHostServices(), this);

        this.stage = stage;

        stage.getIcons().add(new Image("illarion_download256.png"));

        nextScene();
        stage.setResizable(false);
        stage.show();
    }

    public void setScene(@Nonnull Parent sceneContent) {
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

    private void loadConfig() {
        if (cfg == null) {
            DirectoryManager dm = DirectoryManager.getInstance();
            cfg = new ConfigSystem(dm.resolveFile(DirectoryManager.Directory.User, "download.xcfgz"));
            cfg.setDefault("channelClient", 0);
            cfg.setDefault("channelEasyNpc", 1);
            cfg.setDefault("channelEasyQuest", 1);
            cfg.setDefault("channelMapEditor", 1);

            model.setConfig(cfg);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public boolean hasNextScene() {
        return currentScene < SCENE_MAIN;
    }

    @Override
    public void nextScene() throws IOException {
        loadConfig();
        initLogs();

        if (hasNextScene()) {
            showNormal();
        }
    }

    private static void initLogs() {
        Path dir = DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User);
        System.setProperty("log_dir", dir.toAbsolutePath().toString());

        //Reload:
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(lc);
        lc.reset();
        try {
            ci.autoConfig();
        } catch (JoranException ignored) {
        }
    }

    @Override
    public void showOptions() throws IOException {
        setScene(new ChannelSelectView(model));
    }

    @Override
    public void showUninstall() throws IOException {
        setScene(new UninstallView(model));
    }

    @Override
    public void showNormal() throws IOException {
        setScene(new MainView(model));
    }
}

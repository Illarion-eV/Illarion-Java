package illarion.download.gui;

import illarion.common.config.ConfigSystem;
import illarion.common.util.DirectoryManager;
import illarion.download.gui.model.GuiModel;
import illarion.download.gui.view.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

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

    public void setScene(@Nonnull final Parent sceneContent) {
        if (stage == null) {
            return;
        }

        final Scene scene = new Scene(sceneContent, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(null);
        if (sceneContent instanceof SceneUpdater) {
            ((SceneUpdater) sceneContent).updateScene(scene);
        }
        stage.setScene(scene);
    }

    private void loadConfig() {
        if (cfg == null) {
            final DirectoryManager dm = DirectoryManager.getInstance();
            if (dm.isDirectorySet(DirectoryManager.Directory.User)) {
                cfg = new ConfigSystem(dm.resolveFile(DirectoryManager.Directory.User, "download.xcfgz"));
                cfg.setDefault("channelClient", 0);
                cfg.setDefault("channelEasyNpc", 1);
                cfg.setDefault("channelEasyQuest", 1);
                cfg.setDefault("channelMapEditor", 1);

                model.setConfig(cfg);
            }
        }
    }

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public boolean hasNextScene() {
        return currentScene < SCENE_MAIN;
    }

    @Override
    public void nextScene() throws IOException {
        loadConfig();

        if (hasNextScene()) {
            while (true) {
                currentScene++;
                switch (currentScene) {
                    case SCENE_SELECT_DATA:
                        if (DirectoryManager.getInstance().isDirectorySet(DirectoryManager.Directory.Data)) {
                            continue;
                        }
                        break;
                    case SCENE_SELECT_USER:
                        if (DirectoryManager.getInstance().isDirectorySet(DirectoryManager.Directory.User)) {
                            continue;
                        }
                        break;
                    case SCENE_MAIN:
                        break;
                }
                showNormal();
                break;
            }
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
        switch (currentScene) {
            case SCENE_SELECT_DATA:
                setScene(new DataDirSelectView(model));
                break;
            case SCENE_SELECT_USER:
                setScene(new UserDirSelectView(model));
                break;
            case SCENE_MAIN:
                setScene(new MainView(model));
                break;
        }
    }
}

package illarion.download.gui;

import illarion.download.gui.model.GuiModel;
import illarion.download.gui.view.DataDirSelectView;
import illarion.download.gui.view.MainView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GuiApplication extends Application {
    private static final double SCENE_WIDTH = 600.0;
    private static final double SCENE_HEIGHT = 400.0;

    @Nullable
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        stage.initStyle(StageStyle.TRANSPARENT);
        final GuiModel model = new GuiModel(stage, getHostServices());

        this.stage = stage;

        setScene(new DataDirSelectView(model));
        stage.setResizable(false);
        stage.show();
    }

    public void setScene(@Nonnull final Parent sceneContent) {
        if (stage == null) {
            return;
        }
        stage.setScene(new Scene(sceneContent, SCENE_WIDTH, SCENE_HEIGHT));
        stage.getScene().setFill(null);
    }

    public static void main(final String[] args) {

        launch();
    }
}

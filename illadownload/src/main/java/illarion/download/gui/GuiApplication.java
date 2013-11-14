package illarion.download.gui;

import illarion.download.gui.model.GuiModel;
import illarion.download.gui.view.DataDirSelectView;
import illarion.download.gui.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GuiApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.initStyle(StageStyle.TRANSPARENT);
        final GuiModel model = new GuiModel(stage, getHostServices());

        stage.setScene(new Scene(new MainView(model), 620, 410));
        stage.getScene().setFill(Color.TRANSPARENT);
        stage.show();

    }

    public static void main(final String[] args) {

        launch();
    }
}

package illarion.download.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.layout.AnchorPane;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MainViewController extends AbstractController {
    @FXML
    public AnchorPane newsPane;
    @FXML
    public AnchorPane questsPane;

    public void initialize() {
        final ProgressIndicatorBuilder builder = ProgressIndicatorBuilder.create();
        builder.progress(0.5);

        newsPane.getChildren().add(builder.build());
        questsPane.getChildren().add(builder.build());
    }

    @FXML
    public void goToAccount(@Nonnull final ActionEvent actionEvent) {
        getModel().getHostServices().showDocument("http://illarion.org/community/account/index.php");
    }
}

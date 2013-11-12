package illarion.download.gui.controller;

import illarion.download.gui.model.GuiModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ApplicationController implements Controller {
    @FXML
    public AnchorPane footer;

    @FXML
    public AnchorPane content;

    @FXML
    public AnchorPane rootPane;

    private GuiModel model;

    private double initialX;
    private double initialY;

    public void initialize() {
        rootPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.MIDDLE) {
                    initialX = me.getSceneX();
                    initialY = me.getSceneY();
                }
            }
        });

        rootPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.MIDDLE) {
                    model.getStage().setX(me.getScreenX() - initialX);
                    model.getStage().setY(me.getScreenY() - initialY);
                }
            }
        });
    }

    @FXML
    public void close(@Nonnull final ActionEvent event) {
       model.getStage().close();
    }

    @FXML
    public void minimize(@Nonnull final ActionEvent event) {
        model.getStage().setIconified(true);
    }

    @FXML
    public void gotoAccount(@Nonnull final ActionEvent actionEvent) {
        model.getHostServices().showDocument("http://illarion.org/community/account/index.php");
    }

    @Override
    public void setModel(@Nonnull final GuiModel model) {
        this.model = model;
    }
}

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
public class ApplicationController extends AbstractController {
    @FXML
    public AnchorPane footer;

    @FXML
    public AnchorPane content;

    @FXML
    public AnchorPane rootPane;

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
                    getModel().getStage().setX(me.getScreenX() - initialX);
                    getModel().getStage().setY(me.getScreenY() - initialY);
                }
            }
        });
    }

    @FXML
    public void close(@Nonnull final ActionEvent event) {
        getModel().getStage().close();
    }

    @FXML
    public void minimize(@Nonnull final ActionEvent event) {
        getModel().getStage().setIconified(true);
    }
}

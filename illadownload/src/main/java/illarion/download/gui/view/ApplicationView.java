package illarion.download.gui.view;

import illarion.download.gui.model.GuiModel;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ApplicationView extends AnchorPane {
    public ApplicationView(@Nonnull final GuiModel model) throws IOException {
        final Parent root = Util.loadFXML("applicationFrame.fxml", model, Util.loadResourceBundle("applicationFrame"));
        getStylesheets().add("applicationFrame.css");
        getChildren().add(root);
        setBottomAnchor(root, 0.0);
        setTopAnchor(root, 0.0);
        setLeftAnchor(root, 0.0);
        setRightAnchor(root, 0.0);
    }

    protected final Pane getContentPane() {
        return (Pane) lookup("#content");
    }

    protected final Pane getFooterPane() {
        return (Pane) lookup("#footer");
    }

    protected final void setContent(@Nonnull final Node content, @Nonnull final Node footer) {
        getContentPane().getChildren().add(content);
        getFooterPane().getChildren().add(footer);

        maximizeOnAnchorPane(content);
        maximizeOnAnchorPane(footer);
    }

    private static void maximizeOnAnchorPane(@Nonnull final Node node) {
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
    }
}

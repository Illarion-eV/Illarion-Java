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
        boolean isApplet;
        try {
            isApplet =  model.getHostServices().getWebContext() != null;
        } catch (Exception e) {
            isApplet = false;
        }

        final Parent root = Util.loadFXML("applicationFrame.fxml", model, Util.loadResourceBundle("applicationFrame"));

        if (isApplet) {
            root.getStyleClass().add("applet");
            root.lookup("#header").setVisible(false);
        } else {
            root.getStyleClass().add("application");
        }

        final String stylesheet = Util.getCssReference("applicationFrame");
        if (stylesheet != null) {
            getStylesheets().add(stylesheet);
        } else {
            System.out.println("Failed to locate stylesheet: applicationFrame");
        }
        getChildren().add(root);
        maximizeOnAnchorPane(root);
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
        setRightAnchor(node, 0.0);
        setLeftAnchor(node, 0.0);
        setTopAnchor(node, 0.0);
        setBottomAnchor(node, 0.0);
    }
}

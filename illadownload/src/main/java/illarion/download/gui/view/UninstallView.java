package illarion.download.gui.view;

import illarion.download.gui.model.GuiModel;
import javafx.scene.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class UninstallView extends ApplicationView {
    public UninstallView(@Nonnull final GuiModel model) throws IOException {
        super(model);

        final Node viewContents = Util.loadFXML("uninstallView.fxml", model, Util.loadResourceBundle("uninstallView"));

        setContent(viewContents.lookup("#content"), viewContents.lookup("#footer"));
    }
}

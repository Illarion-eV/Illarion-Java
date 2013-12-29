package illarion.download.gui.view;

import illarion.download.gui.model.GuiModel;
import javafx.scene.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MainView extends ApplicationView {
    public MainView(@Nonnull final GuiModel model) throws IOException {
        super(model);

        final Node viewContents = Util.loadFXML("mainView.fxml", model, Util.loadResourceBundle("mainView"));

        setContent(viewContents.lookup("#content"), viewContents.lookup("#footer"));
    }
}

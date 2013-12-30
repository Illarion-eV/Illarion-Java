package illarion.download.gui.view;

import illarion.download.gui.model.GuiModel;
import javafx.scene.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DataDirSelectView extends ApplicationView {
    public DataDirSelectView(@Nonnull final GuiModel model) throws IOException {
        super(model);

        final Node viewContents = Util.loadFXML("dataDirSelectView.fxml", model,
                Util.loadResourceBundle("dataDirSelectView"));

        setContent(viewContents.lookup("#content"), viewContents.lookup("#footer"));
    }
}

package illarion.download.gui.view;

import illarion.download.gui.model.GuiModel;
import javafx.scene.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class UserDirSelectView extends ApplicationView {
    public UserDirSelectView(@Nonnull final GuiModel model) throws IOException {
        super(model);

        final Node viewContents = Util.loadFXML("userDirSelectView.fxml", model,
                Util.loadResourceBundle("userDirSelectView"));

        setContent(viewContents.lookup("#content"), viewContents.lookup("#footer"));
    }
}

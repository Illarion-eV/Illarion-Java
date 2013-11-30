package illarion.download.gui.view;

import illarion.download.gui.model.GuiModel;
import javafx.scene.Node;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Andreas Grob &lt;vilarion@illarion.org&gt;
 */
public class ChannelSelectView extends ApplicationView {
    public ChannelSelectView(@Nonnull final GuiModel model) throws IOException {
        super(model);

        final Node viewContents = Util.loadFXML("channelSelectView.fxml", model,
                Util.loadResourceBundle("channelSelectView"));

        setContent(viewContents.lookup("#content"), viewContents.lookup("#footer"));
    }
}

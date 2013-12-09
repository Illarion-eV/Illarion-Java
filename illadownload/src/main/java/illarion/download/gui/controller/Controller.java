package illarion.download.gui.controller;

import illarion.download.gui.model.GuiModel;
import javafx.fxml.Initializable;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Controller extends Initializable {
    void setModel(@Nonnull GuiModel model);
}

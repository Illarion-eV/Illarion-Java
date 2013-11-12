package illarion.download.gui.controller;

import illarion.download.gui.model.GuiModel;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Controller {
    void setModel(@Nonnull GuiModel model);
}

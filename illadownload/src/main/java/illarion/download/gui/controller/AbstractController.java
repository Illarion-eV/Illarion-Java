package illarion.download.gui.controller;

import illarion.download.gui.model.GuiModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the abstract implementation for a controller. It implements the storage for the model reference.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("NullableProblems")
public class AbstractController implements Controller {
    /**
     * The stored reference to the GUI model.
     */
    @Nullable
    private GuiModel model;

    protected AbstractController() {}

    @Override
    public void setModel(@Nonnull final GuiModel model) {
        this.model = model;
    }

    @Nonnull
    public GuiModel getModel() {
        if (model == null) {
            throw new NullPointerException("GUIModel was not set yet.");
        }
        return model;
    }
}

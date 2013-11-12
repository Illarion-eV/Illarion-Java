package illarion.download.gui.model;

import javafx.application.HostServices;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GuiModel {
    @Nonnull
    private final Stage stage;

    @Nonnull
    private final HostServices hostServices;

    public GuiModel(@Nonnull final Stage stage, @Nonnull final HostServices hostServices) {
        this.stage = stage;
        this.hostServices = hostServices;
    }

    @Nonnull
    public Stage getStage() {
        return stage;
    }

    @Nonnull
    public HostServices getHostServices() {
        return hostServices;
    }
}

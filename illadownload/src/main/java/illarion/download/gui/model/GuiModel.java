package illarion.download.gui.model;

import illarion.common.config.Config;
import illarion.download.gui.Storyboard;
import javafx.application.HostServices;
import javafx.stage.Stage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GuiModel {
    @Nonnull
    private final Stage stage;

    @Nonnull
    private final HostServices hostServices;

    @Nonnull
    private final Storyboard storyboard;

    @Nullable
    private Config config;

    public GuiModel(@Nonnull final Stage stage, @Nonnull final HostServices hostServices,
                    @Nonnull final Storyboard storyboard) {
        this.stage = stage;
        this.hostServices = hostServices;
        this.storyboard = storyboard;
    }

    @Nonnull
    public Stage getStage() {
        return stage;
    }

    @Nonnull
    public HostServices getHostServices() {
        return hostServices;
    }

    @Nonnull
    public Storyboard getStoryboard() {
        return storyboard;
    }

    public void setConfig(@Nonnull final Config config) {
        this.config = config;
    }

    @Nullable
    public Config getConfig() {
        return config;
    }
}

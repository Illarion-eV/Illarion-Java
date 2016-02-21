package illarion.client.gui.controller.create;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.util.account.AccountSystem;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CultureScreenController implements ScreenController {
    @Nonnull
    private final AccountSystem accountSystem;
    @Nonnull
    private final GameContainer container;
    @Nullable
    private List<CultureOption> options;

    public CultureScreenController(@Nonnull AccountSystem accountSystem, @Nonnull GameContainer container) {
        this.accountSystem = accountSystem;
        this.container = container;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        options = new ArrayList<>(6);
        for (int i = 1; i <= 6; i++) {
            options.add(new CultureOption(screen, "culture" + Integer.toString(i)));
        }
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    private static final class CultureOption {
        @Nonnull
        private final Element container;
        @Nonnull
        private final Button button;
        @Nonnull
        private final Element image;

        public CultureOption(@Nonnull Screen screen, @Nonnull String containerKey) {
            container = Objects.requireNonNull(screen.findElementById(containerKey));
            button = Objects.requireNonNull(container.findNiftyControl("#button", Button.class));
            image = Objects.requireNonNull(container.findElementById("#image"));
        }
    }
}

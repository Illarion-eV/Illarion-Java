package illarion.common.bug;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ReportDialogFactorySwing implements ReportDialogFactory {
    @Nonnull
    @Override
    public ReportDialog createDialog() {
        return new ReportDialogSwing();
    }
}

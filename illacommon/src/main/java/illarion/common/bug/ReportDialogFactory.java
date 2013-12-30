package illarion.common.bug;

import javax.annotation.Nonnull;

/**
 * This is the definition of a factory that creates report dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface ReportDialogFactory {
    /**
     * Create a new instance of the report dialog.
     *
     * @return the instance of the report dialog that is supposed to be used.
     */
    @Nonnull
    ReportDialog createDialog();
}

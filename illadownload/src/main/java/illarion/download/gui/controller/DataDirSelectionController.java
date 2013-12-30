package illarion.download.gui.controller;

import illarion.common.util.DirectoryManager;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DataDirSelectionController extends AbstractDirSelectionController {
    public DataDirSelectionController() {
        super(DirectoryManager.Directory.Data);
    }
}

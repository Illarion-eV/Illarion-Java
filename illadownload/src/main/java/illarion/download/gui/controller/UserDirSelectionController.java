package illarion.download.gui.controller;

import illarion.common.util.DirectoryManager;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class UserDirSelectionController extends AbstractDirSelectionController {
    public UserDirSelectionController() {
        super(DirectoryManager.Directory.User);
    }
}

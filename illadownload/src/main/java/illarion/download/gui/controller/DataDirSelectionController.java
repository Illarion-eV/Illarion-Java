package illarion.download.gui.controller;

import illarion.common.util.DirectoryManager;
import illarion.download.gui.model.GuiModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DataDirSelectionController extends AbstractDirSelectionController {
    protected DataDirSelectionController() {
        super(DirectoryManager.Directory.Data);
    }
}

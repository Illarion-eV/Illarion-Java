package illarion.download.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Andreas Grob &lt;vilarion@illarion.org&gt;
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ChannelSelectionController extends AbstractController {

    public ComboBox<String> targetClient;
    public ComboBox<String> targetEasyNpc;
    public ComboBox<String> targetEasyQuest;
    public ComboBox<String> targetMapEditor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final ObservableList<String> targets = FXCollections.observableArrayList(
                resourceBundle.getString("optionRelease"),
                resourceBundle.getString("optionSnapshot")
        );
        targetClient.setItems(targets);
        targetEasyNpc.setItems(targets);
        targetEasyQuest.setItems(targets);
        targetMapEditor.setItems(targets);
    }
}

package illarion.download.gui.controller;

import illarion.common.util.ProgressMonitor;
import illarion.common.util.ProgressMonitorCallback;
import illarion.download.launcher.JavaLauncher;
import illarion.download.maven.MavenDownloader;
import illarion.download.maven.MavenDownloaderCallback;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MainViewController extends AbstractController implements MavenDownloaderCallback, ProgressMonitorCallback {
    @FXML
    public AnchorPane newsPane;
    @FXML
    public AnchorPane questsPane;
    @FXML
    public ProgressBar progress;
    @FXML
    public Label progressDescription;
    @FXML
    public Button launchEasyNpcButton;
    @FXML
    public Button launchEasyQuestButton;
    @FXML
    public Button launchMapEditButton;
    @FXML
    public Button launchClientButton;

    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        progress.setProgress(0.0);
        progressDescription.setText(resourceBundle.getString("selectStartApp"));
    }

    @Nullable
    private String launchClass;

    @FXML
    public void goToAccount(@Nonnull final ActionEvent actionEvent) {
        getModel().getHostServices().showDocument("http://illarion.org/community/account/index.php");
    }

    @FXML
    public void startEasyNpc(@Nonnull final ActionEvent actionEvent) {
        updateLaunchButtons(false, false, true, false, false);
        launch("org.illarion", "easynpc", "illarion.easynpc.gui.MainFrame");
    }

    @FXML
    public void startEasyQuest(@Nonnull final ActionEvent actionEvent) {
        updateLaunchButtons(false, false, false, true, false);
        launch("org.illarion", "easyquest", "illarion.easyquest.gui.MainFrame");
    }

    @FXML
    public void startMapEdit(@Nonnull final ActionEvent actionEvent) {
        updateLaunchButtons(false, false, false, false, true);
        launch("org.illarion", "mapeditor", "illarion.mapedit.MapEditor");
    }

    @FXML
    public void launchClient(@Nonnull final ActionEvent actionEvent) {
        updateLaunchButtons(false, true, false, false, false);
        launch("org.illarion", "client", "illarion.client.IllaClient");
    }

    private void updateLaunchButtons(final boolean enabled, final boolean client, final boolean easyNpc,
                                     final boolean easyQuest, final boolean mapEdit) {
        if (Platform.isFxApplicationThread()) {
            launchClientButton.setDisable(!enabled);
            launchMapEditButton.setDisable(!enabled);
            launchEasyQuestButton.setDisable(!enabled);
            launchEasyNpcButton.setDisable(!enabled);
            if (enabled) {
                launchClientButton.setText(resourceBundle.getString("launchClient"));
                launchMapEditButton.setText(resourceBundle.getString("launchMapEdit"));
                launchEasyQuestButton.setText(resourceBundle.getString("launchEasyQuest"));
                launchEasyNpcButton.setText(resourceBundle.getString("launchEasyNpc"));
            } else {
                launchClientButton.setText(resourceBundle.getString(client ? "starting" : "launchClient"));
                launchMapEditButton.setText(resourceBundle.getString(mapEdit ? "starting" : "launchMapEdit"));
                launchEasyQuestButton.setText(resourceBundle.getString(easyQuest ? "starting" : "launchEasyQuest"));
                launchEasyNpcButton.setText(resourceBundle.getString(easyNpc ? "starting" : "launchEasyNpc"));
            }
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    updateLaunchButtons(enabled, client, easyNpc, easyQuest, mapEdit);
                }
            });
        }
    }

    private void launch(@Nonnull final String groupId, @Nonnull final String artifactId,
                        @Nonnull final String launchClass) {
        this.launchClass = launchClass;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final MavenDownloader downloader = new MavenDownloader(true);
                    downloader.downloadArtifact(groupId,artifactId, MainViewController.this);
                } catch (@Nonnull final Exception e) {
                    cancelLaunch();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            progress.setProgress(0.0);
                            progressDescription.setText(e.getLocalizedMessage());
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void reportNewState(@Nonnull final State state, @Nullable final ProgressMonitor progress) {
        if (Platform.isFxApplicationThread()) {
            switch (state) {
                case SearchingNewVersion:
                    progressDescription.setText(resourceBundle.getString("searchingNewVersion"));
                    break;
                case ResolvingDependencies:
                    progressDescription.setText(resourceBundle.getString("resolvingDependencies"));
                    break;
                case ResolvingArtifacts:
                    progressDescription.setText(resourceBundle.getString("resolvingArtifacts"));
                    break;
            }
            if (progress == null) {
                this.progress.setProgress(-1.0);
            } else {
                progress.setCallback(this);
                this.progress.setProgress(progress.getProgress());
            }
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    reportNewState(state, progress);
                }
            });
        }
    }

    @Override
    public void resolvingDone(@Nullable final Collection<File> classpath) {
        if (classpath == null || launchClass == null) {
            cancelLaunch();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    progress.setProgress(1.0);
                    progressDescription.setText(resourceBundle.getString("errorClasspathNull"));
                }
            });
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    progress.setProgress(1.0);
                    progressDescription.setText(resourceBundle.getString("launchApplication"));
                }
            });
            final JavaLauncher launcher = new JavaLauncher(true);
            if (launcher.launch(classpath, launchClass)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        getModel().getStage().close();
                    }
                });
                cancelLaunch();
            } else {
                cancelLaunch();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progress.setProgress(1.0);
                        progressDescription.setText(launcher.getErrorData());
                    }
                });
            }
        }
    }

    @Override
    public void updatedProgress(@Nonnull final ProgressMonitor monitor) {
        progress.setProgress(monitor.getProgress());
    }

    private void cancelLaunch() {
        updateLaunchButtons(true, false, false, false, false);
    }

    public void showOptions(@Nonnull final ActionEvent actionEvent) {
        try {
            getModel().getStoryboard().showOptions();
        } catch (@Nonnull final IOException ignored) {
        }
    }
}

package illarion.download.gui.controller;

import illarion.common.util.ProgressMonitor;
import illarion.common.util.ProgressMonitorCallback;
import illarion.download.launcher.JavaLauncher;
import illarion.download.maven.MavenDownloader;
import illarion.download.maven.MavenDownloaderCallback;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.layout.AnchorPane;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
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

    private ResourceBundle resourceBundle;

    private SimpleStringProperty progressDescriptionText = new SimpleStringProperty();
    private SimpleDoubleProperty progressValue = new SimpleDoubleProperty();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        progress.progressProperty().bindBidirectional(progressValue);
        progressDescription.textProperty().bindBidirectional(progressDescriptionText);

        progressValue.set(0.0);
        progressDescriptionText.set(resourceBundle.getString("selectStartApp"));
    }

    @Nullable
    private String launchClass;

    @FXML
    public void goToAccount(@Nonnull final ActionEvent actionEvent) {
        getModel().getHostServices().showDocument("http://illarion.org/community/account/index.php");
    }

    public void launchClient(@Nonnull final ActionEvent actionEvent) {
        launchClass = "illarion.client.IllaClient";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                final MavenDownloader downloader = new MavenDownloader(true);
                downloader.downloadArtifact("org.illarion", "client", MainViewController.this);
                } catch (@Nonnull final Exception e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            progressValue.set(0.0);
                            progressDescriptionText.set(e.getLocalizedMessage());
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
                    progressDescriptionText.set(resourceBundle.getString("searchingNewVersion"));
                    break;
                case ResolvingDependencies:
                    progressDescriptionText.set(resourceBundle.getString("resolvingDependencies"));
                    break;
                case ResolvingArtifacts:
                    progressDescriptionText.set(resourceBundle.getString("resolvingArtifacts"));
                    break;
            }
            if (progress == null) {
                progressValue.set(-1.0);
            } else {
                progress.setCallback(this);
                progressValue.set(progress.getProgress());
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
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    progressValue.set(1.0);
                    progressDescriptionText.set(resourceBundle.getString("errorClasspathNull"));
                }
            });
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    progressValue.set(1.0);
                    progressDescriptionText.set(resourceBundle.getString("launchApplication"));
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
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressValue.set(1.0);
                        progressDescriptionText.set(launcher.getErrorData());
                    }
                });
            }
        }
    }

    @Override
    public void updatedProgress(@Nonnull final ProgressMonitor monitor) {
        progressValue.set(monitor.getProgress());
    }
}

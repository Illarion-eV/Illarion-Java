/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.download.gui.controller;

import illarion.common.config.Config;
import illarion.common.util.ProgressMonitor;
import illarion.common.util.ProgressMonitorCallback;
import illarion.download.cleanup.Cleaner;
import illarion.download.launcher.JavaLauncher;
import illarion.download.maven.MavenDownloader;
import illarion.download.maven.MavenDownloaderCallback;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @FXML
    public Button uninstallButton;

    private ResourceBundle resourceBundle;

    private static final Logger LOGGER = LoggerFactory.getLogger(MainViewController.class);

    @Override
    public void initialize(URL url, @Nonnull ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        progress.setProgress(0.0);
        progressDescription.setText(resourceBundle.getString("selectStartApp"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readNewsAndQuests();
                } catch (@Nonnull XmlPullParserException | IOException | ParseException e) {
                    LOGGER.error("Failed reading news and quests.", e);
                }
            }
        }).start();
    }

    @Nullable
    private String launchClass;
    private boolean useSnapshots;

    private static class NewsQuestEntry {
        @Nonnull
        public final String title;
        public final String timeStamp;
        public final URL linkTarget;

        NewsQuestEntry(@Nonnull String title, @Nonnull String timeStamp, @Nonnull URL linkTarget) {
            this.title = title;
            this.timeStamp = timeStamp;
            this.linkTarget = linkTarget;
        }
    }

    private void readNewsAndQuests() throws XmlPullParserException, IOException, ParseException {
        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
        parserFactory.setValidating(false);
        parserFactory.setNamespaceAware(false);

        XmlPullParser parser = parserFactory.newPullParser();
        URL src = new URL("http://illarion.org/data/xml_launcher.php");
        parser.setInput(new BufferedInputStream(src.openStream()), "UTF-8");

        Collection<NewsQuestEntry> newsList = new ArrayList<>();
        Collection<NewsQuestEntry> questList = new ArrayList<>();

        int current = parser.nextTag();
        while ((current != XmlPullParser.START_TAG) || !"launcher".equals(parser.getName())) {
            current = parser.nextTag();
        }
        parseLauncherXml(parser, newsList, questList);

        showNewsQuestInList(newsList, newsPane);
        showNewsQuestInList(questList, questsPane);
    }

    private static void parseLauncherXml(
            @Nonnull XmlPullParser parser,
            @Nonnull Collection<NewsQuestEntry> newsList,
            @Nonnull Collection<NewsQuestEntry> questList)
            throws IOException, XmlPullParserException, ParseException {
        while (true) {
            int current = parser.nextToken();
            switch (current) {
                case XmlPullParser.END_DOCUMENT:
                    return;
                case XmlPullParser.END_TAG:
                    if ("launcher".equals(parser.getName())) {
                        return;
                    }
                    break;
                case XmlPullParser.START_TAG:
                    switch (parser.getName()) {
                        case "news":
                            parserNewsXml(parser, newsList);
                            break;
                        case "quests":
                            parserQuestXml(parser, questList);
                            break;
                    }
                    break;
            }
        }
    }

    private static void parserNewsXml(
            @Nonnull XmlPullParser parser, @Nonnull Collection<NewsQuestEntry> newsList)
            throws IOException, XmlPullParserException, ParseException {
        boolean useGerman = Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage());

        String title = null;
        String timestamp = null;
        URL linkTarget = null;

        DateFormat parsingFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

        while (true) {
            int current = parser.nextTag();
            switch (current) {
                case XmlPullParser.END_DOCUMENT:
                    return;
                case XmlPullParser.END_TAG:
                    switch (parser.getName()) {
                        case "news":
                            return;
                        case "item":
                            if ((title != null) && (timestamp != null) && (linkTarget != null)) {
                                newsList.add(new NewsQuestEntry(title, timestamp, linkTarget));
                            }
                            break;
                    }
                    break;
                case XmlPullParser.START_TAG:
                    switch (parser.getName()) {
                        case "id":
                            parser.nextText();
                            break;
                        case "title":
                            boolean german = "de".equals(parser.getAttributeValue(null, "lang"));
                            String text = parser.nextText();
                            if (german == useGerman) {
                                title = text;
                            }
                            break;
                        case "link":
                            linkTarget = new URL(parser.nextText());
                            break;
                        case "date":
                            Date date = parsingFormat.parse(parser.nextText());
                            timestamp = dateFormat.format(date);
                            break;
                    }
                    break;
            }
        }
    }

    private static void parserQuestXml(
            @Nonnull XmlPullParser parser, @Nonnull Collection<NewsQuestEntry> newsList)
            throws IOException, XmlPullParserException, ParseException {
        boolean useGerman = Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage());

        String title = null;
        String timestamp = null;
        URL linkTarget = null;

        DateFormat parsingFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

        while (true) {
            int current = parser.nextTag();
            switch (current) {
                case XmlPullParser.END_DOCUMENT:
                    return;
                case XmlPullParser.END_TAG:
                    switch (parser.getName()) {
                        case "quests":
                            return;
                        case "item":
                            if ((title != null) && (timestamp != null) && (linkTarget != null)) {
                                newsList.add(new NewsQuestEntry(title, timestamp, linkTarget));
                            }
                            break;
                    }
                    break;
                case XmlPullParser.START_TAG:
                    switch (parser.getName()) {
                        case "id":
                            parser.nextText();
                            break;
                        case "title":
                            boolean german = "de".equals(parser.getAttributeValue(null, "lang"));
                            String text = parser.nextText();
                            if (german == useGerman) {
                                title = text;
                            }
                            break;
                        case "link":
                            linkTarget = new URL(parser.nextText());
                            break;
                        case "date":
                            Date date = parsingFormat.parse(parser.nextText());
                            timestamp = dateFormat.format(date);
                            break;
                    }
                    break;
            }
        }
    }

    private void showNewsQuestInList(@Nonnull Collection<NewsQuestEntry> list, @Nonnull final Pane display) {
        final VBox storage = new VBox();
        storage.setFillWidth(true);
        AnchorPane.setBottomAnchor(storage, 0.0);
        AnchorPane.setTopAnchor(storage, 0.0);
        AnchorPane.setLeftAnchor(storage, 3.0);
        AnchorPane.setRightAnchor(storage, 3.0);

        for (@Nonnull final NewsQuestEntry entry : list) {
            BorderPane line = new BorderPane();
            line.getStyleClass().add("linkPane");
            line.setLeft(new Label(entry.title));
            line.setRight(new Label(entry.timeStamp));
            line.setCursor(Cursor.HAND);

            line.setMouseTransparent(false);
            line.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if ((mouseEvent.getButton() == MouseButton.PRIMARY) &&
                            (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) &&
                            (mouseEvent.getClickCount() == 1)) {
                        getModel().getHostServices().showDocument(entry.linkTarget.toExternalForm());
                    }
                }
            });
            storage.getChildren().add(line);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                display.getChildren().add(storage);
            }
        });
    }

    @FXML
    public void goToAccount(@Nonnull ActionEvent actionEvent) {
        getModel().getHostServices().showDocument("http://illarion.org/community/account/index.php");
    }

    @FXML
    public void startEasyNpc(@Nonnull ActionEvent actionEvent) {
        updateLaunchButtons(false, false, true, false, false);
        launch("org.illarion", "easynpc", "illarion.easynpc.gui.MainFrame", "channelEasyNpc");
    }

    @FXML
    public void startEasyQuest(@Nonnull ActionEvent actionEvent) {
        updateLaunchButtons(false, false, false, true, false);
        launch("org.illarion", "easyquest", "illarion.easyquest.gui.MainFrame", "channelEasyQuest");
    }

    @FXML
    public void startMapEdit(@Nonnull ActionEvent actionEvent) {
        updateLaunchButtons(false, false, false, false, true);
        launch("org.illarion", "mapeditor", "illarion.mapedit.MapEditor", "channelMapEditor");
    }

    @FXML
    public void launchClient(@Nonnull ActionEvent actionEvent) {
        updateLaunchButtons(false, true, false, false, false);
        launch("org.illarion", "client", "illarion.client.IllaClient", "channelClient");
    }

    private void updateLaunchButtons(
            final boolean enabled,
            final boolean client,
            final boolean easyNpc,
            final boolean easyQuest,
            final boolean mapEdit) {
        if (Platform.isFxApplicationThread()) {
            launchClientButton.setDisable(!enabled);
            launchMapEditButton.setDisable(!enabled);
            launchEasyQuestButton.setDisable(!enabled);
            launchEasyNpcButton.setDisable(!enabled);
            uninstallButton.setDisable(!enabled);
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

    private void launch(
            @Nonnull final String groupId,
            @Nonnull final String artifactId, @Nonnull String launchClass, @Nonnull String configKey) {
        Config cfg = getModel().getConfig();
        if (cfg == null) {
            throw new IllegalStateException("Can't show options without the config system");
        }

        this.launchClass = launchClass;
        this.useSnapshots = cfg.getInteger(configKey) == 1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int attempt = 0;
                while (attempt < 10) {
                    attempt++;
                    try {
                        MavenDownloader downloader = new MavenDownloader(useSnapshots, attempt);
                        downloader.downloadArtifact(groupId, artifactId, MainViewController.this);
                    } catch (@Nonnull Exception e) {
                        if (getInnerExceptionOfTime(SocketTimeoutException.class, e) != null) {
                            LOGGER.warn("Timeout detected. Restarting download with longer timeout.");
                            continue;
                        }
                        LOGGER.error("Error while resolving.", e);
                    }
                    break;
                }
            }
        }).start();
    }

    private static <T extends Throwable> T getInnerExceptionOfTime(
            @Nonnull Class<T> clazz, @Nonnull Throwable search) {
        @Nullable Throwable currentEx = search;
        while (currentEx != null) {
            if (currentEx.getClass().equals(clazz)) {
                //noinspection unchecked
                return (T) currentEx;
            }
            currentEx = currentEx.getCause();
        }
        return null;
    }

    @Override
    public void reportNewState(
            @Nonnull final State state,
            @Nullable final ProgressMonitor progress,
            final boolean offline,
            @Nullable final String detail) {
        if (Platform.isFxApplicationThread()) {
            switch (state) {
                case SearchingNewVersion:
                    progressDescription
                            .setText((offline ? "Offline: " : "") + resourceBundle.getString("searchingNewVersion") +
                                             ((detail == null) ? "" : (" - " + detail)));
                    break;
                case ResolvingDependencies:
                    progressDescription
                            .setText((offline ? "Offline: " : "") + resourceBundle.getString("resolvingDependencies") +
                                             ((detail == null) ? "" : (" - " + detail)));
                    break;
                case ResolvingArtifacts:
                    progressDescription
                            .setText((offline ? "Offline: " : "") + resourceBundle.getString("resolvingArtifacts") +
                                             ((detail == null) ? "" : (" - " + detail)));
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
                    reportNewState(state, progress, offline, detail);
                }
            });
        }
    }

    @Override
    public void resolvingDone(@Nonnull Collection<File> classpath) {
        if (launchClass == null) {
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
            final JavaLauncher launcher = new JavaLauncher(useSnapshots);
            if (launcher.launch(classpath, launchClass)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        getModel().getStage().close();
                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Cleaner cleaner = new Cleaner(Cleaner.Mode.Maintenance);
                        cleaner.clean();
                    }
                }).start();
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
    public void resolvingFailed(@Nonnull final Exception ex) {
        if (getInnerExceptionOfTime(SocketTimeoutException.class, ex) != null) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progress.setProgress(1.0);
                progressDescription.setText(ex.getLocalizedMessage());
            }
        });
    }

    @Override
    public void updatedProgress(@Nonnull ProgressMonitor monitor) {
        progress.setProgress(monitor.getProgress());
    }

    private void cancelLaunch() {
        updateLaunchButtons(true, false, false, false, false);
    }

    @FXML
    public void showOptions(@Nonnull ActionEvent actionEvent) {
        try {
            getModel().getStoryboard().showOptions();
        } catch (@Nonnull IOException ignored) {
        }
    }

    @FXML
    public void uninstall(@Nonnull ActionEvent actionEvent) {
        try {
            getModel().getStoryboard().showUninstall();
        } catch (@Nonnull IOException ignored) {
        }
    }
}

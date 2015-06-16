/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Contract;
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

    private ResourceBundle resourceBundle;

    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(MainViewController.class);

    @Override
    public void initialize(URL location, @Nonnull ResourceBundle resources) {
        resourceBundle = resources;
        progress.setProgress(0.0);
        progressDescription.setText(resources.getString("selectStartApp"));

        new Thread(() -> {
            try {
                readNewsAndQuests();
            } catch (@Nonnull XmlPullParserException | IOException | ParseException e) {
                log.error("Failed reading news and quests.", e);
            }
        }).start();

        EventHandler<KeyEvent> eventEventHandler = new EventHandler<KeyEvent>() {
            private final KeyCombination combo = new KeyCodeCombination(KeyCode.ENTER);

            @Override
            public void handle(@Nonnull KeyEvent event) {
                if (combo.match(event)) {
                    launchClientButton.fire();
                }
                event.consume();
            }
        };
        launchEasyNpcButton.setOnKeyReleased(eventEventHandler);
        launchEasyQuestButton.setOnKeyReleased(eventEventHandler);
        launchMapEditButton.setOnKeyReleased(eventEventHandler);
        launchClientButton.requestFocus();
    }

    @Nullable
    private String launchClass;
    private boolean useSnapshots;

    private static class NewsQuestEntry implements Comparable<NewsQuestEntry> {
        @Nonnull
        public final String title;
        @Nullable
        public final Date timeStamp;
        @Nonnull
        public final URL linkTarget;

        NewsQuestEntry(@Nonnull String title, @Nullable Date timeStamp, @Nonnull URL linkTarget) {
            this.title = title;
            this.timeStamp = (timeStamp == null) ? null : (Date) timeStamp.clone();
            this.linkTarget = linkTarget;
        }

        @Override
        public int compareTo(@Nonnull NewsQuestEntry o) {
            if ((timeStamp == null) && (o.timeStamp != null)) {
                return -1;
            }

            if ((timeStamp != null) && (o.timeStamp == null)) {
                return 1;
            }

            int compare = (timeStamp == null) ? 0 : timeStamp.compareTo(o.timeStamp);
            if (compare == 0) {
                return title.compareTo(o.title);
            }
            return compare;
        }
    }

    private void readNewsAndQuests() throws XmlPullParserException, IOException, ParseException {
        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
        parserFactory.setValidating(false);
        parserFactory.setNamespaceAware(false);

        XmlPullParser parser = parserFactory.newPullParser();
        URL src = new URL("http://illarion.org/data/xml_launcher.php");
        parser.setInput(new BufferedInputStream(src.openStream()), "UTF-8");

        List<NewsQuestEntry> newsList = new ArrayList<>();
        List<NewsQuestEntry> questList = new ArrayList<>();

        int current = parser.nextTag();
        while ((current != XmlPullParser.START_TAG) || !"launcher".equals(parser.getName())) {
            current = parser.nextTag();
        }
        parseLauncherXml(parser, newsList, questList);

        showNewsInList(newsList);
        showQuestsInList(questList);
    }

    private static void parseLauncherXml(
            @Nonnull XmlPullParser parser,
            @Nonnull List<NewsQuestEntry> newsList,
            @Nonnull List<NewsQuestEntry> questList) throws IOException, XmlPullParserException, ParseException {
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
                            parserEntryXml(parser, newsList);
                            break;
                        case "quests":
                            parserEntryXml(parser, questList);
                            break;
                    }
                    break;
            }
        }
    }

    private static void parserEntryXml(
            @Nonnull XmlPullParser parser, @Nonnull List<NewsQuestEntry> list)
            throws IOException, XmlPullParserException, ParseException {
        boolean useGerman = Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage());

        String title = null;
        Date timestamp = null;
        URL linkTarget = null;

        DateFormat parsingFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

        while (true) {
            int current = parser.nextTag();
            switch (current) {
                case XmlPullParser.END_DOCUMENT:
                    log.error("Reached unexpected end of document.");
                    return;
                case XmlPullParser.END_TAG:
                    switch (parser.getName()) {
                        case "quests":
                            Collections.sort(list);
                            return;
                        case "news":
                            Collections.sort(list);
                            Collections.reverse(list);
                            return;
                        case "item":
                            list.add(new NewsQuestEntry(title, timestamp, linkTarget));
                            timestamp = null;
                            title = null;
                            linkTarget = null;
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
                            if (isNullOrEmpty(title) || (!isNullOrEmpty(text) && (german == useGerman))) {
                                title = text;
                            }
                            break;
                        case "link":
                            String linkUrl = parser.nextText();
                            if (!isNullOrEmpty(linkUrl)) {
                                linkTarget = new URL(linkUrl);
                            }
                            break;
                        case "date":
                            String textTimeStamp = parser.nextText();
                            if (!isNullOrEmpty(textTimeStamp)) {
                                timestamp = parsingFormat.parse(textTimeStamp);
                            }
                            break;
                    }
                    break;
            }
        }
    }

    @Contract(value = "null -> true", pure = true)
    private static boolean isNullOrEmpty(@Nullable String testString) {
        return (testString == null) || testString.isEmpty();
    }

    private void showNewsInList(@Nonnull Iterable<NewsQuestEntry> list) {
        showNewsQuestInList(list, newsPane, DateFormat.getDateInstance(DateFormat.MEDIUM));
    }

    private void showQuestsInList(@Nonnull Iterable<NewsQuestEntry> list) {
        showNewsQuestInList(list, questsPane, DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT));
    }

    private void showNewsQuestInList(@Nonnull Iterable<NewsQuestEntry> list, @Nonnull Pane display, @Nonnull
    DateFormat dateFormat) {
        VBox storage = new VBox();
        storage.setFillWidth(true);
        AnchorPane.setBottomAnchor(storage, 0.0);
        AnchorPane.setTopAnchor(storage, 0.0);
        AnchorPane.setLeftAnchor(storage, 3.0);
        AnchorPane.setRightAnchor(storage, 3.0);

        int entryCount = 0;
        for (@Nonnull NewsQuestEntry entry : list) {
            if (entryCount == 4) {
                break;
            }
            entryCount += 1;

            BorderPane line = new BorderPane();
            line.getStyleClass().add("linkPane");

            Label title = new Label(entry.title);
            title.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);
            line.setCenter(title);
            BorderPane.setAlignment(title, Pos.BOTTOM_LEFT);

            if (entry.timeStamp != null) {
                Label timeStamp = new Label(dateFormat.format(entry.timeStamp));
                timeStamp.setTextOverrun(OverrunStyle.CLIP);
                line.setRight(timeStamp);
                line.setCursor(Cursor.HAND);
            }

            line.setMouseTransparent(false);
            line.setOnMouseClicked(mouseEvent -> {
                if ((mouseEvent.getButton() == MouseButton.PRIMARY) &&
                        Objects.equals(mouseEvent.getEventType(), MouseEvent.MOUSE_CLICKED) &&
                        (mouseEvent.getClickCount() == 1)) {
                    getModel().getHostServices().showDocument(entry.linkTarget.toExternalForm());
                }
            });
            storage.getChildren().add(line);
        }

        Platform.runLater(() -> display.getChildren().add(storage));
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
            boolean enabled,
            boolean client,
            boolean easyNpc,
            boolean easyQuest,
            boolean mapEdit) {
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
            Platform.runLater(() -> updateLaunchButtons(enabled, client, easyNpc, easyQuest, mapEdit));
        }
    }

    private void launch(
            @Nonnull String groupId,
            @Nonnull String artifactId,
            @Nonnull String launchClass,
            @Nonnull String configKey) {
        Config cfg = getModel().getConfig();

        this.launchClass = launchClass;
        useSnapshots = cfg.getInteger(configKey) == 1;
        new Thread(() -> {
            int attempt = 0;
            while (attempt < 10) {
                attempt++;
                try {
                    MavenDownloader downloader = new MavenDownloader(useSnapshots, attempt);
                    downloader.downloadArtifact(groupId, artifactId, this);
                } catch (@Nonnull Exception e) {
                    //noinspection ThrowableResultOfMethodCallIgnored
                    if (getInnerExceptionOfType(SocketTimeoutException.class, e) != null) {
                        log.warn("Timeout detected. Restarting download with longer timeout.");
                        continue;
                    }
                    log.error("Error while resolving.", e);
                }
                break;
            }
        }).start();
    }

    @Nullable
    private static <T extends Throwable> T getInnerExceptionOfType(
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
            @Nonnull State state,
            @Nullable ProgressMonitor progress,
            boolean offline,
            @Nullable String detail) {
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
            Platform.runLater(() -> reportNewState(state, progress, offline, detail));
        }
    }

    @Override
    public void resolvingDone(@Nonnull Collection<File> classpath) {
        if (launchClass == null) {
            cancelLaunch();
            Platform.runLater(() -> {
                progress.setProgress(1.0);
                progressDescription.setText(resourceBundle.getString("errorClasspathNull"));
            });
        } else {
            Platform.runLater(() -> {
                progress.setProgress(1.0);
                progressDescription.setText(resourceBundle.getString("launchApplication"));
            });
            JavaLauncher launcher = new JavaLauncher(getModel().getConfig(), useSnapshots);
            if (launcher.launch(classpath, launchClass)) {
                Platform.runLater(() -> {
                    try {
                        if (getModel().getConfig().getBoolean("stayOpenAfterLaunch")) {
                            getModel().getStoryboard().showNormal();
                        } else {
                            getModel().getStage().close();
                        }
                    } catch (IOException e) {
                        getModel().getStage().close();
                    }
                });
                new Thread(() -> {
                    Cleaner cleaner = new Cleaner();
                    cleaner.clean();
                }).start();
            } else {
                cancelLaunch();
                Platform.runLater(() -> {
                    progress.setProgress(1.0);
                    progressDescription.setText(launcher.getErrorData());
                });
            }
        }
    }

    @Override
    public void resolvingFailed(@Nonnull Exception ex) {
        //noinspection ThrowableResultOfMethodCallIgnored
        if (getInnerExceptionOfType(SocketTimeoutException.class, ex) != null) {
            return;
        }
        Platform.runLater(() -> {
            progress.setProgress(1.0);
            progressDescription.setText(ex.getLocalizedMessage());
            log.error("Resolving failed.", ex);
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
}

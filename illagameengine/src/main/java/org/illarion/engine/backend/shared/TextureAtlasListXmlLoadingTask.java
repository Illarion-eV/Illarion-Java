/*
 * This file is part of the engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.backend.shared;

import illarion.common.util.ProgressMonitor;
import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * The purpose of this task is to read the XML file of one set of texture atlas files.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class TextureAtlasListXmlLoadingTask<T> implements Runnable, TextureAtlasTask {
    /**
     * The logger that provides the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(TextureAtlasListXmlLoadingTask.class);

    /**
     * The factory required to create the XML parser. Setting up this parser should be done before its assigned to
     * this task.
     */
    @Nonnull
    private final XmlPullParserFactory parserFactory;

    /**
     * The name of the atlas. Its required to fetch the correct XML file.
     */
    @Nonnull
    private final String atlasName;

    /**
     * The parent texture manager. This one is needed to fetch the actual texture files later during the loading
     * progress.
     */
    @Nonnull
    private final AbstractTextureManager<T> textureManager;

    /**
     * The progress monitor that is supposed to keep track of this loading task.
     */
    @Nonnull
    private final ProgressMonitor progressMonitor;

    /**
     * The executor that takes care for the execution of the tasks.
     */
    @Nullable
    private final Executor taskExecutor;

    /**
     * Stores if the task is done.
     */
    private boolean done;

    /**
     * Create a new loading task. This task is meant to be executed concurrently. It will request the required
     * textures from the parent texture manager once the loading is progressed to this point.
     *
     * @param parserFactory the parser factory
     * @param atlasName the name of the atlas files
     * @param textureManager the parent texture manager
     * @param progressMonitor the monitor of the loading progress
     * @param taskExecutor the executor that takes care for executing further tasks.
     */
    public TextureAtlasListXmlLoadingTask(
            @Nonnull final XmlPullParserFactory parserFactory,
            @Nonnull final String atlasName,
            @Nonnull final AbstractTextureManager<T> textureManager,
            @Nonnull final ProgressMonitor progressMonitor,
            @Nullable final Executor taskExecutor) {
        this.parserFactory = parserFactory;
        this.atlasName = atlasName;
        this.textureManager = textureManager;
        this.progressMonitor = progressMonitor;
        this.taskExecutor = taskExecutor;
        done = false;
        progressMonitor.setProgress(0.f);
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void run() {
        InputStream xmlStream = null;
        try {
            final XmlPullParser parser = parserFactory.newPullParser();

            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            xmlStream = classLoader.getResourceAsStream(atlasName + "-atlas.xml");
            parser.setInput(xmlStream, "UTF-8");

            int currentEvent = parser.nextTag();
            int expectedAtlasCount = 0;
            @Nullable TextureAtlasFinalizeTask<T> currentTextureTask = null;
            while (currentEvent != XmlPullParser.END_DOCUMENT) {
                if (currentEvent == XmlPullParser.START_TAG) {
                    final String tagName = parser.getName();
                    switch (tagName) {
                        case "atlasList":
                            expectedAtlasCount = getExpectedAtlasCount(parser, expectedAtlasCount);
                            if (expectedAtlasCount > 0) {
                                progressMonitor.setWeight(expectedAtlasCount);
                            }
                            break;
                        case "atlas":
                            @Nullable final String currentAtlasName = getAtlasTextureName(parser);
                            if (currentAtlasName != null) {
                                final FutureTask<T> preLoadTask = new FutureTask<>(
                                        new TextureAtlasPreLoadTask<>(textureManager, currentAtlasName));
                                if (taskExecutor == null) {
                                    preLoadTask.run();
                                } else {
                                    taskExecutor.execute(preLoadTask);
                                }

                                final float progressToAdd = (expectedAtlasCount == 0) ? 0.f : (1.f /
                                        expectedAtlasCount);
                                currentTextureTask = new TextureAtlasFinalizeTask<>(preLoadTask, currentAtlasName,
                                                                                    textureManager, progressMonitor,
                                                                                    progressToAdd);
                            }
                            break;
                        case "sprite":
                            if (currentTextureTask != null) {
                                transferSpriteData(parser, currentTextureTask);
                            }
                            break;
                    }
                } else if (currentEvent == XmlPullParser.END_TAG) {
                    final String tagName = parser.getName();
                    if ("atlas".equals(tagName)) {
                        if (currentTextureTask != null) {
                            textureManager.addUpdateTask(currentTextureTask);
                            textureManager.addLoadingTask(currentTextureTask);
                            currentTextureTask = null;
                        }
                    } else if ("atlasList".equals(tagName)) {
                        break;
                    }
                }
                currentEvent = parser.nextTag();
            }
        } catch (@Nonnull final XmlPullParserException e) {
            LOGGER.error("Failed to load requested texture atlas: " + atlasName, e);
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Reading error while loading texture atlas: " + atlasName, e);
        } finally {
            done = true;
            if (xmlStream != null) {
                try {
                    xmlStream.close();
                } catch (@Nonnull final IOException ignored) {
                }
            }
        }
    }

    private static int getExpectedAtlasCount(@Nonnull final XmlPullParser parser, final int oldCount) {
        if (oldCount > 0) {
            return oldCount;
        }
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if ("atlasCount".equals(parser.getAttributeName(i))) {
                try {
                    return Integer.parseInt(parser.getAttributeValue(i));
                } catch (@Nonnull final NumberFormatException e) {
                    LOGGER.warn("Found atlas count entry but failed to parse it.");
                }
                break;
            }
        }
        return oldCount;
    }

    @Nullable
    private static String getAtlasTextureName(@Nonnull final XmlPullParser parser) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if ("file".equals(parser.getAttributeName(i))) {
                final String atlasName = parser.getAttributeValue(i);
                if (atlasName.endsWith(".png")) {
                    return atlasName.substring(0, atlasName.length() - 4);
                }
                return atlasName;
            }
        }
        return null;
    }

    private void transferSpriteData(
            @Nonnull final XmlPullParser parser, @Nonnull final TextureAtlasFinalizeTask<T> task) {
        @Nullable String name = null;
        int posX = -1;
        int posY = -1;
        int width = -1;
        int height = -1;

        final int attributeCount = parser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            @Nonnull final String attributeName = parser.getAttributeName(i);
            @Nonnull final String attributeValue = parser.getAttributeValue(i);
            try {
                if ("x".equals(attributeName)) {
                    posX = Integer.parseInt(attributeValue);
                } else if ("y".equals(attributeName)) {
                    posY = Integer.parseInt(attributeValue);
                } else if ("height".equals(attributeName)) {
                    height = Integer.parseInt(attributeValue);
                } else if ("width".equals(attributeName)) {
                    width = Integer.parseInt(attributeValue);
                } else if ("name".equals(attributeName)) {
                    name = attributeValue;
                }
            } catch (@Nonnull final NumberFormatException e) {
                LOGGER.error("Error while parsing texture atlas sprite: " +
                                     attributeName + "=\"" + attributeValue + '"');
            }
        }

        if ((name != null) && (posX > -1) && (posY > -1) && (width > -1) && (height > -1)) {
            task.addSprite(name, posX, posY, width, height);
        } else {
            LOGGER.error("Unable to receive all required values for sprite definition!");
        }
    }
}

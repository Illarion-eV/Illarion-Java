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
package illarion.download;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.download.gui.GuiApplication;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.net.URL;
import java.nio.file.Path;

/**
 * Main entry class for the launcher/downloader.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Application {
    private Application() {
    }

    public static void main(String... args) {
        initLogs();
        System.setProperty("javafx.userAgentStylesheetUrl", "CASPIAN");

        javafx.application.Application.launch(GuiApplication.class, args);
    }

    private static void initLogs() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Path dir = DirectoryManager.getInstance().getDirectory(Directory.User);
        System.setProperty("log_dir", dir.toAbsolutePath().toString());

        //Reload:
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(lc);
        lc.reset();
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL resource = cl.getResource("logback-with-file.xml");
            if (resource != null) {
                ci.configureByResource(resource);
            } else {
                ci.autoConfig();
            }
        } catch (JoranException ignored) {
        }
    }
}

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
package illarion.mapedit;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import illarion.common.bug.CrashReporter;
import illarion.common.bug.ReportDialogFactorySwing;
import illarion.common.util.AppIdent;
import illarion.common.util.Crypto;
import illarion.common.util.DirectoryManager;
import illarion.common.util.TableLoader;
import illarion.mapedit.crash.DefaultCrashHandler;
import illarion.mapedit.crash.exceptions.UnhandlableException;
import illarion.mapedit.gui.GuiController;
import illarion.mapedit.gui.MainFrame;
import illarion.mapedit.gui.MapEditorConfig;
import illarion.mapedit.gui.SplashScreen;
import illarion.mapedit.resource.ResourceManager;
import illarion.mapedit.resource.loaders.*;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Main MapEditor class. This class starts the map editor and handles all
 * configuration files and central settings.
 *
 * @author Martin Karing
 * @since 0.99
 */
public final class MapEditor {
    /**
     * The identifier of the application.
     */
    @SuppressWarnings("nls")
    public static final AppIdent APPLICATION = new AppIdent("Illarion Mapeditor");

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MapEditor.class);

    /**
     * Constructor of the map editor that loads up all required data.
     */
    @SuppressWarnings("nls")
    public MapEditor() {
        Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);
    }

    /**
     * Crash the editor with a message.
     *
     * @param message the message the editor is supposed to crash with
     */
    public static void crashEditor(String message) {
        LOGGER.error(message);
        System.exit(-1);
    }

    /**
     * Stop the map editor correctly.
     */
    public static void exit() {
        MainFrame.getInstance().exit();
        CrashReporter.getInstance().waitForReport();
        MapEditorConfig.getInstance().save();
    }

    /**
     * Main function to call to start the map editor.
     *
     * @param args the argument of the system call
     */
    public static void main(String[] args) {
        initLogging();
        MapEditorConfig.getInstance().init();
        initExceptionHandler();

        SplashScreen.getInstance().setVisible(true);

        JRibbonFrame.setDefaultLookAndFeelDecorated(MapEditorConfig.getInstance().isUseWindowDecoration());
        JDialog.setDefaultLookAndFeelDecorated(MapEditorConfig.getInstance().isUseWindowDecoration());

        MapEditor instance = new MapEditor();

        loadResources();
        GuiController controller = new GuiController();
        controller.initialize();

        controller.start();
    }

    private static void loadResources() {
        ResourceManager resourceManager = ResourceManager.getInstance();
        resourceManager
                .addResources(ImageLoader.getInstance(), TextureLoaderAwt.getInstance(), TileLoader.getInstance(),
                              ItemNameLoader.getInstance(), ItemLoader.getInstance(), SongLoader.getInstance(),
                              ItemGroupLoader.getInstance(), OverlayLoader.getInstance());

        while (resourceManager.hasNextToLoad()) {
            try {
                LOGGER.debug("Loading {}", resourceManager.getNextDescription());
                SplashScreen.getInstance().setMessage("Loading " + resourceManager.getNextDescription());
                resourceManager.loadNext();
            } catch (IOException e) {
                LOGGER.warn("{} failed!", resourceManager.getPrevDescription());
                //                Crash the editor
                throw new UnhandlableException("Can't load " + resourceManager.getPrevDescription(), e);
            }
        }
    }

    private static void initExceptionHandler() {
        CrashReporter.getInstance().setConfig(MapEditorConfig.getInstance().getInternalCfg());
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());
        CrashReporter.getInstance().setDialogFactory(new ReportDialogFactorySwing());
        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler.getInstance());
        Thread.currentThread().setUncaughtExceptionHandler(DefaultCrashHandler.getInstance());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setUncaughtExceptionHandler(DefaultCrashHandler.getInstance());
            }
        });
    }

    /**
     * Prepare the proper output of the log files.
     */
    @SuppressWarnings("nls")
    private static void initLogging() {
        System.out.println("Startup done.");
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Path userDir = DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User);
        if (userDir == null) {
            return;
        }
        System.setProperty("log_dir", userDir.toAbsolutePath().toString());

        //Reload:
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(lc);
        lc.reset();
        try {
            ci.autoConfig();
        } catch (JoranException e) {
            e.printStackTrace();
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
}

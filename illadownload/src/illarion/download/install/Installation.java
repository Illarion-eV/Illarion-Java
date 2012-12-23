/*
 * This file is part of the Illarion Download Utility.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Download Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Download Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Download Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.install;

import illarion.common.util.DirectoryManager;
import illarion.download.install.gui.swing.*;
import illarion.download.install.resources.ResourceManager;
import illarion.download.install.resources.dev.Client;
import illarion.download.install.resources.dev.EasyNpcEditor;
import illarion.download.install.resources.dev.EasyQuestEditor;
import illarion.download.install.resources.dev.Mapeditor;
import illarion.download.tasks.clean.Cleaner;
import illarion.download.tasks.download.DownloadManager;
import illarion.download.tasks.launch.Launcher;
import illarion.download.tasks.unpack.FailMonitor;
import illarion.download.tasks.unpack.UnpackManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This class is used to control the installation chain.
 *
 * @author Martin Karing
 * @version 1.01
 * @since 1.00
 */
public final class Installation {
    /**
     * The text that contains the version string of this application.
     */
    public static final String VERSION = "1.01"; //$NON-NLS-1$

    /**
     * The GUI that is used to display the installation progress. While its
     * created here it does not mean by all needs that its really displayed.
     */
    private final BaseSWING baseGUI = new BaseSWING();

    /**
     * The logger that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Installation.class);

    /**
     * If this flag is turned true, the installation is started again.
     */
    private boolean restartInstallation;

    /**
     * Check if this downloader targets the production system.
     *
     * @return {@code true} in case the applications published are <b>NOT</b> the development versions
     */
    public static boolean isProduction() {
        return !"test".equals(System.getProperty("illarion.target"));
    }

    /**
     * The main function to start this installation and launching process.
     *
     * @param args the start arguments
     */
    @SuppressWarnings("nls")
    public static void main(final String[] args) {
        setupLogger();

        BaseSWING.updateLookAndFeel();
        LOGGER.info("Installation started");
        final Installation install = new Installation();

        install.restartInstallation = true;
        while (install.restartInstallation) {
            install.restartInstallation = false;

            install.checkAndInstallDirectories();
            install.selectApplication();
            install.prepareApplication();
            install.launch();
        }

        install.finish();
        System.exit(0);
    }

    /**
     * Initialize the logging output of this class.
     */
    private static void setupLogger() {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        PropertyConfigurator.configure(classLoader.getResourceAsStream("logging.properties"));
    }

    /**
     * This function reads the directories needed for the Illarion applications
     * and triggers the installation of those directories in case its needed.
     */
    @SuppressWarnings("nls")
    private void checkAndInstallDirectories() {
        final DirectoryManager manager = DirectoryManager.getInstance();

        if (manager.hasDataDirectory()) {
            LOGGER.info("Checking data directory... found");
        } else {
            LOGGER.info("Checking data directory... not found");
            final AppDirectorySWING content = new AppDirectorySWING();
            baseGUI.show(content);

            content.waitForContinue();
        }

        if (manager.hasUserDirectory()) {
            LOGGER.info("Checking user directory... found");
        } else {
            LOGGER.info("Checking user directory... not found");
            final UserDirectorySWING content = new UserDirectorySWING();
            baseGUI.show(content);

            content.waitForContinue();
        }

        manager.save();
    }

    /**
     * Finish the installation and cleanup everything that remains.
     */
    private void finish() {
        baseGUI.setVisible(false);
        baseGUI.dispose();
    }

    /**
     * Launch the selected and prepared application.
     */
    private void launch() {
        final Launcher launcher = new Launcher();

        boolean retry = true;
        while (retry) {
            retry = false;
            if (!launcher.launch()) {
                final FailedLaunchSWING failedLaunch = new FailedLaunchSWING(launcher.getErrorData());
                baseGUI.show(failedLaunch);

                failedLaunch.waitForContinue();

                switch (failedLaunch.getResult()) {
                    case Retry:
                        retry = true;
                        break;
                    case DeleteRetry:
                        ResourceManager.getInstance().resetResources();
                        restartInstallation = true;
                        break;
                }
            }
        }
    }

    /**
     * Prepare the application selected for execution.
     */
    private void prepareApplication() {
        boolean retry = true;

        while (retry) {
            retry = false;
            FailMonitor.getInstance().clearErrors();
            ResourceManager.getInstance().discoverDependencies();

            final DownloadManager dManager = new DownloadManager();
            final UnpackManager uManager = new UnpackManager();
            final ProgressSWING progressDisplay = new ProgressSWING();
            baseGUI.show(progressDisplay);

            dManager.addDownloadProgressListener(progressDisplay);
            dManager.addDownloadProgressListener(uManager);
            uManager.addUnpackProgressListener(progressDisplay);
            uManager.addUnpackProgressListener(FailMonitor.getInstance());
            uManager.addUnpackProgressListener(new Cleaner());

            try {
                baseGUI.waitForFinishShowing();
            } catch (final InterruptedException e) {
                // interrupted .. just try to go on
            }
            ResourceManager.getInstance().scheduleDownloads(dManager);

            dManager.shutdown();
            uManager.shutdown();
            progressDisplay.setToFinished();

            ResourceManager.getInstance().saveResourceDatabase();

            if (FailMonitor.getInstance().hasErrors()) {
                final FailedInformationSWING failedInfo = new FailedInformationSWING();
                baseGUI.show(failedInfo);
                failedInfo.waitForContinue();

                if (failedInfo.getResult() == FailedInformationSWING.RESULT_RETRY) {
                    retry = true;
                } else if (failedInfo.getResult() == FailedInformationSWING.RESULT_LAUNCH) {
                    continue;
                }
            }
        }
    }

    /**
     * This functions takes care that the application to launch gets selected.
     */
    @SuppressWarnings("nls")
    private void selectApplication() {
        final String sysProp = System.getProperty("illarion.download.launch");
        if ((sysProp != null) && (sysProp.length() > 2)) {
            if (sysProp.equalsIgnoreCase("client")) {
                ResourceManager.getInstance().setMainResource(Client.getInstance());
                return;
            } else if (sysProp.equalsIgnoreCase("mapeditor")) {
                ResourceManager.getInstance().setMainResource(Mapeditor.getInstance());
                return;
            } else if (sysProp.equalsIgnoreCase("easynpc")) {
                ResourceManager.getInstance().setMainResource(EasyNpcEditor.getInstance());
                return;
            } else if (sysProp.equalsIgnoreCase("easyquest")) {
                ResourceManager.getInstance().setMainResource(EasyQuestEditor.getInstance());
                return;
            }
        }
        final AppSelectionSWING content = new AppSelectionSWING();
        baseGUI.show(content);
        content.waitForContinue();

        ResourceManager.getInstance().setMainResource(
                content.getSelectedResource());
    }

}

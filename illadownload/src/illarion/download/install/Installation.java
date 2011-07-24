/*
 * This file is part of the Illarion Download Manager.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Download Manager is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Download Manager is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Download Manager. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.install;

import illarion.common.util.DirectoryManager;

import illarion.download.install.gui.swing.AppDirectorySWING;
import illarion.download.install.gui.swing.AppSelectionSWING;
import illarion.download.install.gui.swing.BaseSWING;
import illarion.download.install.gui.swing.FailedInformationSWING;
import illarion.download.install.gui.swing.ProgressSWING;
import illarion.download.install.gui.swing.UserDirectorySWING;
import illarion.download.install.resources.ResourceManager;
import illarion.download.tasks.clean.Cleaner;
import illarion.download.tasks.download.DownloadManager;
import illarion.download.tasks.launch.Launcher;
import illarion.download.tasks.unpack.FailMonitor;
import illarion.download.tasks.unpack.UnpackManager;

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
     * The main function to start this installation and launching process.
     * 
     * @param args the start arguments
     */
    @SuppressWarnings("nls")
    public static void main(final String[] args) {
        BaseSWING.updateLookAndFeel();
        System.out.println("Installation started!");
        final Installation install = new Installation();
        install.checkAndInstallDirectories();
        install.selectApplication();
        install.prepareApplication();
        install.launch();

        install.finish();
        System.exit(0);
    }

    /**
     * This function reads the directories needed for the Illarion applications
     * and triggers the installation of those directories in case its needed.
     */
    @SuppressWarnings("nls")
    private void checkAndInstallDirectories() {
        final DirectoryManager manager = DirectoryManager.getInstance();

        System.out.print("Checking data directory...");
        if (!manager.hasDataDirectory()) {
            System.out.println("not found");
            final AppDirectorySWING content = new AppDirectorySWING();
            baseGUI.show(content);

            content.waitForContinue();
        } else {
            System.out.println("found");
        }

        System.out.print("Checking user directory...");
        if (!manager.hasUserDirectory()) {
            System.out.println("not found");
            final UserDirectorySWING content = new UserDirectorySWING();
            baseGUI.show(content);

            content.waitForContinue();
        } else {
            System.out.println("found");
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
        launcher.launch();
    }

    /**
     * Prepare the application selected for execution.
     */
    private void prepareApplication() {
        boolean retry = true;

        while (retry) {
            retry = false;
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
                final FailedInformationSWING failedInfos =
                    new FailedInformationSWING();
                baseGUI.show(failedInfos);
                failedInfos.waitForContinue();

                if (failedInfos.getResult() == FailedInformationSWING.RESULT_RETRY) {
                    retry = true;
                } else if (failedInfos.getResult() == FailedInformationSWING.RESULT_LAUNCH) {
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
            if (sysProp.equalsIgnoreCase("tsclient")) {
                ResourceManager.getInstance().setMainResource(
                    illarion.download.install.resources.dev.Client
                        .getInstance());
                return;
            } else if (sysProp.equalsIgnoreCase("mapeditor")) {
                ResourceManager.getInstance().setMainResource(
                    illarion.download.install.resources.dev.Mapeditor
                        .getInstance());
                return;
            } else if (sysProp.equalsIgnoreCase("easynpc")) {
                ResourceManager.getInstance().setMainResource(
                    illarion.download.install.resources.dev.EasyNpcEditor
                        .getInstance());
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

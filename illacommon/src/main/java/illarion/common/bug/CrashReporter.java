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
package illarion.common.bug;

import biz.futureware.mantis.rpc.soap.client.*;
import illarion.common.config.Config;
import illarion.common.util.AppIdent;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.common.util.MessageSource;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * This class stores the crash reporter itself. It holds all settings done to
 * the reporter and handles sending the crash reports as well as showing the
 * required dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CrashReporter {
    /**
     * This is the key used in the configuration to store and read the settings
     * for the reporting system.
     */
    public static final String CFG_KEY = "errorReport"; //$NON-NLS-1$

    /**
     * This constant is used as mode value in case the crash reporter is
     * supposed to send the crash reports every time.
     */
    public static final int MODE_ALWAYS = 1;

    /**
     * This constant is used as mode value in case the crash reporter is
     * supposed to ask the user if the message shall be send to the server or
     * not.
     */
    public static final int MODE_ASK = 0;

    /**
     * This constant is used as mode value in case the crash reporter is
     * supposed to discard the crash report.
     */
    public static final int MODE_NEVER = 2;

    /**
     * This URL is the URL of the server that is supposed to receive the crash
     * data using a HTTP POST request.
     */
    @Nullable
    private static final URL CRASH_SERVER;

    /**
     * The singleton instance of this class.
     */
    @Nonnull
    private static final CrashReporter INSTANCE = new CrashReporter();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(CrashReporter.class);

    static {
        URL result = null;
        try {
            result = new URL("https://illarion.org/mantis/api/soap/mantisconnect.php"); //$NON-NLS-1$
        } catch (@Nonnull MalformedURLException e) {
            log.warn("Preparing the crash report target URL failed. Crash reporter not functional."); //$NON-NLS-1$
        }
        CRASH_SERVER = result;
    }

    /**
     * The configuration handler that is used for the settings of this class.
     */
    @Nullable
    private Config cfg;

    /**
     * The currently displayed report dialog is displayed in this class.
     */
    @Nullable
    private ReportDialog dialog;

    /**
     * This is the source of the messages that are displayed in the crash report
     * dialog.
     */
    @Nullable
    private MessageSource messages;

    /**
     * This value stores the currently set mode.
     */
    private int mode;

    /**
     * This is the factory that is used to create report dialogs.
     */
    @Nullable
    private ReportDialogFactory dialogFactory;

    @Nullable
    private CountDownLatch crashReportDoneLatch;

    /**
     * Private constructor of the crash reporter that prepares all the required
     * data.
     */
    private CrashReporter() {
        mode = MODE_ASK;
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    @Contract(pure = true)
    public static CrashReporter getInstance() {
        return INSTANCE;
    }

    /**
     * Set the instance of the factory that is used to create a report dialog.
     *
     * @param dialogFactory the dialog factory
     */
    public void setDialogFactory(@Nullable ReportDialogFactory dialogFactory) {
        this.dialogFactory = dialogFactory;
    }

    /**
     * Report a crash to the Illarion Server in case the application is supposed
     * to do so.
     *
     * @param crash the data about the crash
     */
    public void reportCrash(@Nonnull CrashData crash) {
        reportCrash(crash, false);
    }

    /**
     * Report a crash to the Illarion Server in case the application is supposed
     * to do so.
     *
     * @param crash the data about the crash
     * @param ownThread {@code true} in case the crash report is supposed
     * to be started in a additional thread
     */
    public void reportCrash(@Nonnull CrashData crash, boolean ownThread) {
        if (ownThread) {
            new Thread(() -> {
                reportCrash(crash, false);
            }).start();
        }

        if ("NoClassDefFoundError".equals(crash.getExceptionName())) {
            try {
                Files.createFile(
                        DirectoryManager.getInstance().resolveFile(Directory.Data, "corrupted"));
            } catch (@Nonnull IOException e) {
                log.error("Failed to mark data as corrupted.");
            }
        }

        waitForReport();

        switch (mode) {
            case MODE_ALWAYS:
                sendCrashData(crash);
                break;
            case MODE_NEVER:
                return;
            default:
                crashReportDoneLatch = new CountDownLatch(1);
                dialog = dialogFactory.createDialog();

                dialog.setCrashData(crash);
                dialog.setMessageSource(messages);
                dialog.showDialog();

                int result = dialog.getResult();
                switch (result) {
                    case ReportDialog.SEND_ALWAYS:
                        setMode(MODE_ALWAYS);
                        if (cfg != null) {
                            cfg.set(CFG_KEY, MODE_ALWAYS);
                        }
                        sendCrashData(crash);
                        break;
                    case ReportDialog.SEND_ONCE:
                        sendCrashData(crash);
                        break;
                    case ReportDialog.SEND_NEVER:
                        setMode(MODE_NEVER);
                        if (cfg != null) {
                            cfg.set(CFG_KEY, MODE_NEVER);
                        }
                        break;
                    default:
                        break;
                }

                crashReportDoneLatch.countDown();
                crashReportDoneLatch = null;
                dialog = null;
                break;
        }
    }

    /**
     * Set the configuration that is used for this crash reporter.
     *
     * @param config the new configuration
     */
    public void setConfig(@Nullable Config config) {
        cfg = config;
        if (config != null) {
            setMode(config.getInteger(CFG_KEY));
        }
    }

    /**
     * Set the message source that supplies the messages for the dialog. In case
     * this is set to {@code null} its impossible to display a window
     * asking the user if the error report shall be send or not. In this case no
     * report message will be send.
     *
     * @param source the new source of messages
     */
    public void setMessageSource(MessageSource source) {
        messages = source;
    }

    /**
     * This function blocks the current thread from execution in case the crash
     * reporter is currently showing a crash report or is sending the
     * information on a crash to the server.
     */
    public void waitForReport() {
        CountDownLatch localLatch = crashReportDoneLatch;
        if (localLatch != null) {
            try {
                localLatch.await();
            } catch (InterruptedException e) {
                log.debug("Wait for report was interrupted!", e);
                // Thread interrupted. Just exit the function
            }
        }
    }

    private static final ObjectRef REPRODUCIBILITY_NA_NUM = new ObjectRef(BigInteger.valueOf(100), null);
    private static final ObjectRef SEVERITY_CRASH_NUM = new ObjectRef(BigInteger.valueOf(70), null);
    private static final ObjectRef PRIORITY_HIGH_NUM = new ObjectRef(BigInteger.valueOf(40), null);

    private static final String CATEGORY = "Automatic";

    /**
     * Send the data of the crash to the Illarion server.
     *
     * @param data the data that was collected about the crash
     */
    private static void sendCrashData(@Nonnull CrashData data) {
        if (CRASH_SERVER == null) {
            return;
        }

        try {
            MantisConnector connector = new MantisConnector();

            ProjectData selectedProject = connector.getProject(data.getMantisProject());
            if (selectedProject == null) {
                log.error("Failed to find {} project.", data.getMantisProject());
                return;
            }

            AppIdent application = data.getApplicationIdentifier();
            String summery = data.getExceptionName() + " in Thread " + data.getThreadName();

            String exceptionDescription = "Exception: " + data.getExceptionName() + "\nBacktrace:\n" +
                    data.getStackBacktrace() + "\nDescription: " + data.getDescription();

            String description = "Application:" + application.getApplicationIdentifier() +
                    (application.getCommitCount() > 0 ? " (DEV)" : "") +
                    "\nThread: " + data.getThreadName() +
                    '\n' + exceptionDescription;



            @Nullable IssueData similarIssue = null;
            @Nullable IssueData possibleDuplicateIssue = null;
            @Nullable IssueData duplicateIssue = null;

            FilterData filter = connector.getFilter(selectedProject);
            Collection<IssueHeaderData> headers = connector.getIssueHeaders(selectedProject, filter);

            for (@Nonnull IssueHeaderData header : headers) {
                if (!CATEGORY.equals(header.getCategory())) {
                    continue;
                }

                if (!saveString(header.getSummary()).equals(summery)) {
                    continue;
                }

                @Nonnull IssueData checkedIssue = connector.getIssue(header);

                if (!saveString(checkedIssue.getDescription()).endsWith(exceptionDescription)) {
                    continue;
                }

                similarIssue = checkedIssue;

                if (!saveString(checkedIssue.getVersion()).equals(application.getApplicationRootVersion())) {
                    continue;
                }

                if (!saveString(checkedIssue.getOs()).equals(System.getProperty("os.name"))) {
                    continue;
                }

                if (!saveString(checkedIssue.getOs_build()).equals(System.getProperty("os.version"))) {
                    continue;
                }

                possibleDuplicateIssue = checkedIssue;

                if (saveString(checkedIssue.getDescription()).equals(description)) {
                    duplicateIssue = checkedIssue;
                    break;
                }
            }

            if (duplicateIssue != null) {
                connector.addNote(duplicateIssue, "Same problem occurred again.");
            } else if (possibleDuplicateIssue != null) {
                connector.addNote(possibleDuplicateIssue, "A problem that is by all means very similar occurred:\n" +
                        description + "\nOperating System: " +
                        System.getProperty("os.name") + ' ' +
                        System.getProperty("os.version"));
            } else {
                IssueData issue = new IssueData();
                issue.setCategory(CATEGORY);
                issue.setSummary(summery);
                issue.setDescription(description);
                issue.setVersion(application.getApplicationRootVersion());
                issue.setOs(System.getProperty("os.name"));
                issue.setOs_build(System.getProperty("os.version"));
                issue.setReproducibility(REPRODUCIBILITY_NA_NUM);
                issue.setSeverity(SEVERITY_CRASH_NUM);
                issue.setPriority(PRIORITY_HIGH_NUM);

                BigInteger id = connector.addIssue(selectedProject, issue);
                log.info("Added new Issue #{}", id);

                if (similarIssue != null) {
                    connector.addNote(issue, "Similar issue was found at #" + similarIssue.getId());
                    connector.addRelation(issue, similarIssue);
                }
            }
        } catch (ServiceException | RemoteException e) {
            log.error("Failed to send error reporting data.", e);
        }
    }

    @Nonnull
    @Contract(pure = true)
    private static String saveString(@Nullable String input) {
        if (input == null) {
            return "";
        }
        return input;
    }

    /**
     * Set a new value for the mode of this crash reporter. The legal values for
     * this mode are {@link #MODE_ALWAYS}, {@link #MODE_ASK} and
     * {@link #MODE_NEVER}.
     *
     * @param newMode the new mode value
     * @throws IllegalArgumentException in case the invalid mode value is chosen
     */
    public void setMode(int newMode) {
        if ((newMode != MODE_ALWAYS) && (newMode != MODE_ASK) && (newMode != MODE_NEVER)) {
            mode = MODE_ASK;
            return;
        }

        mode = newMode;
    }
}

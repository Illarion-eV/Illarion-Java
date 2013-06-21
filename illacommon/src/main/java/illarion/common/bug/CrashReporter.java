/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.bug;

import illarion.common.config.Config;
import illarion.common.util.AppIdent;
import illarion.common.util.DirectoryManager;
import illarion.common.util.MessageSource;
import javolution.lang.Reflection;
import javolution.lang.Reflection.Constructor;
import org.apache.log4j.Logger;
import org.mantisbt.connect.IMCSession;
import org.mantisbt.connect.MCException;
import org.mantisbt.connect.axis.MCSession;
import org.mantisbt.connect.model.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
     * This constant is used as display value in case the crash reporter is
     * supposed to display the crash window as SWING window.
     */
    public static final int DISPLAY_SWING = 2;

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
     * The character set used to encode the data for the Illarion server.
     */
    @SuppressWarnings("nls")
    private static final String CHARSET = "UTF-8";

    /**
     * This URL is the URL of the server that is supposed to receive the crash
     * data using a HTTP POST request.
     */
    @Nullable
    private static final URL CRASH_SERVER;

    /**
     * The singleton instance of this class.
     */
    private static final CrashReporter INSTANCE = new CrashReporter();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(CrashReporter.class);

    static {
        URL result = null;
        try {
            result = new URL("http://illarion.org/mantis/api/soap/mantisconnect.php"); //$NON-NLS-1$
        } catch (@Nonnull final MalformedURLException e) {
            LOGGER.warn("Preparing the crash report target URL failed. Crash reporter not functional."); //$NON-NLS-1$
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
    private ReportDialog dialog = null;

    /**
     * This value stores the currently set display system.
     */
    private int display;

    /**
     * This is the source of the messages that are displayed in the crash report
     * dialog.
     */
    private MessageSource messages;

    /**
     * This value stores the currently set mode.
     */
    private int mode;

    /**
     * Private constructor of the crash reporter that prepares all the required
     * data.
     */
    private CrashReporter() {
        mode = MODE_ASK;
        display = DISPLAY_SWING;
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static CrashReporter getInstance() {
        return INSTANCE;
    }

    public void dumpCrash(@Nonnull final CrashData crash) {
        LOGGER.fatal("Fatal error occured: " + crash.getDescription());
        LOGGER.fatal("Fatal error exception: " + crash.getExceptionName());
        LOGGER.fatal("Fatal error thread: " + crash.getThreadName());
        LOGGER.fatal("Fatal error backtrace: " + crash.getStackBacktrace());
        LOGGER.fatal(crash.getApplicationIdentifier().getApplicationName() + " is going down. Brace for impact.");
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String dateStr = sdf.format(cal.getTime());
        final File target = new File(DirectoryManager.getInstance().getUserDirectory(), "crash_" + dateStr + ".dump");

        ObjectOutputStream oOut = null;
        try {
            oOut = new ObjectOutputStream(new FileOutputStream(target));
            oOut.writeObject(crash);
            oOut.flush();
        } catch (@Nonnull final FileNotFoundException e) {
            // ignored
        } catch (@Nonnull final IOException e) {
            // ignored
        } finally {
            if (oOut != null) {
                try {
                    oOut.close();
                } catch (@Nonnull final IOException e) {
                    // ignored
                }
            }
        }
    }

    /**
     * Report a crash to the Illarion Server in case the application is supposed
     * to do so.
     *
     * @param crash the data about the crash
     */
    public void reportCrash(@Nonnull final CrashData crash) {
        reportCrash(crash, false);
    }

    /**
     * Report a crash to the Illarion Server in case the application is supposed
     * to do so.
     *
     * @param crash     the data about the crash
     * @param ownThread <code>true</code> in case the crash report is supposed
     *                  to be started in a additional thread
     */
    @SuppressWarnings("nls")
    public void reportCrash(@Nonnull final CrashData crash, final boolean ownThread) {
        if (ownThread) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    reportCrash(crash, false);
                }
            }).start();
        }

        if ("NoClassDefFoundError".equals(crash.getExceptionName())) {
            try {
                //noinspection ResultOfMethodCallIgnored
                new File(DirectoryManager.getInstance().getDataDirectory(), "corrupted").createNewFile();
            } catch (@Nonnull final IOException e) {
                LOGGER.error("Failed to mark data as corrupted.");
            }
        }

        waitForReport();

        switch (mode) {
            case MODE_ALWAYS:
                sendCrashData(crash);
                break;
            case MODE_NEVER:
                return;
            case MODE_ASK:
            default:
                synchronized (this) {
                    Constructor constr = null;
                    if (display == DISPLAY_SWING) {
                        constr =
                                Reflection.getInstance().getConstructor(
                                        "illarion.common.bug.ReportDialogSwing()");
                    }

                    if (constr == null) {
                        return;
                    }
                    dialog = (ReportDialog) constr.newInstance();
                }

                dialog.setCrashData(crash);
                dialog.setMessageSource(messages);
                dialog.showDialog();

                final int result = dialog.getResult();
                switch (result) {
                    case ReportDialog.SEND_ALWAYS:
                        setMode(MODE_ALWAYS);
                        if (cfg != null) {
                            cfg.set(CFG_KEY, MODE_ALWAYS);
                        }
                        //$FALL-THROUGH$
                    case ReportDialog.SEND_ONCE:
                        sendCrashData(crash);
                        break;
                    case ReportDialog.SEND_NEVER:
                        setMode(MODE_NEVER);
                        if (cfg != null) {
                            cfg.set(CFG_KEY, MODE_NEVER);
                        }
                        //$FALL-THROUGH$
                    case ReportDialog.SEND_NOT:
                    default:
                        break;
                }

                synchronized (this) {
                    dialog = null;
                    notify();
                }

                break;
        }
    }

    /**
     * Set the configuration that is used for this crash reporter.
     *
     * @param config the new configuration
     */
    public void setConfig(@Nullable final Config config) {
        cfg = config;
        if (config != null) {
            setMode(config.getInteger(CFG_KEY));
        }
    }

    /**
     * Set the display mode that is supposed to be used by the crash reporter.
     * Best select the mode so it fits best to the rest of your GUI. The legal
     * values is {@link #DISPLAY_SWING}.
     *
     * @param newDisplay the new display value
     * @throws IllegalArgumentException in case a invalid display mode is chosen
     */
    @SuppressWarnings("nls")
    public void setDisplay(final int newDisplay) {
        if (newDisplay != DISPLAY_SWING) {
            throw new IllegalArgumentException("Illegal display value: "
                    + Integer.toString(newDisplay));
        }

        display = newDisplay;
    }

    /**
     * Set the message source that supplies the messages for the dialog. In case
     * this is set to <code>null</code> its impossible to display a window
     * asking the user if the error report shall be send or not. In this case no
     * report message will be send.
     *
     * @param source the new source of messages
     */
    public void setMessageSource(final MessageSource source) {
        messages = source;
    }

    /**
     * This function blocks the current thread from execution in case the crash
     * reporter is currently showing a crash report or is sending the
     * informations on a crash to the server.
     */
    public void waitForReport() {
        synchronized (this) {
            while (dialog != null) {
                try {
                    this.wait(100);
                } catch (@Nonnull final InterruptedException e) {
                    LOGGER.debug("Wait for report was interrupted!", e); //$NON-NLS-1$
                }
            }
        }
    }

    private static final long REPRODUCIBILITY_NA_NUM = 100;
    private static final long SEVERITY_CRASH_NUM = 70;
    private static final long PRIORITY_HIGH_NUM = 40;

    private static final String CATEGORY = "Automatic";

    /**
     * Send the data of the crash to the Illarion server.
     *
     * @param data the data that was collected about the crash
     */
    @SuppressWarnings("nls")
    private static void sendCrashData(@Nonnull final CrashData data) {
        if (CRASH_SERVER == null) {
            return;
        }

        try {
            final IMCSession mantisSession = new MCSession(CRASH_SERVER, "Java Reporting System",
                    "dA23MvKT1KDm4k0bQmMS");
            final IProject[] projects = mantisSession.getAccessibleProjects();
            IProject bugReportProject = null;
            for (final IProject project : projects) {
                if (project.getName().equalsIgnoreCase("Bug reports")) {
                    bugReportProject = project;
                    break;
                }
            }
            if (bugReportProject == null) {
                LOGGER.error("Failed to find bug reports project.");
                return;
            }

            final AppIdent application = data.getApplicationIdentifier();

            final IProject selectedProject = bugReportProject.getSubProject(application.getApplicationName());
            if (selectedProject == null) {
                LOGGER.error("Failed to find " + application.getApplicationName() + " project.");
                return;
            }

            final String summery = data.getExceptionName() + " in Thread " + data.getThreadName();

            final String exceptionDescription = "Exception: " + data.getExceptionName() + "\nBacktrace:\n" +
                    data.getStackBacktrace() + "\nDescription: " + data.getDescription();

            final String description = "Application:" + application.getApplicationIdentifier() + "\nThread: " +
                    data.getThreadName() + '\n' + exceptionDescription;

            @Nullable IIssue similarIssue = null;
            @Nullable IIssue possibleDuplicateIssue = null;
            @Nullable IIssue duplicateIssue = null;

            @Nonnull final IIssueHeader[] headers = mantisSession.getProjectIssueHeaders(selectedProject.getId());
            for (@Nonnull final IIssueHeader header : headers) {
                if (!header.getCategory().equals(CATEGORY)) {
                    continue;
                }

                if (!header.getSummary().equals(summery)) {
                    continue;
                }

                @Nonnull final IIssue checkedIssue = mantisSession.getIssue(header.getId());

                if (!checkedIssue.getDescription().endsWith(exceptionDescription)) {
                    continue;
                }

                similarIssue = checkedIssue;

                if (!checkedIssue.getVersion().equals(application.getApplicationRootVersion())) {
                    continue;
                }

                if (!checkedIssue.getOs().equals(System.getProperty("os.name"))) {
                    continue;
                }

                if (!checkedIssue.getOsBuild().equals(System.getProperty("os.version"))) {
                    continue;
                }

                possibleDuplicateIssue = checkedIssue;

                if (!checkedIssue.getDescription().equals(description)) {
                    continue;
                }

                duplicateIssue = checkedIssue;
                break;
            }

            if (duplicateIssue != null) {
                final INote note = mantisSession.newNote("Same problem problem occurred again.");
                mantisSession.addNote(duplicateIssue.getId(), note);
            } else if (possibleDuplicateIssue != null) {
                final INote note = mantisSession.newNote("A problem that is by all means very similar occurred:\n" +
                        description + "\nOperating System: " + System.getProperty("os.name") + ' ' +
                        System.getProperty("os.version"));
                mantisSession.addNote(possibleDuplicateIssue.getId(), note);
            } else {
                final IIssue issue = mantisSession.newIssue(selectedProject.getId());
                issue.setCategory(CATEGORY);
                issue.setSummary(summery);
                issue.setDescription(description);
                issue.setVersion(application.getApplicationRootVersion());
                issue.setOs(System.getProperty("os.name"));
                issue.setOsBuild(System.getProperty("os.version"));
                issue.setReproducibility(new MCAttribute(REPRODUCIBILITY_NA_NUM, null));
                issue.setSeverity(new MCAttribute(SEVERITY_CRASH_NUM, null));
                issue.setPriority(new MCAttribute(PRIORITY_HIGH_NUM, null));
                issue.setPrivate(false);

                final long id = mantisSession.addIssue(issue);
                LOGGER.info("Added new Issue #" + id);

                if (similarIssue != null) {
                    mantisSession.addNote(id, mantisSession.newNote("Similar issue was found at #" +
                            similarIssue.getId()));
                }
            }
        } catch (MCException e) {
            LOGGER.error("Failed to send error reporting data.", e);
        }
    }

    /**
     * Set a new value for the mode of this crash reporter. The legal values for
     * this mode are {@link #MODE_ALWAYS}, {@link #MODE_ASK} and
     * {@link #MODE_NEVER}.
     *
     * @param newMode the new mode value
     * @throws IllegalArgumentException in case the invalid mode value is chosen
     */
    public void setMode(final int newMode) {
        if ((newMode != MODE_ALWAYS) && (newMode != MODE_ASK)
                && (newMode != MODE_NEVER)) {
            mode = MODE_ASK;
            return;
        }

        mode = newMode;
    }
}

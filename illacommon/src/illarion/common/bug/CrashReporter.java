/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.bug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javolution.lang.Reflection;
import javolution.lang.Reflection.Constructor;
import javolution.text.TextBuilder;

import org.apache.log4j.Logger;

import illarion.common.config.Config;
import illarion.common.util.MessageSource;

/**
 * This class stores the crash reporter itself. It holds all settings done to
 * the reporter and handles sending the crash reports as well as showing the
 * required dialogs.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class CrashReporter {
    /**
     * This is the key used in the configuration to store and read the settings
     * for the reporting system.
     */
    public static final String CFG_KEY = "errorReport"; //$NON-NLS-1$

    /**
     * This constant is used as display value in case the crash reporter is
     * supposed to display the crash window as AWT window.
     */
    public static final int DISPLAY_AWT = 1;

    /**
     * This constant is used as display value in case the crash reporter is
     * supposed to display the crash window as SWING window.
     */
    public static final int DISPLAY_SWING = 2;

    /**
     * This constant is used as display value in case the crash reporter is
     * supposed to display the crash window as SWT window.
     */
    public static final int DISPLAY_SWT = 3;

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
            result =
                new URL("http://illarion.org/development/java_report.php"); //$NON-NLS-1$
        } catch (final MalformedURLException e) {
            LOGGER.warn("Preparing the crash report target URL failed. " + //$NON-NLS-1$
                "Crash reporter not functional."); //$NON-NLS-1$
        }
        CRASH_SERVER = result;
    }

    /**
     * The configuration handler that is used for the settings of this class.
     */
    private Config cfg;

    /**
     * The currently displayed report dialog is displayed in this class.
     */
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
        display = DISPLAY_AWT;
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static CrashReporter getInstance() {
        return INSTANCE;
    }

    /**
     * Report a crash to the Illarion Server in case the application is supposed
     * to do so.
     * 
     * @param crash the data about the crash
     */
    public void reportCrash(final CrashData crash) {
        reportCrash(crash, false);
    }

    /**
     * Report a crash to the Illarion Server in case the application is supposed
     * to do so.
     * 
     * @param crash the data about the crash
     * @param ownThread <code>true</code> in case the crash report is supposed
     *            to be started in a additional thread
     */
    @SuppressWarnings("nls")
    public void reportCrash(final CrashData crash, final boolean ownThread) {
        if (ownThread) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    reportCrash(crash, false);
                }
            }).start();
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
                    } else if (display == DISPLAY_AWT) {
                        constr =
                            Reflection.getInstance().getConstructor(
                                "illarion.common.bug.ReportDialogAwt()");
                    } else if (display == DISPLAY_SWT) {
                        constr =
                            Reflection.getInstance().getConstructor(
                                "illarion.common.bug.ReportDialogSwt()");
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
    public void setConfig(final Config config) {
        cfg = config;
        if (config != null) {
            setMode(config.getInteger(CFG_KEY));
        }
    }

    /**
     * Set the display mode that is supposed to be used by the crash reporter.
     * Best select the mode so it fits best to the rest of your GUI. The legal
     * values are {@link #DISPLAY_AWT} and {@link #DISPLAY_SWING}.
     * 
     * @param newDisplay the new display value
     * @throws IllegalArgumentException in case a invalid display mode is chosen
     */
    @SuppressWarnings("nls")
    public void setDisplay(final int newDisplay) {
        if ((newDisplay != DISPLAY_AWT) && (newDisplay != DISPLAY_SWING)
            && (newDisplay != DISPLAY_SWT)) {
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
                } catch (final InterruptedException e) {
                    LOGGER.debug("Wait for report was interrupted!", e); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Send the data of the crash to the Illarion server.
     * 
     * @param data the data that was collected about the crash
     */
    @SuppressWarnings("nls")
    private void sendCrashData(final CrashData data) {
        if (CRASH_SERVER == null) {
            return;
        }

        Writer output = null;
        Reader in = null;
        try {
            final HttpURLConnection conn =
                (HttpURLConnection) CRASH_SERVER.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            final TextBuilder queryBuilder = TextBuilder.newInstance();
            queryBuilder.append("os=").append(
                URLEncoder.encode(CrashData.getOSName(), CHARSET));
            queryBuilder.append("&app=").append(
                URLEncoder.encode(data.getApplicationName(), CHARSET));
            queryBuilder.append("&version=").append(
                URLEncoder.encode(data.getApplicationVersion(), CHARSET));
            queryBuilder.append("&thread=").append(
                URLEncoder.encode(data.getThreadName(), CHARSET));
            queryBuilder.append("&exception=").append(
                URLEncoder.encode(data.getExceptionName(), CHARSET));
            queryBuilder.append("&stack=").append(
                URLEncoder.encode(data.getStackBacktrace(), CHARSET));
            final String query = queryBuilder.toString();
            TextBuilder.recycle(queryBuilder);

            output = new OutputStreamWriter(conn.getOutputStream());

            output.write(query);
            output.flush();

            in =
                new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = ((BufferedReader) in).readLine()) != null) {
                LOGGER.info(line);
            }
        } catch (final MalformedURLException ex) {
            LOGGER.error("Target URL for the transfer target was malformed",
                ex);
        } catch (final IOException ex) {
            LOGGER.error("Sending the crash report failed", ex);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (final IOException e1) {
                    LOGGER.error("Failed closing the output stream", e1);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException e1) {
                    LOGGER.error("Failed closing the input stream", e1);
                }
            }
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
    private void setMode(final int newMode) {
        if ((newMode != MODE_ALWAYS) && (newMode != MODE_ASK)
            && (newMode != MODE_NEVER)) {
            mode = MODE_ASK;
            cfg.set(CFG_KEY, MODE_ASK);
            return;
        }

        mode = newMode;
        cfg.set(CFG_KEY, newMode);
    }
}

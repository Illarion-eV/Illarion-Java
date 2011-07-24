/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import illarion.client.guiNG.Journal;
import illarion.client.util.AbstractDialog;
import illarion.client.util.CellLayout;
import illarion.client.util.Lang;
import illarion.client.world.Game;

import illarion.common.config.Config;
import illarion.common.config.ConfigChangeListener;
import illarion.common.config.ConfigDialog;
import illarion.common.config.entries.CheckEntry;
import illarion.common.config.entries.NumberEntry;
import illarion.common.config.entries.SelectEntry;
import illarion.common.util.Base64;

import illarion.graphics.Graphics;

/**
 * The login dialog offers the possibility to type in the login values to
 * connect the a selectable or a defined server. Also it offers to access the
 * option dialog and other small things.
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.95
 */
public final class LoginDialog extends AbstractDialog implements
    ConfigChangeListener, KeyListener {
    /**
     * This cell renderer overwrites the default table cell renderer and
     * disables by this the selection border.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    static final class CharListCellRender extends DefaultTableCellRenderer {
        /**
         * The serialization UID of this cell renderer
         */
        private static final long serialVersionUID = 1L;

        /**
         * Overwrite the border function to ensure that no border is drawn on
         * the cells.
         */
        @Override
        public void setBorder(final Border border) {
            // noting
        }
    }

    /**
     * This class is a table model that is used to displaye the character list
     * on the login screen.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    private final class CharList extends AbstractTableModel {
        /**
         * This class defines one character on the character list on the login
         * screen and stores all data that is needed to handle the char
         * correctly.
         * 
         * @author Martin Karing
         * @since 1.22
         */
        private final class Char implements Serializable {
            /**
             * The serialisation UID for this data storage for character login
             * informations.
             */
            private static final long serialVersionUID = 1L;

            /**
             * The date when the character was last played.
             */
            private final String lastPlayed;

            /**
             * The name of the character.
             */
            private final String name;

            /**
             * The status of the character.
             */
            private final int status;

            /**
             * The constructor that stores and converts all values as needed.
             * 
             * @param charName the name of the character
             * @param charLastPlayed the UTC unix timestamp of the last login
             * @param charStatus the status value of the character
             */
            @SuppressWarnings("nls")
            public Char(final String charName, final int charLastPlayed,
                final int charStatus) {
                name = charName;
                status = charStatus;
                if (charLastPlayed == 0) {
                    lastPlayed = Lang.getMsg("login.chars.lastLogin.never");
                } else {
                    final Calendar lastPlayedCal = Calendar.getInstance();
                    lastPlayedCal.setTimeZone(TimeZone.getTimeZone("UTC"));
                    lastPlayedCal.setTimeInMillis(charLastPlayed * 1000L);
                    lastPlayedCal.setTimeZone(TimeZone.getDefault());

                    final DateFormat format =
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                            DateFormat.SHORT, Lang.getInstance().getLocale());
                    format.setTimeZone(TimeZone.getDefault());
                    lastPlayed = format.format(lastPlayedCal.getTime());
                }
            }

            /**
             * The timestamp when the character was last played.
             * 
             * @return the string that displays the timestamp
             */
            public String getLastPlayed() {
                return lastPlayed;
            }

            /**
             * Get the name of the character.
             * 
             * @return the name of the character
             */
            public String getName() {
                return name;
            }

            /**
             * Get the string that displays the status of the character.
             * 
             * @return the human readable status of the character
             */
            @SuppressWarnings("nls")
            public String getStatus() {
                return Lang.getMsg("login.chars.status." + status);
            }
        }

        /**
         * The string that defines the name of a error node
         */
        @SuppressWarnings("nls")
        private static final String NODE_NAME_ERROR = "error";

        /**
         * The serialisation UID for the table model.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The character list that is currently used for the display.
         */
        private ArrayList<Char> activeChars;

        /**
         * The names for the columns of the displayed table.
         */
        @SuppressWarnings("nls")
        private final String[] columnNames = new String[] {
            Lang.getMsg("login.chars.name"),
            Lang.getMsg("login.chars.status"),
            Lang.getMsg("login.chars.lastLogin"), "" };

        /**
         * The list of characters found for the gameserver.
         */
        private final ArrayList<Char> illaServerChars = new ArrayList<Char>();

        /**
         * The list of characters found for the testserver.
         */
        private final ArrayList<Char> testServerChars = new ArrayList<Char>();

        /**
         * The constructor handles the setup of the active char list.
         */
        public CharList() {
            if (Servers.values()[getServer()] == Servers.realserver) {
                activeChars = illaServerChars;
            } else if (Servers.values()[getServer()] == Servers.testserver) {
                activeChars = testServerChars;
            } else {
                activeChars = null;
            }
        }

        /**
         * Get the amount of columns displayed in the table.
         * 
         * @return the amount of columns in the table
         */
        @Override
        public int getColumnCount() {
            return columnNames.length - 1;
        }

        /**
         * Get the names of the colums.
         * 
         * @param column the index of the colum the name is needed for
         * @return the name of the column
         */
        @Override
        public String getColumnName(final int column) {
            if ((column > -1) && (column < 3)) {
                return columnNames[column];
            }
            return columnNames[3];
        }

        /**
         * Get the count of rows that are displayed in this table. This equals
         * the amount of displayed characters.
         * 
         * @return the amount of displayed rows
         */
        @Override
        public int getRowCount() {
            if (activeChars == null) {
                return 0;
            }
            return activeChars.size();
        }

        /**
         * Get the entry on one cell of the table.
         * 
         * @param rowIndex the row the wanted cell is in
         * @param columnIndex the column the wanted cell is in
         * @return the entry of the cell
         */
        @Override
        @SuppressWarnings("nls")
        public String getValueAt(final int rowIndex, final int columnIndex) {
            if ((activeChars == null)
                || ((columnIndex < 0) && (columnIndex > 2))
                || ((rowIndex < 0) || (rowIndex > activeChars.size()))) {
                return "";
            }
            String result;
            switch (columnIndex) {
                case 0:
                    result = activeChars.get(rowIndex).getName();
                    break;
                case 1:
                    result = activeChars.get(rowIndex).getStatus();
                    break;
                case 2:
                    result = activeChars.get(rowIndex).getLastPlayed();
                    break;
                default:
                    result = "";
                    break;
            }
            return result;
        }

        /**
         * Read the char data from a XML structure.
         * 
         * @param root the root node of the XML structure
         */
        @SuppressWarnings({ "nls", "synthetic-access" })
        public void readXML(final Node root) {
            if (!root.getNodeName().equals("chars")
                && !root.getNodeName().equals(NODE_NAME_ERROR)) {
                final NodeList childs = root.getChildNodes();
                final int count = childs.getLength();
                for (int i = 0; i < count; i++) {
                    readXML(childs.item(i));
                }
                return;
            }
            if (root.getNodeName().equals(NODE_NAME_ERROR)) {
                return;
            }
            final NodeList childs = root.getChildNodes();
            final int count = childs.getLength();
            int oldLength = 0;
            if (activeChars != null) {
                oldLength = activeChars.size();
            }

            final String accLang =
                root.getAttributes().getNamedItem("lang").getNodeValue();
            if (accLang.equals("de") && Lang.getInstance().isEnglish()) {
                IllaClient.getCfg().set("locale", "de");
                Lang.getInstance().recheckLocale();

                updateDisplayedTexts();
                buildSizeAndLocation();
            } else if (accLang.equals("us") && Lang.getInstance().isGerman()) {
                IllaClient.getCfg().set("locale", "en");
                Lang.getInstance().recheckLocale();

                updateDisplayedTexts();
                buildSizeAndLocation();
            }

            boolean foundSomething = false;
            for (int i = 0; i < count; i++) {
                final Node charNode = childs.item(i);
                if (!foundSomething) {
                    foundSomething = true;
                    illaServerChars.clear();
                    testServerChars.clear();
                }
                final String charName = charNode.getTextContent();
                final int status =
                    Integer.parseInt(charNode.getAttributes()
                        .getNamedItem("status").getNodeValue());
                final int lastLogin =
                    Integer.parseInt(charNode.getAttributes()
                        .getNamedItem("lastLogin").getNodeValue());
                final String charServer =
                    charNode.getAttributes().getNamedItem("server")
                        .getNodeValue();

                final Char addChar = new Char(charName, lastLogin, status);

                if (charServer.equals("illarionserver")) {
                    illaServerChars.add(addChar);
                } else if (charServer.equals("testserver")) {
                    testServerChars.add(addChar);
                }
            }

            if (!foundSomething) {
                illaServerChars.clear();
                testServerChars.clear();
            }

            if (activeChars != null) {
                final int newLength = activeChars.size();
                if (newLength == oldLength) {
                    fireTableRowsUpdated(0, oldLength - 1);
                } else if (newLength > oldLength) {
                    fireTableRowsUpdated(0, oldLength - 1);
                    fireTableRowsInserted(oldLength, newLength - 1);
                } else {
                    fireTableRowsUpdated(0, newLength - 1);
                    fireTableRowsDeleted(newLength, oldLength - 1);
                }
            }
        }

        /**
         * Set the active server and toggle the display of the chars in case its
         * needed.
         * 
         * @param newServer the new server that shall be displayed
         */
        public void setActiveServer(final Servers newServer) {
            int oldLength = 0;
            if (newServer == Servers.realserver) {
                if (activeChars == illaServerChars) {
                    return;
                }
                if (activeChars == null) {
                    oldLength = 0;
                } else {
                    oldLength = activeChars.size();
                }
                activeChars = illaServerChars;
            } else if (newServer == Servers.testserver) {
                if (activeChars == testServerChars) {
                    return;
                }
                if (activeChars == null) {
                    oldLength = 0;
                } else {
                    oldLength = activeChars.size();
                }
                activeChars = testServerChars;
            } else {
                if (activeChars == null) {
                    return;
                }
                oldLength = activeChars.size();
                activeChars = null;
            }
            int newLength = 0;
            if (activeChars != null) {
                newLength = activeChars.size();
            }

            if (newLength == oldLength) {
                fireTableRowsUpdated(0, oldLength - 1);
            } else if (newLength > oldLength) {
                fireTableRowsUpdated(0, oldLength - 1);
                fireTableRowsInserted(oldLength, newLength - 1);
            } else {
                fireTableRowsUpdated(0, newLength - 1);
                fireTableRowsDeleted(newLength, oldLength - 1);
            }
        }
    }

    /**
     * Configuration settings value for high graphic quality.
     */
    protected static final int SETTINGS_GRAPHIC_HIGH = 1;

    /**
     * Configuration settings value for low graphic quality.
     */
    protected static final int SETTINGS_GRAPHIC_LOW = 3;

    /**
     * Configuration settings value for maximal graphic quality.
     */
    protected static final int SETTINGS_GRAPHIC_MAX = 0;

    /**
     * Configuration settings value for minimal graphic quality.
     */
    protected static final int SETTINGS_GRAPHIC_MIN = 4;

    /**
     * Configuration settings value for normal graphic quality.
     */
    protected static final int SETTINGS_GRAPHIC_NORMAL = 2;

    /**
     * The horizontal gaps of the border layout of the about dialog.
     */
    private static final int ABOUTDLG_HORZ_GAP = 0;

    /**
     * The vertical gaps of the border layout of the about dialog.
     */
    private static final int ABOUTDLG_VERT_GAP = 20;

    /**
     * The URL of the English account system page.
     */
    private static final String ACCSYS_ENGLISH =
        "http://illarion.org/community/account/us_charlist.php"; //$NON-NLS-1$  //  @jve:decl-index=0:

    /**
     * The URL of the German account system page.
     */
    private static final String ACCSYS_GERMAN =
        "http://illarion.org/community/account/de_charlist.php"; //$NON-NLS-1$  //  @jve:decl-index=0:

    /**
     * The list of known browsers for the run under Linux OS. This is needed for
     * the {@link #openURL(String)} method.
     */
    @SuppressWarnings("nls")
    private static final String[] BROWSERS = { "firefox", "opera",
        "konqueror", "epiphany", "seamonkey", "galeon", "kazehakase",
        "mozilla", "netscape" };

    /**
     * The horizontal spaces between the elements and the borders.
     */
    private static final int HORIZONTAL_SPACES = 10;

    /**
     * The instance of the logger that is used write the log output of this
     * class.
     */
    private static final Logger LOGGER = Logger.getLogger(LoginDialog.class);

    /**
     * The maximal length of the password.
     */
    private static final int MAX_PASSWORD_LENGTH = 20;

    /**
     * The minimal length of the name so the login button gets enabled.
     */
    private static final int MINIMAL_NAME_LENGTH = 4;

    /**
     * The minimal length for the password so the login button gets enabled.
     */
    private static final int MINIMAL_PASS_LENGTH = 4;

    /**
     * The serial version UID that offers the serialisation possibilities of
     * this class and ensures that always the correct serialised versions are
     * loaded.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The vertical spaces between the elements and the borders.
     */
    private static final int VERTICAL_SPACES = 10;

    /**
     * The about dialog that shows some background informations about the
     * development of the client. Its build in case its requested.
     */
    private JPanel aboutDlg;

    /**
     * The char list that is used to display the characters of the player.
     */
    private CharList charList;

    /**
     * The table element that displays the characters.
     */
    private JTable charTable;

    /**
     * The parent frame of the login window that should be the game screen
     * itself.
     */
    private final Frame frame;

    /**
     * Reference to the game handler that is the parent of this login handler.
     * This game handler has to get the login value that were typed in and are
     * to send to the server.
     */
    private final transient Game game;

    /**
     * The combo box that holds the character name that is send to the server as
     * login name. Also it holds the names of the last few character names that
     * were used to login.
     */
    private JTextField login;

    /**
     * The message shown in the login screen. Could be a error message or a
     * information message.
     */
    private JLabel optionMessage;

    /**
     * The text field that holds the password that is send to the server for
     * login. All inputs are masked.
     */
    private JTextField password;

    /**
     * The button used to reload the character list.
     */
    private JButton reloadButton;

    /**
     * The check box that allows to toggle if the password shall get stored or
     * not.
     */
    private JCheckBox savePassword;

    /**
     * The combo box that allows to select the server that is the connection
     * target. The game server client does not show this combo box.
     */
    private JComboBox server;

    /**
     * A checking variable if the login values are valid. Means that they have a
     * minimal length. Only in case this variable is true the play button will
     * be enabled.
     */
    private boolean validLogin;

    /**
     * Constructor for the login dialog. It creates all objects on the dialog
     * and sets the needed references so the login window becomes a child window
     * of the actual game window.
     * 
     * @param gameClass the instance of the game class that will store the login
     *            data for the login action itself
     * @param parent the parent window for the login window, the login window
     *            will be a modal window of the parent window
     */
    @SuppressWarnings("nls")
    protected LoginDialog(final Game gameClass, final Frame parent) {
        super(parent, Lang.getMsg("login.title"), true);

        game = gameClass;
        frame = parent;
        ClientWindow.getInstance().setIcon(frame);
        textMap = new HashMap<Component, String>();

        buildWindow();
    }

    /**
     * Show manual for the requested language.
     * 
     * @param lang the locale value for the selected language
     */
    @SuppressWarnings("nls")
    public static void showManual(final Locale lang) {
        if (lang == Locale.GERMAN) {
            openURL("file://" + IllaClient.getFile("/manual_de.pdf"));
        } else {
            openURL("file://" + IllaClient.getFile("/manual_en.pdf"));
        }
    }

    /**
     * Open a URL with the systems browser.
     * <p>
     * <b>Bare Bones Browser Launch</b><br>
     * Homepage: <a href="http://www.centerkey.com/java/browser/">
     * http://www.centerkey.com/java/browser/ </a>
     * </p>
     * Supports:
     * <ul>
     * <li>Mac OS X</li>
     * <li>GNU/Linux</li>
     * <li>Unix</li>
     * <li>Windows XP/Vista</li>
     * </ul>
     * 
     * @param url the URL that shall be opened
     * @version 2.0 (May 26, 2009)
     * @author Dem Pilafian
     */
    @SuppressWarnings("nls")
    protected static void openURL(final String url) {
        final String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                final Class<?> fileMgr =
                    Class.forName("com.apple.eio.FileManager");
                final Method openURL =
                    fileMgr.getDeclaredMethod("openURL",
                        new Class[] { String.class });
                openURL.invoke(null, new Object[] { url });
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec(
                    "rundll32 url.dll,FileProtocolHandler " + url);
            } else { // assume Unix or Linux
                boolean found = false;
                for (final String browser : BROWSERS) {
                    if (!found) {
                        found =
                            Runtime.getRuntime()
                                .exec(new String[] { "which", browser })
                                .waitFor() == 0;
                        if (found) {
                            Runtime.getRuntime().exec(
                                new String[] { browser, url });
                        }
                    }
                }
                if (!found) {
                    throw new IllegalStateException(Arrays.toString(BROWSERS));
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Error attempting to launch web browser", e);
        }
    }

    /**
     * Load a image from the resources of the client. This image can be used
     * with java graphics and java2d.
     * 
     * @param path the path the image shall be loaded from
     * @return the image that was loaded
     */
    @SuppressWarnings("nls")
    private static ImageIcon loadIcon(final String path) {
        try {
            final InputStream in = IllaClient.getResource(path);
            final byte[] buffer = new byte[in.available()];
            int data = 0;
            while (true) {
                final int read = in.read(buffer, data, buffer.length - data);
                if (read <= 0) {
                    break;
                }
                data += read;
            }
            return new ImageIcon(buffer);
        } catch (final Exception e) {
            LOGGER.error("can't load image " + path, e);
            return null;
        }
    }

    /**
     * Function called in case the user clicks on cancel.
     */
    @Override
    public void actionCancel() {
        validLogin = false;
        setVisible(false);
    }

    /**
     * Function called in case the user clicks on login.
     */
    @Override
    @SuppressWarnings("nls")
    public void actionOK() {
        final String name =
            (String) charTable.getValueAt(charTable.getSelectedRow(), 0);
        game.setLogin(name, password.getText());
        validLogin = true;

        // save the login name
        IllaClient.getCfg().set("lastLogin", login.getText());

        // store password if requested
        storePassword(savePassword.isSelected(), password.getText());

        // this command must be last as it ends the modal dialog
        setVisible(false);
    }

    /**
     * This monitors the configuration and displays a warning message in case it
     * is needed.
     */
    @SuppressWarnings("nls")
    @Override
    public void configChanged(final Config cfg, final String key) {
        if ((key.equals("lowMemory") || key.equals("engine"))
            && (optionMessage != null)) {
            optionMessage.setVisible(true);
        }
    }

    /**
     * Get the server that is selected as login target.
     * 
     * @return the index of the server that is selected in the list of servers
     */
    public int getServer() {
        return server.getSelectedIndex();
    }

    /**
     * Invoked in case a key is pressed with focus on the login window.
     * 
     * @param keyEvent the event that triggered this function
     */
    @Override
    public void keyPressed(final KeyEvent keyEvent) {
        // handled by keyTyped
    }

    /**
     * Invoked in case a key is released with focus on the login window.
     * 
     * @param keyEvent the event that triggered this function
     */
    @Override
    public void keyReleased(final KeyEvent keyEvent) {
        // handled by keyTyped
    }

    /**
     * This function is invoked when ever a key is pressed on the login form.
     * After each click its checked if the minimal length values for the
     * password and the character name are reached and if the lengths are okay
     * the login button is enabled.
     * 
     * @param keyEvent the event that triggered this function
     */
    @Override
    public void keyTyped(final KeyEvent keyEvent) {
        final String txt = login.getText();
        if (txt != null) {
            setButtonEnabled(BUTTON_OK, (charTable.getSelectedRow() > -1)
                && (password.getText().length() >= MINIMAL_PASS_LENGTH));
            reloadButton
                .setEnabled((login.getText().length() >= MINIMAL_NAME_LENGTH)
                    && (password.getText().length() >= MINIMAL_PASS_LENGTH));
        }
    }

    /**
     * Set the current shown error message of the client.
     * 
     * @param message the error message shown in the login screen
     */
    @SuppressWarnings("nls")
    public void setErrorMessage(final String message) {

        optionMessage.setText("<HTML>" + message.replace("\n", "<BR>")
            + "</HTML>");
        optionMessage.setVisible(true);
        pack();
    }

    /**
     * Set the server that is used as login target. This sets the selection on
     * the login screen.
     * 
     * @param selServer the index of the server that shall be selected in the
     *            list of servers that is usually shown on the server selection
     *            list
     */
    public void setServer(final int selServer) {
        server.setSelectedIndex(selServer);
    }

    /**
     * Get the URL of the account system regarding a locale settings.
     * 
     * @param locale the locale version that is requested
     * @return the string that contains the URL
     */
    String getAccountSystemURL(final Locale locale) {
        if (locale == Locale.GERMAN) {
            return ACCSYS_GERMAN;
        }
        return ACCSYS_ENGLISH;
    }

    /**
     * Show the about dialog. In case its not created yet, its initialised.
     */
    @SuppressWarnings("nls")
    void showAbout() {
        if (aboutDlg == null) {
            aboutDlg =
                new JPanel(new BorderLayout(ABOUTDLG_HORZ_GAP,
                    ABOUTDLG_VERT_GAP));
            addTitleImage(aboutDlg);

            final JPanel text = new JPanel(new CellLayout());
            text.add(new JLabel(Lang.getMsg("about.version")));
            text.add(new JLabel(IllaClient.VERSION));
            text.add(new JLabel(Lang.getMsg("about.code")));
            text.add(new JLabel("Martin \"Nitram\" Karing"));
            text.add(new JLabel());
            text.add(new JLabel("Ralf \"Ragorn\" Schumacher"));
            text.add(new JLabel());
            text.add(new JLabel("Andreas \"Vilarion\" Grob"));
            text.add(new JLabel());
            text.add(new JLabel("Nop"));
            text.add(new JLabel(Lang.getMsg("about.graphics")));
            text.add(new JLabel("Achae Eanstray"));
            text.add(new JLabel());
            text.add(new JLabel("Grobul"));
            text.add(new JLabel());
            text.add(new JLabel("Esther \"Kadiya\" Sense"));
            text.add(new JLabel());
            text.add(new JLabel("Martin \"Moorfox\" Polak"));
            text.add(new JLabel());
            text.add(new JLabel("Nop"));
            text.add(new JLabel(Lang.getMsg("about.opengl")));
            text.add(new JLabel(
                "Lightweight Java Game Library (www.lwjgl.org)"));

            text.add(new JLabel(Lang.getMsg("about.ogg")));
            text.add(new JLabel("VorbisSPI (www.javazoom.net)"));
            text.add(new JLabel(Lang.getMsg("about.folder")));
            text.add(new JLabel(IllaClient.getFile("")));
            aboutDlg.add(text);
        }

        JOptionPane.showMessageDialog(this, aboutDlg,
            Lang.getMsg("about.title"), JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Show the options dialog. This also sets then configuration entries of the
     * options dialog in case they were not initialized before.
     */
    @SuppressWarnings("nls")
    void showOptions() {
        final ConfigDialog optionDlg = new ConfigDialog();
        optionDlg.setDisplaySystem(ConfigDialog.DISPLAY_SWING);
        optionDlg.setMessageSource(Lang.getInstance());
        optionDlg.setConfig(IllaClient.getCfg());

        ConfigDialog.Page page = new ConfigDialog.Page("option.common");
        page.addEntry(new ConfigDialog.Entry("option.mode", new CheckEntry(
            "fullScreen")));
        page.addEntry(new ConfigDialog.Entry("option.textlog", new CheckEntry(
            "textLog")));
        page.addEntry(new ConfigDialog.Entry("option.ids", new CheckEntry(
            "showIDs")));
        page.addEntry(new ConfigDialog.Entry("option.report", new SelectEntry(
            "errorReport", SelectEntry.STORE_INDEX, Lang
                .getMsg("option.report.ask"), Lang
                .getMsg("option.report.always"), Lang
                .getMsg("option.report.never"))));
        page.addEntry(new ConfigDialog.Entry("option.music", new CheckEntry(
            "musicOn")));
        page.addEntry(new ConfigDialog.Entry("option.volume", new NumberEntry(
            "musicVolume", 0, 100)));
        page.addEntry(new ConfigDialog.Entry("option.sounds", new CheckEntry(
            "soundOn")));
        page.addEntry(new ConfigDialog.Entry("option.volume", new NumberEntry(
            "soundVolume", 0, 100)));
        optionDlg.addPage(page);

        page = new ConfigDialog.Page("option.graphics");
        page.addEntry(new ConfigDialog.Entry("option.engine", new SelectEntry(
            "engine", SelectEntry.STORE_INDEX, Lang
                .getMsg("option.engine.lwjgl"), Lang
                .getMsg("option.engine.jogl"))));
        page.addEntry(new ConfigDialog.Entry("option.resolution",
            new SelectEntry("resolution", SelectEntry.STORE_VALUE,
                (Object[]) Graphics.getInstance().getRenderDisplay()
                    .getPossibleResolutions())));
        page.addEntry(new ConfigDialog.Entry(
            "option.memory",
            new SelectEntry("lowMemory", SelectEntry.STORE_INDEX, Lang
                .getMsg("option.memory.max"), Lang
                .getMsg("option.memory.high"), Lang
                .getMsg("option.memory.normal"), Lang
                .getMsg("option.memory.low"), Lang.getMsg("option.memory.min"))));
        optionDlg.addPage(page);

        page = new ConfigDialog.Page("option.game");
        page.addEntry(new ConfigDialog.Entry("option.journal.lines",
            new NumberEntry(Journal.CFG_JOURNAL_LENGTH, 10, 1000)));
        page.addEntry(new ConfigDialog.Entry("option.journal.font",
            new SelectEntry(Journal.CFG_JOURNAL_FONT, SelectEntry.STORE_INDEX,
                Lang.getMsg("option.journal.font.large"), Lang
                    .getMsg("option.journal.font.small"))));

        page = new ConfigDialog.Page("option.special");
        page.addEntry(new ConfigDialog.Entry("option.tweakWheel",
            new CheckEntry("relativeMouseWheel")));
        page.addEntry(new ConfigDialog.Entry("option.tweakMouse",
            new CheckEntry("disableMouseWrap")));
        page.addEntry(new ConfigDialog.Entry("option.grapMouse",
            new CheckEntry("grapMouse")));
        optionDlg.addPage(page);

        optionDlg.show();
    }

    /**
     * Update the displayed list of characters.
     */
    @SuppressWarnings("nls")
    void updateCharacters() {
        final String loginName = login.getText();
        final String passwd = password.getText();

        if ((loginName.length() < MINIMAL_NAME_LENGTH)
            || (passwd.length() < MINIMAL_PASS_LENGTH)) {
            reloadButton.setEnabled(false);
            return;
        }

        reloadButton.setEnabled(false);

        final JTable charactersTable = charTable;
        final CharList charactersList = charList;

        final String serverURI =
            Servers.values()[server.getSelectedIndex()].getServerHost();

        SwingUtilities.invokeLater(new Runnable() {
            /**
             * The logger instance that takes care for the logging output of
             * this class.
             */
            private final Logger log = Logger.getLogger(LoginDialog.class);

            @Override
            public void run() {
                try {

                    final URL requestURL =
                        new URL("http://" + serverURI
                            + "/community/account/xml_charlist.php");

                    final HttpURLConnection conn =
                        (HttpURLConnection) requestURL.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    final StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("name=");
                    queryBuilder.append(URLEncoder.encode(loginName, "UTF-8"));
                    queryBuilder.append("&passwd=");
                    queryBuilder.append(URLEncoder.encode(passwd, "UTF-8"));

                    final String query = queryBuilder.toString();
                    final OutputStreamWriter output =
                        new OutputStreamWriter(conn.getOutputStream());

                    output.write(query);
                    output.flush();
                    output.close();

                    final int oldSelected = charactersTable.getSelectedRow();

                    final DocumentBuilderFactory dbf =
                        DocumentBuilderFactory.newInstance();
                    final DocumentBuilder db = dbf.newDocumentBuilder();
                    final Document doc = db.parse(conn.getInputStream());

                    charactersList.readXML(doc);

                    if (charactersTable.getRowCount() > 0) {
                        if (oldSelected > -1) {
                            if (oldSelected > charactersTable.getRowCount()) {
                                charactersTable.clearSelection();
                                charactersTable.addRowSelectionInterval(0, 0);
                            } else {
                                charactersTable.clearSelection();
                                charactersTable.addRowSelectionInterval(
                                    oldSelected, oldSelected);
                            }
                        } else {
                            charactersTable.clearSelection();
                            charactersTable.addRowSelectionInterval(0, 0);
                        }
                    }
                    keyTyped(null);
                } catch (final UnknownHostException e) {
                    log.error("Failed to resolve hostname, for fetching the charlist");
                } catch (final Exception e) {
                    log.error("Loading the charlist from the server failed");
                }
            }
        });
    }

    /**
     * Show the login dialog.
     * 
     * @return true in case the login is valid, false if not
     */
    protected boolean display() {
        validLogin = false;
        toFront();
        password.requestFocus();
        setVisible(true);

        return validLogin;
    }

    /**
     * At finalization of this dialog the configuration listener is removed.
     */
    @Override
    protected void finalize() throws Throwable {
        IllaClient.getCfg().removeListener(this);
        super.finalize();
    }

    /**
     * At the title image to the content window along with its title.
     * 
     * @param content the content pane that holds all components of the login
     *            window
     */
    @SuppressWarnings("nls")
    private void addTitleImage(final JPanel content) {
        final JLabel titleLabel = new JLabel(Lang.getMsg("login.title.label"));
        final ImageIcon titleImage = loadIcon("data/gui/login_title.png");
        titleLabel.setIcon(titleImage);
        titleLabel.setOpaque(false);
        if (IllaClient.MULTI_CLIENT) {
            titleLabel.setText("Test Client");
        }
        titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        titleLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        content.add(titleLabel);
    }

    /**
     * Construct the entire window and setup all values for a proper display.
     */
    @SuppressWarnings("nls")
    private void buildWindow() {
        getContentPane().removeAll();
        super.init();
        // basic dialog contents
        addTextComponent(this, "login.title");
        final JPanel content = (JPanel) getContentPane();
        ((BorderLayout) content.getLayout()).setHgap(HORIZONTAL_SPACES);
        ((BorderLayout) content.getLayout()).setVgap(VERTICAL_SPACES);

        final JPanel title = new JPanel(new FlowLayout());
        addTitleImage(title);
        title.setAlignmentY(0.5f);
        content.add(title, BorderLayout.NORTH);

        final JPanel center = new JPanel(new BorderLayout());
        content.add(center);

        final JPanel panel = new JPanel(new CellLayout());
        final Border contentBorder =
            BorderFactory.createEmptyBorder(HORIZONTAL_SPACES,
                VERTICAL_SPACES, HORIZONTAL_SPACES, VERTICAL_SPACES);
        content.setBorder(contentBorder);
        center.add(panel);

        // login fields
        server = new JComboBox(getServerNames());
        server.setSelectedIndex(IllaClient.DEFAULT_SERVER.ordinal());
        if (IllaClient.MULTI_CLIENT) {
            panel.add(addTextComponent(new JLabel(), "login.server"));
            
            panel.add(server);
            server.addActionListener(new ActionListener() {
                @Override
                @SuppressWarnings("synthetic-access")
                public void actionPerformed(final ActionEvent e) {
                    charList.setActiveServer(Servers.values()[server
                        .getSelectedIndex()]);
                }
            });
        }
        panel.add(addTextComponent(new JLabel(), "login.login"));
        
        login = new JTextField(IllaClient.getCfg().getString("lastLogin"));
        panel.add(login);

        panel.add(addTextComponent(new JLabel(), "login.password"));
        password = new JPasswordField(MAX_PASSWORD_LENGTH);
        panel.add(password);

        // save passwort toogle
        panel.add(new JLabel());
        savePassword = new JCheckBox(Lang.getMsg("login.keepPassword"));
        panel.add(savePassword);

        // free row
        panel.add(new JLabel(" "));
        panel.add(new JLabel());

        panel.add(addTextComponent(new JLabel(), "login.chars"));

        charList = new CharList();
        charTable = new JTable(charList);
        charTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        charTable.setRowSelectionAllowed(true);
        charTable.setColumnSelectionAllowed(false);
        charTable.getTableHeader().setReorderingAllowed(false);
        charTable.getTableHeader().setResizingAllowed(false);
        charTable.setDefaultRenderer(Object.class, new CharListCellRender());
        charTable.addKeyListener(this);
        charTable.addMouseListener(new MouseAdapter() {
            @Override
            @SuppressWarnings("synthetic-access")
            public void mouseClicked(final MouseEvent e) {
                keyTyped(null);
                if (e.getClickCount() == 2) {
                    if (isButtonEnabled(AbstractDialog.BUTTON_OK)) {
                        actionOK();
                    }
                    return;
                }
            }
        });

        final JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(charTable);
        scroll.setPreferredSize(new Dimension(scroll.getWidth(), ((charTable
            .getRowHeight() + charTable.getRowMargin()) * 6) - 3));
        panel.add(scroll);

        panel.add(new JLabel());
        reloadButton = addTextComponent(new JButton(), "login.chars.reload");
        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateCharacters();
            }
        });
        panel.add(reloadButton);

        // free row
        panel.add(new JLabel(" "));
        panel.add(new JLabel(" "));

        // select last used character
        login.setEditable(true);

        // listener for enabling the buttons
        login.addKeyListener(this);
        password.addKeyListener(this);

        // add full screen check box
        final GridLayout optionsLayout =
            new GridLayout(2, 2, HORIZONTAL_SPACES, VERTICAL_SPACES);
        final JPanel options = new JPanel(optionsLayout);
        panel.add(new JLabel());
        panel.add(options);

        // additional buttons
        final JPanel links = new JPanel(new GridLayout(1, 5));
        center.add(links, BorderLayout.SOUTH);

        final JButton webBtn = addTextComponent(new JButton(), "login.web");
        webBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                openURL(getAccountSystemURL(Lang.getInstance().getLocale()));
            }
        });
        links.add(webBtn);

        final JButton manualBtn = addTextComponent(new JButton(), "login.manual");
        manualBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                showManual(Lang.getInstance().getLocale());
            }
        });
        links.add(manualBtn);

        final JButton forumBtn = addTextComponent(new JButton(), "login.forum");
        forumBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                openURL("http://illarion.org/community/forums/");
            }
        });
        links.add(forumBtn);

        optionMessage = addTextComponent(new JLabel(), "login.options.warning");
        optionMessage.setHorizontalAlignment(SwingConstants.CENTER);

        final JButton optionBtn = addTextComponent(new JButton(), "login.options");
        optionBtn.addActionListener(new ActionListener() {
            @Override
            @SuppressWarnings("synthetic-access")
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    showOptions();
                } catch (final Exception ex) {
                    LoginDialog.LOGGER.error(
                        "Error while showing option dialog", ex);
                }
            }
        });
        links.add(optionBtn);

        final JButton aboutBtn = addTextComponent(new JButton(), "login.about");
        aboutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                showAbout();
            }
        });
        links.add(aboutBtn);

        // add the buttons
        setButtonEnabled(BUTTON_OK, false);
        setButtonName(BUTTON_CANCEL, Lang.getMsg("button.cancel"));

        // restore password, possibly enabling the ok button
        restorePassword();

        optionMessage.setForeground(java.awt.Color.RED);
        optionMessage.setVisible(false);
        center.add(optionMessage, BorderLayout.NORTH);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setDefaultButton(BUTTON_OK);
        updateCharacters();

        updateDisplayedTexts();
        buildSizeAndLocation();

        IllaClient.getCfg().addListener(this);
    }
    
    /**
     * This function stores a text component in the required list and returns
     * it. Its used to easily add the required components to the GUI.
     * 
     * @param comp the component that is added
     * @param textKey the key in the translation file for this key
     * @return the object that was entered as comp
     */
    private <T extends Component> T addTextComponent(final T comp, final String textKey) {
        textMap.put(comp, textKey);
        return comp;
    }
    
    /**
     * This map is used to store the components of the login display that store
     * any text along with their key to the language system.
     */
    private Map<Component, String> textMap;
    
    /**
     * Update the texts of all components stored in the map.
     */
    private void updateDisplayedTexts() {
        for(Map.Entry<Component, String> entry : textMap.entrySet()) {
            Component comp = entry.getKey();
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText(Lang.getMsg(entry.getValue()));
            } else if (comp instanceof JButton) {
                ((JButton) comp).setText(Lang.getMsg(entry.getValue()));
            } else if (comp instanceof JDialog) {
                ((JDialog) comp).setTitle(Lang.getMsg(entry.getValue()));
            }
        }
        

        setButtonName(BUTTON_OK, Lang.getMsg("button.play")); //$NON-NLS-1$
        setButtonName(BUTTON_CANCEL, Lang.getMsg("button.cancel")); //$NON-NLS-1$
    }
    
    /**
     * Set the size and the location of the login dialog properly.
     */
    private void buildSizeAndLocation() {
        setResizable(false);
        setPreferredSize(null);
        pack();
        final int maxDim = Math.max(getHeight(), getWidth());
        setPreferredSize(new Dimension((int) (maxDim * 1.15f), maxDim));
        center();
    }

    /**
     * Get a list of the names of the available servers that are defined. This
     * list is shown in the server selector in case this selector is shown.
     * 
     * @return each array index contains the name of one server
     */
    private String[] getServerNames() {
        final Servers[] servers = Servers.values();
        final String[] serverNames = new String[servers.length];
        for (int i = 0; i < servers.length; ++i) {
            serverNames[i] = servers[i].getServerName();
        }
        return serverNames;
    }

    /**
     * Load the saved password from the configuration file and insert it to the
     * password field on the login window.
     */
    @SuppressWarnings("nls")
    private void restorePassword() {
        savePassword.setSelected(IllaClient.getCfg()
            .getBoolean("savePassword"));
        if (savePassword.isSelected()) {
            final String encoded =
                IllaClient.getCfg().getString("fingerprint");
            if (encoded != null) {
                password.setText(shufflePassword(encoded, true));
                keyTyped(null);
            }
        }
    }

    /**
     * Shuffle the letters of the password around a bit.
     * 
     * @param pw the encoded password or the decoded password that stall be
     *            shuffled
     * @param decode false for encoding the password, true for decoding.
     * @return the encoded or the decoded password
     */
    @SuppressWarnings("nls")
    private String shufflePassword(final String pw, final boolean decode) {

        try {
            final Charset usedCharset = Charset.forName("UTF-8");
            // creating the key
            final DESKeySpec keySpec =
                new DESKeySpec(IllaClient.getFile("").getBytes(usedCharset));
            final SecretKeyFactory keyFactory =
                SecretKeyFactory.getInstance("DES");
            final SecretKey key = keyFactory.generateSecret(keySpec);

            final Cipher cipher = Cipher.getInstance("DES");
            if (decode) {
                byte[] encrypedPwdBytes =
                    Base64.decode(pw.getBytes(usedCharset));
                cipher.init(Cipher.DECRYPT_MODE, key);
                encrypedPwdBytes = cipher.doFinal(encrypedPwdBytes);
                return new String(encrypedPwdBytes, usedCharset);
            }

            final byte[] cleartext = pw.getBytes(usedCharset);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(Base64.encode(cipher.doFinal(cleartext)),
                usedCharset);
        } catch (final InvalidKeyException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        } catch (final NoSuchAlgorithmException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        } catch (final InvalidKeySpecException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        } catch (final NoSuchPaddingException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        } catch (final IllegalBlockSizeException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        } catch (final BadPaddingException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        } catch (final IllegalArgumentException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        }
    }

    /**
     * Store the password in the configuration file or remove the stored
     * password from the configuration.
     * 
     * @param store store the password or remove it, true means that the
     *            password is stored, false that it is removed
     * @param pw the password that stall be stored to the configuration file
     */
    @SuppressWarnings("nls")
    private void storePassword(final boolean store, final String pw) {
        if (store) {
            IllaClient.getCfg().set("savePassword", true);
            IllaClient.getCfg().set("fingerprint", shufflePassword(pw, false));
        } else {
            IllaClient.getCfg().set("savePassword", false);
            IllaClient.getCfg().remove("fingerprint");
        }
    }
}

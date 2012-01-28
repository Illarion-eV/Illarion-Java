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

import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.NetComm;
import illarion.client.net.client.LoginCmd;
import illarion.client.net.client.MapDimensionCmd;
import illarion.client.util.Lang;
import illarion.client.world.MapDimensions;
import illarion.client.world.World;
import illarion.common.util.Base64;
import javolution.util.FastTable;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * This class is used to store the login parameters and handle the requests that
 * need to be send to the server in order to perform the login properly.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Login {
    private static final class CharEntry {
        private final String charName;
        private final int charStatus;
        public CharEntry(final String name, final int status) {
            charName = name;
            charStatus = status;
        }
        
        public String getName() {
            return charName;
        }
        
        public int getStatus() {
            return charStatus;
        }
    }
    
    private String loginName;
    private String password;
    private CharEntry selectedChar;
    private final List<CharEntry> charList;
    
    private Login() {
        charList = new FastTable<CharEntry>();
    }
    
    private static final Login INSTANCE = new Login();
    
    public static Login getInstance() {
        return INSTANCE;
    }
    
    public void setLoginData(final String name, final String pass) {
        loginName = name;
        password = pass;
    }
    
    public void restoreLoginData() {
        restoreLogin();
        restorePassword();
        restoreStorePassword();
    }
    
    public void storeData(final boolean storePasswd) {
        IllaClient.getCfg().set("lastLogin", loginName);
        IllaClient.getCfg().set("savePassword", storePasswd);
        if (storePasswd) {
            storePassword(true, password);
        } else {
            storePassword(false, null);
        }
        IllaClient.getCfg().save();
    }
    
    public String getLoginName() {
        if (loginName == null) {
            return "";
        }
        return loginName;
    }
    
    public String getPassword() {
        if (password == null) {
            return "";
        }
        return password;
    }
    
    public void requestCharacterList() {
        lastError = -1;
        final String serverURI = Servers.testserver.getServerHost();
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
            queryBuilder.append(URLEncoder.encode(getLoginName(), "UTF-8"));
            queryBuilder.append("&passwd=");
            queryBuilder.append(URLEncoder.encode(getPassword(), "UTF-8"));

            final String query = queryBuilder.toString();
            
            conn.connect();
            
            final OutputStreamWriter output =
                new OutputStreamWriter(conn.getOutputStream());

            output.write(query);
            output.flush();
            output.close();

            final DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(conn.getInputStream());

            readXML(doc);
        } catch (final UnknownHostException e) {
            lastError = 2;
            LOGGER.error("Failed to resolve hostname, for fetching the charlist");
        } catch (final Exception e) {
            lastError = 2;
            LOGGER.error("Loading the charlist from the server failed");
        }
    }

    /**
     * The string that defines the name of a error node
     */
    @SuppressWarnings("nls")
    private static final String NODE_NAME_ERROR = "error";
    
    /**
     * The instance of the logger that is used write the log output of this
     * class.
     */
    private static final Logger LOGGER = Logger.getLogger(Login.class);
    
    private int lastError = -1;
    
    public boolean hasError() {
        return (lastError != -1);
    }
    
    public int getErrorId() {
        return lastError;
    }
    
    public String getErrorText() {
        return Lang.getMsg("login.error." + Integer.toString(lastError));
    }
    
    private void readXML(final Node root) {
        lastError = -1;
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
            lastError = Integer.parseInt(root.getAttributes().getNamedItem("id").getNodeValue());
            return;
        }
        final NodeList childs = root.getChildNodes();
        final int count = childs.getLength();

        final String accLang =
            root.getAttributes().getNamedItem("lang").getNodeValue();
        if (accLang.equals("de") && Lang.getInstance().isEnglish()) {
            IllaClient.getCfg().set("locale", "de");
            Lang.getInstance().recheckLocale();
        } else if (accLang.equals("us") && Lang.getInstance().isGerman()) {
            IllaClient.getCfg().set("locale", "en");
            Lang.getInstance().recheckLocale();
        }

        boolean foundSomething = false;
        for (int i = 0; i < count; i++) {
            final Node charNode = childs.item(i);
            if (!foundSomething) {
                foundSomething = true;
                charList.clear();
            }
            final String charName = charNode.getTextContent();
            final int status =
                Integer.parseInt(charNode.getAttributes()
                    .getNamedItem("status").getNodeValue());
            final String charServer =
                charNode.getAttributes().getNamedItem("server")
                    .getNodeValue();

            final CharEntry addChar = new CharEntry(charName, status);

            if (charServer.equals("testserver")) {
                charList.add(addChar);
            }
        }

        if (!foundSomething) {
            charList.clear();
        }
    }
    
    public int getCharacterCount() {
        return charList.size();
    }
    
    public String getCharacterName(final int index) {
        return charList.get(index).getName();
    }
    
    public int getCharacterStatus(final int index) {
        return charList.get(index).getStatus();
    }
    
    public void selectCharacter(final int index) {
        if (index < 0) {
            return;
        }
        selectedChar = charList.get(index);
    }
    
    public void selectCharacter(final String charName) {
        for (CharEntry chars : charList) {
            if (chars.getName().equals(charName)) {
                selectedChar = chars;
                return;
            }
        }
    }
    
    public String getSelectedCharacterName() {
        return selectedChar.getName();
    }
    
    public boolean login() {
        if (selectedChar == null) {
            return false;
        }
        
        final NetComm netComm = World.getNet();
        if (!netComm.connect()) {
            return false;
        }
        
        final LoginCmd loginCmd = CommandFactory.getInstance().getCommand(CommandList.CMD_LOGIN, LoginCmd.class);
        loginCmd.setLogin(getSelectedCharacterName(), password);
        loginCmd.setVersion(Servers.testserver.getClientVersion());
        loginCmd.send();
        
        final MapDimensionCmd mapDimCmd = CommandFactory.getInstance().getCommand(CommandList.CMD_MAPDIMENSION, MapDimensionCmd.class);
        mapDimCmd.setMapDimensions(MapDimensions.getInstance().getStripesWidth() >> 2, MapDimensions.getInstance().getStripesHeight() >> 2);
        mapDimCmd.send();
        
        return true;
    }

    /**
     * Load the saved password from the configuration file and insert it to the
     * password field on the login window.
     */
    @SuppressWarnings("nls")
    private void restorePassword() {
        final String encoded =
            IllaClient.getCfg().getString("fingerprint");
        if (encoded != null) {
            password = shufflePassword(encoded, true);
        }
    }
    
    private boolean storePassword;
    
    private void restoreStorePassword() {
        storePassword = IllaClient.getCfg().getBoolean("savePassword");
    }
    
    public boolean storePassword() {
        return storePassword;
    }
    
    private void restoreLogin() {
        loginName = IllaClient.getCfg().getString("lastLogin");
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
        } catch (final GeneralSecurityException e) {
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

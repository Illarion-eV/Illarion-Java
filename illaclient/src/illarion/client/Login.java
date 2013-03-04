/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client;

import illarion.client.net.NetComm;
import illarion.client.net.client.LoginCmd;
import illarion.client.util.GlobalExecutorService;
import illarion.client.util.Lang;
import illarion.client.world.World;
import illarion.common.data.IllarionSSLSocketFactory;
import illarion.common.util.Base64;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is used to store the login parameters and handle the requests that
 * need to be send to the server in order to perform the login properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Login {

    public static final class CharEntry {
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
    private String loginCharacter;
    private List<Login.CharEntry> charList;

    private Login() {
        charList = new ArrayList<Login.CharEntry>();
    }

    private static final Login INSTANCE = new Login();

    @Nonnull
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
            storePassword(password);
        } else {
            deleteStoredPassword();
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

    public interface RequestCharListCallback {
        void finishedRequest(int errorCode);
    }

    private final class RequestCharacterListTask implements Callable<Void> {
        private final Login.RequestCharListCallback callback;

        private RequestCharacterListTask(final Login.RequestCharListCallback callback) {
            this.callback = callback;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Nullable
        @Override
        public Void call() throws Exception {
            requestCharacterListInternal(callback);
            return null;
        }
    }

    public boolean isCharacterListRequired() {
        return (IllaClient.DEFAULT_SERVER != Servers.testserver)
                || IllaClient.getCfg().getBoolean("serverAccountLogin");
    }

    public void requestCharacterList(final Login.RequestCharListCallback resultCallback) {
        GlobalExecutorService.getService().submit(new Login.RequestCharacterListTask(resultCallback));
    }

    private void requestCharacterListInternal(@Nonnull final Login.RequestCharListCallback resultCallback) {
        final String serverURI = IllaClient.DEFAULT_SERVER.getServerHost();
        try {
            final URL requestURL = new URL("https://" + serverURI + "/account/xml_charlist.php");

            final StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("name=");
            queryBuilder.append(URLEncoder.encode(getLoginName(), "UTF-8"));
            queryBuilder.append("&passwd=");
            queryBuilder.append(URLEncoder.encode(getPassword(), "UTF-8"));
            final String query = queryBuilder.toString();

            final HttpsURLConnection conn = (HttpsURLConnection) requestURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(query.getBytes().length));
            conn.setUseCaches(false);
            conn.setSSLSocketFactory(IllarionSSLSocketFactory.getFactory());

            conn.connect();

            final OutputStreamWriter output = new OutputStreamWriter(conn.getOutputStream());

            output.write(query);
            output.flush();
            output.close();

            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(conn.getInputStream());

            readXML(doc, resultCallback);
        } catch (@Nonnull final UnknownHostException e) {
            resultCallback.finishedRequest(2);
            LOGGER.error("Failed to resolve hostname, for fetching the charlist");
        } catch (@Nonnull final Exception e) {
            resultCallback.finishedRequest(2);
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

    private void readXML(@Nonnull final Node root, @Nonnull final Login.RequestCharListCallback resultCallback) {
        if (!"chars".equals(root.getNodeName()) && !NODE_NAME_ERROR.equals(root.getNodeName())) {
            final NodeList children = root.getChildNodes();
            final int count = children.getLength();
            for (int i = 0; i < count; i++) {
                readXML(children.item(i), resultCallback);
            }
            return;
        }
        if (NODE_NAME_ERROR.equals(root.getNodeName())) {
            final int error = Integer.parseInt(root.getAttributes().getNamedItem("id").getNodeValue());
            resultCallback.finishedRequest(error);
            return;
        }
        final NodeList children = root.getChildNodes();
        final int count = children.getLength();

        final String accLang = root.getAttributes().getNamedItem("lang").getNodeValue();
        if ("de".equals(accLang)) {
            IllaClient.getCfg().set(Lang.LOCALE_CFG, Lang.LOCALE_CFG_GERMAN);
        } else if ("us".equals(accLang)) {
            IllaClient.getCfg().set(Lang.LOCALE_CFG, Lang.LOCALE_CFG_ENGLISH);
        }

        charList.clear();
        for (int i = 0; i < count; i++) {
            final Node charNode = children.item(i);
            final String charName = charNode.getTextContent();
            final int status = Integer.parseInt(charNode.getAttributes().getNamedItem("status").getNodeValue());
            final String charServer = charNode.getAttributes().getNamedItem("server").getNodeValue();

            final Login.CharEntry addChar = new Login.CharEntry(charName, status);

            switch (IllaClient.DEFAULT_SERVER) {
                case localserver:
                    charList.add(addChar);
                    break;
                case testserver:
                    if ("testserver".equals(charServer)) {
                        charList.add(addChar);
                    }
                    break;
                case realserver:
                    if ("illarionserver".equals(charServer)) {
                        charList.add(addChar);
                    }
                    break;
            }
        }

        resultCallback.finishedRequest(0);
    }

    public List<Login.CharEntry> getCharacterList() {
        return Collections.unmodifiableList(charList);
    }

    public void setLoginCharacter(final String character) {
        loginCharacter = character;
    }

    public String getLoginCharacter() {
        if (!isCharacterListRequired()) {
            return loginName;
        }
        return loginCharacter;
    }

    public boolean login() {
        final NetComm netComm = World.getNet();
        if (!netComm.connect()) {
            return false;
        }

        final int clientVersion;
        if (IllaClient.DEFAULT_SERVER == Servers.testserver) {
            clientVersion = IllaClient.getCfg().getInteger("clientVersion");
        } else {
            clientVersion = IllaClient.DEFAULT_SERVER.getClientVersion();
        }
        World.getNet().sendCommand(new LoginCmd(getLoginCharacter(), password, clientVersion));

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
     * @param pw     the encoded password or the decoded password that stall be
     *               shuffled
     * @param decode false for encoding the password, true for decoding.
     * @return the encoded or the decoded password
     */
    @Nonnull
    @SuppressWarnings("nls")
    private static String shufflePassword(@Nonnull final String pw, final boolean decode) {

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
        } catch (@Nonnull final GeneralSecurityException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        } catch (@Nonnull final IllegalArgumentException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        }
    }

    /**
     * Store the password in the configuration file or remove the stored password from the configuration.
     *
     * @param pw the password that stall be stored to the configuration file
     */
    @SuppressWarnings("nls")
    private void storePassword(@Nonnull final String pw) {
        IllaClient.getCfg().set("savePassword", true);
        IllaClient.getCfg().set("fingerprint", shufflePassword(pw, false));
    }

    /**
     * Remove the stored password.
     */
    private void deleteStoredPassword() {
        IllaClient.getCfg().set("savePassword", false);
        IllaClient.getCfg().remove("fingerprint");
    }
}

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
package illarion.client;

import illarion.client.net.NetComm;
import illarion.client.net.client.LoginCmd;
import illarion.client.util.GlobalExecutorService;
import illarion.client.util.Lang;
import illarion.client.world.World;
import illarion.common.data.IllarionSSLSocketFactory;
import illarion.common.util.Base64;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
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

    public static final int DEVSERVER = 0;
    public static final int TESTSERVER = 1;
    public static final int GAMESERVER = 2;
    public static final int CUSTOMSERVER = 3;

    public static final class CharEntry {
        private final String charName;
        private final int charStatus;

        public CharEntry(String name, int status) {
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

    @Nullable
    private String loginName;
    @Nullable
    private String password;
    @Nullable
    private Servers server;
    @Nullable
    private String loginCharacter;
    @Nonnull
    private final List<CharEntry> charList;

    private Login() {
        charList = new ArrayList<>();
    }

    private static final Login INSTANCE = new Login();

    @Nonnull
    public static Login getInstance() {
        return INSTANCE;
    }

    public void setLoginData(String name, String pass) {
        loginName = name;
        password = pass;
    }

    public void setServer(int server) {
        for (Servers serverEntry : Servers.values()) {
            if (serverEntry.getServerKey() == server) {
                setServer(serverEntry);
                return;
            }
        }
    }

    public void setServer(@Nonnull Servers server) {
        this.server = server;
    }

    public void restoreLoginData() {
        restoreLogin();
        restorePassword();
        restoreStorePassword();
    }

    public void storeData(boolean storePassword) {
        if (IllaClient.DEFAULT_SERVER == Servers.realserver) {
            IllaClient.getInstance().setUsedServer(Servers.realserver);
        } else {
            IllaClient.getCfg().set("server", getServer().getServerKey());
            IllaClient.getInstance().setUsedServer(getServer());
        }

        switch (getServer()) {
            case customserver:
                IllaClient.getCfg().set("customLastLogin", getLoginName());
                IllaClient.getCfg().set("customSavePassword", storePassword);
                break;
            default:
                IllaClient.getCfg().set("lastLogin", getLoginName());
                IllaClient.getCfg().set("savePassword", storePassword);
        }
        if (storePassword) {
            storePassword(getPassword());
        } else {
            deleteStoredPassword();
        }

        IllaClient.getCfg().save();
    }

    @Nonnull
    public String getLoginName() {
        if (loginName == null) {
            return "";
        }
        return loginName;
    }

    @Nonnull
    public String getPassword() {
        if (password == null) {
            return "";
        }
        return password;
    }

    @Nonnull
    public Servers getServer() {
        if (server == null) {
            return Servers.realserver;
        }
        return server;
    }

    public interface RequestCharListCallback {
        void finishedRequest(int errorCode);
    }

    private final class RequestCharacterListTask implements Callable<Void> {
        private final RequestCharListCallback callback;

        private RequestCharacterListTask(RequestCharListCallback callback) {
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
        switch (getServer()) {
            case customserver:
                return IllaClient.getCfg().getBoolean("serverAccountLogin");
            default:
                return true;
        }
    }

    public void requestCharacterList(RequestCharListCallback resultCallback) {
        GlobalExecutorService.getService().submit(new RequestCharacterListTask(resultCallback));
    }

    private void requestCharacterListInternal(@Nonnull RequestCharListCallback resultCallback) {
        String serverURI = IllaClient.DEFAULT_SERVER.getServerHost();
        try {
            URL requestURL = new URL("https://" + serverURI + "/account/xml_charlist.php");

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("name=");
            queryBuilder.append(URLEncoder.encode(getLoginName(), "UTF-8"));
            queryBuilder.append("&passwd=");
            queryBuilder.append(URLEncoder.encode(getPassword(), "UTF-8"));
            String query = queryBuilder.toString();

            HttpsURLConnection conn = (HttpsURLConnection) requestURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(query.getBytes("UTF-8").length));
            conn.setUseCaches(false);

            SSLSocketFactory sslSocketFactory = IllarionSSLSocketFactory.getFactory();
            if (sslSocketFactory != null) {
                conn.setSSLSocketFactory(sslSocketFactory);
            }

            conn.connect();

            try (OutputStreamWriter output = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
                output.write(query);
                output.flush();
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(conn.getInputStream());

            readXML(doc, resultCallback);
        } catch (@Nonnull UnknownHostException e) {
            resultCallback.finishedRequest(2);
            LOGGER.error("Failed to resolve hostname, for fetching the charlist");
        } catch (@Nonnull Exception e) {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    private void readXML(@Nonnull Node root, @Nonnull RequestCharListCallback resultCallback) {
        if (!"chars".equals(root.getNodeName()) && !NODE_NAME_ERROR.equals(root.getNodeName())) {
            NodeList children = root.getChildNodes();
            int count = children.getLength();
            for (int i = 0; i < count; i++) {
                readXML(children.item(i), resultCallback);
            }
            return;
        }
        if (NODE_NAME_ERROR.equals(root.getNodeName())) {
            int error = Integer.parseInt(root.getAttributes().getNamedItem("id").getNodeValue());
            resultCallback.finishedRequest(error);
            return;
        }
        NodeList children = root.getChildNodes();
        int count = children.getLength();

        String accLang = root.getAttributes().getNamedItem("lang").getNodeValue();
        if ("de".equals(accLang)) {
            IllaClient.getCfg().set(Lang.LOCALE_CFG, Lang.LOCALE_CFG_GERMAN);
        } else if ("us".equals(accLang)) {
            IllaClient.getCfg().set(Lang.LOCALE_CFG, Lang.LOCALE_CFG_ENGLISH);
        }

        charList.clear();
        for (int i = 0; i < count; i++) {
            Node charNode = children.item(i);
            String charName = charNode.getTextContent();
            int status = Integer.parseInt(charNode.getAttributes().getNamedItem("status").getNodeValue());
            String charServer = charNode.getAttributes().getNamedItem("server").getNodeValue();

            CharEntry addChar = new CharEntry(charName, status);

            switch (IllaClient.getInstance().getUsedServer()) {
                case customserver:
                    charList.add(addChar);
                    break;
                case testserver:
                    if ("testserver".equals(charServer)) {
                        charList.add(addChar);
                    }
                    break;
                case devserver:
                    if ("devserver".equals(charServer)) {
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

    public List<CharEntry> getCharacterList() {
        return Collections.unmodifiableList(charList);
    }

    public void setLoginCharacter(@Nonnull String character) {
        loginCharacter = character;
    }

    @Nullable
    public String getLoginCharacter() {
        if (!isCharacterListRequired()) {
            return loginName;
        }
        return loginCharacter;
    }

    public boolean login() {
        NetComm netComm = World.getNet();
        if (!netComm.connect()) {
            return false;
        }

        String loginChar = getLoginCharacter();
        if (loginChar == null) {
            return false;
        }

        int clientVersion;
        switch (getServer()) {
            case customserver:
                if (IllaClient.getCfg().getBoolean("clientVersionOverwrite")) {
                    clientVersion = IllaClient.getCfg().getInteger("clientVersion");
                } else {
                    clientVersion = getServer().getClientVersion();
                }
                break;
            default:
                clientVersion = getServer().getClientVersion();
        }
        World.getNet().sendCommand(new LoginCmd(loginChar, getPassword(), clientVersion));

        return true;
    }

    /**
     * Load the saved password from the configuration file and insert it to the
     * password field on the login window.
     */
    @SuppressWarnings("nls")
    private void restorePassword() {
        String encoded;
        switch (getServer()) {
            case customserver:
                encoded = IllaClient.getCfg().getString("customFingerprint");
                break;
            default:
                encoded = IllaClient.getCfg().getString("fingerprint");
        }

        password = (encoded != null) ? shufflePassword(encoded, true) : "";
    }

    private boolean storePassword;

    private void restoreStorePassword() {
        switch (getServer()) {
            case customserver:
                storePassword = IllaClient.getCfg().getBoolean("customSavePassword");
                break;
            default:
                storePassword = IllaClient.getCfg().getBoolean("savePassword");
        }
    }

    public boolean getStorePassword() {
        return storePassword;
    }

    private void restoreLogin() {
        switch (getServer()) {
            case customserver:
                loginName = IllaClient.getCfg().getString("customLastLogin");
                break;
            default:
                loginName = IllaClient.getCfg().getString("lastLogin");
        }
    }

    public void restoreServer() {
        setServer(IllaClient.getCfg().getInteger("server"));
    }

    /**
     * Shuffle the letters of the password around a bit.
     *
     * @param pw the encoded password or the decoded password that stall be
     * shuffled
     * @param decode false for encoding the password, true for decoding.
     * @return the encoded or the decoded password
     */
    @Nonnull
    @SuppressWarnings("nls")
    private static String shufflePassword(@Nonnull String pw, boolean decode) {

        try {
            Charset usedCharset = Charset.forName("UTF-8");
            // creating the key
            Path userDir = DirectoryManager.getInstance().getDirectory(Directory.User);
            KeySpec keySpec = new DESKeySpec(userDir.toAbsolutePath().toString().getBytes(usedCharset));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES");
            if (decode) {
                byte[] encrypedPwdBytes = Base64.decode(pw.getBytes(usedCharset));
                cipher.init(Cipher.DECRYPT_MODE, key);
                encrypedPwdBytes = cipher.doFinal(encrypedPwdBytes);
                return new String(encrypedPwdBytes, usedCharset);
            }

            byte[] cleartext = pw.getBytes(usedCharset);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(Base64.encode(cipher.doFinal(cleartext)), usedCharset);
        } catch (@Nonnull GeneralSecurityException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        } catch (@Nonnull IllegalArgumentException e) {
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
    private void storePassword(@Nonnull String pw) {
        switch (getServer()) {
            case customserver:
                IllaClient.getCfg().set("customSavePassword", true);
                IllaClient.getCfg().set("customFingerprint", shufflePassword(pw, false));
                break;
            default:
                IllaClient.getCfg().set("savePassword", true);
                IllaClient.getCfg().set("fingerprint", shufflePassword(pw, false));
        }
    }

    /**
     * Remove the stored password.
     */
    private void deleteStoredPassword() {
        switch (getServer()) {
            case customserver:
                IllaClient.getCfg().set("customSavePassword", false);
                IllaClient.getCfg().remove("customFingerprint");
                break;
            default:
                IllaClient.getCfg().set("savePassword", false);
                IllaClient.getCfg().remove("fingerprint");
        }
    }
}

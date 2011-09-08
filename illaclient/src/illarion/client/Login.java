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

import illarion.client.util.Lang;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javolution.util.FastTable;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used to store the login parameters and handle the requests that
 * need to be send to the server in order to perform the login properly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
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
    
    String getLoginName() {
        return loginName;
    }
    
    String getPassword() {
        return password;
    }
    
    public void requestCharacterList() {
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
}

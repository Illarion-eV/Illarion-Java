/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyQuest Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyQuest Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import illarion.easyquest.Lang;

final class ServerBand extends JRibbonBand {
    /**
     * The serialization UID of this ribbon band.
     */
    private static final long serialVersionUID = 1L;
    
    private final JCommandButton characterButton;
    private final JCommandButton statusButton;

    /**
     * Default constructor that prepares the buttons displayed on this band.
     */
    @SuppressWarnings("nls")
    public ServerBand() {
        super(Lang.getMsg(ServerBand.class, "title"), null);

        characterButton = 
        	new JCommandButton(Lang.getMsg(ServerBand.class, "character"),
                    Utils.getResizableIconFromResource("character.png"));
        
        characterButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
                ServerBand.class, "characterTooltipTitle"), Lang.getMsg(
                ServerBand.class, "characterTooltip")));
        
        statusButton = 
        	new JCommandButton(Lang.getMsg(ServerBand.class, "status"),
                    Utils.getResizableIconFromResource("setstatus.png"));

        statusButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
                ServerBand.class, "statusTooltipTitle"), Lang.getMsg(
                ServerBand.class, "statusTooltip")));

        final ActionListener statusAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	final Editor currentEditor = MainFrame.getInstance().getCurrentQuestEditor();
            	final int id = currentEditor.getQuestID();
            	int status;
            	try {
            		status = currentEditor.getSelectedStatusNumber();
            	} catch (IllegalStateException exception) {
            		JOptionPane.showMessageDialog(MainFrame.getInstance(),
            				Lang.getMsg(ServerBand.class, "wrongSelectionText") + exception.getMessage(),
            				Lang.getMsg(ServerBand.class, "wrongSelectionCaption"),
                            JOptionPane.ERROR_MESSAGE);
            		return;
            	}
            	
            	int msgType = JOptionPane.INFORMATION_MESSAGE;
            	String msgText = null;
            	String msgCaption = null;
            	
                try {

                    final URL requestURL =
                        new URL("http://illarion.org/easyquest/xml_setqueststatus.php");

                    final HttpURLConnection conn =
                        (HttpURLConnection) requestURL.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    final StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("character=");
                    queryBuilder.append(URLEncoder.encode(Config.getInstance().getCharacter(), "UTF-8"));
                    queryBuilder.append("&password=");
                    queryBuilder.append(URLEncoder.encode(Config.getInstance().getPassword(), "UTF-8"));
                    queryBuilder.append("&questid=");
                    queryBuilder.append(id);
                    queryBuilder.append("&queststatus=");
                    queryBuilder.append(status);

                    final String query = queryBuilder.toString();
                    final OutputStreamWriter output =
                        new OutputStreamWriter(conn.getOutputStream());

                    output.write(query);
                    output.flush();
                    output.close();

                    final String result = new Scanner(conn.getInputStream()).useDelimiter("\\A").next();

                    if (result.equals("SUCCESS")) {
                    	msgCaption = Lang.getMsg(ServerBand.class, "successCaption");
                    	msgText = Lang.getMsg(ServerBand.class, "successText") + Config.getInstance().getCharacter();
                    } else {
                    	msgType = JOptionPane.ERROR_MESSAGE;
                    	if (result.equals("E_MISS")) {
                    		msgCaption = Lang.getMsg(ServerBand.class, "incompatibleCaption");
                        	msgText = Lang.getMsg(ServerBand.class, "incompatibleText");
                    	} else if (result.equals("E_CHAR")) {
                    		msgCaption = Lang.getMsg(ServerBand.class, "noCharCaption");
                        	msgText = Lang.getMsg(ServerBand.class, "noCharText") + Config.getInstance().getCharacter();
                    	} else if (result.equals("E_LOGIN")) {
                    		msgCaption = Lang.getMsg(ServerBand.class, "noLoginCaption");
                        	msgText = Lang.getMsg(ServerBand.class, "noLoginText");
                    	}
                    }
                	 
                } catch (final UnknownHostException exception) {
                	msgType = JOptionPane.ERROR_MESSAGE;
                	msgCaption = Lang.getMsg(ServerBand.class, "noHostCaption");
                	msgText = Lang.getMsg(ServerBand.class, "noHostText");
                } catch (final Exception exception) {
                	msgType = JOptionPane.ERROR_MESSAGE;
                	msgCaption = Lang.getMsg(ServerBand.class, "failureCaption");
                	msgText = Lang.getMsg(ServerBand.class, "failureText");
                }
                
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                		msgText,
                        msgCaption,
                        msgType);
            }
        };
        
        statusButton.addActionListener(statusAction);

        addCommandButton(characterButton, RibbonElementPriority.TOP);
        addCommandButton(statusButton, RibbonElementPriority.TOP);

        final List<RibbonBandResizePolicy> policies =
            new ArrayList<RibbonBandResizePolicy>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}

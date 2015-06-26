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
package illarion.easyquest.gui;

import illarion.easyquest.Lang;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mid2Low;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mirror;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

final class ServerBand extends JRibbonBand {
    /**
     * The serialization UID of this ribbon band.
     */
    private static final long serialVersionUID = 1L;

    @Nonnull
    private final JCommandButton statusButton;

    /**
     * Default constructor that prepares the buttons displayed on this band.
     */
    public ServerBand() {
        super(Lang.getMsg(ServerBand.class, "title"), null);

        statusButton = new JCommandButton(Lang.getMsg(ServerBand.class, "status"),
                                          Utils.getResizableIconFromResource("setstatus.png"));

        statusButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(ServerBand.class, "statusTooltipTitle"),
                                                          Lang.getMsg(ServerBand.class, "statusTooltip")));

        ActionListener statusAction = e -> {
            Editor currentEditor = MainFrame.getInstance().getCurrentQuestEditor();
            int id = currentEditor.getQuestID();
            int status;
            try {
                status = currentEditor.getSelectedStatusNumber();
            } catch (IllegalStateException exception) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        Lang.getMsg(ServerBand.class, "wrongSelectionText") +
                                exception.getMessage(),
                        Lang.getMsg(ServerBand.class, "wrongSelectionCaption"),
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int msgType = JOptionPane.INFORMATION_MESSAGE;
            String msgText = null;
            String msgCaption = null;

            try {

                URL requestURL = new URL("http://illarion.org/easyquest/xml_setqueststatus.php");

                HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String query =
                        "character=" + URLEncoder.encode(Config.getInstance().getCharacter(), "UTF-8") +
                                "&password=" + URLEncoder.encode(Config.getInstance().getPassword(), "UTF-8") +
                                "&questid=" + id + "&queststatus=" + status;
                OutputStreamWriter output = new OutputStreamWriter(conn.getOutputStream(),
                        Charset.forName("UTF-8"));

                output.write(query);
                output.flush();
                output.close();

                String result = new Scanner(conn.getInputStream(), "UTF-8").useDelimiter("\\A").next();

                if (result.equals("SUCCESS")) {
                    msgCaption = Lang.getMsg(ServerBand.class, "successCaption");
                    msgText = Lang.getMsg(ServerBand.class, "successText") + Config.getInstance().getCharacter();
                } else {
                    msgType = JOptionPane.ERROR_MESSAGE;
                    switch (result) {
                        case "E_MISS":
                            msgCaption = Lang.getMsg(ServerBand.class, "incompatibleCaption");
                            msgText = Lang.getMsg(ServerBand.class, "incompatibleText");
                            break;
                        case "E_CHAR":
                            msgCaption = Lang.getMsg(ServerBand.class, "noCharCaption");
                            msgText = Lang.getMsg(ServerBand.class, "noCharText") +
                                    Config.getInstance().getCharacter();
                            break;
                        case "E_LOGIN":
                            msgCaption = Lang.getMsg(ServerBand.class, "noLoginCaption");
                            msgText = Lang.getMsg(ServerBand.class, "noLoginText");
                            break;
                    }
                }
            } catch (@Nonnull UnknownHostException exception) {
                msgType = JOptionPane.ERROR_MESSAGE;
                msgCaption = Lang.getMsg(ServerBand.class, "noHostCaption");
                msgText = Lang.getMsg(ServerBand.class, "noHostText");
            } catch (@Nonnull Exception exception) {
                msgType = JOptionPane.ERROR_MESSAGE;
                msgCaption = Lang.getMsg(ServerBand.class, "failureCaption");
                msgText = Lang.getMsg(ServerBand.class, "failureText");
            }

            JOptionPane.showMessageDialog(MainFrame.getInstance(), msgText, msgCaption, msgType);
        };

        statusButton.addActionListener(statusAction);

        addCommandButton(statusButton, RibbonElementPriority.TOP);

        List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new Mirror(getControlPanel()));
        policies.add(new Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}

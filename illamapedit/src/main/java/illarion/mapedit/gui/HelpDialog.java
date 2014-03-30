/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui;

import illarion.mapedit.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Tim
 * @author Fredrik K
 */
public class HelpDialog extends JDialog implements HyperlinkListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpDialog.class);

    public HelpDialog(final JFrame frame) {
        super(frame, Lang.getMsg("gui.docu.Name"), false);
        setLayout(new BorderLayout());

        JEditorPane html = new JEditorPane(new HTMLEditorKit().getContentType(), "");

        html.setEditable(false);
        setMinimumSize(new Dimension(300, 400));
        setPreferredSize(new Dimension(500, 500));
        html.addHyperlinkListener(this);
        final URL url = HelpDialog.class.getResource(String.format("/docu/%s/mapeditor_docu.html",
                (Lang.getInstance().isGerman()) ? "de" : "en"));
        html.setContentType("text/html");
        try {
            html.setPage(url);
        } catch (IOException e) {
            html.setContentType("text/plain");
            html.setText(Lang.getMsg("gui.docu.IOError"));
            LOGGER.warn(Lang.getMsg("gui.docu.IOError"), e);
        }
        add(new JScrollPane(html, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
            BorderLayout.CENTER);
        pack();
    }

    @Override
    public void hyperlinkUpdate(@Nonnull final HyperlinkEvent e) {
        if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException e1) {
                        LOGGER.warn("Can't launch browser: ", e1);
                    }
                }
            }
        });
    }
}

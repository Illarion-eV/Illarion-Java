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
package illarion.mapedit.gui.docu;

import illarion.mapedit.Lang;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author Tim
 */
public class DocuPage implements HyperlinkListener {
    private static final Logger LOGGER = Logger.getLogger(DocuPage.class);
    private URL url;

    public DocuPage(final URI url) {
        try {
            this.url = url.toURL();
        } catch (MalformedURLException e) {
            this.url = null;
        }
    }

    public void applyPage(final JEditorPane pane) {
        pane.setEditable(false);
        if (url == null) {
            pane.setText(Lang.getMsg("gui.docu.IOError"));
            pane.setContentType("text/plain");
            return;
        }
        try {
            pane.setPage(url);
            pane.setContentType("text/html");
        } catch (IOException e) {
            pane.setText(Lang.getMsg("gui.docu.IOError"));
            pane.setContentType("text/plain");
        }
        pane.addHyperlinkListener(this);
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent e) {
        LOGGER.debug("Hyperlink pressed: " + e.getURL());
    }
}

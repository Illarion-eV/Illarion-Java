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
import illarion.mapedit.events.menu.ShowHelpDialogEvent;
import illarion.mapedit.resource.loaders.DocuLoader;
import javolution.text.TextBuilder;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXTree;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Tim
 */
public class HelpDialog extends JDialog implements HyperlinkListener, TreeSelectionListener {
    private static final Logger LOGGER = Logger.getLogger(HelpDialog.class);
    @Nonnull
    private final JEditorPane html;

    public HelpDialog(final JFrame frame) {
        super(frame, Lang.getMsg("gui.docu.Name"), false);
        AnnotationProcessor.process(this);
        setLayout(new BorderLayout());

        html = new JEditorPane(new HTMLEditorKit().getContentType(), "");
        final JXTree tree = new JXTree(DocuLoader.getInstance());

        html.setEditable(false);
        setMinimumSize(new Dimension(700, 100));
        html.addHyperlinkListener(this);

        tree.addTreeSelectionListener(this);
        add(new JScrollPane(html,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        add(new JScrollPane(tree), BorderLayout.WEST);
        pack();
    }

    @EventSubscriber
    public void onShowHelpDialog(final ShowHelpDialogEvent e) {
        setVisible(true);
    }

    @Override
    public void hyperlinkUpdate(@Nonnull final HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            LOGGER.warn("Can't launch browser: ", e1);
                        } catch (URISyntaxException e1) {
                            LOGGER.warn("Can't launch browser: ", e1);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void valueChanged(@Nonnull final TreeSelectionEvent e) {
        final Object[] path = e.getPath().getPath();
        if (path[path.length - 1] instanceof DocuLoader.Folder) {
            return;
        }
        final TextBuilder b = new TextBuilder();
        try {
            for (final Object o : path) {
                final DocuLoader.File file = (DocuLoader.File) o;
                b.append(file.getPath());
                if (!file.isFile()) {
                    b.append('/');
                }
            }
            final URL url = HelpDialog.class.getResource(b.toString());
            html.setContentType("text/html");
            html.setPage(url);
        } catch (IOException e1) {
            html.setContentType("text/plain");
            html.setText(Lang.getMsg("gui.docu.IOError") + '\n' + b.toString());
        }
    }

}

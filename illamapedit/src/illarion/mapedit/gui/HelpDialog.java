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
import illarion.mapedit.gui.docu.DocuPage;
import illarion.mapedit.gui.docu.DocuTreeModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXTree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.io.File;
import java.net.URI;

/**
 * @author Tim
 */
public class HelpDialog extends JDialog {

    public HelpDialog(final JFrame frame) {
        super(frame, Lang.getMsg("gui.docu.Name"), false);
        AnnotationProcessor.process(this);
        setLayout(new BorderLayout());

        final JEditorPane html = new JEditorPane();
        final JXTree tree = new JXTree(new DocuTreeModel());

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                final Object o = e.getPath().getPath()[e.getPath().getPath().length - 1];
                if (o instanceof File) {
                    final URI uri = ((File) o).toURI();
                    final DocuPage page = new DocuPage(uri);
                    page.applyPage(html);
                }
            }
        });

        add(html, BorderLayout.CENTER);
        add(tree, BorderLayout.WEST);
        pack();
    }

    @EventSubscriber
    public void onShowHelpDialog(final ShowHelpDialogEvent e) {
        setVisible(true);
    }
}

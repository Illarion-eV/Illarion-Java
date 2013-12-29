/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyNPC Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyNPC Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui;

import illarion.easynpc.Lang;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This ribbon band contains the searching utility that can be used in the editor.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SearchBand extends JRibbonBand {
    /**
     * The serialization UID of this search band.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The constructor of the search band that creates all contents of this band
     * properly.
     */
    @SuppressWarnings("nls")
    public SearchBand() {
        super(Lang.getMsg(SearchBand.class, "title"), null);

        startGroup(Lang.getMsg(SearchBand.class, "fastSearchGroup"));
        final JTextField textBox = new JTextField();
        textBox.setPreferredSize(new Dimension(150, textBox.getPreferredSize().height));
        final JRibbonComponent ribbonTextBox = new JRibbonComponent(textBox);
        addRibbonComponent(ribbonTextBox);

        ribbonTextBox.setRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "fastSearchTooltipTitle"),
                                                     Lang.getMsg(getClass(), "fastSearchTooltip")));

        textBox.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(@Nonnull final KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }

                final Editor scriptEditor = MainFrame.getInstance().getCurrentScriptEditor();
                final RSyntaxTextArea editor = scriptEditor.getEditor();

                final SearchContext search = new SearchContext();
                search.setSearchFor(textBox.getText());
                search.setMatchCase(false);
                search.setRegularExpression(false);
                search.setSearchForward(true);

                SearchEngine.find(editor, search);
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                // nothing
            }

            @Override
            public void keyTyped(final KeyEvent e) {
                // nothing
            }
        });

        startGroup();

        final JCommandButton findButton = new JCommandButton(Lang.getMsg(SearchBand.class, "advancedSearchButton"),
                                                             Utils.getResizableIconFromResource("find.png"));
        findButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "findButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "findButtonTooltip")));
        findButton.addActionListener(new ActionListener() {
            @Nullable
            private SearchDialog dialog = null;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (dialog == null) {
                    dialog = new SearchDialog();
                }
                dialog.setVisible(true);
            }
        });

        addCommandButton(findButton, RibbonElementPriority.TOP);

        final List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}

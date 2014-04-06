/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.easynpc.gui;

import illarion.easynpc.Lang;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is the advanced search dialog that can be used to search and replace contents of the script that is currently
 * load.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SearchDialog extends JDialog {
    /**
     * The serialization UID of this dialog.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The constructor of the dialog that prepares the dialog for proper
     * display.
     */
    public SearchDialog() {
        super(MainFrame.getInstance(), Lang.getMsg(SearchDialog.class, "title"), false);

        final SearchDialog dialog = this;

        JPanel mainPanel = new JPanel(new GridBagLayout());
        getContentPane().add(mainPanel);

        GridBagConstraints generalConstraints = new GridBagConstraints();
        generalConstraints.gridheight = 1;
        generalConstraints.gridwidth = 1;
        generalConstraints.insets.bottom = 5;
        generalConstraints.insets.right = 5;
        generalConstraints.insets.left = 5;
        generalConstraints.insets.top = 5;
        generalConstraints.anchor = GridBagConstraints.WEST;

        JLabel searchLabel = new JLabel(Lang.getMsg(SearchDialog.class, "searchForLabel"));
        JLabel replaceLabel = new JLabel(Lang.getMsg(SearchDialog.class, "replaceWithLabel"));

        final JTextField searchText = new JTextField();
        final JTextField replaceField = new JTextField();
        searchText.setPreferredSize(new Dimension(300, searchText.getPreferredSize().height));
        replaceField.setPreferredSize(new Dimension(300, replaceField.getPreferredSize().height));

        final JCheckBox caseSensitiveCheck = new JCheckBox(Lang.getMsg(SearchDialog.class, "caseCheck"));
        final JCheckBox regExpCheck = new JCheckBox(Lang.getMsg(SearchDialog.class, "regExpCheck"));

        JButton findNextBtn = new JButton(Lang.getMsg(SearchDialog.class, "findNextButton"));
        JButton replaceOneBtn = new JButton(Lang.getMsg(SearchDialog.class, "replaceButton"));
        JButton replaceAllBtn = new JButton(Lang.getMsg(SearchDialog.class, "replaceAllButton"));
        JButton closeBtn = new JButton(Lang.getMsg(SearchDialog.class, "closeButton"));
        Dimension buttonDim = findNextBtn.getPreferredSize();
        buttonDim.width = Math.max(buttonDim.width, replaceOneBtn.getPreferredSize().width);
        buttonDim.width = Math.max(buttonDim.width, replaceAllBtn.getPreferredSize().width);
        buttonDim.width = Math.max(buttonDim.width, closeBtn.getPreferredSize().width);
        buttonDim.width += 10;
        findNextBtn.setPreferredSize(buttonDim);
        replaceOneBtn.setPreferredSize(buttonDim);
        replaceAllBtn.setPreferredSize(buttonDim);
        closeBtn.setPreferredSize(buttonDim);

        findNextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Editor scriptEditor = MainFrame.getInstance().getCurrentScriptEditor();
                RSyntaxTextArea editor = scriptEditor.getEditor();

                SearchContext search = new SearchContext();
                search.setSearchFor(searchText.getText());
                search.setMatchCase(caseSensitiveCheck.isSelected());
                search.setRegularExpression(regExpCheck.isSelected());
                search.setSearchForward(true);

                SearchEngine.find(editor, search);
            }
        });

        replaceOneBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Editor scriptEditor = MainFrame.getInstance().getCurrentScriptEditor();
                RSyntaxTextArea editor = scriptEditor.getEditor();

                SearchContext search = new SearchContext();
                search.setSearchFor(searchText.getText());
                search.setReplaceWith(replaceField.getText());
                search.setMatchCase(caseSensitiveCheck.isSelected());
                search.setRegularExpression(regExpCheck.isSelected());
                search.setSearchForward(true);

                SearchEngine.replace(editor, search);
            }
        });

        replaceAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Editor scriptEditor = MainFrame.getInstance().getCurrentScriptEditor();
                RSyntaxTextArea editor = scriptEditor.getEditor();

                SearchContext search = new SearchContext();
                search.setSearchFor(searchText.getText());
                search.setReplaceWith(replaceField.getText());
                search.setMatchCase(caseSensitiveCheck.isSelected());
                search.setRegularExpression(regExpCheck.isSelected());
                search.setSearchForward(true);

                SearchEngine.replaceAll(editor, search);
            }
        });

        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });

        generalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(searchLabel, generalConstraints);

        generalConstraints.gridwidth = 1;
        generalConstraints.insets.bottom = 20;
        mainPanel.add(searchText, generalConstraints);
        generalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(findNextBtn, generalConstraints);

        generalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        generalConstraints.insets.bottom = 5;
        mainPanel.add(replaceLabel, generalConstraints);

        generalConstraints.gridwidth = 1;
        mainPanel.add(replaceField, generalConstraints);
        generalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(replaceOneBtn, generalConstraints);

        generalConstraints.gridwidth = 1;
        generalConstraints.insets.bottom = 20;
        mainPanel.add(new JLabel(), generalConstraints);
        generalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(replaceAllBtn, generalConstraints);

        generalConstraints.gridwidth = 1;
        generalConstraints.insets.bottom = 5;
        mainPanel.add(caseSensitiveCheck, generalConstraints);
        generalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(new JLabel(), generalConstraints);

        generalConstraints.gridwidth = 1;
        mainPanel.add(regExpCheck, generalConstraints);
        generalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(closeBtn, generalConstraints);

        setAlwaysOnTop(true);

        validate();
        pack();

        Dimension parentDim = getOwner().getSize();
        Point parentPos = getOwner().getLocation();

        setLocation(((parentDim.width - getSize().width) / 2) + parentPos.x,
                    ((parentDim.height - getSize().height) / 2) + parentPos.y);
        setResizable(false);
    }
}

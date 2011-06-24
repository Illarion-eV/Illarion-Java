/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import illarion.easynpc.Lang;

/**
 * This is the advanced search dialog that can be used to search and replace
 * contents of the script that is currently load.
 * 
 * @author Martin Karing
 * @since 1.01
 */
final class SearchDialog extends JDialog {
    /**
     * The serialization UID of this dialog.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The constructor of the dialog that prepares the dialog for proper
     * display.
     */
    @SuppressWarnings("nls")
    public SearchDialog() {
        super(MainFrame.getInstance(), Lang
            .getMsg(SearchDialog.class, "title"), false);

        final SearchDialog dialog = this;

        final JPanel mainPanel = new JPanel(new GridBagLayout());
        getContentPane().add(mainPanel);

        final GridBagConstraints generalConstraints = new GridBagConstraints();
        generalConstraints.gridheight = 1;
        generalConstraints.gridwidth = 1;
        generalConstraints.insets.bottom = 5;
        generalConstraints.insets.right = 5;
        generalConstraints.insets.left = 5;
        generalConstraints.insets.top = 5;
        generalConstraints.anchor = GridBagConstraints.WEST;

        final JLabel searchLabel =
            new JLabel(Lang.getMsg(SearchDialog.class, "searchForLabel"));
        final JLabel replaceLabel =
            new JLabel(Lang.getMsg(SearchDialog.class, "replaceWithLabel"));

        final JTextField searchText = new JTextField();
        final JTextField replaceField = new JTextField();
        searchText.setPreferredSize(new Dimension(300, searchText
            .getPreferredSize().height));
        replaceField.setPreferredSize(new Dimension(300, replaceField
            .getPreferredSize().height));

        final JCheckBox caseSensetiveCheck =
            new JCheckBox(Lang.getMsg(SearchDialog.class, "caseCheck"));
        final JCheckBox regExpCheck =
            new JCheckBox(Lang.getMsg(SearchDialog.class, "regExpCheck"));

        final JButton findNextBtn =
            new JButton(Lang.getMsg(SearchDialog.class, "findNextButton"));
        final JButton replaceOneBtn =
            new JButton(Lang.getMsg(SearchDialog.class, "replaceButton"));
        final JButton replaceAllBtn =
            new JButton(Lang.getMsg(SearchDialog.class, "replaceAllButton"));
        final JButton closeBtn =
            new JButton(Lang.getMsg(SearchDialog.class, "closeButton"));
        final Dimension buttonDim = findNextBtn.getPreferredSize();
        buttonDim.width =
            Math.max(buttonDim.width, replaceOneBtn.getPreferredSize().width);
        buttonDim.width =
            Math.max(buttonDim.width, replaceAllBtn.getPreferredSize().width);
        buttonDim.width =
            Math.max(buttonDim.width, closeBtn.getPreferredSize().width);
        buttonDim.width += 10;
        findNextBtn.setPreferredSize(buttonDim);
        replaceOneBtn.setPreferredSize(buttonDim);
        replaceAllBtn.setPreferredSize(buttonDim);
        closeBtn.setPreferredSize(buttonDim);

        findNextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String searchString = searchText.getText();
                if (searchString.length() == 0) {
                    return;
                }
                final Editor scriptEditor =
                    MainFrame.getInstance().getCurrentScriptEditor();
                final JEditorPane editor = scriptEditor.getEditor();

                String editorText = editor.getText();
                final int startPos = editor.getCaretPosition();

                if (regExpCheck.isSelected()) {
                    try {
                        Pattern searchPattern;
                        if (caseSensetiveCheck.isSelected()) {
                            searchPattern =
                                Pattern.compile(searchString,
                                    Pattern.CASE_INSENSITIVE
                                        | Pattern.MULTILINE);
                        } else {
                            searchPattern =
                                Pattern.compile(searchString,
                                    Pattern.MULTILINE);
                        }
                        final Matcher match =
                            searchPattern.matcher(editorText);
                        if (match.find(startPos)) {
                            editor.setCaretPosition(match.end());
                            editor.setSelectionStart(match.start());
                            editor.setSelectionEnd(match.end());
                            editor.getCaret().setSelectionVisible(true);
                        }
                    } catch (final Exception ex) {
                        return;
                    }
                } else {
                    if (caseSensetiveCheck.isSelected()) {
                        searchString = searchString.toLowerCase();
                        editorText = editorText.toLowerCase();
                    }
                    final int foundIndex =
                        editorText.indexOf(searchString, startPos);
                    if (foundIndex < 0) {
                        return;
                    }

                    editor.setCaretPosition(foundIndex + searchString.length());
                    editor.setSelectionStart(foundIndex);
                    editor.setSelectionEnd(foundIndex + searchString.length());
                    editor.getCaret().setSelectionVisible(true);
                }
            }
        });

        replaceOneBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String searchString = searchText.getText();
                if (searchString.length() == 0) {
                    return;
                }
                final String replaceString = replaceField.getText();
                final Editor scriptEditor =
                    MainFrame.getInstance().getCurrentScriptEditor();
                final JEditorPane editor = scriptEditor.getEditor();

                String editorText = editor.getText();
                final int startPos = editor.getCaretPosition();

                if (regExpCheck.isSelected()) {
                    try {
                        Pattern searchPattern;
                        if (caseSensetiveCheck.isSelected()) {
                            searchPattern =
                                Pattern.compile(searchString,
                                    Pattern.CASE_INSENSITIVE
                                        | Pattern.MULTILINE);
                        } else {
                            searchPattern =
                                Pattern.compile(searchString,
                                    Pattern.MULTILINE);
                        }
                        final Matcher match =
                            searchPattern.matcher(editorText);
                        final StringBuffer editorBuffer = new StringBuffer();
                        int foundStartPos = -1;
                        if (match.find(startPos)) {
                            match.appendReplacement(editorBuffer,
                                replaceString);
                            foundStartPos = match.start();
                        }
                        match.appendTail(editorBuffer);

                        if (foundStartPos >= 0) {
                            scriptEditor.setScriptText(editorBuffer.toString());
                            editor.setCaretPosition(foundStartPos
                                + replaceString.length());
                            editor.setSelectionStart(foundStartPos);
                            editor.setSelectionEnd(editor.getCaretPosition());
                            editor.getCaret().setSelectionVisible(true);
                        }
                    } catch (final Exception ex) {
                        return;
                    }
                } else {
                    if (caseSensetiveCheck.isSelected()) {
                        searchString = searchString.toLowerCase();
                        editorText = editorText.toLowerCase();
                    }
                    final int foundIndex =
                        editorText.indexOf(searchString, startPos);
                    if (foundIndex < 0) {
                        return;
                    }

                    final StringBuffer editorBuffer =
                        new StringBuffer(editor.getText());
                    editorBuffer.delete(foundIndex,
                        foundIndex + searchString.length());
                    editorBuffer.insert(foundIndex, replaceString);
                    scriptEditor.setScriptText(editorBuffer.toString());

                    editor.setCaretPosition(foundIndex
                        + replaceString.length());
                    editor.setSelectionStart(foundIndex);
                    editor.setSelectionEnd(foundIndex + replaceString.length());
                    editor.getCaret().setSelectionVisible(true);
                }
            }
        });

        replaceAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String searchString = searchText.getText();
                if (searchString.length() == 0) {
                    return;
                }
                final String replaceString = replaceField.getText();
                final Editor scriptEditor =
                    MainFrame.getInstance().getCurrentScriptEditor();
                final JEditorPane editor = scriptEditor.getEditor();

                String editorText = editor.getText();
                final int startPos = 0;

                if (regExpCheck.isSelected()) {
                    try {
                        Pattern searchPattern;
                        if (caseSensetiveCheck.isSelected()) {
                            searchPattern =
                                Pattern.compile(searchString,
                                    Pattern.CASE_INSENSITIVE
                                        | Pattern.MULTILINE);
                        } else {
                            searchPattern =
                                Pattern.compile(searchString,
                                    Pattern.MULTILINE);
                        }
                        final Matcher match =
                            searchPattern.matcher(editorText);
                        scriptEditor.setScriptText(match
                            .replaceAll(replaceString));
                    } catch (final Exception ex) {
                        return;
                    }
                } else {
                    String workingString;
                    if (caseSensetiveCheck.isSelected()) {
                        searchString = searchString.toLowerCase();
                    }
                    while (true) {
                        if (caseSensetiveCheck.isSelected()) {
                            workingString = editorText.toLowerCase();
                        } else {
                            workingString = editorText;
                        }
                        final int foundIndex =
                            editorText.indexOf(workingString, startPos);
                        if (foundIndex < 0) {
                            break;
                        }

                        final StringBuffer editorBuffer =
                            new StringBuffer(editorText);
                        editorBuffer.delete(foundIndex, foundIndex
                            + searchString.length());
                        editorBuffer.insert(foundIndex, replaceString);
                        editorText = editorBuffer.toString();
                    }
                    scriptEditor.setScriptText(editorText);
                }
            }
        });

        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
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
        mainPanel.add(caseSensetiveCheck, generalConstraints);
        generalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(new JLabel(), generalConstraints);

        generalConstraints.gridwidth = 1;
        mainPanel.add(regExpCheck, generalConstraints);
        generalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(closeBtn, generalConstraints);

        setAlwaysOnTop(true);

        validate();
        pack();

        final Dimension parentDim = getOwner().getSize();
        final Point parentPos = getOwner().getLocation();

        setLocation(((parentDim.width - getSize().width) / 2) + parentPos.x,
            ((parentDim.height - getSize().height) / 2) + parentPos.y);
        setResizable(false);
    }
}

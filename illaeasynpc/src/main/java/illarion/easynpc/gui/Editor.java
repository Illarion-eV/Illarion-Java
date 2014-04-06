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

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.Parser;
import illarion.easynpc.gui.syntax.EasyNpcTokenMakerFactory;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The editor is the area that displays the text of the script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Editor extends RTextScrollPane {
    private static final Pattern fullLinePattern = Pattern.compile("^", Pattern.MULTILINE);

    /**
     * The new line separator used.
     */
    private static final String NL = "\n";

    /**
     * The serialization UID of this editor.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The editor pane used to display the current script.
     */
    @Nonnull
    private final RSyntaxTextArea editor;

    /**
     * The instance of a parsed NPC that failed to render properly last time.
     */
    @Nullable
    private ParsedNpc errorNpc;

    @Nullable
    private Path loadScriptFile;

    /**
     * The parsed version of this script.
     */
    @Nullable
    private ParsedNpc parsedVersion;

    private boolean savedSinceLastChange;

    @Nonnull
    private final Timer timer;

    /**
     * The default constructor that prepares the editor for the display.
     */
    Editor() {
        super(new RSyntaxTextArea(), true);

        editor = getEditor();
        editor.setEditable(true);
        editor.setEnabled(true);
        editor.setSyntaxEditingStyle(EasyNpcTokenMakerFactory.SYNTAX_STYLE_EASY_NPC);
        editor.getSyntaxScheme().restoreDefaults(null);

        MenuElement[] elements = editor.getPopupMenu().getSubElements();
        for (MenuElement element : elements) {
            AbstractButton button = (AbstractButton) element;
            if ("Undo".equals(button.getActionCommand()) || "Redo".equals(button.getActionCommand())) {
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        UndoMonitor.getInstance().updateUndoRedoLater(Editor.this);
                    }
                });
            }
        }

        setViewportView(editor);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Config.getInstance().getAutoBuild()) {
                    onCheckScript("checkScript", e);
                }
                timer.stop();
            }
        });
        timer.setInitialDelay(1000);

        editor.getDocument().addUndoableEditListener(UndoMonitor.getInstance());

        editor.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                // nothing to do
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // nothing to do
            }

            @Override
            public void keyTyped(KeyEvent e) {
                clearParsedData();
                changedText();
                if (Config.getInstance().getAutoBuild()) {
                    timer.restart();
                } else if (timer.isRunning()) {
                    timer.stop();
                }
            }
        });

        setMinimumSize(new Dimension(100, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setPreferredSize(new Dimension(800, 600));

        AnnotationProcessor.process(this);
    }

    public boolean changedSinceSave() {
        return savedSinceLastChange;
    }

    /**
     * Clean this editor and remove all references to it.
     */
    public void cleanup() {
        MainFrame.getInstance().getErrorArea().removeErrorEditor(this);
        parsedVersion = null;
        errorNpc = null;
        editor.discardAllEdits();
    }

    @Nonnull
    public RSyntaxTextArea getEditor() {
        return (RSyntaxTextArea) getTextArea();
    }

    /**
     * The NPC that failed to render properly last time.
     *
     * @return the last time invalid NPC.
     */
    @Nullable
    public ParsedNpc getErrorNpc() {
        return errorNpc;
    }

    @Nonnull
    public String getFileName() {
        if (loadScriptFile == null) {
            return "New Script";
        }
        return loadScriptFile.getFileName().toString();
    }

    /**
     * Get the focus of the editor to a specified location in the text.
     *
     * @param pos the position in the script text that should be focused
     */
    void getFocusToPosition(int pos) {
        editor.setCaretPosition(pos);
        editor.requestFocusInWindow();
    }

    public void getLineToFocus(int line) {
        JTabbedPane parentPane = getParent();
        parentPane.setSelectedComponent(this);

        Matcher fullLineMatch = fullLinePattern.matcher(editor.getText());
        boolean lastFind = true;
        for (int i = 0; i < line; i++) {
            lastFind = fullLineMatch.find();
            if (!lastFind) {
                break;
            }
        }

        if (lastFind) {
            editor.setCaretPosition(fullLineMatch.start());
            editor.requestFocusInWindow();
        }
    }

    @Nonnull
    @Override
    public JTabbedPane getParent() {
        return (JTabbedPane) super.getParent();
    }

    /**
     * Get the parsed version of the NPC written in this editor. In case its
     * needed the script is parsed to get the data.
     *
     * @return the parsed NPC data
     */
    @Nullable
    public ParsedNpc getParsedData() {
        ParsedNpc currentData = parsedVersion;
        if (currentData != null) {
            return currentData;
        }

        ParsedNpc newData = Parser.parse(getScriptText());

        if (newData.hasErrors()) {
            errorNpc = newData;
            parsedVersion = null;
            MainFrame.getInstance().getErrorArea().addErrorEditor(this);
            return null;
        }

        parsedVersion = newData;
        errorNpc = null;
        MainFrame.getInstance().getErrorArea().removeErrorEditor(this);
        return newData;
    }

    /**
     * Get the file name of the script that is load in this editor. Its possible
     * that this value returns {@code null} in case the editor is not
     * assigned to a load script.
     *
     * @return the script file that is load in this editor
     */
    @Nullable
    public Path getScriptFile() {
        return loadScriptFile;
    }

    /**
     * Get the text shown in this editor.
     *
     * @return the text shown in this editor
     */
    public String getScriptText() {
        String retText = editor.getText();
        if (retText == null) {
            return "";
        }
        return retText;
    }

    public void setTemplateText(@Nonnull String string) {
        editor.setText(string);
        editor.setCaretPosition(0);
        editor.discardAllEdits();
        saved();
    }

    /**
     * Display a easy NPC script in this editor.
     *
     * @param script the script to display in this editor
     */
    public void loadScript(@Nonnull EasyNpcScript script) {
        setScriptText(script);
        setLoadScriptFile(script.getSourceScriptFile());
        editor.discardAllEdits();
        saved();
    }

    public void resetEditorKit() {
        String text = editor.getText();
        //editor.setEditorKit(new EasyNpcSyntaxKit());
        editor.setText(text);
    }

    public void saved() {
        savedSinceLastChange = false;
    }

    public void setLoadScriptFile(@Nullable Path file) {
        if (file != null) {
            loadScriptFile = file;
        }
    }

    /**
     * Set the text shown in this editor.
     *
     * @param text the text that shall be shown in the editor now
     */
    void setScriptText(@Nonnull String text) {
        int oldCaret = editor.getCaretPosition();
        editor.setText(text);
        if (oldCaret > -1) {
            editor.setCaretPosition(Math.min(oldCaret, text.length() - 1));
        }
        clearParsedData();
        changedText();
    }

    /**
     * Set the text shown in this editor.
     *
     * @param script the script supplying the text
     */
    void setScriptText(@Nonnull EasyNpcScript script) {
        StringBuilder buffer = new StringBuilder();

        int count = script.getEntryCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                buffer.append(script.getEntry(i).getLine());
                buffer.append(NL);
            }
            buffer.setLength(buffer.length() - 1);
        }
        setScriptText(buffer.toString());
    }

    void changedText() {
        savedSinceLastChange = true;
    }

    /**
     * Clear the data that was parsed.
     */
    void clearParsedData() {
        parsedVersion = null;
    }

    /**
     * Check if this editor is the current active script editor.
     *
     * @return {@code true} in case this editor is currently active
     */
    boolean isActiveEditor() {
        return MainFrame.getInstance().getCurrentScriptEditor().equals(this);
    }

    @EventTopicSubscriber(topic = "paste")
    public void onPasteEvent(String topic, ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transfer = sysClip.getContents(null);
        String data;
        try {
            data = (String) transfer.getTransferData(DataFlavor.stringFlavor);
        } catch (@Nonnull Exception e1) {
            return;
        }
        StringBuilder buffer = new StringBuilder(editor.getText());

        int pos = editor.getCaretPosition();
        buffer.insert(pos, data);
        setScriptText(buffer.toString());
        getFocusToPosition(pos + data.length());
    }

    @EventTopicSubscriber(topic = "copy")
    public void onCopyEvent(String topic, ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

        String selText = editor.getSelectedText();
        if ((selText == null) || (selText.length() <= 0)) {
            return;
        }
        sysClip.setContents(new StringSelection(selText), null);
    }

    @EventTopicSubscriber(topic = "cut")
    public void onCutEvent(String topic, ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        int selStart = editor.getSelectionStart();
        int selEnd = editor.getSelectionEnd();

        StringBuilder buffer = new StringBuilder(editor.getText());
        buffer.delete(selStart, selEnd);
        setScriptText(buffer.toString());
        getFocusToPosition(selStart);
    }

    @EventTopicSubscriber(topic = "undoLastAction")
    public void onUndoEvent(String topic, ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        if (editor.canUndo()) {
            editor.undoLastAction();
        }
        UndoMonitor.getInstance().updateUndoRedo(this);
    }

    @EventTopicSubscriber(topic = "redoLastAction")
    public void onRedoEvent(String topic, ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        if (editor.canRedo()) {
            editor.redoLastAction();
        }
        UndoMonitor.getInstance().updateUndoRedo(this);
    }

    @EventTopicSubscriber(topic = "checkScript")
    public void onCheckScript(String topic, ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        if (parsedVersion == null) {
            final String scriptText = getScriptText();
            Runnable worker = new SwingWorker<ParsedNpc, Void>() {
                @Override
                protected ParsedNpc doInBackground() throws Exception {
                    return Parser.parse(scriptText);
                }

                @Override
                protected void done() {
                    try {
                        ParsedNpc currentData = get();
                        if (currentData.hasErrors()) {
                            errorNpc = currentData;
                            parsedVersion = null;
                            MainFrame.getInstance().getErrorArea().addErrorEditor(Editor.this);
                        } else {
                            parsedVersion = currentData;
                            errorNpc = null;
                            MainFrame.getInstance().getErrorArea().removeErrorEditor(Editor.this);
                        }
                    } catch (InterruptedException | ExecutionException ignored) {
                    }
                }
            };
            worker.run();
        }
    }
}

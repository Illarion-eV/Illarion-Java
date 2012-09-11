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

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.Parser;
import illarion.easynpc.gui.syntax.EasyNpcTokenMaker;
import illarion.easynpc.parser.events.ParserFinishedEvent;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

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
import java.io.File;
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
    @SuppressWarnings("nls")
    private static final String NL = "\n";

    /**
     * The serialization UID of this editor.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The editor pane used to display the current script.
     */
    private final RSyntaxTextArea editor;

    /**
     * The instance of a parsed NPC that failed to render properly last time.
     */
    private ParsedNpc errorNpc;

    private File loadScriptFile;

    /**
     * The parsed version of this script.
     */
    private ParsedNpc parsedVersion;

    private boolean savedSinceLastChange = false;

    private final Timer timer;

    /**
     * The default constructor that prepares the editor for the display.
     */
    Editor() {
        super(new RSyntaxTextArea(), true);

        editor = getEditor();
        editor.setEditable(true);
        editor.setEnabled(true);
        ((RSyntaxDocument) editor.getDocument()).setSyntaxStyle(new EasyNpcTokenMaker());
        editor.getSyntaxScheme().restoreDefaults(null);

        final MenuElement[] elements = editor.getPopupMenu().getSubElements();
        for (final MenuElement element : elements) {
            final AbstractButton button = (AbstractButton) element;
            if ("Undo".equals(button.getActionCommand()) || "Redo".equals(button.getActionCommand())) {
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        UndoMonitor.getInstance().updateUndoRedoLater(Editor.this);
                    }
                });
            }
        }

        setViewportView(editor);

        final Editor parentEditor = this;

        timer = new Timer(1000, new ActionListener() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void actionPerformed(final ActionEvent e) {
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
            public void keyPressed(final KeyEvent e) {
                // nothing to do
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                // nothing to do
            }

            @SuppressWarnings("synthetic-access")
            @Override
            public void keyTyped(final KeyEvent e) {
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

    public RSyntaxTextArea getEditor() {
        return (RSyntaxTextArea) getTextArea();
    }

    /**
     * The NPC that failed to render properly last time.
     *
     * @return the last time invalid NPC.
     */
    public ParsedNpc getErrorNpc() {
        return errorNpc;
    }

    public String getFileName() {
        if (loadScriptFile == null) {
            return "New Script";
        }
        return loadScriptFile.getName();
    }

    /**
     * Get the focus of the editor to a specified location in the text.
     *
     * @param pos the position in the script text that should be focused
     */
    public void getFocusToPosition(final int pos) {
        editor.setCaretPosition(pos);
        editor.requestFocusInWindow();
    }

    public void getLineToFocus(final int line) {
        final JTabbedPane parentPane = getParent();
        parentPane.setSelectedComponent(this);

        final Matcher fullLineMatch =
                fullLinePattern.matcher(editor.getText());
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
    public ParsedNpc getParsedData() {
        ParsedNpc currentData = parsedVersion;
        if (currentData != null) {
            return currentData;
        }

        final EasyNpcScript script = new EasyNpcScript();
        script.readFromEditor(this);
        currentData = Parser.getInstance().parse(script);

        if (currentData.hasErrors()) {
            errorNpc = currentData;
            parsedVersion = null;
            MainFrame.getInstance().getErrorArea().addErrorEditor(this);
            return null;
        }

        parsedVersion = currentData;
        errorNpc = null;
        MainFrame.getInstance().getErrorArea().removeErrorEditor(this);
        return currentData;
    }

    /**
     * Get the file name of the script that is load in this editor. Its possible
     * that this value returns <code>null</code> in case the editor is not
     * assigned to a load script.
     *
     * @return the script file that is load in this editor
     */
    public File getScriptFile() {
        return loadScriptFile;
    }

    /**
     * Get the text shown in this editor.
     *
     * @return the text shown in this editor
     */
    public String getScriptText() {
        final String retText = editor.getText();
        if (retText == null) {
            return "";
        }
        return retText;
    }

    /**
     * Display a easy NPC script in this editor.
     *
     * @param script the script to display in this editor
     */
    public void loadScript(final EasyNpcScript script) {
        final StringBuffer buffer = new StringBuffer();

        final int count = script.getEntryCount();
        for (int i = 0; i < count; i++) {
            buffer.append(script.getEntry(i).getLine());
            buffer.append(NL);
        }
        buffer.setLength(buffer.length() - 1);
        editor.setText(buffer.toString());
        editor.setCaretPosition(0);
        setLoadScriptFile(script.getSourceScriptFile());
        editor.discardAllEdits();
    }

    public void resetEditorKit() {
        final String text = editor.getText();
        //editor.setEditorKit(new EasyNpcSyntaxKit());
        editor.setText(text);
    }

    public void saved() {
        savedSinceLastChange = false;
    }

    public void setLoadScriptFile(final File file) {
        if (file != null) {
            loadScriptFile = file;
        }
    }

    /**
     * Set the text shown in this editor.
     *
     * @param text the text that shall be shown in the editor now
     */
    public void setScriptText(final String text) {
        editor.setText(text);
        clearParsedData();
        changedText();
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
    public boolean isActiveEditor() {
        return MainFrame.getInstance().getCurrentScriptEditor() == this;
    }

    @EventTopicSubscriber(topic = "paste")
    public void onPasteEvent(final String topic, final ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        final Transferable transfer = sysClip.getContents(null);
        final String data;
        try {
            data = (String) transfer.getTransferData(DataFlavor.stringFlavor);
        } catch (final Exception e1) {
            return;
        }
        final StringBuilder buffer = new StringBuilder(editor.getText());

        final int pos = editor.getCaretPosition();
        buffer.insert(pos, data);
        setScriptText(buffer.toString());
        getFocusToPosition(pos + data.length());
    }

    @EventTopicSubscriber(topic = "copy")
    public void onCopyEvent(final String topic, final ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

        final String selText = editor.getSelectedText();
        if ((selText == null) || (selText.length() <= 0)) {
            return;
        }
        sysClip.setContents(new StringSelection(selText), null);
    }

    @EventTopicSubscriber(topic = "cut")
    public void onCutEvent(final String topic, final ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        final int selStart = editor.getSelectionStart();
        final int selEnd = editor.getSelectionEnd();

        final StringBuilder buffer = new StringBuilder(editor.getText());
        buffer.delete(selStart, selEnd);
        setScriptText(buffer.toString());
        getFocusToPosition(selStart);
    }

    @EventTopicSubscriber(topic = "undoLastAction")
    public void onUndoEvent(final String topic, final ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        if (editor.canUndo()) {
            editor.undoLastAction();
        }
        UndoMonitor.getInstance().updateUndoRedo(this);
    }

    @EventTopicSubscriber(topic = "redoLastAction")
    public void onRedoEvent(final String topic, final ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        if (editor.canRedo()) {
            editor.redoLastAction();
        }
        UndoMonitor.getInstance().updateUndoRedo(this);
    }

    @EventTopicSubscriber(topic = "checkScript")
    public void onCheckScript(final String topic, final ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        if (parsedVersion == null) {
            final EasyNpcScript script = new EasyNpcScript();
            script.readFromEditor(this);
            Parser.getInstance().parseAsynchronously(script);
        }
    }

    @EventTopicSubscriber(topic = "parseScript")
    public void onParseScript(final String topic, final ActionEvent event) {
        if (!isActiveEditor()) {
            return;
        }

        Utils.reparseScript(this);
    }

    @EventSubscriber
    public void onParserFinished(final ParserFinishedEvent event) {
        if (event.getScript().getSourceEditor() == this) {
            final ParsedNpc currentData = event.getNpc();
            if (currentData.hasErrors()) {
                errorNpc = currentData;
                parsedVersion = null;
                MainFrame.getInstance().getErrorArea().addErrorEditor(this);
            } else {
                parsedVersion = currentData;
                errorNpc = null;
                MainFrame.getInstance().getErrorArea().removeErrorEditor(this);
            }
        }
    }
}

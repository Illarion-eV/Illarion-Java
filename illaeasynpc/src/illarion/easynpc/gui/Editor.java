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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.undo.UndoManager;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.Parser;
import illarion.easynpc.gui.syntax.EasyNpcSyntaxKit;

/**
 * The editor is the area that displays the text of the script.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class Editor extends JScrollPane {
    private static final Pattern fullLinePattern = Pattern.compile("^",
        Pattern.MULTILINE);

    /**
     * The new line separator used.
     */
    @SuppressWarnings("nls")
    private static final String NL = "\n".intern();

    /**
     * The serialization UID of this editor.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The editor pane used to display the current script.
     */
    private final JEditorPane editor;

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

    private final UndoManager undoManager;

    /**
     * The default constructor that prepares the editor for the display.
     */
    Editor() {
        super();

        editor = new JEditorPane();
        editor.setEditable(true);
        editor.setEnabled(true);
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        setViewportView(editor);

        final Editor parentEditor = this;

        timer = new Timer(1000, new ActionListener() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (Config.getInstance().getAutoBuild()) {
                    Utils.reparseSilent(parentEditor);
                }
                timer.stop();
            }
        });
        timer.setInitialDelay(1000);

        editor.setEditorKit(new EasyNpcSyntaxKit());

        undoManager = new UndoManager();
        undoManager.setLimit(Config.getInstance().getUndoCount());
        editor.getDocument()
            .addUndoableEditListener(UndoMonitor.getInstance());
        editor.getDocument().addUndoableEditListener(undoManager);

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
    }

    public boolean changedSinceSave() {
        return savedSinceLastChange;
    }

    /**
     * Clean this editor and remove all references to it.
     */
    public void cleanup() {
        MainFrame.getInstance().getErrorArea().removeErrorEditor(this);
        if (parsedVersion != null) {
            parsedVersion.recycle();
            parsedVersion = null;
        }
        errorNpc = null;
        undoManager.discardAllEdits();
    }

    public JEditorPane getEditor() {
        return editor;
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
        script.readNPCScript(getScriptText());
        currentData = Parser.getInstance().parse(script);

        if (currentData.hasErrors()) {
            errorNpc = currentData;
            if (parsedVersion != null) {
                parsedVersion.recycle();
                parsedVersion = null;
            }
            MainFrame.getInstance().getErrorArea().addErrorEditor(this);
            return null;
        }

        if (parsedVersion != null) {
            parsedVersion.recycle();
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

    public UndoManager getUndoManager() {
        return undoManager;
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
        undoManager.discardAllEdits();
    }

    public void resetEditorKit() {
        final String text = editor.getText();
        editor.setEditorKit(new EasyNpcSyntaxKit());
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
        if (parsedVersion != null) {
            parsedVersion.recycle();
            parsedVersion = null;
        }
    }
}

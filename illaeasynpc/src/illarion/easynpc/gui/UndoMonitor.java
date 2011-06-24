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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;

import illarion.easynpc.Lang;

/**
 * This class monitors the undo able actions that can be done and updates the
 * buttons displayed at the top of the editor according to this.
 * 
 * @author Martin Karing
 * @since 1.01
 */
public final class UndoMonitor implements UndoableEditListener, ChangeListener {
    /**
     * The singleton instance of this class.
     */
    private static final UndoMonitor INSTANCE = new UndoMonitor();

    /**
     * The button used for the redo operation.
     */
    private final JCommandButton redoButton;

    /**
     * The button used for the undo operation.
     */
    private final JCommandButton undoButton;

    /**
     * The private constructor that prepares all values for this monitor to work
     * properly.
     */
    @SuppressWarnings("nls")
    private UndoMonitor() {
        undoButton =
            new JCommandButton(Utils.getResizableIconFromResource("undo.png"));
        redoButton =
            new JCommandButton(Utils.getResizableIconFromResource("redo.png"));

        undoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "undoButtonTooltipTitle"), Lang.getMsg(getClass(),
            "undoButtonTooltip")));
        redoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "redoButtonTooltipTitle"), Lang.getMsg(getClass(),
            "redoButtonTooltip")));

        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final UndoManager manager =
                    MainFrame.getInstance().getCurrentScriptEditor()
                        .getUndoManager();
                if (manager.canUndo()) {
                    manager.undo();
                }

                updateUndoRedo(manager);
            }
        });
        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final UndoManager manager =
                    MainFrame.getInstance().getCurrentScriptEditor()
                        .getUndoManager();
                if (manager.canRedo()) {
                    manager.redo();
                }

                updateUndoRedo(manager);
            }
        });
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static UndoMonitor getInstance() {
        return INSTANCE;
    }

    /**
     * Get the redo button.
     * 
     * @return The redo button
     */
    public JCommandButton getRedoButton() {
        return redoButton;
    }

    /**
     * Get the undo button.
     * 
     * @return The undo button
     */
    public JCommandButton getUndoButton() {
        return undoButton;
    }

    /**
     * A state changed event indicates that the editor tab was changed. In this
     * case its needed to update the state to the new undo manager.
     */
    @Override
    public void stateChanged(final ChangeEvent e) {
        final JTabbedPane pane = (JTabbedPane) e.getSource();
        final Editor editor = (Editor) pane.getSelectedComponent();
        if (editor != null) {
            updateUndoRedo(editor.getUndoManager());
        } else {
            updateUndoRedo(null);
        }
    }

    /**
     * A undo able event has happened. This means its required to update the
     * state of the buttons.
     */
    @Override
    public void undoableEditHappened(final UndoableEditEvent e) {
        if (MainFrame.getInstance() == null) {
            return;
        }
        final Editor editor = MainFrame.getInstance().getCurrentScriptEditor();
        updateUndoRedo(editor.getUndoManager());
    }

    /**
     * Update the undo and the redo button.
     * 
     * @param manager the manger that delivers the data for the state of the
     *            undo and the redo button, in case the manager is
     *            <code>null</code> the buttons are disabled
     */
    void updateUndoRedo(final UndoManager manager) {
        if (manager == null) {
            redoButton.setEnabled(false);
            undoButton.setEnabled(false);
        } else {
            redoButton.setEnabled(manager.canRedo());
            undoButton.setEnabled(manager.canUndo());
        }
    }

}

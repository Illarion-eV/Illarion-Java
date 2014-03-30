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
import org.bushe.swing.event.EventBusAction;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

/**
 * This class monitors the undo able actions that can be done and updates the
 * buttons displayed at the top of the editor according to this.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class UndoMonitor implements UndoableEditListener, ChangeListener {
    /**
     * The singleton instance of this class.
     */
    private static final UndoMonitor INSTANCE = new UndoMonitor();

    /**
     * The button used for the redo operation.
     */
    @Nonnull
    private final JCommandButton redoButton;

    /**
     * The button used for the undo operation.
     */
    @Nonnull
    private final JCommandButton undoButton;

    /**
     * The private constructor that prepares all values for this monitor to work
     * properly.
     */
    @SuppressWarnings("nls")
    private UndoMonitor() {
        undoButton = new JCommandButton(Utils.getResizableIconFromResource("undo.png"));
        redoButton = new JCommandButton(Utils.getResizableIconFromResource("redo.png"));

        undoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "undoButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "undoButtonTooltip")));
        redoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "redoButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "redoButtonTooltip")));

        undoButton.getActionModel().setActionCommand("undoLastAction");
        redoButton.getActionModel().setActionCommand("redoLastAction");

        undoButton.addActionListener(new EventBusAction());
        redoButton.addActionListener(new EventBusAction());
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static UndoMonitor getInstance() {
        return INSTANCE;
    }

    /**
     * Get the redo button.
     *
     * @return The redo button
     */
    @Nonnull
    public JCommandButton getRedoButton() {
        return redoButton;
    }

    /**
     * Get the undo button.
     *
     * @return The undo button
     */
    @Nonnull
    public JCommandButton getUndoButton() {
        return undoButton;
    }

    /**
     * A state changed event indicates that the editor tab was changed. In this
     * case its needed to update the state to the new undo manager.
     */
    @Override
    public void stateChanged(@Nonnull final ChangeEvent e) {
        final JTabbedPane pane = (JTabbedPane) e.getSource();
        final Editor editor = (Editor) pane.getSelectedComponent();
        if (editor != null) {
            updateUndoRedo(editor);
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
        updateUndoRedo(editor);
    }

    /**
     * Update the undo and the redo button.
     *
     * @param manager the manger that delivers the data for the state of the
     * undo and the redo button, in case the manager is
     * <code>null</code> the buttons are disabled
     */
    public void updateUndoRedo(@Nullable final Editor manager) {
        if (manager == null) {
            redoButton.setEnabled(false);
            undoButton.setEnabled(false);
        } else {
            redoButton.setEnabled(manager.getEditor().canRedo());
            undoButton.setEnabled(manager.getEditor().canUndo());
        }
    }

    /**
     * Update the undo and the redo button.
     *
     * @param manager the manger that delivers the data for the state of the
     * undo and the redo button, in case the manager is
     * <code>null</code> the buttons are disabled
     */
    public void updateUndoRedoLater(final Editor manager) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateUndoRedo(manager);
            }
        });
    }
}

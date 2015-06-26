/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.easyquest.gui;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.view.mxCellState;
import illarion.easyquest.quest.Status;
import illarion.easyquest.quest.Trigger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EventObject;

/**
 * To control this editor, use mxGraph.invokesStopCellEditing, mxGraph.
 * enterStopsCellEditing and mxGraph.escapeEnabled.
 */
public class CellEditor implements mxICellEditor {
    protected mxGraphComponent graphComponent;

    @Nullable
    protected transient Object editingCell;
    @Nullable
    protected transient EventObject triggerEvent;

    protected StatusDialog nodeDialog;
    protected TriggerDialog triggerDialog;

    public CellEditor(mxGraphComponent graphComponent) {
        this.graphComponent = graphComponent;

        nodeDialog = new StatusDialog(MainFrame.getInstance());
        nodeDialog.addOkayListener(e -> stopEditing(false));
        nodeDialog.addCancelListener(e -> stopEditing(true));

        triggerDialog = new TriggerDialog(MainFrame.getInstance());
        triggerDialog.addOkayListener(e -> stopEditing(false));
        triggerDialog.addCancelListener(e -> stopEditing(true));
    }

    /*
      * (non-Javadoc)
      * @see com.mxgraph.swing.view.mxICellEditor#startEditing(java.lang.Object, java.util.EventObject)
      */
    @Override
    public void startEditing(@Nonnull Object cell, EventObject evt) {
        if (editingCell != null) {
            stopEditing(true);
        }

        mxCellState state = graphComponent.getGraph().getView().getState(cell);

        if (state != null) {
            editingCell = cell;
            triggerEvent = evt;

            if (isVertex(cell)) {
                Status value = (Status) ((mxCell) cell).getValue();
                nodeDialog.setLocationRelativeTo(MainFrame.getInstance());
                nodeDialog.setName(value.getName());
                nodeDialog.setStart(value.isStart());
                nodeDialog.setHandlers(value.getHandlers());
                nodeDialog.setVisible(true);
            } else {
                Trigger value = (Trigger) ((mxCell) cell).getValue();
                triggerDialog.setLocationRelativeTo(MainFrame.getInstance());
                triggerDialog.setName(value.getName());
                if (value.getType() != null) {
                    triggerDialog.setTriggerType(value.getType());
                }
                triggerDialog.setId(value.getObjectId());
                triggerDialog.setParameters(value.getParameters());
                triggerDialog.setConditions(value.getConditions());
                triggerDialog.setVisible(true);
            }
        }
    }

    /*
      * (non-Javadoc)
      * @see com.mxgraph.swing.view.mxICellEditor#stopEditing(boolean)
      */
    @Override
    public void stopEditing(boolean cancel) {
        if (editingCell != null) {
            Object cell = editingCell;
            editingCell = null;

            if (!cancel) {
                EventObject trig = triggerEvent;
                triggerEvent = null;

                if (isVertex(cell)) {
                    Status value = getCurrentNodeValue();
                    if (value.isStart()) {
                        ((mxCell) cell).setStyle("StartStyle");
                    } else {
                        ((mxCell) cell).setStyle("");
                    }
                    graphComponent.labelChanged(cell, value, trig);
                } else {
                    Trigger value = getCurrentEdgeValue();
                    graphComponent.labelChanged(cell, value, trig);
                }
            }

            if (isVertex(cell)) {
                nodeDialog.setVisible(false);
            } else {
                triggerDialog.setVisible(false);
            }

            graphComponent.requestFocusInWindow();
        }
    }

    @Nonnull
    public Status getCurrentNodeValue() {
        Status result = new Status();

        result.setName(nodeDialog.getName());
        result.setStart(nodeDialog.isStart());
        result.setHandlers(nodeDialog.getHandlers());

        return result;
    }

    @Nonnull
    public Trigger getCurrentEdgeValue() {
        Trigger result = new Trigger();

        result.setName(triggerDialog.getName());
        result.setObjectId(triggerDialog.getId());
        result.setType(triggerDialog.getTriggerType());
        result.setParameters(triggerDialog.getParameters());
        result.setConditions(triggerDialog.getConditions());

        return result;
    }

    /*
      * (non-Javadoc)
      * @see com.mxgraph.swing.view.mxICellEditor#getEditingCell()
      */
    @Override
    @Nullable
    public Object getEditingCell() {
        // Countering a jgraphx bug: http://forum.jgraph.com/questions/1991/how-to-prevent-custom-cell-editor-from-hiding-edited-cells-label/2016
        return null;
        // return editingCell;
    }

    private static boolean isVertex(@Nullable Object cell) {
        if (cell instanceof mxCell) {
            mxCell c = (mxCell) cell;
            return c.isVertex();
        }
        return false;
    }
}
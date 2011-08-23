/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright 2011 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyQuest Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyQuest Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.EventObject;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.view.mxCellState;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;

import illarion.easyquest.quest.Status;

/**
 * To control this editor, use mxGraph.invokesStopCellEditing, mxGraph.
 * enterStopsCellEditing and mxGraph.escapeEnabled.
 */
public class CellEditor implements mxICellEditor
{
	protected mxGraphComponent graphComponent;

	protected transient Object editingCell;
	protected transient EventObject trigger;
	
	protected StatusDialog nodeDialog;

	public CellEditor(mxGraphComponent graphComponent)
	{   
		this.graphComponent = graphComponent;

        nodeDialog = new StatusDialog(MainFrame.getInstance());
        nodeDialog.addOkayListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopEditing(false);
            }
        });
        nodeDialog.addCancelListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopEditing(true);
            }
        });
	}

	/*
	 * (non-Javadoc)
	 * @see com.mxgraph.swing.view.mxICellEditor#startEditing(java.lang.Object, java.util.EventObject)
	 */
	public void startEditing(Object cell, EventObject evt)
	{
		if (editingCell != null)
		{
			stopEditing(true);
		}

		mxCellState state = graphComponent.getGraph().getView().getState(cell);

		if (state != null)
		{
			editingCell = cell;
			trigger = evt;
			
			if (isVertex(cell))
			{
			    Status value = (Status)((mxCell)cell).getValue();
    			nodeDialog.setLocationRelativeTo(MainFrame.getInstance());
    			nodeDialog.setName(value.getName());
    			nodeDialog.setStart(value.isStart());
    			nodeDialog.setVisible(true);
    		}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.mxgraph.swing.view.mxICellEditor#stopEditing(boolean)
	 */
	public void stopEditing(boolean cancel)
	{
		if (editingCell != null)
		{
			Object cell = editingCell;
			editingCell = null;

			if (!cancel)
			{
				EventObject trig = trigger;
				trigger = null;
				Status value = getCurrentNodeValue();
				if (value.isStart())
				{
				    ((mxCell)cell).setStyle("StartStyle");
				}
				else
				{
				    ((mxCell)cell).setStyle("");
				}
				graphComponent.labelChanged(cell, value, trig);
			}
			else
			{
				mxCellState state = graphComponent.getGraph().getView()
						.getState(cell);
				graphComponent.redraw(state);
			}

            if (isVertex(cell))
            {
                nodeDialog.setVisible(false);
            }
    		graphComponent.requestFocusInWindow();
		}
	}
	
	/**
	 * Gets the initial editing value for the given cell.
	 */
	//protected Status getInitialValue(mxCellState state, EventObject trigger)
	//{
	//	return graphComponent.getEditingValue(state.getCell(), trigger);
	//}

	/**
	 * Returns the current editing value.
	 */
	public Status getCurrentNodeValue()
	{
		Status result = new Status();
	
        result.setName(nodeDialog.getName());
        result.setStart(nodeDialog.isStart());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mxgraph.swing.view.mxICellEditor#getEditingCell()
	 */
	public Object getEditingCell()
	{
		return editingCell;
	}

    private boolean isVertex(Object cell)
    {
        if (cell != null && cell instanceof mxCell)
        {
            final mxCell c = (mxCell)cell;
            return c.isVertex();
        }
        return false;
    }
}
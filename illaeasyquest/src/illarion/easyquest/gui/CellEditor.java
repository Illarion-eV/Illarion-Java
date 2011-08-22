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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Writer;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

/**
 * To control this editor, use mxGraph.invokesStopCellEditing, mxGraph.
 * enterStopsCellEditing and mxGraph.escapeEnabled.
 */
public class CellEditor implements mxICellEditor
{
	protected mxGraphComponent graphComponent;

	protected transient Object editingCell;
	protected transient EventObject trigger;
	
	protected JDialog nodeDialog;

	protected AbstractAction cancelEditingAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			stopEditing(true);
		}
	};

	protected AbstractAction textSubmitAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			stopEditing(false);
		}
	};

	/**
	 * 
	 */
	public CellEditor(mxGraphComponent graphComponent)
	{
		this.graphComponent = graphComponent;

		nodeDialog = new JDialog(MainFrame.getInstance(), "Zustand");
		nodeDialog.setSize(240,130);
		nodeDialog.setResizable(false);
		JPanel main = new JPanel();
		JPanel bottom = new JPanel();
		JLabel label = new JLabel("Name:");
		JTextField text = new JTextField(15);
		JCheckBox checkbox = new JCheckBox("Questbeginn");
		JButton okay = new JButton("OK");
		JButton cancel = new JButton("Abbrechen");
		
		main.add(label, BorderLayout.NORTH);
		main.add(text, BorderLayout.NORTH);
		main.add(checkbox, BorderLayout.CENTER);
		
		bottom.add(okay);
		bottom.add(cancel);

		nodeDialog.add(main, BorderLayout.CENTER);
		nodeDialog.add(bottom, BorderLayout.SOUTH);
	}

	public Component getEditor()
	{
		if (editingCell != null)
		{
			return dialog;
		}

		return null;
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
			dialog.setVisible(true);
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
				graphComponent.labelChanged(cell, getCurrentValue(), trig);
			}
			else
			{
				mxCellState state = graphComponent.getGraph().getView()
						.getState(cell);
				graphComponent.redraw(state);
			}

			graphComponent.requestFocusInWindow();
		}
	}

	/**
	 * Gets the initial editing value for the given cell.
	 */
	protected String getInitialValue(mxCellState state, EventObject trigger)
	{
		return graphComponent.getEditingValue(state.getCell(), trigger);
	}

	/**
	 * Returns the current editing value.
	 */
	public String getCurrentValue()
	{
		String result = "N/I";

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

}
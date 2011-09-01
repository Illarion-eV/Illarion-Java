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

import com.mxgraph.view.mxGraph;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;

import illarion.easyquest.Lang;
import illarion.easyquest.quest.Status;
import illarion.easyquest.quest.Trigger;

public class Graph extends mxGraph {
    public Graph()
    {
        super();
        setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
        setAllowLoops(true);
        
        addListener(mxEvent.ADD_CELLS, new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Object[] cells = (Object[])evt.getProperty("cells");

                for (Object cell : cells)
                {
					if (getModel().isEdge(cell))
					{
						((mxCell)cell).setValue(new Trigger());
					}
    			}
			}
		});
    }
	
	public String getToolTipForCell(Object cell)
	{
		String tip = "<html>";

		if (getModel().isEdge(cell))
		{
			tip += Lang.getMsg(getClass(), "edgeTooltip");
		}
		else
		{
			tip += Lang.getMsg(getClass(), "nodeTooltip");
		}

		tip += "</html>";

		return tip;
	}
	
	public String convertValueToString(Object cell)
	{
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Status)
			{
				Status status = (Status)value;
                return status.getName();
			}
			else if (value instanceof Trigger)
			{
			    Trigger trigger = (Trigger)value;
			    return trigger.getName();
			}
		}

		return super.convertValueToString(cell);
	}
}
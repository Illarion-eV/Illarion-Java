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
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import illarion.easyquest.Lang;
import illarion.easyquest.quest.Status;
import illarion.easyquest.quest.Trigger;

import javax.annotation.Nonnull;

public class Graph extends mxGraph {
    public Graph(@Nonnull mxIGraphModel model) {
        super(model);
        setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
        setAllowLoops(true);

        addListener(mxEvent.ADD_CELLS, (sender, evt) -> {
            Object[] cells = (Object[]) evt.getProperty("cells");

            for (Object cellObject : cells) {
                mxCell cell = (mxCell) cellObject;
                if (cell.isEdge()) {
                    cell.setValue(new Trigger());

                    if (cell.getSource() == null || cell.getTarget() == null) {
                        cell.setStyle(mxConstants.STYLE_STROKECOLOR + "=#FF0000");
                    }
                }
            }
        });

        addListener(mxEvent.CELLS_ADDED, (sender, evt) -> {
            Object[] cells = (Object[]) evt.getProperty("cells");

            for (Object cellObject : cells) {
                mxCell cell = (mxCell) cellObject;
                if (cell.isEdge()) {
                    if (cell.getSource() == null || cell.getTarget() == null) {
                        cell.setStyle(mxConstants.STYLE_STROKECOLOR + "=#FF0000");
                    }
                }
            }
        });

        Graph g = this;
        addListener(mxEvent.CELL_CONNECTED, (sender, evt) -> {
            mxCell edge = (mxCell) evt.getProperty("edge");

            mxCell source = (mxCell) edge.getSource();
            mxCell target = (mxCell) edge.getTarget();

            Object[] cells = {edge};
            if (source == null || target == null) {
                g.setCellStyle(mxConstants.STYLE_STROKECOLOR + "=#FF0000", cells);
            } else {
                g.setCellStyle("", cells);
            }
        });
    }

    @Override
    @Nonnull
    public String getToolTipForCell(Object cell) {
        String tip = "<html>";

        if (getModel().isEdge(cell)) {
            tip += Lang.getMsg(getClass(), "edgeTooltip");
        } else {
            tip += Lang.getMsg(getClass(), "nodeTooltip");
        }

        tip += "</html>";

        return tip;
    }

    @Override
    public String convertValueToString(@Nonnull Object cell) {
        if (cell instanceof mxCell) {
            Object value = ((mxCell) cell).getValue();

            if (value instanceof Status) {
                Status status = (Status) value;
                return status.getName();
            } else if (value instanceof Trigger) {
                Trigger trigger = (Trigger) value;
                return trigger.getName();
            }
        }

        return super.convertValueToString(cell);
    }
}
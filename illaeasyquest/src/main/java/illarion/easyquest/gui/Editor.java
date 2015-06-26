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

import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraphSelectionModel;
import com.mxgraph.view.mxStylesheet;
import illarion.easyquest.EditorKeyboardHandler;
import illarion.easyquest.Lang;
import illarion.easyquest.QuestIO;
import illarion.easyquest.quest.Position;
import illarion.easyquest.quest.Status;
import illarion.easyquest.quest.Trigger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public final class Editor extends mxGraphComponent {

    @Nullable
    private Path questFile;

    private boolean savedSinceLastChange;

    private int questID = 10000;

    @Nonnull
    @SuppressWarnings("unused")
    private final mxKeyboardHandler keyboardHandler;
    @Nonnull
    @SuppressWarnings("unused")
    private final mxRubberband rubberband;

    @Nonnull
    private final mxUndoManager undoManager;

    private final mxIEventListener undoHandler = new mxIEventListener() {
        @Override
        public void invoke(Object source, @Nonnull mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };

    Editor(@Nonnull Graph graph) {
        super(graph);

        mxICell root = (mxCell) graph.getModel().getRoot();
        Object value = root.getValue();
        if (value != null) {
            String txt = value.toString();
            try {
                questID = Integer.parseInt(txt);
            } catch (NumberFormatException e) {
            }
        }

        getConnectionHandler().getMarker().setHotspot(0.5f);

        setToolTips(true);
        setCellEditor(new CellEditor(this));

        keyboardHandler = new EditorKeyboardHandler(this);
        rubberband = new mxRubberband(this);
        undoManager = new mxUndoManager();

        mxCodecRegistry.register(new mxObjectCodec(new Status()));
        mxCodecRegistry.addPackage(Status.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Trigger()));
        mxCodecRegistry.addPackage(Trigger.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Position()));
        mxCodecRegistry.addPackage(Position.class.getPackage().getName());

        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);

        mxIEventListener undoHandler = (source, evt) -> {
            List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
            graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);

        Editor editor = this;

        getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(@Nonnull MouseEvent e) {
                if ((e.getClickCount() == 2) || ((e.getClickCount() == 1) &&
                        (MainFrame.getInstance().getCreateType() == MainFrame.CREATE_STATUS))) {
                    Object cell = getCellAt(e.getX(), e.getY());
                    if (cell == null) {
                        Object parent = graph.getDefaultParent();
                        graph.getModel().beginUpdate();
                        try {
                            Status status = new Status();
                            status.setName("New Quest Status");
                            status.setStart(false);
                            graph.insertVertex(parent, null, status, e.getX() - 60, e.getY() - 15, 120, 30);
                        } finally {
                            graph.getModel().endUpdate();
                        }
                        e.consume();
                    }
                } else if ((e.getClickCount() == 1) &&
                        (MainFrame.getInstance().getCreateType() == MainFrame.CREATE_TRIGGER)) {
                    Object cell = getCellAt(e.getX(), e.getY());
                    if (cell == null) {
                        Object parent = graph.getDefaultParent();
                        graph.getModel().beginUpdate();
                        try {
                            Trigger trigger = new Trigger();
                            trigger.setName("New Quest Trigger");
                            mxICell edge = (mxCell) graph.insertEdge(parent, null, trigger, null, null);
                            edge.getGeometry().setSourcePoint(new mxPoint(e.getX() - 60, e.getY() - 15));
                            edge.getGeometry().setTargetPoint(new mxPoint(e.getX() + 60, e.getY() + 15));
                            editor.labelChanged(edge, trigger, null);
                        } finally {
                            graph.getModel().endUpdate();
                        }
                        e.consume();
                    }
                }
            }
        });
    }

    private static void setup(@Nonnull Graph graph) {
        mxStylesheet stylesheet = graph.getStylesheet();
        Map<String, Object> nodeStyle = stylesheet.getDefaultVertexStyle();
        nodeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        nodeStyle.put(mxConstants.STYLE_ROUNDED, true);
        nodeStyle.put(mxConstants.STYLE_OPACITY, 50);
        nodeStyle.put(mxConstants.STYLE_FILLCOLOR, "#EFEFFF");
        nodeStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#AFAFFF");
        nodeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        stylesheet.setDefaultVertexStyle(nodeStyle);
        Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ELBOW);
        edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, 2.0);
        edgeStyle.put(mxConstants.STYLE_ROUNDED, true);
        stylesheet.setDefaultEdgeStyle(edgeStyle);
        Map<String, Object> startStyle = new HashMap<>();
        startStyle.put(mxConstants.STYLE_STROKEWIDTH, 3.0);
        startStyle.put(mxConstants.STYLE_STROKECOLOR, "#0000F0");
        stylesheet.putCellStyle("StartStyle", startStyle);
    }

    @Nonnull
    public mxUndoManager getUndoManager() {
        return undoManager;
    }

    public int getSelectedStatusNumber() throws IllegalStateException {
        Graph graph = (Graph) getGraph();
        mxGraphSelectionModel model = graph.getSelectionModel();
        Object[] nodes = model.getCells();
        int count = 0;
        mxCell status = null;

        for (Object node : nodes) {
            mxCell cell = (mxCell) node;
            if (cell.getValue() instanceof Status) {
                status = cell;
                count += 1;
            }
        }

        if (count == 1) {
            return ((Status) status.getValue()).isStart() ? 0 : Integer.parseInt(status.getId());
        } else {
            throw new IllegalStateException(String.valueOf(count));
        }
    }

    public int getQuestID() {
        return questID;
    }

    public void setQuestID(int questID) {
        if (this.questID != questID) {
            this.questID = questID;
            mxIGraphModel model = getGraph().getModel();
            model.beginUpdate();
            try {
                mxICell root = (mxCell) model.getRoot();
                root.setValue(questID);
            } finally {
                model.endUpdate();
            }
        }
    }

    @Nullable
    public Path getQuestFile() {
        return questFile;
    }

    public void setQuestFile(@Nullable Path file) {
        if (file != null) {
            questFile = file;
        }
    }

    public void saved() {
        savedSinceLastChange = false;
    }

    public boolean changedSinceSave() {
        return savedSinceLastChange;
    }

    public void changedQuest() {
        savedSinceLastChange = true;
    }

    @Nonnull
    public static Editor loadQuest(@Nullable Path quest) {
        mxIGraphModel model;
        if (quest == null) {
            model = new mxGraphModel();
        } else {
            try {
                model = QuestIO.loadGraphModel(quest);
            } catch (IOException e) {
                model = new mxGraphModel();
            }
        }
        Graph graph = new Graph(model);
        setup(graph);
        return new Editor(graph);
    }

    public boolean validQuest() {
        Graph graph = (Graph) getGraph();
        mxGraphSelectionModel model = graph.getSelectionModel();
        model.clear();
        Object parent = graph.getDefaultParent();
        Object[] edges = graph.getChildEdges(parent);
        Object[] nodes = graph.getChildVertices(parent);

        int countStart = 0;
        for (Object node : nodes) {
            mxICell cell = (mxICell) node;
            Status status = (Status) cell.getValue();
            if (status.isStart()) {
                countStart += 1;
            }
        }
        String errors = "";
        if (countStart != 1) {
            errors = errors + Lang.getMsg(Editor.class, "startNumberError") + ' ' + countStart + ".\n";
        }

        int countUnconnected = 0;
        int countNoContent = 0;
        for (Object edge : edges) {
            mxCell cell = (mxCell) edge;
            mxCell source = (mxCell) cell.getSource();
            mxCell target = (mxCell) cell.getTarget();
            if ((source == null) || (target == null)) {
                countUnconnected += 1;
            }

            Trigger trigger = (Trigger) cell.getValue();

            if ((trigger.getType() == null) || (trigger.getObjectId() == null) ||
                    ((trigger.getObjectId() instanceof Long) && ((Long) trigger.getObjectId() == 0)) ||
                    (trigger.getParameters() == null) || (trigger.getConditions() == null)) {
                model.addCell(cell);
                countNoContent += 1;
            }
        }
        if (countUnconnected > 0) {
            errors = errors + Lang.getMsg(Editor.class, "unconnectedError") + ' ' + countUnconnected + ".\n";
        }
        if (countNoContent > 0) {
            errors = errors + Lang.getMsg(Editor.class, "noContentError") + ' ' + countNoContent + ".\n";
        }

        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), errors, Lang.getMsg(Editor.class, "exportFailed"),
                                          JOptionPane.ERROR_MESSAGE);

            return false;
        }

        return true;
    }
}
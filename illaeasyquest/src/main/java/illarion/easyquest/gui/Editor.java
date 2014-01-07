/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyQuest Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyQuest Editor.  If not, see <http://www.gnu.org/licenses/>.
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

    private boolean savedSinceLastChange = false;

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
        public void invoke(final Object source, @Nonnull final mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };

    Editor(@Nonnull final Graph graph) {
        super(graph);

        final mxICell root = (mxCell) graph.getModel().getRoot();
        final Object value = root.getValue();
        if (value != null) {
            final String txt = value.toString();
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

        final Graph g = graph;

        g.getModel().addListener(mxEvent.UNDO, undoHandler);
        g.getView().addListener(mxEvent.UNDO, undoHandler);

        final mxIEventListener undoHandler = new mxIEventListener() {
            public void invoke(final Object source, @Nonnull final mxEventObject evt) {
                final List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
                g.setSelectionCells(g.getSelectionCellsForChanges(changes));
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);

        final Editor editor = this;

        getGraphControl().addMouseListener(new MouseAdapter() {
            public void mouseClicked(@Nonnull final MouseEvent e) {
                if ((e.getClickCount() == 2) || ((e.getClickCount() == 1) &&
                        (MainFrame.getInstance().getCreateType() == MainFrame.CREATE_STATUS))) {
                    final Object cell = getCellAt(e.getX(), e.getY());
                    if (cell == null) {
                        final Object parent = g.getDefaultParent();
                        g.getModel().beginUpdate();
                        try {
                            final Status status = new Status();
                            status.setName("New Quest Status");
                            status.setStart(false);
                            g.insertVertex(parent, null, status, e.getX() - 60, e.getY() - 15, 120, 30);
                        } finally {
                            g.getModel().endUpdate();
                        }
                        e.consume();
                    }
                } else if ((e.getClickCount() == 1) &&
                        (MainFrame.getInstance().getCreateType() == MainFrame.CREATE_TRIGGER)) {
                    final Object cell = getCellAt(e.getX(), e.getY());
                    if (cell == null) {
                        final Object parent = g.getDefaultParent();
                        g.getModel().beginUpdate();
                        try {
                            final Trigger trigger = new Trigger();
                            trigger.setName("New Quest Trigger");
                            final mxICell edge = (mxCell) g.insertEdge(parent, null, trigger, null, null);
                            edge.getGeometry().setSourcePoint(new mxPoint(e.getX() - 60, e.getY() - 15));
                            edge.getGeometry().setTargetPoint(new mxPoint(e.getX() + 60, e.getY() + 15));
                            editor.labelChanged(edge, trigger, null);
                        } finally {
                            g.getModel().endUpdate();
                        }
                        e.consume();
                    }
                }
            }
        });
    }

    private static void setup(@Nonnull final Graph graph) {
        final mxStylesheet stylesheet = graph.getStylesheet();
        final Map<String, Object> nodeStyle = stylesheet.getDefaultVertexStyle();
        nodeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        nodeStyle.put(mxConstants.STYLE_ROUNDED, true);
        nodeStyle.put(mxConstants.STYLE_OPACITY, 50);
        nodeStyle.put(mxConstants.STYLE_FILLCOLOR, "#EFEFFF");
        nodeStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#AFAFFF");
        nodeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        stylesheet.setDefaultVertexStyle(nodeStyle);
        final Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ELBOW);
        edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, 2.0);
        edgeStyle.put(mxConstants.STYLE_ROUNDED, true);
        stylesheet.setDefaultEdgeStyle(edgeStyle);
        final Map<String, Object> startStyle = new HashMap<>();
        startStyle.put(mxConstants.STYLE_STROKEWIDTH, 3.0);
        startStyle.put(mxConstants.STYLE_STROKECOLOR, "#0000F0");
        stylesheet.putCellStyle("StartStyle", startStyle);
    }

    @Nonnull
    public mxUndoManager getUndoManager() {
        return undoManager;
    }

    public int getSelectedStatusNumber() throws IllegalStateException {
        final Graph graph = (Graph) getGraph();
        final mxGraphSelectionModel model = graph.getSelectionModel();
        final Object[] nodes = model.getCells();
        int count = 0;
        mxCell status = null;

        for (final Object node : nodes) {
            final mxCell cell = (mxCell) node;
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

    public void setQuestID(final int questID) {
        if (this.questID != questID) {
            this.questID = questID;
            mxIGraphModel model = getGraph().getModel();
            model.beginUpdate();
            try {
                final mxICell root = (mxCell) model.getRoot();
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

    public void setQuestFile(@Nullable final Path file) {
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
    public static Editor loadQuest(@Nullable final Path quest) {
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
        final Graph graph = new Graph(model);
        setup(graph);
        return new Editor(graph);
    }

    public boolean validQuest() {
        final Graph graph = (Graph) getGraph();
        final mxGraphSelectionModel model = graph.getSelectionModel();
        model.clear();
        final Object parent = graph.getDefaultParent();
        final Object[] edges = graph.getChildEdges(parent);
        final Object[] nodes = graph.getChildVertices(parent);

        int countStart = 0;
        for (final Object node : nodes) {
            final mxICell cell = (mxICell) node;
            final Status status = (Status) cell.getValue();
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
        for (final Object edge : edges) {
            final mxCell cell = (mxCell) edge;
            final mxCell source = (mxCell) cell.getSource();
            final mxCell target = (mxCell) cell.getTarget();
            if ((source == null) || (target == null)) {
                countUnconnected += 1;
            }

            final Trigger trigger = (Trigger) cell.getValue();

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
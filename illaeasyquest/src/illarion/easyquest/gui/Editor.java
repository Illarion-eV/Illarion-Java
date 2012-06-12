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

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.model.mxCell;
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
import illarion.easyquest.quest.*;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

@SuppressWarnings("serial")
public final class Editor extends mxGraphComponent {

    private File questFile;

    private boolean savedSinceLastChange = false;

    private int questID = 10000;

    @SuppressWarnings("unused")
    private mxKeyboardHandler keyboardHandler;
    @SuppressWarnings("unused")
    private mxRubberband rubberband;

    private mxUndoManager undoManager;

    private mxIEventListener undoHandler = new mxIEventListener() {
        public void invoke(Object source, mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt
                    .getProperty("edit"));
        }
    };

    Editor(Graph graph) {
        super(graph);

        mxCell root = (mxCell) graph.getModel().getRoot();
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

        final Graph g = graph;

        g.getModel().addListener(mxEvent.UNDO, undoHandler);
        g.getView().addListener(mxEvent.UNDO, undoHandler);

        mxIEventListener undoHandler = new mxIEventListener() {
            public void invoke(Object source, mxEventObject evt) {
                List<mxUndoableChange> changes = ((mxUndoableEdit) evt
                        .getProperty("edit")).getChanges();
                g.setSelectionCells(g
                        .getSelectionCellsForChanges(changes));
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);

        final Editor editor = this;

        getGraphControl().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 ||
                        (e.getClickCount() == 1 &&
                                MainFrame.getInstance().getCreateType() == MainFrame.CREATE_STATUS)) {
                    Object cell = getCellAt(e.getX(), e.getY());
                    if (cell == null) {
                        Object parent = g.getDefaultParent();
                        g.getModel().beginUpdate();
                        try {
                            Status status = new Status();
                            status.setName("New Quest Status");
                            status.setStart(false);
                            g.insertVertex(parent, null, status, e.getX() - 60, e.getY() - 15, 120,
                                    30);
                        } finally {
                            g.getModel().endUpdate();
                        }
                        e.consume();
                    }
                } else if (e.getClickCount() == 1 &&
                        MainFrame.getInstance().getCreateType() == MainFrame.CREATE_TRIGGER) {
                    Object cell = getCellAt(e.getX(), e.getY());
                    if (cell == null) {
                        Object parent = g.getDefaultParent();
                        g.getModel().beginUpdate();
                        try {
                            Trigger trigger = new Trigger();
                            trigger.setName("New Quest Trigger");
                            mxCell edge = (mxCell) g.insertEdge(parent, null, trigger, null, null);
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

    private static void setup(Graph graph) {
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
        HashMap<String, Object> startStyle = new HashMap<String, Object>();
        startStyle.put(mxConstants.STYLE_STROKEWIDTH, 3.0);
        startStyle.put(mxConstants.STYLE_STROKECOLOR, "#0000F0");
        stylesheet.putCellStyle("StartStyle", startStyle);
    }

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
                count = count + 1;
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
        this.questID = questID;
    }

    public File getQuestFile() {
        return questFile;
    }

    public void setQuestFile(final File file) {
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

    public static Editor loadQuest(String quest) {
        Graph graph = new Graph();
        setup(graph);
        if (!quest.isEmpty()) {
            Document document = mxUtils.parseXml(quest);
            mxCodec codec = new mxCodec(document);
            codec.decode(document.getDocumentElement(), graph.getModel());
        }
        return new Editor(graph);
    }

    public String getQuestXML() {
        getGraph().getModel().beginUpdate();
        try {
            mxCell root = (mxCell) getGraph().getModel().getRoot();
            root.setValue(questID);
        } finally {
            getGraph().getModel().endUpdate();
        }

        mxCodec codec = new mxCodec();
        return mxUtils.getXml(codec.encode(getGraph().getModel()));
    }

    public Map<String, String> getQuestLua(String questName) {
        String questtxt = "";
        Map<String, String> quest = new HashMap<String, String>();

        Graph g = (Graph) getGraph();
        Object[] edges = g.getChildEdges(g.getDefaultParent());

        int i = 1;
        for (Object obj : edges) {
            mxCell edge = (mxCell) obj;
            Trigger trigger = (Trigger) edge.getValue();
            TriggerTemplate template =
                    TriggerTemplates.getInstance().getTemplate(trigger.getType());

            String scriptName = "trigger" + i;
            mxCell source = (mxCell) edge.getSource();
            mxCell target = (mxCell) edge.getTarget();
            Status sourceState = (Status) source.getValue();
            Status targetState = (Status) target.getValue();
            String sourceId = sourceState.isStart() ? "0" : source.getId();
            String targetId = targetState.isStart() ? "0" : target.getId();
            Object[] parameters = trigger.getParameters();
            Handler[] handlers = targetState.getHandlers();
            Set<String> handlerTypes = new HashSet<String>();
            Condition[] conditions = trigger.getConditions();

            String handlerCode = "";
            for (Handler handler : handlers) {
                String type = handler.getType();
                Object[] handlerParameters = handler.getParameters();
                HandlerTemplate handlerTemplate =
                        HandlerTemplates.getInstance().getTemplate(type);
                int playerIndex = handlerTemplate.getPlayerIndex();

                handlerTypes.add(type);

                handlerCode = handlerCode + "    handler." + type.toLowerCase() + "." + type + "(";
                if (handlerParameters.length > 0) {
                    if (playerIndex == 0) {
                        handlerCode = handlerCode + "PLAYER, ";
                    }
                    handlerCode = handlerCode + exportParameter(handlerParameters[0],
                            handlerTemplate.getParameter(0).getType());

                    for (int j = 1; j < handlerParameters.length; ++j) {
                        if (playerIndex == j) {
                            handlerCode = handlerCode + ", PLAYER";
                        }
                        handlerCode = handlerCode + ", "
                                + exportParameter(handlerParameters[j],
                                handlerTemplate.getParameter(j).getType());
                    }
                }
                handlerCode = handlerCode + "):execute()\n";
            }

            String conditionCode = "";
            for (Condition condition : conditions) {
                String type = condition.getType();
                Object[] conditionParameters = condition.getParameters();
                ConditionTemplate conditionTemplate =
                        ConditionTemplates.getInstance().getTemplate(type);
                String conditionString = conditionTemplate.getCondition();
                for (int j = 0; j < conditionParameters.length; ++j) {
                    Object param = conditionParameters[j];
                    String paramName = conditionTemplate.getParameter(j).getName();
                    String paramType = conditionTemplate.getParameter(j).getType();
                    String operator = null;
                    String value = null;
                    if (paramType.equals("INTEGERRELATION")) {
                        IntegerRelation ir = (IntegerRelation) param;
                        value = String.valueOf(ir.getInteger());
                        operator = ir.getRelation().toLua();
                    }
                    conditionString = conditionString
                            .replaceAll("OPERATOR_" + j, operator)
                            .replaceAll(paramName, value);
                }
                if (!conditionCode.isEmpty()) {
                    conditionCode = conditionCode + "   and ";
                }
                conditionCode = conditionCode + conditionString + "\n";
            }
            if (conditionCode.isEmpty()) {
                conditionCode = "true\n";
            }

            String t = "";
            for (String type : handlerTypes) {
                t = t + "require(\"handler." + type.toLowerCase() + "\")\n";
            }
            t = t + template.getHeader()
                    + "module(\"questsystem." + questName + "." + scriptName + "\", package.seeall)" + "\n"
                    + "\n"
                    + "local QUEST_NUMBER = " + questID + "\n"
                    + "local PRECONDITION_QUESTSTATE = " + sourceId + "\n"
                    + "local POSTCONDITION_QUESTSTATE = " + targetId + "\n"
                    + "\n";
            for (int j = 0; j < template.size(); ++j) {
                t = t + "local "
                        + template.getParameter(j).getName()
                        + " = "
                        + exportParameter(parameters[j], template.getParameter(j).getType())
                        + "\n";
            }
            t = t + "\n";
            t = t + template.getBody() + "\n\n";

            t = t + "function HANDLER(PLAYER)\n" + handlerCode + "end\n\n";
            t = t + "function ADDITIONALCONDITIONS(PLAYER)\nreturn " + conditionCode + "end";


            quest.put(scriptName + ".lua", t);

            questtxt = questtxt + template.getCategory() + ","
                    + exportId(trigger.getObjectId(), template.getId().getType()) + ","
                    + template.getEntryPoint() + ","
                    + scriptName + "\n";

            i = i + 1;
        }

        quest.put("quest.txt", questtxt);

        return quest;
    }

    private String exportId(Object parameter, String type) {
        if (type.equals("POSITION")) {
            Position p = (Position) parameter;
            return p.getX() + "," + p.getY() + "," + p.getZ();
        } else if (type.equals("INTEGER")) {
            if (parameter instanceof Long) {
                Long n = (Long) parameter;
                return n.toString();
            } else {
                String s = (String) parameter;
                return s;
            }
        } else {
            return "TYPE NOT SUPPORTED";
        }
    }

    private String exportParameter(Object parameter, String type) {
        if (type.equals("TEXT")) {
            String s = (String) parameter;
            return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        } else if (type.equals("POSITION")) {
            Position p = (Position) parameter;
            return "position(" + p.getX() + ", " + p.getY() + ", " + p.getZ() + ")";
        } else if (type.equals("INTEGER")) {
            if (parameter instanceof Long) {
                Long n = (Long) parameter;
                return n.toString();
            } else {
                String s = (String) parameter;
                return s;
            }
        } else {
            return "TYPE NOT SUPPORTED";
        }
    }

    public boolean validQuest() {
        String errors = "";
        Graph graph = (Graph) getGraph();
        mxGraphSelectionModel model = graph.getSelectionModel();
        model.clear();
        Object parent = graph.getDefaultParent();
        Object[] edges = graph.getChildEdges(parent);
        Object[] nodes = graph.getChildVertices(parent);

        int countStart = 0;
        for (Object node : nodes) {
            mxCell cell = (mxCell) node;
            Status status = (Status) cell.getValue();
            if (status.isStart()) {
                countStart = countStart + 1;
            }
        }
        if (countStart != 1) {
            errors = errors + Lang.getMsg(Editor.class, "startNumberError") + " " + countStart + ".\n";
        }

        int countUnconnected = 0;
        int countNoContent = 0;
        for (Object edge : edges) {
            mxCell cell = (mxCell) edge;
            mxCell source = (mxCell) cell.getSource();
            mxCell target = (mxCell) cell.getTarget();
            if (source == null || target == null) {
                countUnconnected = countUnconnected + 1;
            }

            Trigger trigger = (Trigger) cell.getValue();

            if (trigger.getType() == null || trigger.getObjectId() == null ||
                    (trigger.getObjectId() instanceof Long && (Long) (trigger.getObjectId()) == 0) ||
                    trigger.getParameters() == null || trigger.getConditions() == null) {
                model.addCell(cell);
                countNoContent = countNoContent + 1;
            }
        }
        if (countUnconnected > 0) {
            errors = errors + Lang.getMsg(Editor.class, "unconnectedError") + " " + countUnconnected + ".\n";
        }
        if (countNoContent > 0) {
            errors = errors + Lang.getMsg(Editor.class, "noContentError") + " " + countNoContent + ".\n";
        }

        if (errors.length() != 0) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    errors,
                    Lang.getMsg(Editor.class, "exportFailed"),
                    JOptionPane.ERROR_MESSAGE);

            return false;
        }

        return true;
    }
}
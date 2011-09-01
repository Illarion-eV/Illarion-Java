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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;

import com.mxgraph.model.mxCell;

import illarion.easyquest.quest.Status;
import illarion.easyquest.quest.Handler;
import illarion.easyquest.quest.Trigger;
import illarion.easyquest.quest.Position;
import illarion.easyquest.quest.TriggerTemplate;
import illarion.easyquest.quest.TriggerTemplates;
import illarion.easyquest.quest.HandlerTemplate;
import illarion.easyquest.quest.HandlerTemplates;
import illarion.easyquest.EditorKeyboardHandler;

import illarion.easyquest.Lang;

public final class Editor extends mxGraphComponent {

    private File questFile;
    
    private boolean savedSinceLastChange = false;
    
    private mxKeyboardHandler keyboardHandler;
    private mxRubberband rubberband;

    Editor(Graph graph) {
        super(graph);
        
        setToolTips(true);
        setCellEditor(new CellEditor(this));
        
        keyboardHandler = new EditorKeyboardHandler(this);
        rubberband = new mxRubberband(this);
        
        mxCodecRegistry.register(new mxObjectCodec(new Status()));
        mxCodecRegistry.addPackage(Status.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Trigger()));
        mxCodecRegistry.addPackage(Trigger.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Position()));
        mxCodecRegistry.addPackage(Position.class.getPackage().getName());
        
        final Graph g = graph;
        getGraphControl().addMouseListener(new MouseAdapter()
		{
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) {
		            Object cell = getCellAt(e.getX(), e.getY());
			        if (cell == null) {
			            Object parent = g.getDefaultParent();
			            g.getModel().beginUpdate();
                        try {
                            Status status = new Status();
                            status.setName("New Quest Status");
                            status.setStart(false);
                            g.insertVertex(parent, null, status, e.getX()-60, e.getY()-15, 120,
                            30);
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
        if (quest != "")
        {
            Document document = mxUtils.parseXml(quest);
        	mxCodec codec = new mxCodec(document);
    		codec.decode(document.getDocumentElement(), graph.getModel());
		}
		return new Editor(graph);
    }

    public String getQuestXML() {
        mxCodec codec = new mxCodec();
        return mxUtils.getXml(codec.encode(getGraph().getModel()));
    }
    
    public Map<String, String> getQuestLua(String questName)
    {
        String questtxt = "";
        Map<String, String> quest = new HashMap<String, String>();
        
        Graph g = (Graph)getGraph();
        Object[] edges = g.getChildEdges(g.getDefaultParent());
        
        int i = 1;
        for (Object obj : edges)
        {
            mxCell edge = (mxCell)obj;
            Trigger trigger = (Trigger)edge.getValue();
            TriggerTemplate template =
                TriggerTemplates.getInstance().getTemplate(trigger.getType());
            
            String scriptName = "trigger" + i;
            mxCell source = (mxCell)edge.getSource();
            mxCell target = (mxCell)edge.getTarget();
            Status sourceState = (Status)source.getValue();
            Status targetState = (Status)target.getValue();
            String sourceId = sourceState.isStart() ? "0" : source.getId();
            String targetId = targetState.isStart() ? "0" : target.getId();
            Object[] parameters = trigger.getParameters();
            Handler[] handlers = targetState.getHandlers();
            Set<String> handlerTypes = new HashSet<String>();
            String handlerCode = "";
            for (Handler handler : handlers)
            {
                String type = handler.getType();
                Object[] handlerParameters = handler.getParameters();
                HandlerTemplate handlerTemplate =
                    HandlerTemplates.getInstance().getTemplate(type);
                int playerIndex = handlerTemplate.getPlayerIndex();
                
                handlerTypes.add(type);
                
                handlerCode = handlerCode + "handler." + type.toLowerCase() + "." + type + "(";
                if (handlerParameters.length > 0)
                {
                    if (playerIndex == 0)
                    {
                        handlerCode = handlerCode + "PLAYER, ";
                    }
                    handlerCode = handlerCode + exportParameter(handlerParameters[0],
                        handlerTemplate.getParameter(0).getType());
                    
                    for (int j=1; j<handlerParameters.length; ++j)
                    {
                        if (playerIndex == j)
                        {
                            handlerCode = handlerCode + ", PLAYER";
                        }
                        handlerCode = handlerCode + ", "
                            + exportParameter(handlerParameters[j],
                            handlerTemplate.getParameter(j).getType());
                    }
                }
                handlerCode = handlerCode + "):execute()\n";
            }
            
            String t = "";
            for (String type : handlerTypes)
            {
                t = t + "require(\"handler." + type.toLowerCase() + "\")\n";
            }
            t = t + template.getHeader()
                  + "module(\"questsystem." + questName + "." + scriptName + "\", package.seeall)" + "\n"
                  + "\n"
                  + "local QUEST_NUMBER = " + "10000" + "\n" // TODO: Get quest number from somewhere
                  + "local PRECONDITION_QUESTSTATE = " + sourceId + "\n"
                  + "local POSTCONDITION_QUESTSTATE = " + targetId + "\n"
                  + "\n";
            for (int j=0; j<template.size(); ++j)
    		{
    		    t = t + "local "
    		          + template.getParameter(j).getName()
    		          + " = "
    		          + exportParameter(parameters[j], template.getParameter(j).getType())
    		          + "\n";
    		}
    		t = t + "\n";
    		t = t + template.getBodyBeforeHandler();
    		t = t + handlerCode;
    		t = t + template.getBodyAfterHandler();
    		
    		quest.put(scriptName + ".lua", t);
    		
            questtxt = questtxt + template.getCategory() + ","
                    + trigger.getObjectId() + ","
                    + template.getEntryPoint() + ","
                    + scriptName + "\n";
              
            i = i + 1;
        }
        
        quest.put("quest.txt", questtxt);
        
        return quest;
    }
    
    private String exportParameter(Object parameter, String type)
    {
        if (type.equals("TEXT"))
        {
            String s = (String)parameter;
            return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
        else if (type.equals("POSITION"))
        {
            Position p = (Position)parameter;
            return "position(" + p.getX() + ", " + p.getY() + ", " + p.getZ() + ")";
        }
        else if (type.equals("INTEGER"))
        {
            if (parameter instanceof Long)
            {
                Long n = (Long)parameter;
                return n.toString();
            }
            else
            {
                String s = (String)parameter;
                return s;
            }
        }
        else
        {
            return "TYPE NOT SUPPORTED";
        }
    }
    
    public boolean validQuest()
    {
        String errors = "";
        Graph graph = (Graph)getGraph();
        Object parent = graph.getDefaultParent();
        Object[] edges = graph.getChildEdges(parent);
        Object[] nodes = graph.getChildVertices(parent);
        
        int countStart = 0;
        for (Object node : nodes)
        {
            mxCell cell = (mxCell)node;
            Status status = (Status)cell.getValue();
            if (status.isStart())
            {
                countStart = countStart + 1;
            }
        }
        if (countStart != 1) {
            errors = errors + Lang.getMsg(Editor.class, "startNumberError") + " " + countStart + ".\n";
        }
        
        int countUnconnected = 0;
        for (Object edge : edges)
        {
            mxCell cell = (mxCell)edge;
            mxCell source = (mxCell)cell.getSource();
            mxCell target = (mxCell)cell.getTarget();
            if (source == null || target == null)
            {
                countUnconnected = countUnconnected + 1;
            }
        }
        if (countUnconnected > 0) {
            errors = errors + Lang.getMsg(Editor.class, "unconnectedError") + " " + countUnconnected + ".\n";
        }
        
        if (errors.length() != 0)
        {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                errors,
                Lang.getMsg(Editor.class, "exportFailed"),
                JOptionPane.ERROR_MESSAGE);
                
            return false;
        }
        
        return true;
    }
}
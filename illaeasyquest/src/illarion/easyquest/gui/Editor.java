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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

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
import illarion.easyquest.quest.Trigger;
import illarion.easyquest.quest.Position;
import illarion.easyquest.quest.TriggerTemplate;
import illarion.easyquest.quest.TriggerTemplates;
import illarion.easyquest.EditorKeyboardHandler;

/**
 * The editor is the area that displays the quest graph.
 * 
 * @author Andreas Grob
 * @since 1.00
 */
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
		        if (e.getClickCount() == 1) {
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
        return mxUtils.getPrettyXml(codec.encode(getGraph().getModel()));
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
            
            String t;
            t = template.getHeader()
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
    		          + exportParameter(parameters[j])
    		          + "\n";
    		}
    		t = t + "\n";
    		t = t + template.getBody();
    		
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
    
    private String exportParameter(Object parameter)
    {
        if (parameter instanceof String)
        {
            String s = (String)parameter;
            return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
        else if (parameter instanceof Position)
        {
            Position p = (Position)parameter;
            return "position(" + p.getX() + ", " + p.getY() + ", " + p.getZ() + ")";
        }
        else
        {
            return "TYPE NOT SUPPORTED";
        }
    }
}
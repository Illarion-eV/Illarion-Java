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

import java.util.Hashtable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import org.w3c.dom.Document;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;

import illarion.easyquest.quest.Status;

/**
 * The editor is the area that displays the quest graph.
 * 
 * @author Andreas Grob
 * @since 1.00
 */
public final class Editor extends mxGraphComponent {

    private File questFile;
    
    private boolean savedSinceLastChange = false;

    Editor(Graph graph) {
        super(graph);
        mxCodecRegistry.register(new mxObjectCodec(new Status()));
        mxCodecRegistry.addPackage(Status.class.getPackage().getName());
        
        final Graph g = graph;
        getGraphControl().addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
			    if (e.getClickCount() == 2) {
    				Object cell = getCellAt(e.getX(), e.getY());
    				
    				if (cell != null)
    				{
    					System.out.println("cell="+g.getLabel(cell));
    				}
    			}
			}
		});
    }
    
    private static void setup(Graph graph) {
        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> nodeStyle = new Hashtable<String, Object>();
        nodeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        nodeStyle.put(mxConstants.STYLE_ROUNDED, true);
        nodeStyle.put(mxConstants.STYLE_OPACITY, 50);
        nodeStyle.put(mxConstants.STYLE_FILLCOLOR, "#EFEFFF");
        nodeStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#AFAFFF");
        nodeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        stylesheet.putCellStyle("NODE", nodeStyle);
        Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
        edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ELBOW);
        edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, 2.0);
        edgeStyle.put(mxConstants.STYLE_ROUNDED, true);
        stylesheet.putCellStyle("EDGE", edgeStyle);
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

}
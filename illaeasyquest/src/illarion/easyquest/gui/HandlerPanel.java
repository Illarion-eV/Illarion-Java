/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright 2011 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute i and/or
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

import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;

import illarion.easyquest.quest.Handler;
import illarion.easyquest.quest.HandlerTemplates;
import illarion.easyquest.quest.HandlerTemplate;

@SuppressWarnings("serial")
public class HandlerPanel extends JPanel
{
    private final JComboBox handlerType;
    private final JPanel parameterPanels;
    private final JButton addHandler;
    private final JButton removeHandler;
    private final StatusDialog owner;
    
    public HandlerPanel(StatusDialog owner, Handler handler)
    {
        super();
        
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
        
        this.owner = owner;
        
        handlerType = new JComboBox(HandlerTemplates.getInstance().getTemplates());
        parameterPanels = new JPanel(new GridLayout(0,1,0,5));
        addHandler = new JButton("+");
        removeHandler = new JButton("-");
        
        final JPanel header = new JPanel();
        header.add(handlerType);
        header.add(removeHandler);
        header.add(addHandler);
        
        add(header);
        add(parameterPanels);
        
        final StatusDialog dialog = this.owner;
        handlerType.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if (e.getStateChange() == ItemEvent.SELECTED)
		        {
		            parameterPanels.removeAll();
		            HandlerTemplate template = (HandlerTemplate)e.getItem();
		            for (int i=0; i<template.size(); ++i)
            		{
            		    ParameterPanel parameter = new ParameterPanel(template.getParameter(i));
           		        
            		    parameterPanels.add(parameter);
            		}
            		dialog.pack();
            		dialog.validate();
		        }
		        else if (e.getStateChange() == ItemEvent.DESELECTED)
		        {
		            parameterPanels.removeAll();
		            dialog.pack();
		            dialog.validate();
		        }
		    }
		});
		
		handlerType.setSelectedIndex(-1);
		if (handler != null)
		{
		    handlerType.setSelectedItem(HandlerTemplates.getInstance().getTemplate(handler.getType()));
		    Object[] parameters = handler.getParameters();
		    for (int i=0; i<parameters.length; ++i)
		    {
		        ParameterPanel panel = (ParameterPanel)parameterPanels.getComponent(i);
		        panel.setParameter(parameters[i]);
		    }
		}
		
		addHandler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.addHandler();
            }
        });
        
        final HandlerPanel handlerPanel = this;
        removeHandler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.removeHandler(handlerPanel);
            }
        });
    }
    
    public void clearSelection()
    {
        handlerType.setSelectedIndex(-1);
    }
    
    public Handler getHandler()
    {
        Handler handler = null;
        HandlerTemplate template = (HandlerTemplate)handlerType.getSelectedItem();
        if (template != null)
        {
            int count = parameterPanels.getComponentCount();
            Object[] parameters = new Object[count];
            for (int i=0; i<count; ++i)
            {
                ParameterPanel p = (ParameterPanel)parameterPanels.getComponent(i);
                parameters[i] = p.getParameter();
            }
        
        
            handler = new Handler();
            handler.setType(template.getName());
            handler.setParameters(parameters);
        }
        return handler;
    }
}
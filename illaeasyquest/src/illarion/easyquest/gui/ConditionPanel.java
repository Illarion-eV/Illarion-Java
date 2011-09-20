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
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;

import illarion.easyquest.quest.Condition;
import illarion.easyquest.quest.ConditionTemplates;
import illarion.easyquest.quest.ConditionTemplate;

@SuppressWarnings("serial")
public class ConditionPanel extends JPanel
{
    private final JComboBox conditionType;
    private final JPanel parameterPanels;
    private final JButton addCondition;
    private final JButton removeCondition;
    private final TriggerDialog owner;
    
    public ConditionPanel(TriggerDialog owner, Condition condition)
    {
        super();
        
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
        
        this.owner = owner;
        ConditionTemplate[] templates = ConditionTemplates.getInstance().getTemplates();
        Arrays.sort(templates);
        conditionType = new JComboBox(templates);
        parameterPanels = new JPanel(new GridLayout(0,1,0,5));
        addCondition = new JButton("+");
        removeCondition = new JButton("-");
        
        final JPanel header = new JPanel();
        header.add(conditionType);
        header.add(removeCondition);
        header.add(addCondition);
        
        add(header);
        add(parameterPanels);
        
        final TriggerDialog dialog = this.owner;
        conditionType.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if (e.getStateChange() == ItemEvent.SELECTED)
		        {
		            parameterPanels.removeAll();
		            ConditionTemplate template = (ConditionTemplate)e.getItem();
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
		
		conditionType.setSelectedIndex(-1);
		if (condition != null)
		{
		    conditionType.setSelectedItem(ConditionTemplates.getInstance().getTemplate(condition.getType()));
		    Object[] parameters = condition.getParameters();
		    for (int i=0; i<parameters.length; ++i)
		    {
		        ParameterPanel panel = (ParameterPanel)parameterPanels.getComponent(i);
		        panel.setParameter(parameters[i]);
		    }
		}
		
		addCondition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.addCondition();
            }
        });
        
        final ConditionPanel conditionPanel = this;
        removeCondition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.removeCondition(conditionPanel);
            }
        });
    }
    
    public void clearSelection()
    {
        conditionType.setSelectedIndex(-1);
    }
    
    public Condition getCondition()
    {
        Condition condition = null;
        ConditionTemplate template = (ConditionTemplate)conditionType.getSelectedItem();
        if (template != null)
        {
            int count = parameterPanels.getComponentCount();
            Object[] parameters = new Object[count];
            for (int i=0; i<count; ++i)
            {
                ParameterPanel p = (ParameterPanel)parameterPanels.getComponent(i);
                parameters[i] = p.getParameter();
            }
        
        
            condition = new Condition();
            condition.setType(template.getName());
            condition.setParameters(parameters);
        }
        return condition;
    }
}
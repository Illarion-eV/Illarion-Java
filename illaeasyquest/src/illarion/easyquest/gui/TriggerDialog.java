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

import java.text.NumberFormat;

import java.awt.Component;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.BorderFactory;

import illarion.easyquest.quest.TriggerTemplates;
import illarion.easyquest.quest.TriggerTemplate;

public class TriggerDialog extends JDialog
{
    
    private final JTextField name;
    private final JFormattedTextField objectId;
    private final JComboBox trigger;
    private final JButton okay;
    private final JButton cancel;
    private final JPanel main;
    private final JLabel labelId;
    
    public TriggerDialog(Frame owner)
    {
        super(owner, "Trigger");
        
        final JPanel header = new JPanel(new GridLayout(0,2,0,5));
        main = new JPanel(new GridLayout(0,1,0,5));
		final Box buttons = Box.createHorizontalBox();
		final JLabel labelName = new JLabel("Name:");
		labelId = new JLabel("Objekt ID:");
		final JLabel labelType = new JLabel("Typ:");
		name = new JTextField(17);
		NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        objectId = new JFormattedTextField(format);
        objectId.setHorizontalAlignment(JFormattedTextField.RIGHT);
		objectId.setValue(new Long(0));
		trigger = new JComboBox();
		okay = new JButton("OK");
		cancel = new JButton("Abbrechen");
		
		for (int i=0; i<TriggerTemplates.getInstance().size(); ++i)
		{
		    trigger.addItem(TriggerTemplates.getInstance().getTemplate(i));
		}
		
		trigger.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if (e.getStateChange() == ItemEvent.SELECTED)
		        {
		            main.removeAll();
		            TriggerTemplate template = (TriggerTemplate)e.getItem();
		            for (int i=0; i<template.size(); ++i)
            		{
            		    main.add(new ParameterPanel(template.getParameter(i)));
            		}
            		String category = template.getCategory();
            		String label = category.substring(0, 1).toUpperCase()
            		    + category.substring(1) + " ID:";
            		labelId.setText(label);
            		pack();
            		validate();
		        }
		    }
		});
		
		trigger.setSelectedIndex(-1);
		trigger.setSelectedIndex(0);
        
		setResizable(false);
		
		header.add(labelName);
		header.add(name);
		header.add(labelId);
		header.add(objectId);
		header.add(labelType);
		header.add(trigger);
		header.add(main);
		header.setBorder(BorderFactory.createEmptyBorder(5,5,10,5));
		
		buttons.add(Box.createHorizontalGlue());
		buttons.add(okay);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(cancel);
		buttons.setBorder(BorderFactory.createEmptyBorder(20,5,5,5));
		
		main.setBorder(BorderFactory.createTitledBorder("Parameter"));
		
		getRootPane().setDefaultButton(okay);

		add(header, BorderLayout.NORTH);
		add(main, BorderLayout.CENTER);
		add(buttons, BorderLayout.PAGE_END);
		
		pack();
    }
   
    public String getName()
    {
        return name.getText();
    }
    
    public void setName(String value)
    {
        name.setText(value);
    }
    
    public long getId()
    {
        return ((Number)objectId.getValue()).longValue();
    }
    
    public void setId(long value)
    {
        objectId.setValue(new Long(value));
    }
    
    public String getType()
    {
        return ((TriggerTemplate)trigger.getSelectedItem()).getName();
    }
    
    public void setType(String type)
    {
        trigger.setSelectedItem(TriggerTemplates.getInstance().getTemplate(type));
    }
    
    public Object[] getParameters()
    {
        int count = main.getComponentCount();
        Object[] parameters = new Object[count];
        for (int i=0; i<count; ++i)
        {
            Component c = main.getComponent(i);
            parameters[i] = ((ParameterPanel)c).getParameter();
        }
        return parameters;
    }
    
    public void setParameters(Object[] parameters)
    {
        int count = main.getComponentCount();
        
        if (parameters != null)
        {
            for (int i=0; i<count; ++i)
            {
                Component c = main.getComponent(i);
                ((ParameterPanel)c).setParameter(parameters[i]);
            }
        }
        else
        {
            for (int i=0; i<count; ++i)
            {
                Component c = main.getComponent(i);
                ((ParameterPanel)c).setParameter(null);
            }
        }
    }
    
    public void addOkayListener(ActionListener listener)
    {
        okay.addActionListener(listener);
    }
    
    public void addCancelListener(ActionListener listener)
    {
        cancel.addActionListener(listener);
    }
}
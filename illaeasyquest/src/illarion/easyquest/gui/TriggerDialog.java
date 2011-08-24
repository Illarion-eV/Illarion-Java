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

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;

import illarion.easyquest.quest.TriggerTemplates;
import illarion.easyquest.quest.TriggerTemplate;

public class TriggerDialog extends JDialog
{
    
    private JTextField name;
    private JComboBox trigger;
    private JButton okay;
    private JButton cancel;
    
    public TriggerDialog(Frame owner)
    {
        super(owner, "Trigger");
        
        final JPanel main = new JPanel();
		final JPanel bottom = new JPanel();
		final JLabel label = new JLabel("Name:");
		name = new JTextField(15);
		trigger = new JComboBox();
		okay = new JButton("OK");
		cancel = new JButton("Abbrechen");
		
		for (int i=0; i<TriggerTemplates.getInstance().size(); ++i)
		{
		    trigger.addItem(TriggerTemplates.getInstance().getTemplate(i));
		}
        
        setSize(240,130);
		setResizable(false);
		
		main.add(label, BorderLayout.NORTH);
		main.add(name, BorderLayout.NORTH);
		main.add(trigger, BorderLayout.CENTER);
		
		bottom.add(okay);
		bottom.add(cancel);

		add(main, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
    }
   
    public String getName()
    {
        return name.getText();
    }
    
    public void setName(String value)
    {
        name.setText(value);
    }
    
    public String getType()
    {
        return ((TriggerTemplate)trigger.getSelectedItem()).getName();
    }
    
    public void setType(String type)
    {
        trigger.setSelectedItem(TriggerTemplates.getInstance().getTemplate(type));
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
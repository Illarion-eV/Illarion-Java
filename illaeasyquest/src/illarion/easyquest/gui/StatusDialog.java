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
import javax.swing.JCheckBox;
import javax.swing.JButton;

public class StatusDialog extends JDialog
{
    
    private JTextField name;
    private JCheckBox start;
    private JButton okay;
    private JButton cancel;
    
    public StatusDialog(Frame owner)
    {
        super(owner, "Status");
        
        final JPanel main = new JPanel();
		final JPanel bottom = new JPanel();
		final JLabel label = new JLabel("Name:");
		name = new JTextField(15);
		start = new JCheckBox("Questbeginn");
		okay = new JButton("OK");
		cancel = new JButton("Abbrechen");
        
        setSize(240,130);
		setResizable(false);
		
		main.add(label, BorderLayout.NORTH);
		main.add(name, BorderLayout.NORTH);
		main.add(start, BorderLayout.CENTER);
		
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
    
    public boolean isStart()
    {
        return start.isSelected();
    }
    
    public void setStart(boolean value)
    {
        start.setSelected(value);
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
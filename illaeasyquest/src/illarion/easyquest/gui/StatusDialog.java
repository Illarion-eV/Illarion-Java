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

import java.util.List;
import java.util.ArrayList;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.Box;
import javax.swing.BorderFactory;

import illarion.easyquest.quest.HandlerTemplates;
import illarion.easyquest.quest.HandlerTemplate;
import illarion.easyquest.quest.Handler;
import illarion.easyquest.Lang;

public class StatusDialog extends JDialog
{
    
    private final JTextField name;
    private final JCheckBox start;
    private final Box handlerPanels;
    private final JButton okay;
    private final JButton cancel;
    
    public StatusDialog(Frame owner)
    {
        super(owner);
        setTitle(Lang.getMsg(getClass(), "title"));
        
        final JPanel main = new JPanel();
        handlerPanels = Box.createVerticalBox();
		final Box buttons = Box.createHorizontalBox();
		final JLabel label = new JLabel(Lang.getMsg(getClass(), "name")+":");
		name = new JTextField(15);
		start = new JCheckBox(Lang.getMsg(getClass(), "start"));
		okay = new JButton(Lang.getMsg(getClass(), "ok"));
		cancel = new JButton(Lang.getMsg(getClass(), "cancel"));
        
		setResizable(false);
		
		buttons.add(Box.createHorizontalGlue());
		buttons.add(okay);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(cancel);
		buttons.setBorder(BorderFactory.createEmptyBorder(20,5,5,5));

        main.add(label);
        main.add(name);
        main.add(start);
        main.setBorder(BorderFactory.createEmptyBorder(5,5,10,5));

        handlerPanels.setBorder(BorderFactory.createTitledBorder(Lang.getMsg(getClass(), "handlers")));
        
        getRootPane().setDefaultButton(okay);

		add(main, BorderLayout.NORTH);
		add(handlerPanels, BorderLayout.CENTER);
		add(buttons, BorderLayout.SOUTH);
		
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
    
    public boolean isStart()
    {
        return start.isSelected();
    }
    
    public void setStart(boolean value)
    {
        start.setSelected(value);
    }
    
    public Handler[] getHandlers()
    {
        int count = (handlerPanels.getComponentCount() + 1) / 2;
        List<Handler> handlers = new ArrayList<Handler>();
        for (int i=0; i<count; ++i)
        {
            HandlerPanel hp = (HandlerPanel)handlerPanels.getComponent(2*i);
            Handler h = hp.getHandler();
            if (h != null)
            {
                handlers.add(h);
            }
        }
        return handlers.toArray(new Handler[0]);
    }
    
    public void setHandlers(Handler[] handlers)
    {
        handlerPanels.removeAll();
        
        if (handlers != null && handlers.length > 0)
        {
            handlerPanels.add(new HandlerPanel(this, handlers[0]));
            for (int i=1; i<handlers.length; ++i)
            {
                handlerPanels.add(new JSeparator());
                handlerPanels.add(new HandlerPanel(this, handlers[i]));
            }
        }
        else
        {
            handlerPanels.add(new HandlerPanel(this, null));
        }
        
        pack();
        validate();
    }
    
    public void addHandler()
    {
        handlerPanels.add(new JSeparator());
        handlerPanels.add(new HandlerPanel(this, null));
        pack();
        validate();
    }
    
    public void removeHandler(HandlerPanel handler)
    {
        if (handlerPanels.getComponentCount() > 1)
        {
            int z = handlerPanels.getComponentZOrder(handler);
            if (z != 0)
            {
                handlerPanels.remove(z-1);
            }
            else
            {
                handlerPanels.remove(z+1);
            }
            handlerPanels.remove(handler);
        }
        else
        {
            ((HandlerPanel)handlerPanels.getComponent(0)).clearSelection();
        }
        
        pack();
        validate();
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
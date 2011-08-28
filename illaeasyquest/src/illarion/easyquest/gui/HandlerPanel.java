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

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;

import illarion.easyquest.quest.Handler;
import illarion.easyquest.quest.HandlerTemplates;

public class HandlerPanel extends JPanel
{
    private final Handler handler;
    private final JComboBox handlerType;
    private final JButton addHandler;
    private final JButton removeHandler;
    
    public HandlerPanel(Handler handler)
    {
        this.handler = handler;
        
        handlerType = new JComboBox(HandlerTemplates.getInstance().getTemplates());
        addHandler = new JButton("+");
        removeHandler = new JButton("-");
        
        add(handlerType);
        add(removeHandler);
        add(addHandler);
    }
    
    public Handler getHandler()
    {
        return handler;
    }
}
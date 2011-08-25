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

import javax.swing.JPanel;
import javax.swing.JLabel;

import illarion.easyquest.quest.TriggerTemplateParameter;

public class ParameterPanel extends JPanel
{
    private Component comp;
    
    public ParameterPanel(TriggerTemplateParameter parameter)
    {
        JLabel description = new JLabel(parameter.getDescription()+":");
        Component comp = new TextParameter();
        
        add(description);
        add(comp);
    }
}
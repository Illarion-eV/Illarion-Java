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

import java.text.NumberFormat;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JFormattedTextField;

import illarion.easyquest.quest.Position;

public class PositionParameter extends JPanel implements Parameter
{
    private final JFormattedTextField xField;
    private final JFormattedTextField yField;
    private final JFormattedTextField zField;
    
    public PositionParameter()
    {
        super(new GridLayout(1,3));
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        xField = new JFormattedTextField(format);
        yField = new JFormattedTextField(format);
        zField = new JFormattedTextField(format);
        xField.setHorizontalAlignment(JFormattedTextField.RIGHT);
        yField.setHorizontalAlignment(JFormattedTextField.RIGHT);
        zField.setHorizontalAlignment(JFormattedTextField.RIGHT);
        add(xField);
        add(yField);
        add(zField);
        setParameter(new Position((short)0,(short)0,(short)0));
    }
    
    public void setParameter(Object parameter)
    {
        Position p = (Position)parameter;
        xField.setValue(p.x);
        yField.setValue(p.y);
        zField.setValue(p.z);
    }
    
    public Object getParameter()
    {
        return new Position(Short.parseShort((String)xField.getValue()),
                            Short.parseShort((String)yField.getValue()),
                            Short.parseShort((String)zField.getValue()));
    }
}
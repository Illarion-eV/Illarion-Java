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

import illarion.easyquest.quest.IntegerRelation;
import illarion.easyquest.quest.Relation;
import illarion.easyquest.quest.Relation.Type;

import java.awt.BorderLayout;
import java.text.NumberFormat;

import javax.swing.text.NumberFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class IntegerRelationParameter extends JPanel implements Parameter
{
	private JComboBox relation;
	private JFormattedTextField integer;
	private static final Relation EQUAL = new Relation(Type.EQUAL);
	private static final Relation NOTEQUAL = new Relation(Type.NOTEQUAL);
	private static final Relation LESSER = new Relation(Type.LESSER);
	private static final Relation GREATER = new Relation(Type.GREATER);
	private static final Relation LESSEROREQUAL = new Relation(Type.LESSEROREQUAL);
	private static final Relation GREATEROREQUAL = new Relation(Type.GREATEROREQUAL);
    
    public IntegerRelationParameter()
    {
        super(new BorderLayout(5,0));
        relation = new JComboBox();
        relation.addItem(EQUAL);
        relation.addItem(NOTEQUAL);
        relation.addItem(LESSER);
        relation.addItem(GREATER);
        relation.addItem(LESSEROREQUAL);
        relation.addItem(GREATEROREQUAL);
        integer = new JFormattedTextField();
        add(relation, BorderLayout.WEST);
        add(integer, BorderLayout.CENTER);
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
        integer.setFormatterFactory(factory);
        integer.setHorizontalAlignment(JFormattedTextField.RIGHT);
        setParameter(new IntegerRelation());
    }
    
    public void setParameter(Object parameter)
    {
    	IntegerRelation rel;
        if (parameter != null)
        {
        	rel = (IntegerRelation)parameter;
        }
        else
        {
        	rel = new IntegerRelation();
        }
        integer.setValue(new Long(rel.getInteger()));
        relation.setSelectedItem(rel.getRelation());
    }
    
    public Object getParameter()
    {
        return new IntegerRelation(
        		(Relation)relation.getSelectedItem(),
        		(Long)integer.getValue()
        );
    }
}
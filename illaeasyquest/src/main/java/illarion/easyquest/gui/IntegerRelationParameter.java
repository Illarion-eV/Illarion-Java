/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyQuest Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyQuest Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.gui;

import illarion.easyquest.quest.IntegerRelation;
import illarion.easyquest.quest.Relation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class IntegerRelationParameter extends JPanel implements Parameter {
    private JComboBox relation;
    private JFormattedTextField integer;
    private static final Relation EQUAL = new Relation(Relation.EQUAL);
    private static final Relation NOTEQUAL = new Relation(Relation.NOTEQUAL);
    private static final Relation LESSER = new Relation(Relation.LESSER);
    private static final Relation GREATER = new Relation(Relation.GREATER);
    private static final Relation LESSEROREQUAL = new Relation(Relation.LESSEROREQUAL);
    private static final Relation GREATEROREQUAL = new Relation(Relation.GREATEROREQUAL);
    private static final Map<Integer, Relation> relationMap = new HashMap<Integer, Relation>() {{
        put(Relation.EQUAL, EQUAL);
        put(Relation.NOTEQUAL, NOTEQUAL);
        put(Relation.LESSER, LESSER);
        put(Relation.GREATER, GREATER);
        put(Relation.LESSEROREQUAL, LESSEROREQUAL);
        put(Relation.GREATEROREQUAL, GREATEROREQUAL);
    }};

    public IntegerRelationParameter() {
        super(new BorderLayout(5, 0));
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

    public void setParameter(@Nullable Object parameter) {
        IntegerRelation rel;
        if (parameter != null) {
            rel = (IntegerRelation) parameter;
        } else {
            rel = new IntegerRelation();
        }
        integer.setValue(new Long(rel.getInteger()));
        relation.setSelectedItem(relationMap.get(rel.getRelation().getType()));
    }

    @Nonnull
    public Object getParameter() {
        return new IntegerRelation(
                (Relation) relation.getSelectedItem(),
                (Long) integer.getValue()
        );
    }
}
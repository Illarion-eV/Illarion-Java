/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui.awt;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import illarion.mapedit.graphics.AbstractEntity;

/**
 * The parts list stores and displays all the actually loaded parts.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class PartsList extends Panel implements ItemListener {
    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    private ScrollPane displayedPane;

    /**
     * The list of groups displayed by this parts list.
     */
    private final Choice groupNames;

    /**
     * The parts displayed by this parts list.
     */
    private final ArrayList<Panel> parts;

    private final int partsType;

    public PartsList(final int type) {
        super(new BorderLayout(5, 5));

        parts = new ArrayList<Panel>();

        groupNames = new Choice();
        groupNames.addItemListener(this);
        partsType = type;
        add(groupNames, BorderLayout.NORTH);
    }

    @SuppressWarnings("nls")
    public void addGroup(final String groupName) {
        groupNames.add(groupName);

        final ScrollPane scrollArea = new ScrollPane();
        final Panel scrollPanel = new Panel(new GridLayout(0, 1));
        parts.add(scrollPanel);
        scrollArea.add(scrollPanel);
        scrollPanel.add(new PartsEntry(null, "delete", partsType, 0));

        if (parts.size() == 1) {
            add(scrollArea, BorderLayout.CENTER);
            displayedPane = scrollArea;
        }

        validate();
    }

    public void addPart(final int group, final AbstractEntity part) {
        if ((group == -1) || (part.getId() == 0)) {
            return;
        }
        final PartsEntry entry =
            new PartsEntry(part.getIcon(), part.getName(), partsType,
                part.getId());
        parts.get(group).add(entry);

        validate();
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        for (int i = 0, n = groupNames.getItemCount(); i < n; i++) {
            if (groupNames.getItem(i).equals(e.getItem())) {
                remove(displayedPane);
                displayedPane = (ScrollPane) parts.get(i).getParent();
                add(displayedPane, BorderLayout.CENTER);

                validate();
            }
        }
    }
}

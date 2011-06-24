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
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import illarion.mapedit.tools.parts.AbstractMousePart;

/**
 * This part of the GUI allows to select the part that shall be placed with the
 * mouse. This is either a item or a tile.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class PartSelector extends Panel {
    static final int ACTIVATE_ITEMS = 2;

    static final int ACTIVATE_TILES = 1;

    /**
     * The serialization UID of this selector.
     */
    private static final long serialVersionUID = 1L;

    private final PartsList itemsList;
    private final PartsList tilesList;

    @SuppressWarnings("nls")
    public PartSelector() {
        super(new BorderLayout(5, 5));

        final Panel typePanel = new Panel(new GridLayout(1, 2, 5, 5));
        final Button typeTilesBtn = new Button("Tiles");
        final Button typeItemsBtn = new Button("Items");

        typeTilesBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setButtonMode(typeTilesBtn, true);
                setButtonMode(typeItemsBtn, false);
                setActivePartsList(ACTIVATE_TILES);
            }
        });
        typeItemsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setButtonMode(typeTilesBtn, false);
                setButtonMode(typeItemsBtn, true);
                setActivePartsList(ACTIVATE_ITEMS);
            }
        });

        typePanel.add(typeTilesBtn);
        typePanel.add(typeItemsBtn);

        add(typePanel, BorderLayout.NORTH);

        tilesList = new PartsList(AbstractMousePart.TYPE_TILE);
        itemsList = new PartsList(AbstractMousePart.TYPE_ITEM);

        setButtonMode(typeTilesBtn, true);
        setButtonMode(typeItemsBtn, false);
        setActivePartsList(ACTIVATE_TILES);
    }

    /**
     * This method is used to toogle the colors of the buttons to display that
     * they are not in the default state or that they are activated.
     * 
     * @param button the button that is supposed to be toogled
     * @param enabled <code>true</code> in case the color shall be set to the
     *            enabled state, <code>false</code> to set it to the default
     *            state.
     */
    static void setButtonMode(final Button button, final boolean enabled) {
        if (enabled) {
            button.setBackground(Color.GRAY);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(SystemColor.control);
            button.setForeground(SystemColor.controlText);
        }
    }

    public PartsList getItemsList() {
        return itemsList;
    }

    public PartsList getTilesList() {
        return tilesList;
    }

    @SuppressWarnings("nls")
    void setActivePartsList(final int activeList) {
        if (activeList == ACTIVATE_TILES) {
            remove(itemsList);
            add(tilesList, BorderLayout.CENTER);
        } else if (activeList == ACTIVATE_ITEMS) {
            remove(tilesList);
            add(itemsList, BorderLayout.CENTER);
        } else {
            throw new IllegalArgumentException(
                "Invalid value for active list selector");
        }
        validate();
    }
}

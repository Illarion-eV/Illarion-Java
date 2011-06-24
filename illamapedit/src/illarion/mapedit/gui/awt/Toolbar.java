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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import illarion.mapedit.MapEditor;
import illarion.mapedit.graphics.MapDisplay;
import illarion.mapedit.tools.AbstractTool;
import illarion.mapedit.tools.ToolArea;
import illarion.mapedit.tools.ToolRandomArea;
import illarion.mapedit.tools.ToolSingle;
import illarion.mapedit.tools.parts.MousePartItem;

import illarion.common.util.Location;

/**
 * The tool bar of the GUI is used to access the global functions of the editor.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class Toolbar extends Panel {
    /**
     * The serialization UID of the tool bar.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The button that activates the area tool.
     */
    private final Button areaToolBtn;

    /**
     * The label that displays the current location on the map.
     */
    private final Label locationLabel;

    /**
     * The label that shows if another tool is activated.
     */
    private final Label otherToolLabel;

    /**
     * The button that activates the random area tool.
     */
    private final Button rndAreaToolBtn;

    /**
     * The button that activates the single tool.
     */
    private final Button singleToolBtn;

    /**
     * Create the tool bar with the correct settings.
     */
    @SuppressWarnings("nls")
    public Toolbar() {
        super(new BorderLayout());
        setBackground(SystemColor.control);

        final Panel displaySettings = new Panel(new GridLayout(3, 3, 5, 5));
        add(displaySettings, BorderLayout.EAST);

        final Button hideItemsBtn = new Button("Showing Items");
        hideItemsBtn.addActionListener(new ActionListener() {
            private final String hideText = "Showing Items";
            private final String showText = "Hiding Items";

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (MapEditor.getDisplay().getSettingsItem() == MapDisplay.ItemDisplay.hide) {
                    if (MapEditor.getDisplay().getZoom() >= 0.5f) {
                        MapEditor.getDisplay().setSettingsItem(
                            MapDisplay.ItemDisplay.show);
                    }
                    ((Button) (e.getSource())).setLabel(hideText);
                    setButtonMode((Button) e.getSource(), false);
                    MapEditor.getConfig().set("hideItems", false);
                } else {
                    MapEditor.getDisplay().setSettingsItem(
                        MapDisplay.ItemDisplay.hide);
                    ((Button) (e.getSource())).setLabel(showText);
                    setButtonMode((Button) e.getSource(), true);
                    MapEditor.getConfig().set("hideItems", true);
                }
            }
        });
        if (MapEditor.getConfig().getBoolean("hideItems")) {
            MapEditor.getDisplay()
                .setSettingsItem(MapDisplay.ItemDisplay.hide);
            hideItemsBtn.setLabel("Hiding Items");
            setButtonMode(hideItemsBtn, true);
        }
        displaySettings.add(hideItemsBtn);

        final Button hideTilesBtn = new Button("Showing Tiles");
        hideTilesBtn.addActionListener(new ActionListener() {
            private final String hideText = "Showing Tiles";
            private final String showText = "Hiding Tiles";

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (MapEditor.getDisplay().getSettingsTile() == MapDisplay.TileDisplay.hide) {
                    if (MapEditor.getDisplay().getZoom() < 0.5f) {
                        MapEditor.getDisplay().setSettingsTile(
                            MapDisplay.TileDisplay.simple);
                    } else {
                        MapEditor.getDisplay().setSettingsTile(
                            MapDisplay.TileDisplay.full);
                    }
                    ((Button) (e.getSource())).setLabel(hideText);
                    setButtonMode((Button) e.getSource(), false);
                    MapEditor.getConfig().set("hideTiles", false);
                } else {
                    MapEditor.getDisplay().setSettingsTile(
                        MapDisplay.TileDisplay.hide);
                    ((Button) (e.getSource())).setLabel(showText);
                    setButtonMode((Button) e.getSource(), true);
                    MapEditor.getConfig().set("hideTiles", true);
                }
            }
        });
        if (MapEditor.getConfig().getBoolean("hideTiles")) {
            MapEditor.getDisplay()
                .setSettingsTile(MapDisplay.TileDisplay.hide);
            hideTilesBtn.setLabel("Hiding Tiles");
            setButtonMode(hideTilesBtn, true);
        }
        displaySettings.add(hideTilesBtn);

        final Button hideGridBtn = new Button("Showing Grid");
        hideGridBtn.addActionListener(new ActionListener() {
            private final String hideText = "Showing Grid";
            private final String showText = "Hiding Grid";

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (MapEditor.getDisplay().getSettingsGrid() == MapDisplay.GridDisplay.hide) {
                    MapEditor.getDisplay().setSettingsGrid(
                        MapDisplay.GridDisplay.show);
                    ((Button) (e.getSource())).setLabel(hideText);
                    setButtonMode((Button) e.getSource(), false);
                    MapEditor.getConfig().set("hideGrid", false);
                } else {
                    MapEditor.getDisplay().setSettingsGrid(
                        MapDisplay.GridDisplay.hide);
                    ((Button) (e.getSource())).setLabel(showText);
                    setButtonMode((Button) e.getSource(), true);
                    MapEditor.getConfig().set("hideGrid", true);
                }
            }
        });
        if (MapEditor.getConfig().getBoolean("hideGrid")) {
            MapEditor.getDisplay()
                .setSettingsGrid(MapDisplay.GridDisplay.hide);
            hideGridBtn.setLabel("Hiding Grid");
            setButtonMode(hideGridBtn, true);
        }
        displaySettings.add(hideGridBtn);

        final Button showNormalBtn = new Button("Normal Mapview");
        final Button showLightsBtn = new Button("Lights Mapview");
        final Button showBlockedBtn = new Button("Blocked Mapview");
        showNormalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MapEditor.getDisplay().setSettingsSpecial(
                    MapDisplay.SpecialDisplay.none);
                MapEditor.getConfig().set("displayMap", 0);
                setButtonMode(showNormalBtn, true);
                setButtonMode(showLightsBtn, false);
                setButtonMode(showBlockedBtn, false);
            }
        });
        displaySettings.add(showNormalBtn);

        showLightsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MapEditor.getDisplay().setSettingsSpecial(
                    MapDisplay.SpecialDisplay.light);
                MapEditor.getConfig().set("displayMap", 1);
                setButtonMode(showNormalBtn, false);
                setButtonMode(showLightsBtn, true);
                setButtonMode(showBlockedBtn, false);
            }
        });
        displaySettings.add(showLightsBtn);

        showBlockedBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MapEditor.getDisplay().setSettingsSpecial(
                    MapDisplay.SpecialDisplay.blocked);
                MapEditor.getConfig().set("displayMap", 2);
                setButtonMode(showNormalBtn, false);
                setButtonMode(showLightsBtn, false);
                setButtonMode(showBlockedBtn, true);
            }
        });
        displaySettings.add(showBlockedBtn);

        final int mapDispSetting =
            MapEditor.getConfig().getInteger("displayMap");
        switch (mapDispSetting) {
            case 1:
                MapEditor.getDisplay().setSettingsSpecial(
                    MapDisplay.SpecialDisplay.light);
                setButtonMode(showLightsBtn, true);
                break;
            case 2:
                MapEditor.getDisplay().setSettingsSpecial(
                    MapDisplay.SpecialDisplay.blocked);
                setButtonMode(showBlockedBtn, true);
                break;
            default:
                MapEditor.getDisplay().setSettingsSpecial(
                    MapDisplay.SpecialDisplay.none);
                setButtonMode(showNormalBtn, true);
        }

        displaySettings.add(new Label("Zoom:"));

        final Label zoomDisplay = new Label("100%");
        final Scrollbar zoom =
            new Scrollbar(Scrollbar.HORIZONTAL, 100, 1, 2, 101);
        zoom.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(final AdjustmentEvent e) {
                MapEditor.getDisplay().setZoom(e.getValue() / 100.f);
                zoomDisplay.setText(Integer.toString(e.getValue()) + "%");
                if (e.getValue() < 25) {
                    if (!MapEditor.getConfig().getBoolean("hideTiles")) {
                        MapEditor.getDisplay().setSettingsTile(
                            MapDisplay.TileDisplay.simple);
                    }
                    if (!MapEditor.getConfig().getBoolean("hideItems")) {
                        MapEditor.getDisplay().setSettingsItem(
                            MapDisplay.ItemDisplay.hide);
                    }
                } else {
                    if (!MapEditor.getConfig().getBoolean("hideTiles")) {
                        MapEditor.getDisplay().setSettingsTile(
                            MapDisplay.TileDisplay.full);
                    }
                    if (!MapEditor.getConfig().getBoolean("hideItems")) {
                        MapEditor.getDisplay().setSettingsItem(
                            MapDisplay.ItemDisplay.show);
                    }
                }
            }
        });
        displaySettings.add(zoom);
        displaySettings.add(zoomDisplay);

        final Panel toolPanel = new Panel(new GridLayout(3, 5, 5, 5));

        toolPanel.add(new Label("General Placement:"));

        singleToolBtn = new Button("Single");
        areaToolBtn = new Button("Area");
        rndAreaToolBtn = new Button("Random Area");

        final AbstractTool defaultTool = new ToolSingle();
        defaultTool.activateTool();
        singleToolBtn.addActionListener(new ActionListener() {
            private final AbstractTool tool = defaultTool;

            @Override
            @SuppressWarnings("synthetic-access")
            public void actionPerformed(final ActionEvent e) {
                tool.activateTool();
                setButtonMode(singleToolBtn, true);
                setButtonMode(areaToolBtn, false);
                setButtonMode(rndAreaToolBtn, false);
                otherToolLabel.setText(null);
                setButtonMode(otherToolLabel, false);
            }
        });

        areaToolBtn.addActionListener(new ActionListener() {
            private final AbstractTool tool = new ToolArea();

            @Override
            @SuppressWarnings("synthetic-access")
            public void actionPerformed(final ActionEvent e) {
                tool.activateTool();
                setButtonMode(singleToolBtn, false);
                setButtonMode(areaToolBtn, true);
                setButtonMode(rndAreaToolBtn, false);
                otherToolLabel.setText(null);
                setButtonMode(otherToolLabel, false);
            }
        });

        rndAreaToolBtn.addActionListener(new ActionListener() {
            private final AbstractTool tool = new ToolRandomArea();

            @Override
            @SuppressWarnings("synthetic-access")
            public void actionPerformed(final ActionEvent e) {
                tool.activateTool();
                setButtonMode(singleToolBtn, false);
                setButtonMode(areaToolBtn, false);
                setButtonMode(rndAreaToolBtn, true);
                otherToolLabel.setText(null);
                setButtonMode(otherToolLabel, false);
            }
        });

        otherToolLabel = new Label();
        otherToolLabel.setAlignment(Label.CENTER);

        toolPanel.add(singleToolBtn);
        toolPanel.add(areaToolBtn);
        toolPanel.add(rndAreaToolBtn);
        toolPanel.add(otherToolLabel);

        setButtonMode(singleToolBtn, true);
        setButtonMode(areaToolBtn, false);
        setButtonMode(rndAreaToolBtn, false);

        toolPanel.add(new Label("Item Placement:"));

        final Button itemAddBtn = new Button("Add");
        final Button itemUniqueBtn = new Button("Unique");
        final Button itemReplaceBtn = new Button("Replace");

        itemAddBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MousePartItem.setPlacementMode(MousePartItem.MODE_ADD);
                setButtonMode(itemAddBtn, true);
                setButtonMode(itemUniqueBtn, false);
                setButtonMode(itemReplaceBtn, false);
            }
        });
        itemUniqueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MousePartItem.setPlacementMode(MousePartItem.MODE_UNIQUE);
                setButtonMode(itemAddBtn, false);
                setButtonMode(itemUniqueBtn, true);
                setButtonMode(itemReplaceBtn, false);
            }
        });
        itemReplaceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MousePartItem.setPlacementMode(MousePartItem.MODE_REPLACE);
                setButtonMode(itemAddBtn, false);
                setButtonMode(itemUniqueBtn, false);
                setButtonMode(itemReplaceBtn, true);
            }
        });

        setButtonMode(itemAddBtn, true);
        setButtonMode(itemUniqueBtn, false);
        setButtonMode(itemReplaceBtn, false);

        toolPanel.add(itemAddBtn);
        toolPanel.add(itemUniqueBtn);
        toolPanel.add(itemReplaceBtn);
        toolPanel.add(new Label());

        toolPanel.add(new Label());
        toolPanel.add(new Label());
        toolPanel.add(new Label());
        toolPanel.add(new Label());
        toolPanel.add(new Label());

        add(toolPanel, BorderLayout.WEST);

        locationLabel = new Label();
        locationLabel.setAlignment(Label.CENTER);
        add(locationLabel, BorderLayout.CENTER);
    }

    /**
     * This method is used to toggle the colors of the buttons to display that
     * they are not in the default state or that they are activated.
     * 
     * @param button the button that is supposed to be toggled
     * @param enabled <code>true</code> in case the color shall be set to the
     *            enabled state, <code>false</code> to set it to the default
     *            state.
     */
    static void setButtonMode(final Component button, final boolean enabled) {
        if (enabled) {
            button.setBackground(Color.GRAY);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(null);
            button.setForeground(null);
        }
    }

    /**
     * Display the current location of the cursor on the map.
     * 
     * @param loc the current location of the cursor on the map
     */
    @SuppressWarnings("nls")
    public void setCurrentLocation(final Location loc) {
        if (loc == null) {
            locationLabel.setText(null);
        } else {
            locationLabel.setText("Current Location: " + loc.toString());
        }
    }

    /**
     * In case another tool is activated write it down in this tool. This will
     * disable all tool buttons and show the name of the tool in a additional
     * label.
     * 
     * @param toolName the label of the tool
     */
    public void setOtherTool(final String toolName) {
        setButtonMode(singleToolBtn, false);
        setButtonMode(areaToolBtn, false);
        setButtonMode(rndAreaToolBtn, false);
        otherToolLabel.setText(toolName);
        setButtonMode(otherToolLabel, true);
    }
}

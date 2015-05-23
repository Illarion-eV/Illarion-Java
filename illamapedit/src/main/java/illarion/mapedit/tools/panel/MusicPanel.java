/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.mapedit.tools.panel;

import illarion.mapedit.Lang;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.tools.ToolManager;
import illarion.mapedit.tools.panel.components.SongTable;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

/**
 * @author Tim
 * @author Fredrik K
 */
public class MusicPanel extends JPanel {
    @Nonnull
    private final JCheckBox delCheckBox;
    @Nonnull
    private final JRadioButton fillSelectedCheckbox;
    @Nonnull
    private final JRadioButton fillAreaCheckbox;
    @Nonnull
    private final SongTable songTable;

    /**
     * Default constructor
     */
    public MusicPanel() {
        setLayout(new BorderLayout());

        songTable = new SongTable();
        add(songTable, BorderLayout.CENTER);

        JPanel northPanel = new JPanel(new GridLayout(0, 2));

        delCheckBox = new JCheckBox();
        fillSelectedCheckbox = new JRadioButton();
        fillAreaCheckbox = new JRadioButton();
        fillAreaCheckbox.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(fillAreaCheckbox);
        group.add(fillSelectedCheckbox);
        ResizableIcon icon = ImageLoader.getResizableIcon("player_play");
        icon.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));
        JButton playButton = new JButton(icon);

        playButton.addActionListener(e -> songTable.playSelectedSong());

        northPanel.add(new JLabel(Lang.getMsg("tools.FillSelected")));
        northPanel.add(fillSelectedCheckbox);
        northPanel.add(new JLabel(Lang.getMsg("tools.FillArea")));
        northPanel.add(fillAreaCheckbox);
        northPanel.add(new JLabel(Lang.getMsg("tools.MusicTool.Delete")));
        northPanel.add(delCheckBox);
        northPanel.add(new JLabel(Lang.getMsg("tools.MusicTool.Listen")));
        northPanel.add(playButton);
        add(northPanel, BorderLayout.NORTH);
    }

    /**
     * Get selected musicID
     *
     * @return musicID or 0 if eraser is selected
     */
    public int getMusicID() {
        Integer musicID = 0;

        if (!delCheckBox.isSelected()) {
            musicID = songTable.getSelectedMusicID();
        }
        return musicID;
    }

    public boolean isFillSelected() {
        return fillSelectedCheckbox.isSelected();
    }

    public boolean isFillArea() {
        return fillAreaCheckbox.isSelected();
    }
}

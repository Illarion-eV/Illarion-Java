/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Tim
 * @author Fredrik K
 */
public class MusicPanel extends JPanel {

    @Nonnull
    private final JCheckBox delCheckBox;
    @Nonnull
    private final JSpinner radiusSpinner;
    @Nonnull
    private final SongTable songTable;

    /**
     * Default constructor
     */
    public MusicPanel() {
        setLayout(new BorderLayout());

        songTable = new SongTable();
        add(songTable, BorderLayout.CENTER);

        final JPanel northPanel = new JPanel(new GridLayout(0, 2));

        radiusSpinner = new JSpinner(new SpinnerNumberModel(1, 1, ToolManager.TOOL_RADIUS, 1));
        delCheckBox = new JCheckBox();
        final ResizableIcon icon =  ImageLoader.getResizableIcon("player_play") ;
        icon.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));
        final JButton playButton = new JButton(icon);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                songTable.playSelectedSong();
            }
        });

        northPanel.add(new JLabel(Lang.getMsg("tools.MusicTool.Radius")));
        northPanel.add(radiusSpinner);
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

    /**
     * Get the radius for painting
     * @return the chosen radius.
     */
    public int getRadius() {
        return (Integer) radiusSpinner.getValue();
    }
}

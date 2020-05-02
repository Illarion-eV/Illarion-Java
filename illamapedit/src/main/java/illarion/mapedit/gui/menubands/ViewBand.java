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
package illarion.mapedit.gui.menubands;

import illarion.mapedit.Lang;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.render.*;
import javolution.util.FastTable;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.High2Low;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mid2Low;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mirror;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Tim
 */
public class ViewBand extends JRibbonBand {

    public ViewBand(@Nonnull RendererManager manager) {
        super(Lang.getMsg("gui.viewband.Name"), null);

        TileRenderer tileRenderer = new TileRenderer(manager);
        manager.addRenderer(new SelectedTileRenderer(manager));

        newRenderButton(manager, tileRenderer);
        newRenderButton(manager, new ItemRenderer(manager));
        renderEmptyTilesButton(tileRenderer);
        newRenderButton(manager, new GridRenderer(manager));
        newRenderButton(manager, new MusicRenderer(manager));
        newRenderButton(manager, new WarpRenderer(manager));
        newRenderButton(manager, new ItemDataRenderer(manager));
        newRenderButton(manager, new AnnotationRenderer(manager));
        newRenderButton(manager, new ObstacleRenderer(manager));

        List<RibbonBandResizePolicy> resize = new FastTable<>();
        resize.add(new Mirror(getControlPanel()));
        resize.add(new Mid2Low(getControlPanel()));
        resize.add(new High2Low(getControlPanel()));

        setResizePolicies(resize);
    }

    private void newRenderButton(@Nonnull RendererManager manager, @Nonnull AbstractMapRenderer renderer) {
        JCommandToggleButton btn = new JCommandToggleButton(renderer.getLocalizedName(),
                                                                  renderer.getRendererIcon());
        btn.getActionModel().setSelected(renderer.isDefaultOn());
        btn.addActionListener(e -> {
            if (btn.getActionModel().isSelected()) {
                manager.addRenderer(renderer);
            } else {
                manager.removeRenderer(renderer);
            }
        });
        if (renderer.isDefaultOn()) {
            manager.addRenderer(renderer);
        }
        addCommandButton(btn, renderer.getPriority());
    }

    private void renderEmptyTilesButton(@Nonnull TileRenderer renderer) {
        JCommandToggleButton btn = new JCommandToggleButton(renderer.getEmptyTileLocalizedName(),
                                                                  renderer.getEmptyTileRendererIcon());
        btn.getActionModel().setSelected(renderer.isEmptyTileDefaultOn());
        btn.addActionListener(e -> {
            renderer.setRenderEmptyTiles(btn.getActionModel().isSelected());
            EventBus.publish(new RepaintRequestEvent());
        });
        if (renderer.isEmptyTileDefaultOn()) {
            renderer.setRenderEmptyTiles(true);
        }
        addCommandButton(btn, renderer.getEmptyTilePriority());
    }
}

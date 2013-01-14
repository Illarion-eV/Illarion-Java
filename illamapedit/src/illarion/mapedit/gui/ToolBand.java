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
package illarion.mapedit.gui;

import illarion.mapedit.Lang;
import illarion.mapedit.gui.util.ToolMenuButton;
import illarion.mapedit.tools.*;
import javolution.util.FastList;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author Tim
 */
public class ToolBand extends JRibbonBand {


    public ToolBand() {
        super(Lang.getMsg("gui.toolband.Name"), null);

        final JCommandButton toolButton = new JCommandButton(
                Lang.getMsg("gui.toolband.Name"),
                null
        );
        final JCommandPopupMenu menu = new JCommandPopupMenu();


        final Collection<AbstractTool> tools = new FastList<AbstractTool>();


        //TOOLS HERE
        tools.add(new SingleItemTool());
        tools.add(new SingleTileTool());
        tools.add(new TileBrushTool());
        tools.add(new ItemEraserTool());
        tools.add(new TileEraserTool());
        tools.add(new MusicTool());
        tools.add(new WarpTool());

        for (final AbstractTool t : tools) {
            menu.addMenuButton(new ToolMenuButton(
                    t, t.getLocalizedName(), t.getToolIcon()
            ));
        }


        toolButton.setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
        toolButton.setPopupCallback(new PopupPanelCallback() {
            @Nonnull
            @Override
            public JPopupPanel getPopupPanel(final JCommandButton commandButton) {
                return menu;
            }
        });

        addCommandButton(toolButton, RibbonElementPriority.TOP);

        final List<RibbonBandResizePolicy> resize = new FastList<RibbonBandResizePolicy>();
        resize.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        resize.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        resize.add(new CoreRibbonResizePolicies.High2Low(getControlPanel()));

        setResizePolicies(resize);
    }


}

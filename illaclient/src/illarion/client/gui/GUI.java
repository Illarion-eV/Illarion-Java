/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui;

import illarion.graphics.Graphics;

import org.illarion.nifty.renderer.input.IllarionInputSystem;
import org.illarion.nifty.renderer.render.IllarionRenderDevice;
import org.illarion.nifty.renderer.render.RenderImageFactory;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.sound.SoundSystem;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.spi.sound.SoundHandle;
import de.lessvoid.nifty.tools.TimeProvider;

/**
 * This class maintains the NiftyGUI class and takes care for updating and
 * rendering it.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class GUI {
    /**
     * The primary nifty GUI instance that is used to control the rendering and
     * maintance of the entire GUI.
     */
    private final Nifty niftyGui;

    public GUI() {
        niftyGui =
            new Nifty(new IllarionRenderDevice(Graphics.getInstance()
                .getRenderDisplay(), new RenderImageFactory(
                new GuiImageFactory())), new SoundDevice() {
                    
                    @Override
                    public void update(int delta) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public SoundHandle loadSound(SoundSystem soundSystem, String filename) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public SoundHandle loadMusic(SoundSystem soundSystem, String filename) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                }, new IllarionInputSystem(),
                new TimeProvider());
    }
    
    public void prepare() {
        niftyGui.fromXmlWithoutStartScreen("illarion/client/gui/xml/gui.xml");
        Graphics.getInstance().getRenderManager().addTask(new GuiRenderTask(niftyGui));
    }
    
    public void showLogin() {
        niftyGui.gotoScreen("login");
    }
    
    public void shutdown() {
        niftyGui.exit();
    }
}

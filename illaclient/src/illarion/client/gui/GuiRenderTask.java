package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import illarion.graphics.RenderTask;

class GuiRenderTask implements RenderTask {

    private final Nifty gui;
    
    GuiRenderTask(final Nifty guiInstance) {
        gui = guiInstance;
    }
    
    @Override
    public boolean render(int delta) {
        gui.update();
        gui.render(true);
        
        return true;
    }

}

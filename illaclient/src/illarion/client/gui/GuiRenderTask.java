package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import illarion.graphics.RenderTask;

class GuiRenderTask implements RenderTask {
    
    GuiRenderTask() {
    }
    
    @Override
    public boolean render(int delta) {
        GUI.getInstance().render(true);
        return GUI.getInstance().getSelfRendering();
    }

}

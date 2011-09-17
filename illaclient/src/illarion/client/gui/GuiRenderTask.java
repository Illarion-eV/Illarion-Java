package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import illarion.graphics.Graphics;
import illarion.graphics.RenderTask;
import illarion.graphics.SpriteColor;

class GuiRenderTask implements RenderTask {
    
    GuiRenderTask() {
        color = Graphics.getInstance().getSpriteColor();
        color.set(1.f);
        color.setAlpha(1.f);
    }
    
    private final SpriteColor color;
    
    @Override
    public boolean render(int delta) {
        Graphics.getInstance().getDrawer().drawRectangle(40, 0, 50, 10, color);
        GUI.getInstance().render(false);
        Graphics.getInstance().getDrawer().drawRectangle(60, 0, 70, 10, color);
        return GUI.getInstance().getSelfRendering();
    }

}

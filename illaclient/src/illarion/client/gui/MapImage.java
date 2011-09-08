/**
 * 
 */
package illarion.client.gui;

import illarion.client.ClientWindow;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.world.Game;
import illarion.graphics.Graphics;
import illarion.graphics.RenderDisplay;
import illarion.graphics.SpriteColor;

import org.illarion.nifty.renderer.render.RenderImageFactory.DynamicImageSource;

/**
 * @author Martin Karing
 *
 */
public final class MapImage implements DynamicImageSource {

    /* (non-Javadoc)
     * @see org.illarion.nifty.renderer.render.RenderImageFactory.DynamicImageSource#getWidth()
     */
    @Override
    public int getWidth() {
        return ClientWindow.getInstance().getScreenWidth();
    }

    /* (non-Javadoc)
     * @see org.illarion.nifty.renderer.render.RenderImageFactory.DynamicImageSource#getHeight()
     */
    @Override
    public int getHeight() {
        return ClientWindow.getInstance().getScreenHeight();
    }

    /* (non-Javadoc)
     * @see org.illarion.nifty.renderer.render.RenderImageFactory.DynamicImageSource#renderImage(int, int, int, int, illarion.graphics.SpriteColor, float)
     */
    @Override
    public void renderImage(int x, int y, int width, int height,
        SpriteColor color, float imageScale) {
        Graphics.getInstance().getRenderDisplay().setAreaLimit(x, y, width, height);
        Game.getDisplay().render(Graphics.getInstance().getRenderManager().getCurrentDelta(), width, height);
        Graphics.getInstance().getRenderDisplay().unsetAreaLimit();
    }

    /* (non-Javadoc)
     * @see org.illarion.nifty.renderer.render.RenderImageFactory.DynamicImageSource#renderImage(int, int, int, int, int, int, int, int, illarion.graphics.SpriteColor, float, int, int)
     */
    @Override
    public void renderImage(int x, int y, int w, int h, int srcX, int srcY,
        int srcW, int srcH, SpriteColor color, float scale, int centerX,
        int centerY) {
        
        RenderDisplay display = Graphics.getInstance().getRenderDisplay();
        
        MapDisplayManager mapRender = Game.getDisplay();
        
        if (mapRender == null) {
            return;
        }
        
        display.setAreaLimit(x, y, w, h);
        display.applyOffset(-srcX, -srcY);
        mapRender.render(Graphics.getInstance().getRenderManager().getCurrentDelta(), w, h);
        display.resetOffset();
        display.unsetAreaLimit();
    }

}

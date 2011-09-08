package org.illarion.nifty.renderer.render;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

import org.illarion.nifty.renderer.render.RenderImageFactory.DynamicImageSource;

import de.lessvoid.nifty.tools.Color;

public class IllarionDynamicRenderImage implements IllarionRenderImage {

    /**
     * The sprite color that is used to transfer the nifty color data to the
     * illarion render environment.
     */
    private static final SpriteColor TEMP_COLOR;

    static {
        TEMP_COLOR = Graphics.getInstance().getSpriteColor();
    }
    
    private final DynamicImageSource internalImage;

    public IllarionDynamicRenderImage(DynamicImageSource image) {
        internalImage = image;
    }

    @Override
    public int getWidth() {
        return internalImage.getWidth();
    }

    @Override
    public int getHeight() {
        return internalImage.getHeight();
    }

    @Override
    public void dispose() {
        // nothing
    }

    @Override
    public void renderImage(int x, int y, int width, int height, Color color,
        float imageScale) {
        
        TEMP_COLOR.set(color.getRed(), color.getGreen(), color.getBlue());
        TEMP_COLOR.setAlpha(color.getAlpha());
        
        internalImage.renderImage(x, y, width, height, TEMP_COLOR, imageScale);
    }

    @Override
    public void renderImage(int x, int y, int w, int h, int srcX, int srcY,
        int srcW, int srcH, Color color, float scale, int centerX, int centerY) {
        
        TEMP_COLOR.set(color.getRed(), color.getGreen(), color.getBlue());
        TEMP_COLOR.setAlpha(color.getAlpha());
        
        internalImage.renderImage(x, y, w, h, srcX, srcY, srcW, srcH, TEMP_COLOR, scale, centerX, centerY);
    }

}

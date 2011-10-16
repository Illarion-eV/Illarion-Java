package org.illarion.nifty.renderer.render;

import illarion.graphics.common.TextureLoader;

import de.lessvoid.nifty.spi.render.RenderImage;

/**
 * This render image to link in the resources of Illarion to Nifty. It uses the
 * texture loader in order to fetch the images.
 * 
 * @author Martin Karing
 */
public class RenderImageIllarion implements RenderImage {
    private org.newdawn.slick.Image image;

    /**
     * Create a new RenderImage.
     * 
     * @param renderTools
     * @param name the name of the resource in the file system
     * @param filterParam use linear filter (true) or nearest filter (false)
     */
    public RenderImageIllarion(final String name, final boolean filterParam) {
        image = TextureLoader.getInstance().getTexture(name);
    }

    /**
     * Get width of image.
     * 
     * @return width
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * Get height of image.
     * 
     * @return height
     */
    public int getHeight() {
        return image.getHeight();
    }

    public org.newdawn.slick.Image getImage() {
        return image;
    }

    public void dispose() {

    }

}

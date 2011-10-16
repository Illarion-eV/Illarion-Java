package org.illarion.nifty.renderer.render;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.TextureImpl;

import de.lessvoid.nifty.render.BlendMode;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.Color;

/**
 * Lwjgl RenderDevice Implementation.
 * 
 * @author void
 */
public class RenderDeviceIllarion implements RenderDevice {
    private static Logger log = Logger.getLogger(RenderDeviceIllarion.class
        .getName());
    private static IntBuffer viewportBuffer = BufferUtils
        .createIntBuffer(4 * 4);
    private long time;
    private long frames;
    private long lastFrames;
    private boolean displayFPS = false;
    private boolean logFPS = false;
    private RenderFont fpsFont;

    // we keep track of which GL states we've already set to make sure we don't
    // set
    // the same state twice.
    private BlendMode currentBlendMode = null;

    private Graphics g;

    /**
     * Set the graphics instance this render device is supposed to use for all
     * rendering operations.
     * 
     * @param newG the graphics instance
     */
    public void updateGraphicInstance(final Graphics newG) {
        g = newG;
    }

    /**
     * The standard constructor. You'll use this in production code. Using this
     * constructor will configure the RenderDevice to not log FPS on System.out.
     */
    public RenderDeviceIllarion() {
        time = System.currentTimeMillis();
        frames = 0;
    }

    /**
     * The development mode constructor allows to display the FPS on screen when
     * the given flag is set to true. Note that setting displayFPS to false will
     * still log the FPS on System.out every couple of frames.
     * 
     * @param displayFPS
     */
    public RenderDeviceIllarion(final boolean displayFPS) {
        this();
        this.logFPS = true;
        this.displayFPS = displayFPS;
        if (this.displayFPS) {
            fpsFont = createFont("fps.fnt");
        }
    }

    /**
     * Get Width.
     * 
     * @return width of display mode
     */
    public int getWidth() {
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewportBuffer);
        return viewportBuffer.get(2);
    }

    /**
     * Get Height.
     * 
     * @return height of display mode
     */
    public int getHeight() {
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewportBuffer);
        return viewportBuffer.get(3);
    }

    public void beginFrame() {
        log.fine("beginFrame()");

        // set inital states for each frame
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setBlendMode(BlendMode.BLEND);

        g.clearClip();
    }

    public void endFrame() {
        log.fine("endFrame");
        frames++;
        long diff = System.currentTimeMillis() - time;
        if (diff >= 1000) {
            time += diff;
            lastFrames = frames;
            if (logFPS) {
                System.out.println("fps: " + frames);
            }
            frames = 0;
        }
        if (displayFPS) {
            renderFont(fpsFont, "FPS: " + String.valueOf(lastFrames), 10,
                getHeight() - fpsFont.getHeight() - 10, Color.WHITE, 1.0f);
        }
    }

    public void clear() {
        log.fine("clear()");

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Create a new RenderImage.
     * 
     * @param filename filename
     * @param filterLinear linear filter the image
     * @return RenderImage
     */
    public RenderImage createImage(final String filename,
        final boolean filterLinear) {
        return new RenderImageIllarion(filename, filterLinear);
    }

    /**
     * Create a new RenderFont.
     * 
     * @param filename filename
     * @return RenderFont
     */
    public RenderFont createFont(final String filename) {
        return new RenderFontIllarion(filename);
    }
    
    private final org.newdawn.slick.Color tempColor = new org.newdawn.slick.Color(0);

    /**
     * Render a quad.
     * 
     * @param x x
     * @param y y
     * @param width width
     * @param height height
     * @param color color
     */
    public void renderQuad(final int x, final int y, final int width,
        final int height, final Color color) {
        log.fine("renderQuad()");

        convertToSlickColor(color, tempColor);
        g.setColor(tempColor);
        g.fillRect(x, y, width, height);
    }

    public void renderQuad(final int x, final int y, final int width,
        final int height, final Color topLeft, final Color topRight,
        final Color bottomRight, final Color bottomLeft) {
        log.fine("renderQuad2()");

        TextureImpl.bindNone();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(topLeft.getRed(), topLeft.getGreen(),
            topLeft.getBlue(), topLeft.getAlpha());
        GL11.glVertex2i(x, y);
        GL11.glColor4f(topRight.getRed(), topRight.getGreen(),
            topRight.getBlue(), topRight.getAlpha());
        GL11.glVertex2i(x + width, y);
        GL11.glColor4f(bottomRight.getRed(), bottomRight.getGreen(),
            bottomRight.getBlue(), bottomRight.getAlpha());
        GL11.glVertex2i(x + width, y + height);
        GL11.glColor4f(bottomLeft.getRed(), bottomLeft.getGreen(),
            bottomLeft.getBlue(), bottomLeft.getAlpha());
        GL11.glVertex2i(x, y + height);
        GL11.glEnd();
    }

    /**
     * Render the image using the given Box to specify the render attributes.
     * 
     * @param x x
     * @param y y
     * @param width width
     * @param height height
     * @param color color
     * @param scale scale
     */
    public void renderImage(final RenderImage image, final int x, final int y,
        final int width, final int height, final Color color, final float scale) {
        log.fine("renderImage()");
        
        RenderImageIllarion lwjglImage = ((RenderImageIllarion) image);
        
        convertToSlickColor(color, tempColor);
        g.pushTransform();
        g.scale(scale, scale);
        g.drawImage(lwjglImage.getImage(), x, y, x + width, y + height, 0, 0, lwjglImage.getWidth(), lwjglImage.getHeight(), tempColor);
        g.popTransform();
    }

    /**
     * Render sub image.
     * 
     * @param x x
     * @param y y
     * @param w w
     * @param h h
     * @param srcX x
     * @param srcY y
     * @param srcW w
     * @param srcH h
     * @param color color
     */
    public void renderImage(final RenderImage image, final int x, final int y,
        final int w, final int h, final int srcX, final int srcY,
        final int srcW, final int srcH, final Color color, final float scale,
        final int centerX, final int centerY) {
        log.fine("renderImage2()");

        RenderImageIllarion lwjglImage = ((RenderImageIllarion) image);
        
        convertToSlickColor(color, tempColor);
        g.pushTransform();
        g.scale(scale, scale);
        g.drawImage(lwjglImage.getImage(), x, y, x + w, y + h, srcX, srcY, srcX + srcW, srcY + srcH, tempColor);
        g.popTransform();
    }

    /**
     * render the text.
     * 
     * @param text text
     * @param x x
     * @param y y
     * @param color color
     * @param fontSize size
     */
    public void renderFont(final RenderFont font, final String text,
        final int x, final int y, final Color color, final float fontSize) {
        log.fine("renderFont()");

        setBlendMode(BlendMode.BLEND);

        if (color == null) {
            if (isSupportedFontInstance(font)) {
                ((RenderFontIllarion) font).drawText(x, y, text, fontSize,
                    org.newdawn.slick.Color.white);
            }
        } else {
            if (isSupportedFontInstance(font)) {
                ((RenderFontIllarion) font).drawText(x, y, text, fontSize,
                    convertToSlickColor(color));
            }
        }
    }

    /**
     * Enable clipping to the given region.
     * 
     * @param x0 x0
     * @param y0 y0
     * @param x1 x1
     * @param y1 y1
     */
    public void enableClip(final int x0, final int y0, final int x1,
        final int y1) {
        log.fine("enableClip()");
        
        g.setClip(x0, y0, x1 - x0, y1 - y0);
    }

    /**
     * Disable Clip.
     */
    public void disableClip() {
        log.fine("disableClip()");

        g.clearClip();
    }

    public void setBlendMode(final BlendMode renderMode) {
        log.fine("setBlendMode()");

        if (renderMode.equals(currentBlendMode)) {
            return;
        }
        currentBlendMode = renderMode;
        if (currentBlendMode.equals(BlendMode.BLEND)) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        } else if (currentBlendMode.equals(BlendMode.MULIPLY)) {
            GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
        }
    }

    /**
     * Convert a Nifty color to a Slick color.
     * 
     * @param color nifty color
     * @return slick color
     */
    private static org.newdawn.slick.Color convertToSlickColor(
        final Color color) {
        
        final org.newdawn.slick.Color result = new org.newdawn.slick.Color(0);
        convertToSlickColor(color, result);
        return result;
    }

    /**
     * Convert a Nifty color to a Slick color.
     * 
     * @param color nifty color
     * @return slick color
     */
    private static void convertToSlickColor(final Color color,
        final org.newdawn.slick.Color targetColor) {
        if (color != null) {
            targetColor.r = color.getRed();
            targetColor.g = color.getGreen();
            targetColor.b = color.getBlue();
            targetColor.a = color.getAlpha();
        } else {
            targetColor.r = 1.f;
            targetColor.g = 1.f;
            targetColor.b = 1.f;
            targetColor.a = 1.f;
        }
    }

    private boolean isSupportedFontInstance(final RenderFont font) {
        return font instanceof RenderFontIllarion;
    }

    public MouseCursor createMouseCursor(final String filename,
        final int hotspotX, final int hotspotY) throws IOException {
        return new LwjglMouseCursor(filename, hotspotX, hotspotY);
    }

    public void enableMouseCursor(final MouseCursor mouseCursor) {
        ((LwjglMouseCursor) mouseCursor).enableCursor();
    }

    public void disableMouseCursor() {
        LwjglMouseCursor.disableCursor();
    }
}

package illarion.graphics.jogl.font;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.awt.TextRenderer;

import illarion.common.util.FastMath;
import illarion.graphics.Graphics;
import illarion.graphics.RenderDisplay;
import illarion.graphics.jogl.DriverSettingsJOGL;
import illarion.graphics.jogl.SpriteColorJOGL;

public class JavaFontJOGL implements FontJOGL {
    final Font data;
    final FontMetrics metrics;
    final TextRenderer renderer;

    public JavaFontJOGL(final Font font) {
        data = font;
        final BufferedImage tempImage =
            new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D gl2d = (Graphics2D) tempImage.getGraphics();
        gl2d.setFont(font);
        metrics = gl2d.getFontMetrics();
        renderer = new TextRenderer(font, true);
        renderer.setSmoothing(true);
        renderer.setUseVertexArrays(true);
    }

    @Override
    public int getWidth(String text) {
        /*
         * Fast method not working due moving characters when selecting
         */
        int length = 0;

        for (int i=0; i<text.length(); i++) {
          char currentCharacter = text.charAt(i);
          char nextCharacter = text.charAt(i);

          Integer w = getCharacterAdvance(currentCharacter, nextCharacter, 1.f);
          if (w != null) {
            length += w;
          }
        }
        return length;
        
        //return (int) FastMath.floor(renderer.getBounds(text).getWidth());
    }

    @Override
    public int getHeight() {
        return metrics.getHeight();
    }

    @Override
    public Integer getCharacterAdvance(char currentCharacter,
        char nextCharacter, float size) {
        GlyphVector vector =
            data.createGlyphVector(renderer.getFontRenderContext(),
                new char[] { currentCharacter, nextCharacter });
        return (int) ((vector.getGlyphPosition(1).getX() - vector
            .getGlyphPosition(0).getX()) * size);
    }

    @Override
    public void renderString(String text, int posX, int posY,
        SpriteColorJOGL color, float size) {

        final GL gl = GLU.getCurrentGL();
        if (!gl.isGL2ES1()) {
            return;
        }

        final GL2ES1 gl2 = gl.getGL2ES1();
        gl2.glPushMatrix();        
        RenderDisplay display = Graphics.getInstance().getRenderDisplay();
        DriverSettingsJOGL.getInstance().reset();

        renderer.beginRendering(display.getWidth(), display.getHeight());

        gl2.glPushMatrix();
        gl2.glScalef(size, size, 1.f);

        renderer.setColor(color.getRedf(), color.getGreenf(),
            color.getBluef(), color.getAlphaf());
        renderer.draw(text, posX, display.getHeight() - getHeight() + metrics.getDescent() - posY);

        gl2.glPopMatrix();

        renderer.endRendering();
        gl2.glPopMatrix();
    }

}

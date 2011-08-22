package illarion.graphics.jogl.font;

import illarion.graphics.FontData;
import illarion.graphics.FontData.GlyphData;
import illarion.graphics.jogl.SpriteColorJOGL;
import illarion.graphics.jogl.TextureJOGL;
import illarion.graphics.jogl.render.AbstractTextureRender;

public class TextureFontJOGL implements FontJOGL {
    private final FontData data;
    private final AbstractTextureRender texRender;

    public TextureFontJOGL(final FontData font) {
        data = font;
        texRender = AbstractTextureRender.getInstance();
    }

    @Override
    public int getWidth(final String text) {
        return data.getStringWidth(text);
    }

    @Override
    public int getHeight() {
        return data.getAscent() + data.getDescent();
    }

    @Override
    public Integer getCharacterAdvance(char currentCharacter,
        char nextCharacter, float size) {

        return data.getCharacterAdvance(currentCharacter, nextCharacter, size);
    }

    @Override
    public void renderString(String text, int posX, int posY,
        SpriteColorJOGL color, float size) {

        GlyphData glyph;
        GlyphData[] glyphes = new GlyphData[text.length()];
        int[] penPos = new int[text.length()];
        data.getGlyphes(text, 0, text.length(), size, glyphes, penPos);

        int penX = 0;

        for (int i = 0; i < text.length(); i++) {
            glyph = glyphes[i];
            final TextureJOGL texture = (TextureJOGL) glyph.getTexture();
            if (texture != null) {
                texRender.drawTexture(penX + glyph.getX(),
                    posY + glyph.getY(), 0.f, texture.getImageWidth() * size,
                    texture.getImageHeight() * size, texture, color, false,
                    0.f);
            }
        }
    }
}

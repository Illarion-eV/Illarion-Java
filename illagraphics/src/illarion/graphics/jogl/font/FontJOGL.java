/**
 * 
 */
package illarion.graphics.jogl.font;

import illarion.graphics.jogl.SpriteColorJOGL;

/**
 * @author Martin Karing
 *
 */
public interface FontJOGL {
    public int getWidth(String text);
    public int getHeight();
    public Integer getCharacterAdvance(char currentCharacter, char nextCharacter, float size);
    public void renderString(String text, int posX, int posY, SpriteColorJOGL color, float size);
}

/**
 * 
 */
package illarion.graphics.lwjgl.font;

import illarion.graphics.lwjgl.SpriteColorLWJGL;

/**
 * @author Martin Karing
 *
 */
public interface FontLWJGL {
    public int getWidth(String text);
    public int getHeight();
    public Integer getCharacterAdvance(char currentCharacter, char nextCharacter, float size);
    public void renderString(String text, int posX, int posY, SpriteColorLWJGL color, float size);
}

package illarion.client.gui;

import illarion.client.world.characters.CharacterAttribute;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface PlayerStatusGui {
    /**
     * Set a attribute to a new value.
     *
     * @param attribute the attribute to change
     * @param value the new value of the attribute
     */
    void setAttribute(@Nonnull CharacterAttribute attribute, int value);
}

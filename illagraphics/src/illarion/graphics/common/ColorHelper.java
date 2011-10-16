package illarion.graphics.common;

import org.newdawn.slick.Color;

public final class ColorHelper {
    /**
     * Private constructor to avoid the creation of any instances of this class.
     */
    private ColorHelper() {};
    
    public static float getLuminationf(final Color color) {
        return (color.r + color.g + color.b) / 3.f;
    }
}

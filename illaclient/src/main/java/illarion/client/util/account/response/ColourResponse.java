package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;
import org.illarion.engine.graphic.Color;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ColourResponse {
    @SerializedName("red")
    private int red;

    @SerializedName("green")
    private int green;

    @SerializedName("blue")
    private int blue;

    @SerializedName("alpha")
    private int alpha;

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public Color getColour() {
        return new Color(red, green, blue, alpha);
    }
}

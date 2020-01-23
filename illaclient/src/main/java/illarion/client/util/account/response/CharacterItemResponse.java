package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterItemResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("position")
    private int position;

    @SerializedName("number")
    private int number;

    @SerializedName("quality")
    private int quality;

    public int getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public int getNumber() {
        return number;
    }

    public int getQuality() {
        return quality;
    }
}

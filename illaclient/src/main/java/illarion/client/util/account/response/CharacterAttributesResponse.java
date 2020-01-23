package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterAttributesResponse {
    @SerializedName("agility")
    private int agility;

    @SerializedName("constitution")
    private int constitution;

    @SerializedName("dexterity")
    private int dexterity;

    @SerializedName("essence")
    private int essence;

    @SerializedName("intelligence")
    private int intelligence;

    @SerializedName("perception")
    private int perception;

    @SerializedName("strength")
    private int strength;

    @SerializedName("willpower")
    private int willpower;
}

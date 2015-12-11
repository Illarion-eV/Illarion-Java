package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AttributesCreationResponse {
    @SerializedName("age")
    private MinMaxResponse age;

    @SerializedName("weight")
    private MinMaxResponse weight;

    @SerializedName("height")
    private MinMaxResponse height;

    @SerializedName("agility")
    private MinMaxResponse agility;

    @SerializedName("constitution")
    private MinMaxResponse constitution;

    @SerializedName("dexterity")
    private MinMaxResponse dexterity;

    @SerializedName("essence")
    private MinMaxResponse essence;

    @SerializedName("intelligence")
    private MinMaxResponse intelligence;

    @SerializedName("perception")
    private MinMaxResponse perception;

    @SerializedName("strength")
    private MinMaxResponse strength;

    @SerializedName("willpower")
    private MinMaxResponse willpower;

    @SerializedName("totalAttributePoints")
    private int totalAttributePoints;
}

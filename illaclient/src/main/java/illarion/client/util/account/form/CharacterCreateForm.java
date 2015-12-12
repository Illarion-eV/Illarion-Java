package illarion.client.util.account.form;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterCreateForm {
    @SerializedName("name")
    @Nonnull
    private String name;
    
    @SerializedName("race")
    private int race;

    @SerializedName("sex")
    private int sex;

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

    @SerializedName("startPack")
    private int startPack;

    @SerializedName("hairId")
    private int hairId;

    @SerializedName("beardId")
    private int beardId;

    @SerializedName("email")
    private int skinColorRed;

    @SerializedName("skinColorGreen")
    private int skinColorGreen;

    @SerializedName("skinColorBlue")
    private int skinColorBlue;

    @SerializedName("skinColorAlpha")
    private int skinColorAlpha;

    @SerializedName("hairColorRed")
    private int hairColorRed;

    @SerializedName("hairColorGreen")
    private int hairColorGreen;

    @SerializedName("hairColorBlue")
    private int hairColorBlue;

    @SerializedName("hairColorAlpha")
    private int hairColorAlpha;
}

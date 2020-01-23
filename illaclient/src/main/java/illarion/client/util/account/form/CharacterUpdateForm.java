package illarion.client.util.account.form;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterUpdateForm {
    @SerializedName("descriptionDe")
    @Nullable
    private String descriptionDe;

    @SerializedName("descriptionEn")
    @Nullable
    private String descriptionEn;

    @SerializedName("storyDe")
    @Nullable
    private String storyDe;

    @SerializedName("storyEn")
    @Nullable
    private String storyEn;

    @SerializedName("picture")
    @Nullable
    private Object picture;
}

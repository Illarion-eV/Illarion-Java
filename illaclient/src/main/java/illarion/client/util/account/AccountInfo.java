package illarion.client.util.account;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * This is the deserialization object for a response of account information.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountInfo {
    @SerializedName("name")
    private String name;

    @SerializedName("state")
    private int state;

    @SerializedName("maxChars")
    private int maximalCharacters;

    @SerializedName("lang")
    private String language;

    @SerializedName("chars")
    private Map<String, List<CharacterListEntry>> chars;

    @SerializedName("create")
    private List<CreateCharacterRoute> createRoutes;
}

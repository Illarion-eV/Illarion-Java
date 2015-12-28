package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This is the deserialization object for a response of account information.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountGetResponse {
    @Nullable
    @SerializedName("name")
    private String name;

    @SerializedName("state")
    private int state;

    @SerializedName("maxChars")
    private int maximalCharacters;

    @Nullable
    @SerializedName("lang")
    private String language;

    @Nullable
    @SerializedName("chars")
    private List<AccountGetCharsResponse> chars;

    @Nullable
    @SerializedName("create")
    private List<AccountGetCreateResponse> createRoutes;

    @Nonnull
    public String getName() {
        assert name != null;

        return name;
    }

    public int getState() {
        return state;
    }

    public int getMaximalCharacters() {
        return maximalCharacters;
    }

    @Nonnull
    public Locale getLanguage() {
        return "de".equals(language) ? Locale.GERMAN : Locale.ENGLISH;
    }

    @Nonnull
    public List<AccountGetCharsResponse> getChars() {
        assert chars != null;

        return Collections.unmodifiableList(chars);
    }

    @Nonnull
    public List<AccountGetCreateResponse> getCreateRoutes() {
        assert createRoutes != null;

        return Collections.unmodifiableList(createRoutes);
    }
}

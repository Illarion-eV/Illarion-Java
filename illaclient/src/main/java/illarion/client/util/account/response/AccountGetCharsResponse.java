package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountGetCharsResponse {
    @Nullable
    @SerializedName("id")
    private String id;

    @Nullable
    @SerializedName("name")
    private String name;

    @Nullable
    @SerializedName("list")
    private List<AccountGetCharResponse> list;

    @Nonnull
    public String getId() {
        assert id != null;

        return id;
    }

    @Nonnull
    public String getName() {
        assert name != null;

        return name;
    }

    @Nonnull
    public List<AccountGetCharResponse> getList() {
        assert list != null;

        return Collections.unmodifiableList(list);
    }
}

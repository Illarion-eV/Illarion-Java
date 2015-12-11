package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountGetCharsResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("list")
    private List<AccountGetCharResponse> list;
}

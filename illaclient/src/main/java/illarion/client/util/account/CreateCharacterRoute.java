package illarion.client.util.account;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CreateCharacterRoute {
    @SerializedName("route")
    private String route;

    @SerializedName("name")
    private String name;
}

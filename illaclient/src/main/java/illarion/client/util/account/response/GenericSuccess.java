package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GenericSuccess {
    @SerializedName("success")
    private GenericDetails success;
}

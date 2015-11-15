package illarion.client.util.account;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterListEntry {
    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private String status;

    @SerializedName("race")
    private String raceId;

    @SerializedName("sex")
    private String typeId;

    @SerializedName("lastSaveTime")
    private Date lastSaveTime;

    @SerializedName("onlineTime")
    private String onlineTime;
}

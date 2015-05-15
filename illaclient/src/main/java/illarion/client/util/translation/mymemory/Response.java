/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.util.translation.mymemory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class Response {
    @SerializedName("responseData")
    private ResponseData responseData;
    @SerializedName("responseDetails")
    private String responseDetails;
    @SerializedName("responseStatus")
    private int responseStatus;
    @SerializedName("responderId")
    private int responderId;
    @SerializedName("matches")
    private List<Match> matches;

    public ResponseData getResponseData() {
        return responseData;
    }

    public String getResponseDetails() {
        return responseDetails;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public int getResponderId() {
        return responderId;
    }
}

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

import java.util.Date;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class Match {
    @SerializedName("id")
    private long id;
    @SerializedName("segment")
    private String segment;
    @SerializedName("translation")
    private String translation;
    @SerializedName("quality")
    private int quality;
    @SerializedName("reference")
    private String reference;
    @SerializedName("usage-count")
    private int usageCount;
    @SerializedName("subject")
    private String subject;
    @SerializedName("created-by")
    private String createdBy;
    @SerializedName("last-updated-by")
    private String lastUpdateBy;
    @SerializedName("create-date")
    private Date createDate;
    @SerializedName("last-update-date")
    private Date lastUpdateDate;
    @SerializedName("tm_properties")
    private String tmProperties;
    @SerializedName("match")
    private double match;
}

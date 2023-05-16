package com.aknown389.dm.models.global

import com.google.gson.annotations.SerializedName

data class PostReactions(
    @SerializedName("Angry") var Angry: Int? = null,
    @SerializedName("Happy") var Happy: Int? = null,
    @SerializedName("Like") var Like: Int? = null,
    @SerializedName("Love") var Love: Int? = null,
    @SerializedName("Sad") var Sad: Int? = null,
    @SerializedName("Wow") var Wow: Int? = null
)
package com.aknown389.dm.models.news

import com.aknown389.dm.db.local.NewsDataEntities
import com.google.gson.annotations.SerializedName

data class NewsArticlesModels(
    @SerializedName("data")                 val `data`: List<NewsDataEntities>,
    @SerializedName("message")              val message: String? = null,
    @SerializedName("status")               val status: Boolean? = null,
    @SerializedName("status_code")          val status_code: Int? = null,
    @SerializedName("hasMorePage")          val hasMorePage: Boolean? = null
)
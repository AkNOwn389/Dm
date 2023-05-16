package com.aknown389.dm.models.homepostmodels


data class FeedResponseModelV2(
    val `data`: List<PostDataModel>,
    val hasMorePage: Boolean,
    val nextPageKey:Int,
    val message: String,
    val status: Boolean,
    val status_code: Int
)
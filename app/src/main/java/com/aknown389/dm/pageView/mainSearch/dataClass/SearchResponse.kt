package com.aknown389.dm.pageView.mainSearch.dataClass


data class SearchResponse(
    val `data`: List<MainSearchItemData>,
    val hasMorePage: Boolean,
    val message: String,
    val status: Boolean,
    val status_code: Int
)
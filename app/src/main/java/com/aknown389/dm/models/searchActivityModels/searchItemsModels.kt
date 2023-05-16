package com.aknown389.dm.models.searchActivityModels

data class searchItemsModels(
    val `data`: List<Data>,
    val hasMorePage: Boolean,
    val message: String,
    val status: Boolean,
    val status_code: Int
)
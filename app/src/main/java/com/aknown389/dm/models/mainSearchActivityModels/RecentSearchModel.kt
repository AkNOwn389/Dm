package com.aknown389.dm.models.mainSearchActivityModels

data class RecentSearchModel(
    val `data`: List<DataX>,
    val message: String,
    val status: Boolean,
    val status_code: Int
)
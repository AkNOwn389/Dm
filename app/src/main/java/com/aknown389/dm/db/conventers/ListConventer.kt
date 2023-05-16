package com.aknown389.dm.db.conventers

import androidx.room.TypeConverter
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.models.global.VideoUrl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConventer {
    @TypeConverter
    fun fromImageUrlList(value: List<ImageUrl?>?): String? {
        if (value.isNullOrEmpty()){
            return null
        }
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toImageUrlList(value: String?): List<ImageUrl?>? {
        if (value.isNullOrEmpty()){
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<ImageUrl>>() {}.type
        return gson.fromJson(value, type)
    }
    @TypeConverter
    fun fromVideoUrlList(value: List<VideoUrl?>?): String? {
        if (value.isNullOrEmpty()){
            return null
        }
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toVideoUrlList(value: String?): List<VideoUrl?>? {
        if (value.isNullOrEmpty()){
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<VideoUrl>>() {}.type
        return gson.fromJson(value, type)
    }
    @TypeConverter
    fun fromPostReactions(value:PostReactions?): String? {
        if (value == null){
            return null
        }
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPostReactions(value: String?): PostReactions? {
        if (value == null){
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<PostReactions>() {}.type
        return gson.fromJson(value, type)
    }
}
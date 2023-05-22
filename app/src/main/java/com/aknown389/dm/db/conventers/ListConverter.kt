package com.aknown389.dm.db.conventers

import androidx.room.TypeConverter
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.models.global.VideoUrl
import com.aknown389.dm.models.loginRegModels.Info
import com.aknown389.dm.models.loginRegModels.Token
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {
    @TypeConverter
    fun fromToken(value:Token?):String?{
        if (value == null){
            return null
        }
        val gson = Gson()
        return gson.toJson(value)
    }
    @TypeConverter
    fun toToken(value: String?):Token?{
        if (value == null){
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<Token>() {}.type
        return gson.fromJson(value, type)
    }
    @TypeConverter
    fun fromInfo(value:Info?):String?{
        if (value == null){
            return null
        }
        val gson = Gson()
        return gson.toJson(value)
    }
    @TypeConverter
    fun toInfo(value:String?):Info?{
        if (value == null){
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<Info>() {}.type
        return gson.fromJson(value, type)
    }
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
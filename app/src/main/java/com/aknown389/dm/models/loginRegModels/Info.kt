package com.aknown389.dm.models.loginRegModels

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Info(
    @PrimaryKey                         val id:String,
    @SerializedName("bgimg")            val backgroundImage: String? = null,
    @SerializedName("bio")              val bio: String? = null,
    @SerializedName("gender")           val gender: String? = null,
    @SerializedName("hobby")            val hobby: List<Any?>? = null,
    @SerializedName("interested")       val interested: String? = null,
    @SerializedName("location")         val location: String? = null,
    @SerializedName("name")             var name: String? = null,
    @SerializedName("profileimg")       var profileimg: String? = null,
    @SerializedName("school")           val school: String? = null,
    @SerializedName("user")             val user: String,
    @SerializedName("works")            val works: String? = null
)
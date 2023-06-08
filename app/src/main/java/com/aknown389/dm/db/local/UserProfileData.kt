package com.aknown389.dm.db.local

import com.google.gson.annotations.SerializedName

data class UserProfileData(
    @SerializedName("id")               val id: Int,
    @SerializedName("bgimg")            val backgroundImage: String? =                      null,
    @SerializedName("bio")              val bio: String? =                                  null,
    @SerializedName("gender")           val gender: String? =                               null,
    @SerializedName("hobby")            val hobby: List<Any?>? =                            null,
    @SerializedName("interested")       val interested: String? =                           null,
    @SerializedName("location")         val location: String? =                             null,
    @SerializedName("name")             var name: String? =                                 null,
    @SerializedName("profileimg")       var profileImage: String? =                         null,
    @SerializedName("school")           val school: String? =                               null,
    @SerializedName("user")             val user: String? =                                 null,
    @SerializedName("works")            val works: String? =                                null,
    @SerializedName("email")            val email: String? =                                null,
    @SerializedName("first_name")       val firstName: String? =                            null,
    @SerializedName("followers")        val followers: Int? =                               null,
    @SerializedName("following")        val following: Int? =                               null,
    @SerializedName("last_name")        val last_name: String? =                            null,
    @SerializedName("username")         val username: String? =                             null,
    @SerializedName("post_lenght")      val post_lenght: Int? =                             null,
)
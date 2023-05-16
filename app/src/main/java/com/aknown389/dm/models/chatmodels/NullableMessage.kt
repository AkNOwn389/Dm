package com.aknown389.dm.models.chatmodels

data class NullableMessage (
    var id: Int? = null,
    var sender_full_name: String? = null,
    var receiver_full_name: String? = null,
    val message_type:Int? = null,
    var username: String? = null,
    val user_full_name:String? = null,
    val user_avatar: String? = null,
    var date_time: String? = null,
    var message_body: String? = null,
    var receiver: String? = null,
    var seen: Boolean? = null,
    var me:Boolean? = null,
    var sender: String? = null,
)
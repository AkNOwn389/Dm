package com.aknown389.dm.models.chatmodels

import android.os.Parcel
import android.os.Parcelable
data class SafeArgsUserChat(
    val username: String?,
    val user_full_name: String?,
    val user_avatar: String?,
    val sender_full_name: String?,
    val receiver_full_name: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(user_full_name)
        parcel.writeString(user_avatar)
        parcel.writeString(sender_full_name)
        parcel.writeString(receiver_full_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SafeArgsUserChat> {
        override fun createFromParcel(parcel: Parcel): SafeArgsUserChat {
            return SafeArgsUserChat(parcel)
        }

        override fun newArray(size: Int): Array<SafeArgsUserChat?> {
            return arrayOfNulls(size)
        }
    }
}

package com.aknown389.dm.pageView.chating

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R

class UserChatViewHolder(view:View):ViewHolder(view) {
    @JvmField
    var messageBody: TextView? = null
    @JvmField
    var messageBodyPartner: TextView? = null
    @JvmField
    var messageImage2:ImageView? = null

    init {
        messageBody = itemView.findViewById(R.id.messageItemBody)
        messageBodyPartner = itemView.findViewById(R.id.messageItemBodyForChatMate)
        messageImage2 = itemView.findViewById(R.id.chatmessageitem2userimage)
    }
}
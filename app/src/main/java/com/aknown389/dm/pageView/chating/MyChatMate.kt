package com.aknown389.dm.pageView.chating

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.aknown389.dm.R
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.models.chatmodels.NullableMessage
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyChatMate(
    private val adapter: UserChatAdapter,
    private val messagelist: ArrayList<NullableMessage>,
    private val holder: UserChatViewHolder,
    private val data: NullableMessage,
    private val context: Context,
    private val parent: ViewGroup,
    private val token:String
) {


    init {
        setUI()
        setListener()
    }

    private fun setUI() {
        holder.messageBodyPartner?.text = data.message_body
        Glide.with(context)
            .load(data.user_avatar)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.messageImage2!!)
    }

    private fun setListener() {
        holder.messageBodyPartner?.setOnLongClickListener {
            val dialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.chat_user_dialog, parent, false)
            //dialog.setContentView(view)

            val deleteBtn: ImageButton? = view.findViewById(R.id.messageDelete)
            val share: ImageButton? = view.findViewById(R.id.messageShare)
            val reply: ImageButton? = view.findViewById(R.id.messageReply)
            val archive: ImageButton? = view.findViewById(R.id.messageArchive)
            true
        }
        holder.messageImage2?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", data.username)
                it.startActivity(intent)
            }
        }
    }
}
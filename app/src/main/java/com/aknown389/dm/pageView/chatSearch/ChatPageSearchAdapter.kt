package com.aknown389.dm.pageView.chatSearch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.activities.UserChatingActivity
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.models.searchActivityModels.Data
import com.bumptech.glide.Glide

class ChatPageSearchAdapter(private val users:MutableList<Data>):RecyclerView.Adapter<ChatPageSearchAdapter.ViewHolder>() {
    private lateinit var context:Context
    private lateinit var chatUserFragment: UserChatingActivity


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val usersItemRoot:ConstraintLayout? = view.findViewById(R.id.searchUserRootOnChat)
        val userName:TextView? = view.findViewById(R.id.search_user_on_chat_name)
        val userlocation:TextView? = view.findViewById(R.id.search_user_on_chat_location)
        val userimage:ImageView? = view.findViewById(R.id.search_user_on_chat_userImage)
        val activetag:TextView? = view.findViewById(R.id.search_user_on_chat_active)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_searchs_user_on_chat, parent, false))
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = users[position]
        setUI(holder, item)
        setListener(holder, item)
        chatUserFragment = UserChatingActivity()
    }

    private fun setListener(holder: ViewHolder, item: Data) {
        holder.usersItemRoot?.setOnClickListener {
            (context as AppCompatActivity).let {
                val intent = Intent(it, UserChatingActivity::class.java)
                intent.putExtra("username", item.username)
                intent.putExtra("userAvatar", item.profileimg)
                intent.putExtra("name", item.name)
                it.startActivity(intent)
            }
        }
        holder.userimage?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", item.username)
                it.startActivity(intent)
            }
        }
        holder.userName?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", item.username)
                it.startActivity(intent)
            }
        }
    }

    private fun setUI(holder: ViewHolder, item: Data) {
        holder.userName?.text = item.name
        holder.userlocation?.text = item.location
        Glide.with(context)
            .load(item.profileimg)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.userimage!!)
    }
}
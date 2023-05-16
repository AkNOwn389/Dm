package com.aknown389.dm.pageView.chatPage

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.activities.ChatPageSearchUserActivity
import com.aknown389.dm.activities.UserChatingActivity
import com.aknown389.dm.models.chatmodels.NullableMessage
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChatHomePageItemAdapter(private val messages:ArrayList<NullableMessage>): RecyclerView.Adapter<ChatHomePageItemAdapter.MyViewHolder>() {
    private lateinit var context: Context
    private lateinit var chatUserFragment: UserChatingActivity
    private lateinit var parent: ViewGroup

    inner class MyViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        val itemusername:TextView? = itemview.findViewById(R.id.chatuseritemName)
        val chatBody:TextView? = itemview.findViewById(R.id.chatuseritemMesssage)
        val userImage:ImageView? = itemview.findViewById(R.id.chatuseritemAvatar)
        val chatItemRoot:ConstraintLayout? = itemview.findViewById(R.id.chatUserItemRoot)
        val searchBar:ConstraintLayout? = itemview.findViewById(R.id.chatpageitem0searchbtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context = parent.context
        this.parent = parent
        return when(viewType){
            99999 -> MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_page_search_bar, parent, false))
            1 -> MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_user_item, parent, false))
            else -> MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_user_item, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].message_type!!
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemData = messages[position]
        when(itemData.message_type){
            99999 -> loadSearchbar(holder)
            1-> loadUserChat(itemData, holder)
        }

        chatUserFragment = UserChatingActivity()
    }

    private fun loadUserChat(itemData: NullableMessage, holder: MyViewHolder) {
        setItemData(itemData, holder)
        setListner(itemData, holder)
    }

    private fun loadSearchbar(holder: MyViewHolder) {
        holder.searchBar?.setOnClickListener {
            (context as AppCompatActivity).let {
                val intent = Intent(it, ChatPageSearchUserActivity::class.java)
                it.startActivity(intent)
            }
        }
    }

    private fun setListner(itemData: NullableMessage, holder: MyViewHolder) {
        holder.chatItemRoot?.setOnClickListener {
            /*
            val username = itemData.username
            val userFullName = itemData.user_full_name
            val userAvatar = itemData.user_avatar
            val senderFullName = itemData.sender_full_name
            val receiverFullName = itemData.receiver_full_name
            val bundle = Bundle()
            val data = SafeArgsUserChat(username, userFullName, userAvatar, senderFullName, receiverFullName)
            bundle.putParcelable("user", data)
            chatUserFragment.arguments = bundle
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right)
                replace(R.id.ChatflFragmentContainer, chatUserFragment)
                commit()
                addToBackStack(null)
            }

             */
            (context as AppCompatActivity).let {
                val intent = Intent(it, UserChatingActivity::class.java)
                intent.putExtra("username", itemData.username)
                intent.putExtra("userAvatar", itemData.user_avatar)
                intent.putExtra("name", itemData.user_full_name)
                it.startActivity(intent)
            }
        }
        holder.chatItemRoot?.setOnLongClickListener {
            val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
            val view:View = LayoutInflater.from(context).inflate(R.layout.dialog_item_chat_page, parent, false)
            val delete:TextView? = view.findViewById(R.id.chatpageitemdialogdelete)
            dialog.setContentView(view)
            dialog.show()
            delete?.setOnClickListener {
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            true
        }
    }

    private fun setItemData(itemData: NullableMessage, holder: MyViewHolder) {
        if(
            itemData.user_full_name != "" && itemData.user_full_name!!.isNotEmpty()
        ){
            holder.itemusername?.text = itemData.user_full_name
        }else{
            holder.itemusername?.text = itemData.username
        }
        val option = RequestOptions().placeholder(R.mipmap.greybg)
        if (itemData.message_body != "" && itemData.message_body!!.isNotEmpty()){
            holder.chatBody?.text = itemData.message_body
        }
        holder.userImage?.let {
            Glide.with(context)
                .load(itemData.user_avatar)
                .apply(option)
                .into(it)
        }
    }
}
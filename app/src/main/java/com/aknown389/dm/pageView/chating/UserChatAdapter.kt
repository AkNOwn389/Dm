package com.aknown389.dm.pageView.chating

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.api.retroInstance.ChatInstance
import com.aknown389.dm.models.chatmodels.NullableMessage
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserChatAdapter(private val messagelist: ArrayList<NullableMessage>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context:Context
    private lateinit var token:String
    private lateinit var manager:DataManager
    private lateinit var parent: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatViewHolder {
        this.context = parent.context
        this.manager = DataManager(parent.context)
        this.parent = parent
        return when(viewType){
            1 -> UserChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_my_message_on_chat, parent, false))
            2 -> UserChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_my_chatmate_on_chat, parent, false))
            else -> UserChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_my_message_on_chat, parent, false))
        }
    }
    override fun getItemCount(): Int {
        return messagelist.size
    }
    override fun getItemViewType(position: Int): Int {
        return if (messagelist[position].me!!){
            1
        }else{
            2
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        this.token = manager.getAccessToken().toString()
        val data = messagelist[position]
        reportSeen(data)
        when(messagelist[position].me!!){
            true -> MyMessageView(
                holder = holder as UserChatViewHolder,
                data = data,
                adapter = this,
                parent = parent,
                token = token,
                messagelist = messagelist,
                context = context
            )
            false -> MyChatMate(
                holder = holder as UserChatViewHolder,
                data = data,
                adapter = this,
                parent = parent,
                token = token,
                messagelist = messagelist,
                context = context
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun reportSeen(data: NullableMessage) {
        if (data.me == true){
            return
        }
        if (data.seen == true){
            return
        }
        GlobalScope.launch(Dispatchers.Main) {
            val response = try {
                ChatInstance.api.seen(token = token, id = data.id!!)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                if (response.body()!!.status){
                    return@launch
                }
            }
        }
    }
}
package com.aknown389.dm.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.api.retroInstance.ChatInstance
import com.aknown389.dm.databinding.FragmentChatHomeNavBinding
import com.aknown389.dm.pageView.chatPage.ChatHomePageItemAdapter
import com.aknown389.dm.models.chatmodels.ChatListenerResponse
import com.aknown389.dm.models.chatmodels.NullableMessage
import com.aknown389.dm.models.chatmodels.typeInterfaceChatHome
import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.NetInterceptor
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import kotlin.Exception

class ChatHomeNav : Fragment() {
    private var binding: FragmentChatHomeNavBinding? = null
    private lateinit var adapter: ChatHomePageItemAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var chatItemList = ArrayList<NullableMessage>()
    private lateinit var context:Context
    private lateinit var token: String
    private lateinit var manager:DataManager
    private lateinit var client: OkHttpClient
    private lateinit var websocket: WebSocket
    private var isLoading = false
    private var hasMorePage = true
    private var isClose = true
    private var ready = false
    private var isSocketReady = false
    private var page = 1
    private fun connectToSocket(){
        this.client = OkHttpClient.Builder()
            .addInterceptor(NetInterceptor())
            .readTimeout(0, TimeUnit.SECONDS)
            .build()
        val request: Request = Request.Builder()
            .url(Constants.WEBSOCKET_BASE_URL+ CHAT_PAGEVIEW_URL)
            .addHeader("Authorization", token)
            .build()
        try {
            this.websocket = client.newWebSocket(request, Listener())
            this.isClose = false
            this.isSocketReady = true
        }catch (e:Exception){
            this.isClose = true
            e.printStackTrace()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.context = requireContext()
        this.binding = FragmentChatHomeNavBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager = DataManager(context)
        token = manager.getAccessToken().toString()
        layoutManager = LinearLayoutManager(context)
        setVal()
        refreshItem()
        setListener()
    }

    private fun setVal() {
        val searchBar = NullableMessage(message_type = 99999)
        adapter = ChatHomePageItemAdapter(chatItemList)
        binding?.rvChatHomeadapter!!.adapter = adapter
        binding?.rvChatHomeadapter!!.layoutManager = layoutManager
        if (searchBar !in chatItemList){
            chatItemList.add(0, searchBar)
            adapter.notifyItemInserted(0)
        }

    }

    private fun setListener() {
        binding?.rvChatHomeadapter!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val visibleItemCount: Int = layoutManager.childCount
                val pastVisibleItem: Int = layoutManager.findFirstCompletelyVisibleItemPosition()

                if (!isLoading){
                    if (visibleItemCount + pastVisibleItem > adapter.itemCount-1){
                        if (!isLoading && hasMorePage){
                            updateItem()
                        }
                    }
                }
            }
        })
        socketListener()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        socketListener()
        super.onResume()
    }

    private fun socketListener() {
        lifecycleScope.launch(Dispatchers.IO) {
            while (true){
                if (isActive){
                    if (this@ChatHomeNav.isClose && this@ChatHomeNav.ready){
                        connectToSocket()
                        delay(500)
                    }else{
                        if (!this@ChatHomeNav.isClose){
                            websocket.send("{\"type\":1}")
                        }
                        delay(2000)
                    }
                }else{
                    return@launch
                }
            }
        }
    }

    private fun updateItem() {
        this.page+=1
        this.isLoading = true
        lifecycleScope.launch(Dispatchers.Main) {
            val response = try {
                ChatInstance.api.getMessagePageView(token = token, page = page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                if (!response.body()!!.hasMorePage){
                    this@ChatHomeNav.hasMorePage = false
                }
                for (i in response.body()!!.data){
                    var go = true
                    for (x in chatItemList){
                        if (i.id == x.id){
                            go = false
                        }
                    }
                    val data = NullableMessage(
                        id = i.id,
                        sender_full_name = i.sender_full_name,
                        receiver_full_name = i.receiver_full_name,
                        message_type = i.message_type,
                        username = i.username,
                        user_full_name = i.user_full_name,
                        user_avatar = i.user_avatar,
                        date_time = i.date_time,
                        message_body = i.message_body,
                        receiver = i.receiver,
                        seen = i.seen,
                        me = i.me,
                        sender = i.sender
                    )
                    if (go){
                        this@ChatHomeNav.chatItemList.add(data)
                        this@ChatHomeNav.adapter.notifyItemInserted(chatItemList.size-1)
                    }
                }
            }
            this@ChatHomeNav.isLoading = false
        }
    }


    private fun refreshItem(){
        this.page = 1
        this.hasMorePage = true
        this.isLoading = true

        lifecycleScope.launch(Dispatchers.Main) {
            val response = try {
                ChatInstance.api.getMessagePageView(token, page)
            }catch (e:Exception){
                e.stackTrace
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                if (!response.body()!!.hasMorePage){
                    this@ChatHomeNav.hasMorePage = false
                }
                for (i in response.body()!!.data){
                    var go = true
                    for (x in chatItemList){
                        if (i.id == x.id){
                            go = false
                        }
                    }
                    val data = NullableMessage(
                        id = i.id,
                        sender_full_name = i.sender_full_name,
                        receiver_full_name = i.receiver_full_name,
                        message_type = i.message_type,
                        username = i.username,
                        user_full_name = i.user_full_name,
                        user_avatar = i.user_avatar,
                        date_time = i.date_time,
                        message_body = i.message_body,
                        receiver = i.receiver,
                        seen = i.seen,
                        me = i.me,
                        sender = i.sender
                    )
                    if (go){
                        this@ChatHomeNav.chatItemList.add(data)
                        this@ChatHomeNav.adapter.notifyItemInserted(chatItemList.size-1)
                    }
                }
            }
            this@ChatHomeNav.ready = true
            this@ChatHomeNav.isLoading = false
        }
    }
    inner class Listener:WebSocketListener(){
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            this@ChatHomeNav.isClose = true
            Log.d(TAG, t.message.toString())
            super.onFailure(webSocket, t, response)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            this@ChatHomeNav.isClose = false
            super.onOpen(webSocket, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val gson = Gson()
            val interFace = gson.fromJson(text, typeInterfaceChatHome::class.java)
            if (interFace.type != null){
                when(interFace.type){
                    1 -> {
                        this@ChatHomeNav.messageSynce(text)
                    }
                }
            }
            super.onMessage(webSocket, text)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            this@ChatHomeNav.isClose = true
            super.onClosed(webSocket, code, reason)
        }
    }

    private fun messageSynce(text: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            Log.d(TAG, "this")
            val gson = Gson()
            val data = gson.fromJson(text, ChatListenerResponse::class.java)
            for (i in data.data!!) {
                Log.d(TAG, i.toString())
                var go = true
                for (x in chatItemList) {
                    Log.d(TAG, manager.getUserData()!!.user)
                    if (i.sender == manager.getUserData()!!.user) {
                        if (i.receiver == x.receiver || i.receiver == x.sender) {
                            if (i.id != x.id){
                                val pos = chatItemList.indexOf(x)
                                chatItemList[pos] = i
                                adapter.notifyItemChanged(pos)
                                Log.d(TAG, "notify item change")
                                go = false
                            }
                        }
                    } else if (i.receiver == manager.getUserData()!!.user) {
                        if (i.sender == x.receiver || i.sender == x.sender) {
                            if (i.id != x.id){
                                val pos = chatItemList.indexOf(x)
                                chatItemList[pos] = i
                                adapter.notifyItemChanged(pos)
                                Log.d(TAG, "notify item change")
                                go = false
                            }
                        }
                    }
                    if (i.id == x.id) {
                        go = false
                    }
                }
                if (go) {
                    Log.d(TAG, "Update item")
                    chatItemList.add(i)
                    adapter.notifyItemInserted(chatItemList.size - 1)
                }
            }
        }
    }

    companion object{
        const val CHAT_PAGEVIEW_URL = "/ws/chatPage"
        const val TAG = "CHATPAGE"
    }
}
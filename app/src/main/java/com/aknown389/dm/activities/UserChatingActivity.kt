package com.aknown389.dm.activities

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityUserChatingBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.aknown389.dm.pageView.chating.TypingData
import com.aknown389.dm.pageView.chating.UserChatAdapter
import com.aknown389.dm.api.retroInstance.ChatInstance
import com.aknown389.dm.models.chatmodels.ChatUserInfo
import com.aknown389.dm.models.chatmodels.HandShake
import com.aknown389.dm.models.chatmodels.NullableMessage
import com.aknown389.dm.models.chatmodels.newMessage
import com.aknown389.dm.models.chatmodels.typeInterface
import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.NetInterceptor
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit


class UserChatingActivity: AppCompatActivity() {
    private var binding: ActivityUserChatingBinding? = null
    private lateinit var chatMessageEditTextView: TextView
    private lateinit var swiper:SwipeRefreshLayout
    private lateinit var onback: ImageButton
    private lateinit var sendPhotoCardView:CardView
    private lateinit var capturePhotoCardView:CardView
    private lateinit var recordAudioCardview:CardView
    private lateinit var sendPhotoImageBtn:ImageButton
    private lateinit var capturePhotoImageBtn:ImageButton
    private lateinit var recordAudioImageBtn:ImageButton
    private lateinit var hideShowFilesSend:ImageButton
    private lateinit var username:TextView
    private lateinit var isActive:TextView
    private lateinit var callBtn:ImageButton
    private lateinit var videoCallBtn:ImageButton
    private lateinit var userImage:ImageView
    private lateinit var sendMessageBtn:ImageButton
    //private lateinit var viewModel: MainViewModel
    private lateinit var rvChat:RecyclerView
    private lateinit var layOutManager:LinearLayoutManager
    private var messageList = ArrayList<NullableMessage>()
    private lateinit var adapter: UserChatAdapter
    private lateinit var manager:DataManager
    private lateinit var token:String
    private lateinit var client: OkHttpClient
    private var myInfo:ChatUserInfo? = null
    private var chatMateInfo:ChatUserInfo? = null
    private var socket:WebSocket? = null
    private val gson:Gson = Gson()
    private var isLoading = false
    private var hasMorePage = true
    private var isClose = false
    private var isDestroy = false
    private var isReady = false
    private var page = 1



    private fun setUpVal(){
        this.swiper = binding!!.chatingSwiper
        this.sendPhotoCardView = binding!!.cardViewSendImage
        this.sendPhotoImageBtn =binding!!.ImageviewAddPhotosUserChat
        this.capturePhotoCardView = binding!!.capturePictureOnChat
        this.capturePhotoImageBtn = binding!!.chatCapturePicture
        this.recordAudioCardview = binding!!.sendRecordAudioChat
        this.recordAudioImageBtn = binding!!.sendRecordAudioChatBtn
        this.rvChat = binding!!.UserchatFragmentRecyclerview
        this.layOutManager = LinearLayoutManager(this)
        this.onback = binding!!.ArrowBackChatUser
        this.hideShowFilesSend = binding!!.addOptionArrowRight
        this.username = binding!!.UserchatTextViewUsername
        this.isActive = binding!!.UserchatActiveTextview
        this.callBtn = binding!!.imageButtonChatUserCall
        this.videoCallBtn = binding!!.SearchButton
        this.userImage = binding!!.ImageviewUserChat
        this.sendMessageBtn = binding!!.sendMessageChatBtn
        this.chatMessageEditTextView = binding!!.UserInputMessageTextView
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        username.text = intent.getStringExtra("username")
        this.adapter = UserChatAdapter(messageList)
        binding?.UserchatFragmentRecyclerview!!.adapter = adapter
        binding?.UserchatFragmentRecyclerview!!.layoutManager = layOutManager

    }
    private fun setupManager(){
        layOutManager.apply {
            stackFromEnd = false
            reverseLayout = true
            isSmoothScrollbarEnabled = true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        this.binding = ActivityUserChatingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setUpVal()
        setupManager()
        setupProfileImage()
        setupViewModel()
        setupListener()
        loadMessage()
        editTextAnimOut()
    }
    private fun connectToSocket(){
        this.client = OkHttpClient.Builder()
            .addInterceptor(NetInterceptor())
            .readTimeout(0, TimeUnit.SECONDS)
            .build()
        val request: Request = Request.Builder()
            .url(Constants.WEBSOCKET_BASE_URL+CHAT_URL+intent.getStringExtra("username"))
            .addHeader("Authorization", token)
            .build()
        try {
            this.socket = client.newWebSocket(request, ChatListener())
            this.isClose = false
        }catch (e:Exception){
            this.isClose = true
            e.printStackTrace()
        }
    }
    private fun update(x:NullableMessage){
        messageList.add(0, x)
        adapter.notifyItemInserted(0)
        layOutManager.scrollToPosition(0)
    }
    private fun hideOption(){
        lifecycleScope.launch {
            if (recordAudioCardview.isVisible || capturePhotoCardView.isVisible || sendPhotoCardView.isVisible) {
                val animation =
                    AnimationUtils.loadAnimation(this@UserChatingActivity, R.anim.exit_to_left)
                chatMessageEditTextView.scrollBarFadeDuration = 3000
                recordAudioCardview.startAnimation(animation)
                recordAudioCardview.isVisible = false
                capturePhotoCardView.startAnimation(animation)
                capturePhotoCardView.isVisible = false
                sendPhotoCardView.startAnimation(animation)
                sendPhotoCardView.isVisible = false
                hideShowFilesSend.setImageResource(R.drawable.baseline_arrow_back_ios_new_24)
                editTextAnimOut()
            }
        }
    }

    private fun hidesShowOption(){
        lifecycleScope.launch {
            if (recordAudioCardview.isVisible || capturePhotoCardView.isVisible || sendPhotoCardView.isVisible) {
                val animation =
                    AnimationUtils.loadAnimation(this@UserChatingActivity, R.anim.exit_to_left)
                recordAudioCardview.isVisible = false
                recordAudioCardview.startAnimation(animation)
                capturePhotoCardView.isVisible = false
                capturePhotoCardView.startAnimation(animation)
                sendPhotoCardView.isVisible = false
                sendPhotoCardView.startAnimation(animation)
                hideShowFilesSend.setImageResource(R.drawable.baseline_arrow_back_ios_new_24)
                editTextAnimOut()
            } else if (!recordAudioCardview.isVisible || !capturePhotoCardView.isVisible || !sendPhotoCardView.isVisible) {
                val animation =
                    AnimationUtils.loadAnimation(this@UserChatingActivity, R.anim.enter_from_left)
                recordAudioCardview.isVisible = true
                recordAudioCardview.startAnimation(animation)
                capturePhotoCardView.isVisible = true
                capturePhotoCardView.startAnimation(animation)
                sendPhotoCardView.isVisible = true
                sendPhotoCardView.startAnimation(animation)
                hideShowFilesSend.setImageResource(R.drawable.arrow_forward_ios)
                editTextAnimIn()
            }
        }
    }
    private fun editTextAnimIn(){
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val seventypercent:Double = 0.7 * width
        val tertiPercent:Double = 0.35 * width


        val editText = binding?.UserInputMessageTextView
        val animator = ValueAnimator.ofInt(seventypercent.toInt(), tertiPercent.toInt())
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = editText?.layoutParams
            layoutParams?.width = value
            editText?.layoutParams = layoutParams
        }
        animator.duration = 100
        animator.start()
    }
    private fun editTextAnimOut(){
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val seventypercent:Double = 0.7 * width
        val tertiPercent:Double = 0.35 * width


        val editText = binding?.UserInputMessageTextView
        val animator = ValueAnimator.ofInt(tertiPercent.toInt(), seventypercent.toInt())
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = editText?.layoutParams
            layoutParams?.width = value
            editText?.layoutParams = layoutParams
        }
        animator.duration = 100
        animator.start()
    }
    private fun setupListener() {
        val handler = Handler(Looper.getMainLooper())
        val stopTypingRunnable = Runnable {
            // Send a message to the server via WebSocket

            socket?.send("{\"message_type\":10}")

        }
        hideShowFilesSend.setOnClickListener {
            hidesShowOption()
        }
        chatMessageEditTextView.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                hideOption()
                layOutManager.scrollToPosition(0)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                // Reset the timer
                socket?.send("{\"message_type\":5}")
                handler.removeCallbacks(stopTypingRunnable)
                handler.postDelayed(stopTypingRunnable, 2000)
            }
        })
        sendMessageBtn.setOnClickListener {
            if (!isLoading && !isClose){
                sendMessageLoader()
            }
        }
        onback.setOnClickListener {
            finish()
        }
        swiper.setOnRefreshListener {
            if (!isLoading && hasMorePage){
                updateChats()
                //swiper.isRefreshing = false
            }else{
                swiper.isRefreshing = false
            }
        }
    }

    private fun setupViewModel() {
        //val repository = Repository()
        //val viewModelFactory = MainViewModelFactory(repository)
        //viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private fun setupProfileImage() {
        val option = RequestOptions().placeholder(R.mipmap.greybg)
        Glide.with(this)
            .load(intent.getStringExtra("userAvatar"))
            .apply(option)
            .override(100, 100)
            .error(R.mipmap.greybg)
            .into(userImage)
    }

    private fun updateChats() {
        this.page +=1
        this.isLoading = true
        lifecycleScope.launch {
            val r = try {
                ChatInstance.api.getUserMessage(token = token, page = page, pk = intent.getStringExtra("username").toString())
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (r.isSuccessful && r.body() != null){
                if (!r.body()!!.hasMorePage){
                    this@UserChatingActivity.hasMorePage = false
                }
                for (i in r.body()!!.data){
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
                    this@UserChatingActivity.messageList.add(data)
                    this@UserChatingActivity.adapter.notifyItemInserted(messageList.size -1)
                }
            }
            swiper.isRefreshing=false
            this@UserChatingActivity.isLoading = false
        }
    }

    private fun sendMessageLoader() {
        this.isLoading = true
        if (chatMessageEditTextView.text == "" || chatMessageEditTextView.text.isEmpty()){
            return
        }
        lifecycleScope.launch {
            try {
                val text = chatMessageEditTextView.text
                this@UserChatingActivity.socket?.send("{\"message_type\":3,\"message_body\":\"${text}\"}")
                val data = NullableMessage(
                    sender_full_name = myInfo!!.name,
                    receiver_full_name = chatMateInfo!!.name,
                    user_avatar = myInfo!!.profileimg,
                    message_type = 1,
                    message_body = chatMessageEditTextView.text.toString(),
                    receiver = chatMateInfo!!.user,
                    seen = false,
                    me = true,
                    sender = myInfo!!.user
                )
                this@UserChatingActivity.messageList.add(0, data)
                this@UserChatingActivity.adapter.notifyItemInserted(0)
                this@UserChatingActivity.layOutManager.scrollToPosition(0)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            this@UserChatingActivity.isLoading = false
        }
        chatMessageEditTextView.text = ""
    }

    private fun loadMessage() {
        this.page = 1
        this.hasMorePage = true
        lifecycleScope.launch {
            val r = try {
                ChatInstance.api.getUserMessage(token = token, page = page, pk = intent.getStringExtra("username").toString())
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (r.isSuccessful && r.body() != null){
                if (!r.body()!!.hasMorePage){
                    this@UserChatingActivity.hasMorePage = false
                }

                for (i in r.body()!!.data){
                    var go = true
                    for (inlist in messageList){
                        if (i.id == inlist.id){
                            go = false
                        }
                    }
                    if (go){
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
                        messageList.add(data)
                        adapter.notifyItemInserted(0)
                        this@UserChatingActivity.layOutManager.scrollToPosition(0)
                    }
                }
            }
            connectToSocket()
        }
    }

    inner class ChatListener:WebSocketListener(){
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (!isDestroy){
                connectToSocket()
            }
            this@UserChatingActivity.isClose = true
            super.onClosed(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (!isDestroy){
                connectToSocket()
            }
            this@UserChatingActivity.isClose = true
            super.onFailure(webSocket, t, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val data = gson.fromJson(text, typeInterface::class.java)
            Log.d(TAG, data.type.toString()+" call")
            when(data.type){
                "handshake" -> {handShake(text)}
                "new_message" -> {newMessage(text)}
                "stopped_typing" -> {stopTyping(text)}
                "is_typing" -> {isTyping(text)}
            }


            super.onMessage(webSocket, text)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            this@UserChatingActivity.isClose = false
            super.onOpen(webSocket, response)
        }
    }

    private fun stopTyping(text: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val data = gson.fromJson(text, TypingData::class.java)
            if (data.user == chatMateInfo!!.user){

                binding?.chatTyping?.visibility = View.GONE

            }
        }
    }

    private fun isTyping(text: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val data = gson.fromJson(text, TypingData::class.java)
            if (data.user == chatMateInfo!!.user){
                binding?.chatTyping?.visibility = View.VISIBLE

            }
        }
    }

    private fun handShake(text: String) {
        val data = gson.fromJson(text, HandShake::class.java)
        try {
            this.myInfo = data.data
            this.chatMateInfo = data.chatmate
            this.isReady = true
        }catch (e:Exception){
            e.printStackTrace()
            finish()
        }
    }

    private fun newMessage(text: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            var messageSender: ChatUserInfo? = null
            var messageReceiver: ChatUserInfo? = null
            var me: Boolean? = null
            val message = gson.fromJson(text, newMessage::class.java)
            if (message.sender == myInfo!!.user) {
                messageSender = this@UserChatingActivity.myInfo
                messageReceiver = this@UserChatingActivity.chatMateInfo
                me = true
            } else {
                messageReceiver = this@UserChatingActivity.myInfo
                messageSender = this@UserChatingActivity.chatMateInfo
                binding?.chatTyping?.visibility = View.INVISIBLE
                me = false
            }
            try {
                var go = true
                val data = NullableMessage(
                    id = message.id,
                    sender_full_name = messageSender!!.name,
                    receiver_full_name = messageReceiver!!.name,
                    message_type = message!!.messageType,
                    username = message.sender,
                    user_full_name = messageSender.name,
                    user_avatar = messageSender.profileimg,
                    date_time = message.date_time,
                    message_body = message.messageBody,
                    receiver = message.receiver,
                    seen = message.seen,
                    me = me,
                    sender = message.sender
                )
                for (i in messageList) {
                    if (i.id == null) {
                        if (data.sender == i.sender && data.message_body == i.message_body) {
                            val pos = messageList.indexOf(i)
                            messageList[pos] = data
                            adapter.notifyItemChanged(pos)
                            go = false
                        }
                    } else if (data.id == i.id) {
                        go = false
                    }
                }
                if (go) {
                    update(data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    override fun onResume() {
        if (this.isClose){
            connectToSocket()
        }
        super.onResume()
    }
    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    companion object{
        val TAG = "UserChatActivity"
        private val CHAT_URL = "/ws/chat/socketTo="
    }
}
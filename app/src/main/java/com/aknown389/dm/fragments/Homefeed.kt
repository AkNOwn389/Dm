package com.aknown389.dm.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.activities.ChatActivity
import com.aknown389.dm.activities.MainSearchActivity
import com.aknown389.dm.activities.NotificationActivity
import com.aknown389.dm.databinding.FragmentHomefeedBinding
import com.aknown389.dm.db.AppDataBase
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions

import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.pageView.homeFeed.utility.SpeedLinearLayoutManager

import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.remote.repository.Repository

import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.NetInterceptor
import com.aknown389.dm.pageView.homeFeed.viewModel.HomeFeedViewModel
import com.aknown389.dm.pageView.homeFeed.viewModel.HomeFeedViewModelFactory
import com.aknown389.dm.pageView.notification.models.NotificationBadge
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
import okio.ByteString
import java.util.concurrent.TimeUnit


//val animator = ObjectAnimator.ofInt(scrollview, "scrollY",0, 500)
//animator.duration = 1000
class Homefeed : Fragment() {

    private var isShimmering: Boolean = false
    private var progressRunning: Boolean = false
    private var binding: FragmentHomefeedBinding? = null
    private lateinit var layoutmanager: SpeedLinearLayoutManager
    private lateinit var adapter: HomeFeedCardViewAdapter
    private lateinit var viewmodel: HomeFeedViewModel
    private lateinit var manager: DataManager
    private lateinit var token: String
    private lateinit var client: OkHttpClient
    private lateinit var ws:WebSocket

    private var isClose = true
    var isLoading = false


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (this.binding == null){
            this.binding = FragmentHomefeedBinding.inflate(layoutInflater, container, false)
        }
        return  binding?.root
    }
    private fun setVal() {
        manager = DataManager(requireContext())
        token = manager.getAccessToken().toString()
        val repository = Repository()
        val dataBase = AppDataBase
        val viewModelFactory = HomeFeedViewModelFactory(repository, dataBase.getDatabase(requireContext()))
        this.viewmodel = ViewModelProvider(this, viewModelFactory)[HomeFeedViewModel::class.java]
        this.adapter = HomeFeedCardViewAdapter(initGlide())
        this.layoutmanager = SpeedLinearLayoutManager(requireContext())
        binding?.recyclerviewhomefeed?.setMediaObjects(adapter.postListdata)
        binding?.recyclerviewhomefeed!!.adapter = adapter
        binding?.recyclerviewhomefeed?.layoutManager = layoutmanager
        this.client = OkHttpClient.Builder()
            .addInterceptor(NetInterceptor())
            .readTimeout(0, TimeUnit.SECONDS)
            .build()

    }

    private fun stopShimmer() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(500)
            binding?.recyclerviewhomefeed?.isVisible = true
            binding?.homefeedshimmerview!!.stopShimmer()
            binding?.homefeedshimmerview!!.isVisible = false
            binding?.homefeedSwipewRefresh?.isRefreshing = false
            this@Homefeed.isShimmering = false
        }
    }

    private fun startShimmer() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding?.homefeedshimmerview!!.startShimmer()
            binding?.homefeedshimmerview!!.isVisible = true
            binding?.recyclerviewhomefeed?.isVisible = false
            this@Homefeed.isShimmering = true
        }
    }

    private fun initGlide(): RequestManager {
        val options = RequestOptions()
            .error(R.mipmap.greybg)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVal()
        refreshFeed()
        setListener()
        socketListener()
    }


    private fun socketListener(){
        lifecycleScope.launch {
            delay(3000)
            while (true){
                if (isActive){
                    if (this@Homefeed.isClose){
                        connectToSocket()
                        delay(4000)
                    }else{
                        delay(4000)
                        ws.send("{\"message\": \"notificationBadgeNumber\"}")
                    }
                }else{
                    return@launch
                }
            }
        }
    }
    private fun connectToSocket(){
        val request: Request = Request.Builder()
            .url(Constants.WEBSOCKET_BASE_URL+NOTIFICATION_BADGE_LISTENER_URL)
            .addHeader("Authorization", token)
            .build()
        try {
            this.ws = this.client.newWebSocket(request, Listener())
            this.isClose = false
        }catch (e:Exception){
            this.isClose = true
            e.printStackTrace()
        }
    }


    private fun setListener() {
        newsFeedDataListener()
        binding?.backToTop?.setOnClickListener {
            backToTop()
        }
        //swife screen
        binding?.homefeedSwipewRefresh!!.setOnRefreshListener{
            refreshFeed()
        }
        //change screen
        binding?.imageButtonHomeChat!!.setOnClickListener {
            Intent(context, ChatActivity::class.java).also{
                startActivity(it)
            }
        }
        binding?.notificationBtn!!.setOnClickListener {
            Intent(context, NotificationActivity::class.java).also {
                startActivity(it)

            }
        }
        binding?.SearchButton!!.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, MainSearchActivity::class.java)
                it.startActivity(intent)
            }
        }
        onRecyclerViewListener()
    }

    private fun newsFeedDataListener() {
        viewmodel.newsfeed.observe(viewLifecycleOwner){ newData ->
            try{
                if (adapter.postListdata.size != 1){
                    for (i in adapter.postListdata){
                        if (i != adapter.postListdata.first()){
                            for (x in newData){
                                if (x.id == i.id){
                                    newData.remove(x)
                                }
                            }
                        }
                    }
                }
            }catch (e:Exception) {
                e.printStackTrace()
            }
            for (i in newData) {
                if (i.image_url.isNullOrEmpty() && i.media_type == 5) {
                    i.media_type = 7
                }
            }
            if (newData.size != 0){
                binding?.recyclerviewhomefeed?.layoutManager?.onSaveInstanceState()
                adapter.setData(newData)
            }
            if (adapter.postListdata.size < 18){
                layoutmanager.scrollToPosition(0)
            }
            if (this.isShimmering){
                stopShimmer()
            }
            if (this.progressRunning){
                binding?.updatingProgress?.visibility = View.GONE
                progressRunning = false
            }
        }
    }


    private fun updateFeed(){
        binding?.updatingProgress?.visibility = View.VISIBLE
        this.progressRunning = true
        viewmodel.updateFeed(requireContext())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshFeed() {
        startShimmer()
        adapter.postListdata.clear()
        val bar = PostDataModel(id = "createPostBar", media_type = 99)
        adapter.postListdata.add(0, bar)
        viewmodel.getFeed(requireContext())
    }

    private fun backToTop(){
        val smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0
        layoutmanager.startSmoothScroll(smoothScroller)
    }



    inner class Listener(): WebSocketListener(){
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            this@Homefeed.isClose = true
            super.onClosed(webSocket, code, reason)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            this@Homefeed.isClose = true
            super.onFailure(webSocket, t, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            lifecycleScope.launch {
                val gson = Gson()
                val data = gson.fromJson(text, NotificationBadge::class.java)
                if (data.status){
                    try {
                        if (data.notification != 0){
                            this@Homefeed.binding?.NotificationBtnBadgCardView?.isVisible = true
                            this@Homefeed.binding?.notificationBadgeTextView?.text = data.notification.toString()
                        }else{
                            this@Homefeed.binding?.NotificationBtnBadgCardView?.isVisible = false
                        }
                        if (data.chat != 0){
                            this@Homefeed.binding?.chatBtnBadgeCardView?.isVisible = true
                            this@Homefeed.binding?.ChatMessageBtnBadgeTextView?.text = data.chat.toString()
                        }else{
                            this@Homefeed.binding?.chatBtnBadgeCardView?.isVisible = false
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
            super.onMessage(webSocket, text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
        }
    }

    private fun onRecyclerViewListener() {
        binding?.recyclerviewhomefeed?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutmanager.findLastVisibleItemPosition() > 10 && adapter.postListdata.size >= 10){
                    if (binding?.backToTop?.visibility == View.GONE){
                        val animation =
                            AnimationUtils.loadAnimation(requireContext(), com.udevel.widgetlab.R.anim.abc_slide_in_top)
                        animation.duration = 800
                        binding?.backToTop?.startAnimation(animation)
                        binding?.backToTop?.visibility = View.VISIBLE
                    }
                }
                if (layoutmanager.findLastVisibleItemPosition() < 10){
                    if (binding?.backToTop?.visibility == View.VISIBLE){
                        val animation =
                            AnimationUtils.loadAnimation(requireContext(), com.udevel.widgetlab.R.anim.abc_slide_out_top)
                        animation.duration = 800
                        binding?.backToTop?.startAnimation(animation)
                        binding?.backToTop?.visibility = View.GONE
                    }
                }
                if (!isLoading){
                    if (layoutmanager.findLastVisibleItemPosition() >= adapter.itemCount-2){
                        if (!viewmodel.isLoading && viewmodel.hasMorePage){
                            updateFeed()
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun onResume() {
        binding?.recyclerviewhomefeed?.onResumePlayer()
        super.onResume()
    }
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
    override fun onPause() {
        binding?.recyclerviewhomefeed?.onPausePlayer()
        super.onPause()
    }
    companion object{
        const val NOTIFICATION_BADGE_LISTENER_URL = "/notificationBadge"
    }
}
package com.aknown389.dm.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.api.retroInstance.VideoFeedInstance
import com.aknown389.dm.databinding.FragmentWatchsvideoBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.aknown389.dm.pageView.videoFeed.VideoFeedAdapter
import com.aknown389.dm.models.videoFeedModels.VideoDataModels
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class WatchVideo : Fragment() {
    private lateinit var context:Context
    private var binding: FragmentWatchsvideoBinding?=null
    private var adapter: VideoFeedAdapter? = null
    private lateinit var manager: DataManager
    private lateinit var token:String
    private var layoutManager:LinearLayoutManager? = null
    private var feeds = ArrayList<VideoDataModels>()

    private var isLoading = false
    private var hasMorePage = true
    private var page = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.context = requireContext()
        this.binding = FragmentWatchsvideoBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVal()
        setListener()
        loadFeed()

    }
    override fun onPause() {
        binding?.recyclerView!!.onPausePlayer()
        super.onPause()
    }
    override fun onResume() {
        binding?.recyclerView!!.onResumePlayer()
        super.onResume()
    }

    private fun loadFeed() {
        this.isLoading = true
        this.page = 1
        binding?.shimmer?.isVisible = true
        binding?.shimmer?.startShimmer()
        invicible()
        lifecycleScope.launch {
            val response = try {
                VideoFeedInstance.api.videoFeed(token, page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful && response.body()!!.status){
                for (new in response.body()!!.data){
                    var go: Boolean = true
                    runBlocking {
                        for (inList in feeds){
                            if (new.id == inList.id){
                                go = false
                            }
                        }
                    }
                    if (go){
                        this@WatchVideo.feeds.add(new)
                        this@WatchVideo.adapter?.notifyItemInserted(feeds.size-1)
                    }
                }
                if (!response.body()!!.hasMorePage){
                    this@WatchVideo.hasMorePage = false
                }
                binding?.recyclerView!!.smoothScrollToPosition(0)
            }
            delay(1000)
            binding?.shimmer?.stopShimmer()
            binding?.shimmer?.isVisible = false
            viciBle()
            try{
                if (response.body()!!.data.isEmpty()){
                    binding?.noContent?.visibility = View.VISIBLE
                }else{
                    binding?.noContent?.visibility = View.GONE
                }
            }catch (e:Exception){
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            }

            this@WatchVideo.isLoading = false
        }
    }
    private fun updateFeed() {
        this.isLoading = true
        this.page +=1
        lifecycleScope.launch {
            val response = try {
                VideoFeedInstance.api.videoFeed(token, page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful && response.body()!!.status){
                for (i in response.body()!!.data){
                    this@WatchVideo.feeds.add(i)
                    this@WatchVideo.adapter?.notifyItemInserted(feeds.size-1)
                }
                if (!response.body()!!.hasMorePage){
                    this@WatchVideo.hasMorePage = false
                }
            }
            this@WatchVideo.isLoading = false
        }
    }

    private fun viciBle() {
        binding?.recyclerView?.isVisible = true
    }

    private fun invicible() {
        binding?.recyclerView?.isVisible = false
        binding?.noContent?.visibility = View.GONE
    }

    private fun setListener() {
        binding?.recyclerView?.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (layoutManager!!.findFirstCompletelyVisibleItemPosition()+layoutManager!!.childCount >= adapter!!.itemCount){
                    if (!isLoading && hasMorePage){
                        updateFeed()
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = layoutManager!!.childCount
                val pastVisibleItem: Int = layoutManager!!.findFirstCompletelyVisibleItemPosition()
                if (!isLoading){
                    if (visibleItemCount + pastVisibleItem > adapter!!.itemCount-1){
                        if (!isLoading && hasMorePage){
                            updateFeed()
                        }
                    }
                }
            }
        })
        binding?.swiper?.setOnRefreshListener {
            if (!isLoading){
                loadFeed()
                binding?.swiper?.isRefreshing = false
            }else{
                binding?.swiper?.isRefreshing = false
            }
        }
    }


    private fun setVal() {
        this.manager = DataManager(context)
        this.token = manager.getAccessToken().toString()
        this.layoutManager = LinearLayoutManager(context)
        this.adapter = VideoFeedAdapter(feeds, initGlide())
        binding?.recyclerView?.setMediaObjects(feeds)
        binding?.recyclerView?.layoutManager = layoutManager
        binding?.recyclerView?.adapter = adapter

    }

    //test
    private fun initGlide(): RequestManager {
        val options = RequestOptions()
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }
    override fun onDestroy() {
        if (binding?.recyclerView != null) {
            binding?.recyclerView!!.releasePlayer()
        }
        adapter = null
        binding = null
        super.onDestroy()
    }
}
package com.aknown389.dm.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityMyGalleryVideosBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.global.VideoUrl
import com.aknown389.dm.pageView.videoGallery.utilities.VideGalleryAdapter
import com.aknown389.dm.pageView.videoGallery.viewModel.VideoGalleryViewModel
import com.aknown389.dm.pageView.videoGallery.viewModel.VideoGalleryViewModelFactory
import com.aknown389.dm.utils.DataManager

class MyGalleryVideos : AppCompatActivity() {
    private var isProgressVisible: Boolean = false
    private lateinit var database: AppDataBase
    private var binding:ActivityMyGalleryVideosBinding? = null
    private lateinit var layoutManager:StaggeredGridLayoutManager
    private lateinit var adapter: VideGalleryAdapter
    private lateinit var viewModel: VideoGalleryViewModel
    private lateinit var manager: DataManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyGalleryVideosBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        iniT()
    }

    private fun iniT(){
        setValue()
        setUI()
        setListener()
        loadGallery()
    }

    private fun loadGallery() {
        binding?.progressBar?.visibility = View.VISIBLE
        isProgressVisible = true
        viewModel.getVideos(this)
    }

    private fun setListener() {
        binding?.swiper?.setOnRefreshListener {
            viewModel.getVideos(this)
            binding?.swiper?.isRefreshing = false
        }
        binding?.back?.setOnClickListener {
            finish()
        }
        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisibleItemPositions = layoutManager.findLastCompletelyVisibleItemPositions(null)
                if (!viewModel.isLoading){
                    if (lastVisibleItemPositions.last()+lastVisibleItemPositions.first() >= adapter.itemCount-1){
                        if (!viewModel.isLoading && viewModel.hasMorePage){
                            updateGallery()
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        viewModel._response.observe(this){ response ->
            if (response != null){
                if (response.status){
                    val videos = ArrayList<VideoUrl>()
                    for (i in response.data){
                        if ( i !in adapter.videoList){
                            videos.add(i)
                        }
                    }
                    adapter.update(videos)
                }
            }
            if (isProgressVisible){
                binding?.progressBar?.visibility = View.GONE
                binding?.updateProgressBar?.visibility = View.GONE
                isProgressVisible = false
            }
        }
    }

    private fun updateGallery() {
        binding?.updateProgressBar?.visibility = View.VISIBLE
        isProgressVisible = true
        viewModel.updateVideos(this)
    }

    private fun setUI() {

    }

    private fun setValue() {
        layoutManager = StaggeredGridLayoutManager(
            3, LinearLayoutManager.VERTICAL)
        adapter = VideGalleryAdapter()
        binding?.recyclerView?.adapter = adapter
        binding?.recyclerView?.layoutManager = layoutManager
        manager = DataManager(this)
        database =  AppDataBase.getDatabase(this)
        viewModel = ViewModelProvider(
            this, VideoGalleryViewModelFactory(database)
        )[VideoGalleryViewModel::class.java]
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
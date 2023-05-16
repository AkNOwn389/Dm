package com.aknown389.dm.pageView.videoFeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.RequestManager
import com.aknown389.dm.R
import com.aknown389.dm.models.videoFeedModels.VideoDataModels
import com.aknown389.dm.utils.DataManager


class VideoFeedAdapter(
    private val mediaObjects: ArrayList<VideoDataModels>,
    private val requestManager: RequestManager
): RecyclerView.Adapter<ViewHolder>() {
    private lateinit var manager:DataManager
    private lateinit var token:String
    private lateinit var parent: ViewGroup
    private val TAG = "VideoPlayerRecyclerView"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoFeedViewHolder {
        this.parent = parent
        this.manager = DataManager(parent.context)
        this.token = manager.getAccessToken().toString()
        return when(viewType){
            6 -> VideoFeedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video_card, parent, false))
            else -> VideoFeedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video_card, parent, false))
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as VideoFeedViewHolder).onBind(requestManager = requestManager)
        val data = mediaObjects[position]
        if (data.videosUrl!=null){
            when(mediaObjects[position].mediaType){
                6 -> SingleVideoView(
                    holder = holder,
                    data = data,
                    context = parent.context,
                    mediaObjects = mediaObjects,
                    parent = parent,
                    adapterContext = this,
                    token = token,
                    requestManager = requestManager
                )
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return mediaObjects[position].mediaType!!
    }
    override fun getItemCount(): Int {
        return mediaObjects.size
    }
    companion object{
        const val TAG = "VIDEO FEED RECYCLERVIEW"
        private const val AppName = "Android ExoPlayer"

    }
}
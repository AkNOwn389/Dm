package com.aknown389.dm.pageView.postViewPage

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.aknown389.dm.R
import com.aknown389.dm.pageView.postViewPage.models.ToDisplayDataModel
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.MimeTypes
import javax.inject.Inject

class VideoPostView @Inject constructor(
    private val adapterContext:PostViewAdapter,
    private val context: Context,
    private val parent: ViewGroup,
    private val item: ToDisplayDataModel,
    private val holder: PostViewHolder,
    private val postDataList:ArrayList<ToDisplayDataModel>,
    private val token: String,
) {
    init {
        loadUI()
    }
    private fun loadUI() {
        Glide.with(context)
            .load(item.videoUrl1000)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.thumbnailUtils!!)
        setVideoPlayer(holder, item)
    }
    private fun setVideoPlayer(holder: PostViewHolder, data: ToDisplayDataModel) {

        val loadControl = DefaultLoadControl()
        val bandwidthMeter = DefaultBandwidthMeter.Builder(parent.context).build()
        val trackSelector = DefaultTrackSelector(parent.context)
        val exoPlayer = ExoPlayer.Builder(parent.context)
            .setBandwidthMeter(bandwidthMeter)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()
        adapterContext.playerList.add(exoPlayer)
        adapterContext.holderList.add(holder)
        holder.player?.visibility = View.INVISIBLE
        holder.player?.player = exoPlayer
        holder.player?.useController = true
        holder.player?.hideController()
        holder.progressBar1!!.visibility = View.VISIBLE
        holder.progressBar2!!.visibility = View.VISIBLE
        holder.player!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        if (data.videoUrl1000 != null){
            val media = MediaItem.Builder()
                .setUri(data.videoUrl1000)
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .build()
            exoPlayer.setMediaItem(media)
            exoPlayer.seekTo(0)
            exoPlayer.prepare()
            addPlayerListener(exoPlayer, holder)
        }

    }
    private fun addPlayerListener(exoPlayer: ExoPlayer, holder: PostViewHolder) {
        exoPlayer.addListener(object : Player.Listener{
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                for (i in adapterContext.playerList){
                    if (i.playWhenReady && i != exoPlayer){
                        i.pause()
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        if (holder.progressBar1 != null || holder.progressBar2 != null) {
                            holder.progressBar1!!.visibility = View.VISIBLE
                            holder.progressBar2!!.visibility = View.VISIBLE
                            holder.player!!.visibility = RecyclerView.INVISIBLE
                            holder.thumbnailUtils!!.visibility = RecyclerView.VISIBLE
                        }
                    }
                    Player.STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                        holder.player!!.player?.pause()
                        holder.player!!.useController = true
                    }
                    Player.STATE_IDLE -> {
                    }
                    Player.STATE_READY -> {
                        if (holder.progressBar1 != null || holder.progressBar2 != null) {
                            holder.progressBar1!!.visibility = View.GONE
                            holder.progressBar2!!.visibility = View.GONE
                            holder.player!!.visibility = RecyclerView.VISIBLE
                            holder.thumbnailUtils!!.visibility = RecyclerView.GONE
                        }
                    }
                }
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
            }
        })
        holder.player!!.setFullscreenButtonClickListener {

        }
    }
}
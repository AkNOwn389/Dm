package com.aknown389.dm.pageView.videoFeed

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.models.videoFeedModels.VideoDataModels
import com.aknown389.dm.utils.DataManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import java.util.ArrayList
import java.util.Objects
import kotlin.Exception

class VideoFeedRecyclerView:RecyclerView {
    //main tools
    private var videoPlayer: ExoPlayer? = null


    private var mediaContainer: FrameLayout? = null
    private var progressBar: ProgressBar? = null
    private var volumeControl: ImageView? = null

    private var currentView: VideoFeedViewHolder? = null
    private var mediaObjects = ArrayList<VideoDataModels>()
    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private var myContext: Context? = null
    private var playPosition = -1
    private var oldPlayPosition = -1
    private var volumeState: VolumeState? = null
    private var manager: DataManager? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }
    private fun init(context: Context) {
        this.myContext = context.applicationContext
        this.manager = DataManager(myContext as Context)
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true)
                    } else {
                        playVideo(false)
                    }
                }
            }
        })
        val display = (Objects.requireNonNull(
            context.getSystemService(Context.WINDOW_SERVICE)
        ) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        videoSurfaceDefaultHeight = point.x
        screenDefaultHeight = point.y
    }
    fun playVideo(isEndOfList: Boolean) {
        val targetPosition: Int
        if (!isEndOfList) {
            val startPosition =
                (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
            var endPosition =
                (layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1
            }

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return
            }

            // if there is more than 1 list-item on the screen
            targetPosition = if (startPosition != endPosition) {
                val startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition)
                val endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition)
                if (startPositionVideoHeight > endPositionVideoHeight) startPosition else endPosition
            } else {
                startPosition
            }
        } else {
            targetPosition = mediaObjects.size - 1
        }

        // video is already playing so return
        if (targetPosition == this.playPosition) {
            return
        }

        // set the position of the list-item that is to be played
        this.playPosition = targetPosition
        val currentPosition = targetPosition - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition()
        val child = getChildAt(currentPosition) ?: return
        val holder = child.tag as VideoFeedViewHolder
        progressBar = holder.progressBar
        volumeControl = holder.volumeControl
        if (holder.playerView == null){
            try {
                if (this.videoPlayer!!.playWhenReady){
                    pauseCurrentPlayer()
                }
            }catch (e:NullPointerException){
                return
            }
            return
        }
        if (holder.playerView!!.player == null){
            try {
                if (this.videoPlayer!!.playWhenReady){
                    pauseCurrentPlayer()
                }
            }catch (e:NullPointerException){
                return
            }
            return
        }
        holder.playerView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        if (this.videoPlayer != null){
            try {
                if (this.videoPlayer!!.playWhenReady){
                    pauseCurrentPlayer()
                }
            }catch (e:NullPointerException){
                return
            }
        }
        this.videoPlayer = holder.playerView!!.player as ExoPlayer
        this.currentView = holder
        when(manager?.getDefaultVolumeState()){
            "OFF" -> {
                setVolumeControl(VolumeState.OFF)
            }
            "ON" -> {
                setVolumeControl(VolumeState.ON)
            }
            null -> {
                setVolumeControl(VolumeState.ON)
            }
            "null" -> {
                setVolumeControl(VolumeState.ON)
            }
        }
        if (holder.playerView!!.player != null){
            holder.playerView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            holder.volumeControl!!.setOnClickListener {toggleVolume()}
            try {
                val mediaItem = mediaObjects[targetPosition]
                if (mediaItem.videosUrl != null) {
                    videoPlayer!!.seekTo(mediaItem.playbackPosition)
                    videoPlayer!!.prepare()
                    videoPlayer!!.playWhenReady = true
                    holder.playerView!!.visibility = View.VISIBLE
                    holder.playerView!!.useController = true
                    holder.playerView!!.hideController()
                    holder.thumbnail?.visibility = INVISIBLE
                    holder.playerView?.visibility = VISIBLE
                    addPlayerListener(videoPlayer!!, holder)
                    this.oldPlayPosition = targetPosition
                }else{
                    return
                }
            }catch (e: Exception){
                Log.d("HOME FEED LOG", e.toString())
                return
            }
        }else{
            return
        }
    }

    private fun addPlayerListener(exoPlayer: ExoPlayer, holder: VideoFeedViewHolder) {
        exoPlayer.addListener(object : Player.Listener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        if (holder.progressBar != null) {
                            holder.progressBar!!.visibility = View.VISIBLE
                        }
                    }
                    Player.STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                        videoPlayer!!.stop()
                        holder.playerView!!.useController = true
                        resetCurrentPlayer()
                        try {
                            smoothScrollToPosition(playPosition+1)
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                    }

                    Player.STATE_IDLE -> {
                        holder.progressBar!!.visibility = INVISIBLE
                    }
                    Player.STATE_READY -> {
                        if (holder.progressBar != null) {
                            holder.progressBar!!.visibility = View.GONE
                        }
                    }
                    Player.COMMAND_PREPARE -> {
                        Log.d(TAG, "command prepare")
                    }
                }
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
            }
        })
        holder.playerView!!.setFullscreenButtonClickListener {

        }
    }

    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val at = playPosition - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition()
        val child = getChildAt(at) ?: return 0
        val location = IntArray(2)
        child.getLocationInWindow(location)
        return if (location[1] < 0) {
            location[1] + videoSurfaceDefaultHeight
        } else {
            screenDefaultHeight - location[1]
        }
    }
    private fun resetCurrentPlayer() {
        mediaObjects[this.oldPlayPosition].playbackPosition = 0
    }
    private fun pauseCurrentPlayer(){
        if (this.videoPlayer == null){
            return
        }
        mediaObjects[this.oldPlayPosition].playbackPosition = videoPlayer!!.currentPosition
        this.videoPlayer!!.stop()
        this.currentView!!.thumbnail?.visibility = VISIBLE
        this.currentView!!.playerView?.visibility = INVISIBLE
        this.currentView!!.progressBar?.visibility = INVISIBLE
        this.currentView = null
        this.videoPlayer = null
    }


    fun onResumePlayer(){
        if (videoPlayer != null) {
            videoPlayer!!.play()
        }
    }

    fun releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.release()
            videoPlayer = null
        }
    }

    fun onPausePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.pause()
        }
    }

    private fun toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                setVolumeControl(VolumeState.ON)
            } else if (volumeState == VolumeState.ON) {
                setVolumeControl(VolumeState.OFF)
            }
        }
    }
    private fun setVolumeControl(state: VolumeState) {
        volumeState = state
        if (state == VolumeState.OFF) {
            manager?.saveDefaultVolumestate("OFF")
            videoPlayer!!.volume = 0f
            animateVolumeControl()
        } else if (state == VolumeState.ON) {
            manager?.saveDefaultVolumestate("ON")
            videoPlayer!!.volume = 1f
            animateVolumeControl()
        }
    }
    private fun animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl!!.bringToFront()
            if (volumeState == VolumeState.OFF) {
                volumeControl!!.setImageResource(R.drawable.baseline_volume_off_24)
            } else if (volumeState == VolumeState.ON) {
                volumeControl!!.setImageResource(R.drawable.baseline_volume_up_24)
            }
            /*
            volumeControl!!.animate().cancel()
            volumeControl!!.alpha = 1f
            volumeControl!!.animate()
                .alpha(0f)
                .setDuration(600).startDelay = 1000

             */
        }
    }

    fun setMediaObjects(mediaObjects: ArrayList<VideoDataModels>) {
        this.mediaObjects = mediaObjects
    }



    private enum class VolumeState {
        ON, OFF
    }
    companion object{
        const val TAG = "VIDEO FEED RECYCLERVIEW"
        private const val AppName = "Android ExoPlayer"
    }

}
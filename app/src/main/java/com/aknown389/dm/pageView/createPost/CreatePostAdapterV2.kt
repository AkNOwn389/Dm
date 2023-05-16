package com.aknown389.dm.pageView.createPost

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class CreatePostAdapterV2(private val context: Context) : RecyclerView.Adapter<CreatePostAdapterV2.ViewHolder>() {
    private var player: ExoPlayer? = null
    private var playbackPosition = 0L
    private var currentWindow = 0
    private var playWhenReady = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_createpost_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 10 // Replace this with your actual item count
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Player.Listener {
        private val playerView = itemView.findViewById<PlayerView>(R.id.player_view)

        fun bind() {
            if (player == null) {
                player = SimpleExoPlayer.Builder(context).build()
                playerView.player = player
                val mediaItem = MediaItem.fromUri("https://www.example.com/video.mp4")
                player?.setMediaItem(mediaItem)
                player?.playWhenReady = playWhenReady
                player?.seekTo(currentWindow, playbackPosition)
                player?.prepare()
            }
            player?.addListener((this))
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                // Media is playing
            } else {
                // Media is paused
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            if (state == Player.STATE_READY && playWhenReady) {
                // Media has been loaded and is ready to play
                if (playerView.isAttachedToWindow && playerView.visibility == View.VISIBLE) {
                    player?.play()
                }
            }
        }
        fun releasePlayer() {
            if (player != null) {
                playbackPosition = player!!.currentPosition
                currentWindow = player!!.currentWindowIndex
                playWhenReady = player!!.playWhenReady
                player!!.release()
                player = null
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.releasePlayer()
    }


}
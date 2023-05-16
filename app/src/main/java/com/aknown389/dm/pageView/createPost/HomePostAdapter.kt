package com.aknown389.dm.pageView.createPost

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.models.homepostmodels.uploadFileModel
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.GONE
import com.google.android.exoplayer2.ui.StyledPlayerView

class HomePostAdapter(private val imageList: ArrayList<uploadFileModel>): RecyclerView.Adapter<HomePostAdapter.ViewHolder>() {
    private val TAG = "CREATEPOST"
    private lateinit var context:Context
    private var playerList = ArrayList<ExoPlayer>()
    private var holderList = ArrayList<ViewHolder>()
    class ViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val imageview:ImageView? = view.findViewById(R.id.imageView)
        val deleteBtn:ImageButton? = view.findViewById(R.id.deleteButton)
        val thumbnail:ImageView? = view.findViewById(R.id.thumbnail)
        val player:StyledPlayerView? = view.findViewById(R.id.player_view)
        val media_container: FrameLayout? = view.findViewById(R.id.media_container)
        val play:ImageButton? = view.findViewById(R.id.playvideo)
        val progressBar:ProgressBar? = view.findViewById(R.id.progressBar)
        val progressBar2:ProgressBar? = view.findViewById(R.id.progressBar2)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        return  when(viewType){
            1 -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_createpost, parent, false))
            2 -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_createpost_video, parent, false))
            else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_createpost, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return imageList[position].type
    }
    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = imageList[position]
        when(item.type){
            1-> loadType1(holder, item)
            2-> loadType2(holder, item)
        }

    }

    private fun loadType2(holder: ViewHolder, item: uploadFileModel) {

        val exoPlayer = ExoPlayer.Builder(context).build()
        playerList.add(exoPlayer)
        holderList.add(holder)
        Glide.with(context)
            .load(item.file)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.imageview!!)
        //holder.imageview?.setImageBitmap(thumbnail)
        holder.deleteBtn?.setOnClickListener {
            val pos = imageList.indexOf(item)
            exoPlayer.stop()
            exoPlayer.release()
            notifyItemRemoved(pos)
            imageList.remove(item)
        }
        holder.play?.setOnClickListener {
            exoPlay(holder, item, exoPlayer)
        }
    }
    private fun exoPlay(holder: ViewHolder, item: uploadFileModel, exoPlayer: ExoPlayer){
        for (i in playerList){
            if (i.playWhenReady){
                i.pause()
            }
        }
        holder.player?.player = exoPlayer
        val uri = Uri.parse(item.file.toString())
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        holder.player?.useController = true
        holder.player?.hideController()
        holder.play?.visibility = GONE
        addListener(holder, item, exoPlayer)
    }

    private fun addListener(
        holder: ViewHolder,
        item: uploadFileModel,
        exoPlayer: ExoPlayer
    ) {
        exoPlayer.addListener(object :Player.Listener{
            override fun onEvents(player: Player, events: Player.Events) {
                for (i in playerList){
                    if (i.playWhenReady && i != exoPlayer) {
                        i.pause()
                    }
                }
                super.onEvents(player, events)
            }
        })
    }

    private fun loadType1(holder: ViewHolder, item: uploadFileModel) {
        try {
            Glide.with((context as AppCompatActivity))
                .load(item.file)
                .placeholder(R.drawable.progress_animation)
                .error(R.mipmap.greybg)
                .into(holder.imageview!!)
        }catch (e:Exception){
            e.printStackTrace()
        }
        holder.deleteBtn?.setOnClickListener {
            val pos = imageList.indexOf(item)
            notifyItemRemoved(pos)
            imageList.remove(item)
        }
    }
    fun releasPlayer(){
        for (i in playerList){
            if (i.playWhenReady){
                i.release()
            }
        }
    }
    fun onPlayerPause(){
        for (i in playerList){
            if (i.playWhenReady){
                i.pause()
            }
        }
    }
}
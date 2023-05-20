package com.aknown389.dm.pageView.homeFeed.recyclerviewItem

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.aknown389.dm.R
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.activities.PostViewActivity
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.models.global.VideoUrl
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.dialog.ShowFriendDailog
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.dialog.ShowMyDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.aknown389.dm.pageView.homeFeed.utility.GlobalSetter
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedRecyclerViewHolder
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.remote.LikePost
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.Reaction
import com.aknown389.dm.utils.Constants
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.gson.Gson
import java.lang.Exception
import javax.inject.Inject

class MultiVideoPostView @Inject constructor(

    private val holder: HomeFeedRecyclerViewHolder,
    private val currentItem: PostDataModel,
    private val context: Context,
    private val postListdata:ArrayList<PostDataModel>,
    private val parent: ViewGroup,
    private val adapterContext: HomeFeedCardViewAdapter,
    private val token:String,
    private val requestManager: RequestManager
) {


    init {
        setUi()
        setListener()
        setPlayer()
        setDiaLog()
    }
    private fun setDiaLog() {
        holder.menuBtn?.setOnClickListener {
            if (currentItem.me == false) {
                ShowFriendDailog(
                    holder = holder,
                    currentItem = currentItem,
                    adapterContext = adapterContext,
                    token = token,
                    context = context,
                    parent = parent,
                    postListdata = postListdata
                )
            } else {
                ShowMyDialog(
                    holder = holder,
                    currentItem = currentItem,
                    adapterContext = adapterContext,
                    token = token,
                    context = context,
                    parent = parent,
                    postListdata = postListdata
                )
            }
        }
    }
    private fun setPlayer() {
        if (holder.playerView == null){
            return
        }
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(
                Constants.minBufferMs,
                Constants.maxBufferMs,
                Constants.bufferForPlaybackMs,
                Constants.bufferForPlaybackAfterRebufferMs
            )
            .build()
        val trackSelector = DefaultTrackSelector(parent.context)
        trackSelector.parameters = trackSelector.buildUponParameters()
            .setForceLowestBitrate(true)
            .setAllowVideoMixedMimeTypeAdaptiveness(true)
            .setAllowMultipleAdaptiveSelections(true)
            .setForceLowestBitrate(true)
            .build()
        val bandwidthMeter = DefaultBandwidthMeter.Builder(parent.context).build()
        val exoPlayer = ExoPlayer.Builder(parent.context)
            .setBandwidthMeter(bandwidthMeter)
            .setTrackSelector(trackSelector)
            .setHandleAudioBecomingNoisy(true)
            .setLoadControl(loadControl)
            .build()

        if (holder.playerView?.player == null){
            holder.playerView?.player = exoPlayer
        }
        val videos:ArrayList<VideoUrl> = currentItem.videos_url as ArrayList<VideoUrl>
        if (videos.isNotEmpty()){
            val firstVideo = videos[0]
            if (firstVideo.original!!.isNotEmpty()){
                val mediaItem = MediaItem.Builder()
                    .setUri(firstVideo.original)
                    .build()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.seekTo(0)
            }
        }
    }
    private fun likePost(reactionType:String, reactType:String){
        LikePost(
            holder,
            currentItem,
            postType = "posts",
            token= token,
            reactionType = reactionType,
            context = context,
            reactType = reactType
        )
    }
    private fun setListener() {
        holder.likePost?.setReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactionsGift)
        holder.likePost?.setDisplayReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactions)
        holder.likePost?.setEnableReactionTooltip(true)
        holder.likePost?.enableReactionDialogDynamicPosition(true)

        holder.likePost?.apply {
            setOnReactionChangeListener(object : ReactImageButton.OnReactionChangeListener {
                override fun onReactionButtonClick(reaction: Reaction?) {
                    likePost(reaction?.reactText!!, "react")
                    Log.d("ReactImageButton", "$reaction")
                }
            })
            setOnButtonClickListener(object : ReactImageButton.OnButtonClickListener{
                override fun onBackToDefault(reaction: Reaction?) {
                    likePost("None", "unReact")
                }

                override fun onFirstReactionClick(reaction: Reaction?) {
                    likePost("Like", "react")
                }
            })
        }
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                // Night mode is not active on device
                if (currentItem.is_like!!) {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                    GlobalSetter.setDefaultReaction(holder, currentItem)
                } else {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active on device
                if (currentItem.is_like!!) {
                    holder.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                    GlobalSetter.setDefaultReaction(holder, currentItem)
                } else {
                    holder.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                }
            }
        }
        holder.likePost?.setOnReactionDialogStateListener(object :
            ReactImageButton.OnReactionDialogStateListener {
            override fun onDialogOpened() {
                Log.d(PicturePostView.TAG, "onDialogOpened")
            }

            override fun onDialogDismiss() {
                Log.d(PicturePostView.TAG, "onDialogDismiss")
            }
        })
        holder.videoLen?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, PostViewActivity::class.java)
                intent.putExtra("postId", currentItem.id)
                intent.putExtra("userAvatar", currentItem.creator_avatar)
                intent.putExtra("username", currentItem.creator)
                intent.putExtra("user_full_name", currentItem.creator_full_name)
                intent.putExtra("noOfComment", currentItem.NoOfcomment.toString())
                intent.putExtra("like", currentItem.NoOflike.toString())
                it.startActivity(intent)
            }
        }
        holder.creatorAvatar?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", currentItem.creator)
                it.startActivity(intent)
            }
        }
        holder.creatorName?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", currentItem.creator)
                it.startActivity(intent)
            }
        }
        holder.commentBtn?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            bundle.putString("userAvatar", currentItem.creator_avatar)
            bundle.putString("username", currentItem.creator)
            bundle.putString("user_full_name", currentItem.creator_full_name)
            bundle.putString("created_at", currentItem.created_at)
            bundle.putString("privacy", currentItem.privacy.toString())
            bundle.putInt("noOfcomment", currentItem.NoOfcomment ?:0)
            bundle.putInt("like", currentItem.NoOflike ?:0)
            bundle.putString("time", currentItem.created_at)
            bundle.putString("title", currentItem.title)
            val reactions = Gson().toJson(currentItem.reactions)
            bundle.putString("postType", "posts")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
        holder.noOfComments?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            bundle.putString("userAvatar", currentItem.creator_avatar)
            bundle.putString("username", currentItem.creator)
            bundle.putString("user_full_name", currentItem.creator_full_name)
            bundle.putString("created_at", currentItem.created_at)
            bundle.putString("privacy", currentItem.privacy.toString())
            bundle.putInt("noOfcomment", currentItem.NoOfcomment ?:0)
            bundle.putInt("like", currentItem.NoOflike ?:0)
            bundle.putString("time", currentItem.created_at)
            bundle.putString("title", currentItem.title)
            val reactions = Gson().toJson(currentItem.reactions)
            bundle.putString("postType", "posts")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
    }
    private fun setUi() {
        GlobalSetter.iconSetterBaseOnLike(holder = holder, currentItem = currentItem)
        holder.caption?.text = currentItem.description
        holder.creatorName?.text = currentItem.creator_full_name
        holder.createDate?.text = buildString {
            append(currentItem.created_at)
            append("･")
        }
        holder.caption?.text = currentItem.description
        Glide.with(parent.context)
            .load(currentItem.creator_avatar)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.white_background)
            .into(holder.creatorAvatar!!)

        try {
            val params = holder.thumbnail!!.layoutParams
            val height = getVideoHeight(currentItem.videos_url?.get(0)!!)
            params.height = height
            holder.thumbnail!!.layoutParams = params
            holder.thumbnail!!.scaleType = ImageView.ScaleType.CENTER_CROP
            //video height
            val videoPlayerParams = holder.playerView!!.layoutParams
            videoPlayerParams.height = height
            holder.playerView!!.layoutParams = params
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
        if(holder.thumbnail != null){
            val video:ArrayList<VideoUrl> = currentItem.videos_url as ArrayList<VideoUrl>
            if (video.isNotEmpty()){
                val firstVideo = video[0]
                if (firstVideo.original != null){
                    val url = getMediaUrl(firstVideo)
                    requestManager.load(url)
                        .override(700, 300)
                        .into(holder.thumbnail!!).toString()
                }
            }
        }

        if (currentItem.videos_url!!.isNotEmpty()){
            val len = currentItem.videos_url.size
            if (len > 1){
                holder.videoLen!!.text = context.getString(R.string.more, (len - 1).toString())
            }else{
                holder.videoLen!!.visibility = View.GONE
            }
        }else{
            holder.videoLen!!.visibility = View.GONE
        }
        when(currentItem.privacy){
            'P' -> holder.privacy?.text = parent.context.getString(R.string.fublic)
            'F' -> holder.privacy?.text = parent.context.getString(R.string.friends)
            'O' -> holder.privacy?.text = parent.context.getString(R.string.only_me)
        }
    }
    private fun getMediaUrl(data: VideoUrl):String?{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo != null && networkInfo.isConnected
        val isWiFi = networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
        try {
            return when {
                !isConnected -> data.w250 // No internet connection
                isWiFi -> data.w1000 // WiFi connection, use high resolution
                else -> data.original // Mobile data connection, use lower resolution
            }
        }catch (e:Exception){
            return null
        }
    }
    private fun getVideoHeight(video: VideoUrl): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val h = displayMetrics.heightPixels
        if (video.width!! > width) {
            val num: Double = (video.width / width).toDouble()
            val height: Int? = video.height?.div(num)?.toInt()
            Log.d(PicturePostView.TAG, height.toString())
            return height!!
        }else{
            val num = width-video.width
            val num2 = video.height?.plus(num)
            Log.d(PicturePostView.TAG, num2.toString())
            return num2!!
        }
    }
}
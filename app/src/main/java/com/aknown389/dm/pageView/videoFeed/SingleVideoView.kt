package com.aknown389.dm.pageView.videoFeed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.aknown389.dm.R
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.pageView.videoFeed.VideoFeedGlobalSetter.setDeafaultReaction
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.aknown389.dm.pageView.videoFeed.VideoFeedGlobalSetter.iconSetterBaseOnLike
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.models.videoFeedModels.VideoDataModels
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.Reaction
import com.aknown389.dm.utils.Constants.Companion.bufferForPlaybackAfterRebufferMs
import com.aknown389.dm.utils.Constants.Companion.bufferForPlaybackMs
import com.aknown389.dm.utils.Constants.Companion.maxBufferMs
import com.aknown389.dm.utils.Constants.Companion.minBufferMs
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SingleVideoView(

    private val holder: VideoFeedViewHolder,
    private val data: VideoDataModels,
    private val context: Context,
    private val mediaObjects:ArrayList<VideoDataModels>,
    private val parent: ViewGroup,
    private val adapterContext: VideoFeedAdapter,
    private val token:String,
    private val requestManager: RequestManager
) {

    init {
        setUI(holder, data)
        setListener(holder, data)
        setPlayer(holder, data)
        setDiaLog(holder, data)
    }
    private fun setDiaLog(
        holder: VideoFeedViewHolder,
        currentItem: VideoDataModels
    ) {
        holder.menuBtn?.setOnClickListener {
            if (currentItem.me == false) {
                showFriendDialog(holder, currentItem)
            }else{
                showMyDialog(holder, currentItem)
            }
        }
    }
    private fun changePrivacy(holder:  VideoFeedViewHolder, currentItem: VideoDataModels, privacy:String){
        (parent.context as? AppCompatActivity)?.let {
            it.lifecycleScope.launch(Dispatchers.Main) {
                if (isActive){
                    val response = try {
                        PostInstance.api.changePrivacy(token, currentItem.id.toString(), privacy)
                    }catch (e:Exception){
                        Log.d("Exception", e.message.toString())
                        return@launch
                    }
                    if (response.isSuccessful){
                        if (response.body()!!.status){
                            val pos = mediaObjects.indexOf(currentItem)
                            when(privacy){
                                "Public" -> currentItem.privacy = 'P'
                                "Friends" -> currentItem.privacy = 'F'
                                "Only-Me" -> currentItem.privacy = 'O'
                            }
                            adapterContext.notifyItemChanged(pos, currentItem)
                            return@launch
                        }
                    }
                }
            }
        }
    }
    private fun showDialogChangePostPrivacy(holder: VideoFeedViewHolder, currentItem: VideoDataModels){
        val dialog = BottomSheetDialog(parent.context, R.style.BottomSheetTheme)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_change_privacy, parent, false)
        val public: TextView? = view.findViewById(R.id.dialogPublic)
        val friends: TextView? = view.findViewById(R.id.friends)
        val onlyMe: TextView? = view.findViewById(R.id.onlyMe)
        dialog.setContentView(view)
        dialog.show()

        public?.setOnClickListener {
            changePrivacy(holder, currentItem, "Public")
            dialog.dismiss()
        }
        friends?.setOnClickListener {
            changePrivacy(holder, currentItem, "Friends")
            dialog.dismiss()
        }
        onlyMe?.setOnClickListener {
            changePrivacy(holder, currentItem, "Only-Me")
            dialog.dismiss()
        }
    }
    private fun showFriendDialog(holder: VideoFeedViewHolder, currentItem: VideoDataModels){
        val dialog = BottomSheetDialog(parent.context, R.style.BottomSheetTheme)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_home_feed_cardview, parent, false)
        val favorite: TextView? = view.findViewById(R.id.addtoFavorite)
        val unfollow: TextView? = view.findViewById(R.id.unfollow)
        val hidePosts: TextView? = view.findViewById(R.id.hideposts)
        val report: TextView? = view.findViewById(R.id.report)
        dialog.setContentView(view)
        dialog.show()
        favorite?.text =
            parent.context.getString(
                R.string.add_to_favorite,
                currentItem.creatorFullName
            )
        unfollow?.text =
            parent.context.getString(R.string.unfollowcode, currentItem.creatorFullName)
        hidePosts?.text =
            parent.context.getString(R.string.hide_all_postscode, currentItem.creatorFullName)
    }
    private fun showMyDialog(holder: VideoFeedViewHolder, currentItem: VideoDataModels){
        val dialog = BottomSheetDialog(parent.context, R.style.BottomSheetTheme)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_my_home_feed_cardview, parent, false)
        val changePrivacy: TextView? = view.findViewById(R.id.changePrivacy)
        val delete: TextView? = view.findViewById(R.id.deletePosts)
        val EditPosts: TextView? = view.findViewById(R.id.editPosts)
        val hidePosts: TextView? = view.findViewById(R.id.ArchivePosts)
        dialog.setContentView(view)
        dialog.show()
        changePrivacy?.setOnClickListener {
            showDialogChangePostPrivacy(holder, currentItem)
        }
        delete?.setOnClickListener {
            val builder = AlertDialog.Builder((parent.context as AppCompatActivity))
            builder.setPositiveButton("Yes"){_, _ ->
                (parent.context as? AppCompatActivity)?.let {
                    it.lifecycleScope.launch(Dispatchers.Main) {
                        val response = try {
                            PostInstance.api.deletePost(token, currentItem.id.toString())
                        }catch (e:Exception){
                            e.printStackTrace()
                            return@launch
                        }
                        if (response.isSuccessful && response.body()!!.status){
                            val body = response.body()!!
                            if (body.message == "posts deleted." || body.status){
                                try {
                                    val pos = mediaObjects.indexOf(currentItem)
                                    adapterContext.notifyItemRemoved(pos)
                                    mediaObjects.removeAt(pos)
                                }catch (e:Exception){
                                    Toast.makeText(parent.context, e.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        dialog.dismiss()
                    }
                }
            }
            builder.setNegativeButton("No"){_, _ -> }
            builder.setTitle("Delete posts?")
            builder.setMessage("Are you sure you want yo delete?")
            builder.create().show()
        }
    }

    private fun selectMediaToPlay(data: VideoDataModels){
        // Create a CoroutineScope
        val scope = CoroutineScope(Dispatchers.Main)

        // Start a new coroutine using the async function
        val deferredUrl = scope.async {
            // Switch to a background thread
            withContext(Dispatchers.IO) {
                // Get the total number of bytes received by the device
                val startBytes = TrafficStats.getTotalRxBytes()

                // Measure the number of bytes received after a delay
                val delayMillis = 5000L
                delay(delayMillis)
                val endBytes = TrafficStats.getTotalRxBytes()

                // Calculate the download speed in bytes per second
                val downloadSpeed = (endBytes - startBytes) * 1000 / delayMillis

                // Choose the media resolution based on the download speed
                when {
                    downloadSpeed < 200_000 -> "url_250_width" // Slow connection, use low resolution
                    else -> "url_1000_width" // Fast connection, use high resolution
                }
            }
        }
        //val mediaUrl = deferredUrl.await()
    }
    private fun getMediaUrl(data: VideoDataModels):String?{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo != null && networkInfo.isConnected
        val isWiFi = networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
        return when {
            !isConnected -> data.url_w250 // No internet connection
            isWiFi -> data.videosUrl // WiFi connection, use high resolution
            else -> data.url_w500 // Mobile data connection, use lower resolution
        }
    }

    private fun setPlayer(holder: VideoFeedViewHolder, data: VideoDataModels) {

        val videoMediaUrl = getMediaUrl(data = data)
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs)
            .build()
        val bandwidthMeter = DefaultBandwidthMeter.Builder(parent.context).build()
        val trackSelector = DefaultTrackSelector(parent.context)
        val exoPlayer = ExoPlayer.Builder(parent.context)
            .setBandwidthMeter(bandwidthMeter)
            .setTrackSelector(trackSelector)
            .setHandleAudioBecomingNoisy(true)
            .setLoadControl(loadControl)
            .build()
        holder.playerView?.player = exoPlayer
        val media = MediaItem.Builder()
            .setUri(videoMediaUrl)
            .build()

        exoPlayer.setMediaItem(media)
        exoPlayer.seekTo(data.playbackPosition)
    }
    private fun setListener(
        holder: VideoFeedViewHolder,
        currentItem: VideoDataModels
    ) {
        holder.userImage?.setOnClickListener {
            (parent.context as AppCompatActivity).let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", currentItem.creator)
                it.startActivity(intent)
            }
        }
        holder.name?.setOnClickListener {
            (parent.context as AppCompatActivity).let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", currentItem.creator)
                it.startActivity(intent)
            }
        }
        holder.caption?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            bundle.putString("userAvatar", currentItem.creatorAvatar)
            bundle.putString("username", currentItem.creator)
            bundle.putString("user_full_name", currentItem.creatorFullName)
            bundle.putInt("noOfcomment", currentItem.noOfComment ?:0)
            bundle.putInt("like", currentItem.noOfLike ?:0)
            bundle.putString("time", currentItem.createdAt)
            bundle.putString("title", currentItem.title)
            val reactions = Gson().toJson(currentItem.reactions)
            bundle.putString("postType", "videos")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
        holder.shareBtn?.setOnClickListener {

        }
        holder.commentBtn?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            bundle.putString("userAvatar", currentItem.creatorAvatar)
            bundle.putString("username", currentItem.creator)
            bundle.putString("user_full_name", currentItem.creatorFullName)
            bundle.putInt("noOfcomment", currentItem.noOfComment ?:0)
            bundle.putInt("like", currentItem.noOfLike ?:0)
            bundle.putString("time", currentItem.createdAt)
            bundle.putString("title", currentItem.title)
            val reactions = Gson().toJson(currentItem.reactions)
            bundle.putString("postType", "videos")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
        holder.noOfComment?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            bundle.putString("userAvatar", currentItem.creatorAvatar)
            bundle.putString("username", currentItem.creator)
            bundle.putString("user_full_name", currentItem.creatorFullName)
            bundle.putInt("noOfcomment", currentItem.noOfComment ?:0)
            bundle.putInt("like", currentItem.noOfLike ?:0)
            bundle.putString("time", currentItem.createdAt)
            bundle.putString("title", currentItem.title)
            val reactions = Gson().toJson(currentItem.reactions)
            bundle.putString("postType", "videos")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
    }
    private fun getImageHeight(img: VideoDataModels): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val h = displayMetrics.heightPixels
        if (img.width!! > width) {
            val num: Double = (img.width / width).toDouble()
            val height: Int? = img.height?.div(num)?.toInt()
            Log.d(PicturePostView.TAG, height.toString())
            return height!!
        }else{
            val num = width-img.width
            val num2 = img.height?.plus(num)
            Log.d(PicturePostView.TAG, num2.toString())
            return num2!!
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setUI(holder: VideoFeedViewHolder, data: VideoDataModels) {
        iconSetterBaseOnLike(holder = holder, currentItem = data)
        holder.name?.text = data.creatorFullName
        holder.caption?.text = data.description
        holder.time?.text = buildString {
            append(data.createdAt)
            append("･")
        }
        holder.noOfLike?.text = data.noOfLike.toString()
        holder.noOfComment?.text = data.noOfComment.toString()+ context.getString(R.string.comments)
        holder.noOfComment?.isVisible = data.noOfComment != 0
        Glide.with(parent.context)
            .load(data.creatorAvatar)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.white_background)
            .into(holder.userImage!!)
        if (data.videosUrl != null){
            this.requestManager
                .load(data.thumbnail)
                .into(holder.thumbnail!!)
        }
        try {
            val params = holder.thumbnail!!.layoutParams
            val height = getImageHeight(data)
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
                if (data.isLike!!) {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                    setDeafaultReaction(holder, data)
                } else {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active on device
                if (data.isLike!!) {
                    holder.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                    setDeafaultReaction(holder, data)
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
        when(data.privacy){
            'P' -> holder.privacy?.text = parent.context.getString(R.string.fublic)
            'F' -> holder.privacy?.text = parent.context.getString(R.string.friends)
            'O' -> holder.privacy?.text = parent.context.getString(R.string.only_me)
        }

    }
    private fun likePost(reactionType:String, reactType:String){
            LikeVideo(holder = holder, currentItem = data, token = token, context = context, reactionType = reactionType, reactType = reactType)
    }
}
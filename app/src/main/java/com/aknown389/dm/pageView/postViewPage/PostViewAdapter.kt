package com.aknown389.dm.pageView.postViewPage

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.aknown389.dm.pageView.postViewPage.PostViewGlobalSetter.defaultReaction
import com.aknown389.dm.pageView.postViewPage.Models.ToDisplayDataModel
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.Reaction
import com.aknown389.dm.utils.DataManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.gson.Gson

class PostViewAdapter(private val postDataList:ArrayList<ToDisplayDataModel>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context:Context
    private lateinit var manager:DataManager
    private lateinit var token:String
    private lateinit var parent: ViewGroup
    var playerList = ArrayList<ExoPlayer>()
    var holderList  = ArrayList<PostViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        this.context = parent.context
        this.manager = DataManager(context)
        this.token = manager.getAccessToken().toString()
        this.parent = parent
        return when(viewType){
            1 -> PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_view, parent, false))
            2 -> PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video_view, parent, false))
            3 -> PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_view_bottom, parent, false))
            4 -> PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_view, parent, false))
            5 -> PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_view_my_comment, parent, false))
            else -> PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_view, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return postDataList[position].media_type?.toInt()!!
    }
    override fun getItemCount(): Int {
        return postDataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = postDataList[position]
        when(item.media_type){
            1 -> ImagePostView(item = item,
                holder = holder as PostViewHolder,
                adapterContext = this,
                context = this.context,
                parent = this.parent,
                token = token,
                postDataList = postDataList
                )
            2 -> VideoPostView(item = item,
                holder = holder as PostViewHolder,
                adapterContext = this,
                context = this.context,
                parent = this.parent,
                token = token,
                postDataList = postDataList)

            3 -> loadType3(item, holder as PostViewHolder)
            4 -> CommentsView(holder = holder as PostViewHolder,
                adapterContext = this,
                context = this.context,
                parent = this.parent,
                token = token,
                postDataList = postDataList,
                item = item
            )
            5 -> CommentsView(
                holder = holder as PostViewHolder,
                adapterContext = this,
                context = this.context,
                parent = this.parent,
                token = token,
                postDataList = postDataList,
                item = item
            )
        }
    }
    private fun loadType3(item: ToDisplayDataModel, holder: PostViewHolder) {
        setUIType3(item, holder)
        setListenerType3(item, holder)
    }

    private fun setUIType3(item: ToDisplayDataModel, holder: PostViewHolder) {
        PostViewGlobalSetter.iconSetterBaseOnLike(holder = holder, currentItem = item)
        if (item.caption == null){
            holder.caption?.isVisible = false
        }else{
            holder.caption?.isVisible = true
            holder.caption?.text = item.caption
        }
    }

    private fun likePost(reactionType:String, reactType:String, item: ToDisplayDataModel, holder: PostViewHolder){
        LikePost(holder = holder, data = item, token = token, context = context, reactionType = reactionType, type = reactType, postType = "posts")
    }
    private fun setListenerType3(item: ToDisplayDataModel, holder: PostViewHolder) {
        holder.likePost?.setReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactionsGift)
        holder.likePost?.setDisplayReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactions)
        holder.likePost?.apply {
            setOnReactionChangeListener(object : ReactImageButton.OnReactionChangeListener {
                override fun onReactionButtonClick(reaction: Reaction?) {
                    likePost(reaction?.reactText!!, reactType = "react", holder = holder, item = item)
                    Log.d("ReactImageButton", "$reaction")
                }
            })
            setOnButtonClickListener(object : ReactImageButton.OnButtonClickListener{
                override fun onBackToDefault(reaction: Reaction?) {
                    likePost("None", reactType = "unReact", holder = holder, item = item)
                }

                override fun onFirstReactionClick(reaction: Reaction?) {
                    likePost("Like", reactType = "react", holder = holder, item = item)
                }
            })
        }
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                // Night mode is not active on device
                if (item.is_like!!) {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                    defaultReaction(holder, item)
                } else {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active on device
                if (item.is_like!!) {
                    holder.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                    defaultReaction(holder, item)
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
        holder.commenBtn?.setOnClickListener {
            goComment(item)
        }
        holder.shareBtn?.setOnClickListener {

        }
        holder.viewAllComment?.setOnClickListener {
            goComment(item)
        }
        holder.likeCommentBody?.setOnClickListener {
            goComment(item)
        }
    }
    private fun goComment(currentItem: ToDisplayDataModel) {
        val bundle: Bundle = Bundle()
        bundle.putString("postId", currentItem.id)
        bundle.putString("userAvatar", currentItem.creator_avatar)
        bundle.putString("username", currentItem.creator)
        bundle.putString("user_full_name", currentItem.creator_full_name)
        bundle.putString("created_at", currentItem.created)
        bundle.putString("privacy", currentItem.privacy.toString())
        bundle.putInt("noOfcomment", currentItem.NoOfcomment ?:0)
        bundle.putInt("like", currentItem.NoOflike ?:0)
        bundle.putString("time", currentItem.created)
        bundle.putString("title", currentItem.title)
        val reactions = Gson().toJson(currentItem.reactions)
        bundle.putString("postType", "posts")
        bundle.putString("reactions", reactions)
        val dialog = CommentDialog()
        dialog.arguments = bundle
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
        dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
    }
    fun onRelease(){
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
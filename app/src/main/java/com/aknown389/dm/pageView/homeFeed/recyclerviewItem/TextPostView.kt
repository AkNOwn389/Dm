package com.aknown389.dm.pageView.homeFeed.recyclerviewItem

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.aknown389.dm.Interface.FbReactionsListener
import com.aknown389.dm.R
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.dialog.ShowFriendDailog
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.dialog.ShowMyDialog
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.remote.Comment
import com.aknown389.dm.pageView.homeFeed.utility.GlobalSetter
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedRecyclerViewHolder
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.remote.LikePost
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.ReactImageButton.OnReactionChangeListener
import com.aknown389.dm.reactionTesting.Reaction
import com.google.gson.Gson
import javax.inject.Inject


class TextPostView @Inject constructor(


    private val holder: HomeFeedRecyclerViewHolder,
    private val currentItem: PostDataModel,
    private val context: Context,
    private val postListdata:ArrayList<PostDataModel>,
    private val parent: ViewGroup,
    private val adapterContext: HomeFeedCardViewAdapter,
    private val token:String
): FbReactionsListener {

    init {
        holder.caption?.text = currentItem.description
        holder.creatorName?.text = currentItem.creator_full_name
        holder.createDate?.text = buildString {
            append(currentItem.created_at)
            append("･")
        }
        setupLoader(currentItem, holder)
        setupListerner(holder, currentItem)
    }
    private fun setDiaLog(
        holder: HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
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

    private fun likePost(reactionType:String, reactType:String){
        LikePost(holder, currentItem, postType = "posts" , token= token, reactionType = reactionType, context = context, reactType)
    }
    private fun setupListerner(
        holder: HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
        holder.likePost?.setEnableReactionTooltip(true)
        holder.likePost?.setReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactionsGift)
        holder.likePost?.setDisplayReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactions)
        holder.likePost?.apply {
            setOnReactionChangeListener(object : OnReactionChangeListener {
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
        setDiaLog(holder, currentItem)
        holder.sendCommenBtn?.setOnClickListener {
            Comment(holder, currentItem, token)
        }
        holder.creatorAvatar?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent =
                    Intent(it, UserViewActivity::class.java).putExtra("username", currentItem.creator)
                it.startActivity(intent)
            }
        }
        holder.creatorName?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent =
                    Intent(it, UserViewActivity::class.java).putExtra("username", currentItem.creator)
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
        holder.viewallcomment?.setOnClickListener {
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

    @SuppressLint("SetTextI18n")
    private fun setupLoader(
        currentItem: PostDataModel,
        holder: HomeFeedRecyclerViewHolder
    ) {
        GlobalSetter.iconSetterBaseOnLike(holder = holder, currentItem = currentItem)
        when (currentItem.privacy) {
            'P' -> {
                holder.privacy?.text = context.getString(R.string.fublic)
            }

            'F' -> {
                holder.privacy?.text = context.getString(R.string.friendsSmall)
            }

            'O' -> {
                holder.privacy?.text = context.getString(R.string.only_me)
            }
        }
        if (currentItem.media_type == 4) {
            holder.isProfileUpdate?.isVisible = true
            holder.isProfileUpdate?.text = currentItem.title
        }
        Glide.with(context)
            .load(currentItem.your_avatar)
            .override(100, 100)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.myAvatar!!)
        Glide.with(context)
            .load(currentItem.creator_avatar)
            .override(100, 100)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.creatorAvatar!!)
    }
    companion object{
        const val TAG = "PicturePostView"
    }
}
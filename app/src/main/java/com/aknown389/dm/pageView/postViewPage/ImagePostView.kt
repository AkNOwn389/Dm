package com.aknown389.dm.pageView.postViewPage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.aknown389.dm.R
import com.aknown389.dm.activities.PhotoViewActivity
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.pageView.photoView.models.Parcel
import com.aknown389.dm.pageView.postViewPage.mappers.toImageUrl
import com.aknown389.dm.pageView.postViewPage.models.ToDisplayDataModel
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.Reaction
import com.google.gson.Gson

class ImagePostView(
    private val adapterContext:PostViewAdapter,
    private val context:Context,
    private val parent:ViewGroup,
    private val item: ToDisplayDataModel,
    private val holder: PostViewHolder,
    private val postDataList:ArrayList<ToDisplayDataModel>,
    private val token: String,
) {

    private var gson: Gson = Gson()

    init {
        setUI()
        setListener()
    }
    @SuppressLint("SetTextI18n")
    private fun setUI() {
        PostViewGlobalSetter.iconSetterBaseOnLike(holder = holder, currentItem = item)
        try {
            Glide.with(context)
                .load(item.imageUrl1000)
                .placeholder(R.mipmap.greybg)
                .error(R.mipmap.greybg)
                .into(holder.image!!)
        }catch (e:Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        if (item.NoOflike == 0){
            holder.noOfLike?.visibility = View.GONE
        }
        if (item.NoOfcomment == 0){
            holder.noOfComment?.visibility = View.GONE
        }
    }
    private fun likePost(reactionType:String, reactType:String){
        LikePost(holder = holder, data = item, token = token, context = context, reactionType = reactionType, type = reactType, postType = "postImage")
    }
    private fun setListener() {

        holder.likePost?.setReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactionsGift)
        holder.likePost?.setDisplayReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactions)
        holder.likePost?.setEnableReactionTooltip(true)
        holder.likePost?.enableReactionDialogDynamicPosition(true)

        holder.likePost?.apply {
            setOnReactionChangeListener(object : ReactImageButton.OnReactionChangeListener {
                override fun onReactionButtonClick(reaction: Reaction?) {
                    likePost(reaction?.reactText!!, reactType = "react")
                    Log.d("ReactImageButton", "$reaction")
                }
            })
            setOnButtonClickListener(object : ReactImageButton.OnButtonClickListener{
                override fun onBackToDefault(reaction: Reaction?) {
                    likePost("None", reactType = "unReact")
                }

                override fun onFirstReactionClick(reaction: Reaction?) {
                    likePost("Like", reactType = "react")
                }
            })
        }
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                // Night mode is not active on device
                if (item.isLike!!) {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                    PostViewGlobalSetter.defaultReaction(holder, item)
                } else {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active on device
                if (item.isLike!!) {
                    holder.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                    PostViewGlobalSetter.defaultReaction(holder, item)
                } else {
                    holder.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                }
            }
        }
        holder.image?.setOnClickListener {
            goToPhotoView()
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
    }
    private fun goComment(currentItem: ToDisplayDataModel) {
        val bundle: Bundle = Bundle()
        bundle.putString("postId", currentItem.ImageOrVideoId)
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

    private fun goToPhotoView(){
        val images = ArrayList<ImageUrl>()
        for (i in postDataList){
            if (i.media_type == 1){
                val imageUrl = i.toImageUrl()
                images.add(imageUrl)
            }
        }
        val pos = images.indexOf(item.toImageUrl())
        val parcel = Parcel(postId = item.ImageOrVideoId,
            userAvatar = item.avatar,
            username = item.user,
            userFullName = item.user_full_name,
            images = images,
            myPosition = pos)

        (context as? AppCompatActivity).let {
            val intent = Intent(it, PhotoViewActivity::class.java)
            intent.putExtra("parcel", gson.toJson(parcel))
            it?.startActivity(intent)
            it?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}
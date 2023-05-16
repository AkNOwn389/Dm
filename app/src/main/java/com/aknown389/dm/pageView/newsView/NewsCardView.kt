package com.aknown389.dm.pageView.newsView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.aknown389.dm.R
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.aknown389.dm.pageView.newsView.NewsGlobalSetter.iconSetterBaseOnLike
import com.aknown389.dm.pageView.newsView.NewsGlobalSetter.setDefaultReaction
import com.aknown389.dm.db.local.NewsDataEntities
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.pageView.newsView.NewsViewHolder
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.Reaction
import com.google.gson.Gson

class NewsCardView constructor(
    private val newsList: ArrayList<NewsDataEntities>,
    private val adapter: NewsAdapter,
    private val holder: NewsViewHolder,
    private val context: Context,
    private val token: String,
    private val currentItem: NewsDataEntities,

    ) {


    init {
        setUI()
        setListener()
    }

    private fun likePost(reactionType:String, reactType:String){
        LikeNews(holder = holder, currentItem = currentItem, token = token, context = context, reactionType = reactionType, reactType = reactType, postType = "news")
    }
    private fun setListener() {
        holder.likeBtn?.setReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactionsGift)
        holder.likeBtn?.setDisplayReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactions)
        holder.likeBtn?.setEnableReactionTooltip(true)
        holder.likeBtn?.enableReactionDialogDynamicPosition(true)

        holder.likeBtn?.apply {
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
                if (currentItem.is_like!!) {
                    holder.likeBtn?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                    setDefaultReaction(holder, currentItem)
                } else {
                    holder.likeBtn?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active on device
                if (currentItem.is_like!!) {
                    holder.likeBtn?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                    setDefaultReaction(holder, currentItem)
                } else {
                    holder.likeBtn?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                }
            }
        }
        holder.likeBtn?.setOnReactionDialogStateListener(object :
            ReactImageButton.OnReactionDialogStateListener {
            override fun onDialogOpened() {
                Log.d(PicturePostView.TAG, "onDialogOpened")
            }

            override fun onDialogDismiss() {
                Log.d(PicturePostView.TAG, "onDialogDismiss")
            }
        })
        holder.creatorAvatar?.setOnClickListener {

        }
        holder.creatorName?.setOnClickListener {

        }

        holder.frame?.setOnClickListener {

        }

        holder.contraintlayout?.setOnClickListener {

        }

        holder.NewsImage?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(currentItem.url)
                it.startActivity(intent)
            }
        }

        holder.content?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(currentItem.url)
                it.startActivity(intent)
            }
        }

        holder.commentBtn?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            bundle.putString("userAvatar", currentItem.avatar)
            bundle.putString("username", currentItem.news_id)
            bundle.putString("user_full_name", currentItem.name)
            bundle.putInt("noOfcomment", currentItem.noOfComment ?:0)
            bundle.putInt("like", currentItem.noOfLike ?:0)
            bundle.putString("time", currentItem.publishedAt)
            bundle.putString("title", currentItem.title)
            val reactions = Gson().toJson(currentItem.reactions)
            bundle.putString("postType", "news")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
        holder.viewallcomment?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            bundle.putString("userAvatar", currentItem.avatar)
            bundle.putString("username", currentItem.news_id)
            bundle.putString("user_full_name", currentItem.name)
            bundle.putInt("noOfcomment", currentItem.noOfComment ?:0)
            bundle.putInt("like", currentItem.noOfLike ?:0)
            bundle.putString("time", currentItem.publishedAt)
            bundle.putString("title", currentItem.title)
            val reactions = Gson().toJson(currentItem.reactions)
            bundle.putString("postType", "news")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
        holder.noOfComments?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            bundle.putString("userAvatar", currentItem.avatar)
            bundle.putString("username", currentItem.news_id)
            bundle.putString("user_full_name", currentItem.name)
            bundle.putInt("noOfcomment", currentItem.noOfComment ?:0)
            bundle.putInt("like", currentItem.noOfLike ?:0)
            bundle.putString("time", currentItem.publishedAt)
            bundle.putString("title", currentItem.title)
            val reactions = Gson().toJson(currentItem.reactions)
            bundle.putString("postType", "news")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUI() {
        iconSetterBaseOnLike(holder = holder, currentItem = currentItem)
        holder.caption!!.text = currentItem.description
        holder.title!!.text = currentItem.title

        holder.author!!.text = currentItem.author
        holder.creatorName!!.text = currentItem.name
        holder.createDate!!.text = currentItem.publishedAt

        if (currentItem.content?.contains("We use cookies") == true){
            holder.content?.visibility = View.GONE
            holder.NewsImage?.visibility = View.GONE
            holder.cardView?.visibility = View.GONE

        }else{
            holder.content!!.text = currentItem.content+" Learn more"
        }

        Glide.with(context)
            .load(currentItem.avatar)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .override(100, 100)
            .into(holder.creatorAvatar!!)

        if (currentItem.urlToImage != null && currentItem.name != "Google News"){
            Glide.with(context)
                .load(currentItem.urlToImage)
                .placeholder(R.mipmap.greybg)
                .error(R.mipmap.white_background)
                .into(holder.NewsImage!!)
        }else{
            holder.NewsImage?.visibility = View.GONE
        }
        if (currentItem.author == "" || currentItem.author.isNullOrEmpty()){
            holder.author?.visibility = View.GONE
        }
    }
}
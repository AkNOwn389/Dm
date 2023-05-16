package com.aknown389.dm.pageView.mainSearch.recyclerviewItem

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.aknown389.dm.R
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.activities.PostViewActivity
import com.aknown389.dm.activities.ProfilepageAtivity
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.mainSearchActivityModels.Data
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.mainSearch.utility.MainSearchGlobalSetter.setDeafaultReaction
import com.aknown389.dm.pageView.mainSearch.utility.Comment
import com.aknown389.dm.pageView.mainSearch.utility.LikePost
import com.aknown389.dm.pageView.mainSearch.utility.Adapter
import com.aknown389.dm.pageView.mainSearch.utility.MainSearchGlobalSetter
import com.aknown389.dm.pageView.mainSearch.utility.MainSearchViewHolder
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.Reaction
import com.google.gson.Gson

class PostSearchView(
    private val adapter: Adapter,
    private val context:Context,
    private val parent: ViewGroup,
    private val holder: MainSearchViewHolder,
    private val token:String,
    private val searchItem: ArrayList<Data>,
    private val data: Data
) {

    init {
        loadCardViewUI()
        setListenerType2(data, holder)
    }

    private fun likePost(reactionType:String, type:String){
        LikePost(
            holder = holder,
            data = data,
            token = token,
            reactionType = reactionType,
            adapter = adapter,
            context = context,
            type = type
        )
    }
    private fun setListenerType2(data: Data, holder: MainSearchViewHolder) {
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
                if (data.is_like!!) {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                    setDeafaultReaction(holder, data)
                } else {
                    holder.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active on device
                if (data.is_like!!) {
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
        holder.commentBtn?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", data.id)
            bundle.putString("userAvatar", data.creator_avatar)
            bundle.putString("username", data.username)
            bundle.putString("user_full_name", data.name)
            bundle.putInt("noOfcomment", data.NoOfcomment ?:0)
            bundle.putInt("like", data.NoOflike ?:0)
            bundle.putString("time", data.created_at)
            bundle.putString("title", data.title)
            val reactions = Gson().toJson(data.reactions)
            bundle.putString("postType", "posts")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
        holder.shareBtn?.setOnClickListener {

        }
        holder.viewAllComment?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", data.id)
            bundle.putString("userAvatar", data.creator_avatar)
            bundle.putString("username", data.username)
            bundle.putString("user_full_name", data.name)
            bundle.putInt("noOfcomment", data.NoOfcomment ?:0)
            bundle.putInt("like", data.NoOflike ?:0)
            bundle.putString("time", data.created_at)
            bundle.putString("title", data.title)
            val reactions = Gson().toJson(data.reactions)
            bundle.putString("postType", "posts")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
        holder.noOfComment?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", data.id)
            bundle.putString("userAvatar", data.creator_avatar)
            bundle.putString("username", data.username)
            bundle.putString("user_full_name", data.name)
            bundle.putInt("noOfcomment", data.NoOfcomment ?:0)
            bundle.putInt("like", data.NoOflike ?:0)
            bundle.putString("time", data.created_at)
            bundle.putString("title", data.title)
            val reactions = Gson().toJson(data.reactions)
            bundle.putString("postType", "posts")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
        }
        holder.postImage?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, PostViewActivity::class.java)
                intent.putExtra("postId", data.id)
                intent.putExtra("userAvatar", data.creator_avatar)
                intent.putExtra("username", data.creator)
                intent.putExtra("user_full_name", data.creator_full_name)
                intent.putExtra("noOfComment", data.NoOfcomment.toString())
                intent.putExtra("like", data.NoOflike.toString())
                it.startActivity(intent)
            }
        }
        holder.postsAvatar?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", data.creator)
                it.startActivity(intent)
            }
        }
        holder.postsName?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", data.creator)
                it.startActivity(intent)
            }
        }
        holder.menuBtn?.setOnClickListener {

        }
        holder.commentSendBtn?.setOnClickListener {
            Comment(
                data = data,
                holder = holder,
                token = token,
                adapter = adapter,
                context = context
            )
        }
        holder.commentInput?.setOnClickListener {

        }
        holder.myImage?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, ProfilepageAtivity::class.java)
                it.startActivity(intent)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadCardViewUI() {
        MainSearchGlobalSetter.iconSetterBaseOnLike(holder = holder, currentItem = data)
        if (data.image_url?.size == 0){
            holder.postImage?.isVisible = false
        }
        if (data.creator_avatar != null){
            Glide.with(context)
                .load(data.creator_avatar)
                .placeholder(R.drawable.progress_animation)
                .error(R.mipmap.greybg)
                .into(holder.postsAvatar!!)
        }

        if (data.image_url != null){
            if (data.image_url[0].image != null){
                Glide.with(context)
                    .load(data.image_url[0].image)
                    .placeholder(R.mipmap.greybg)
                    .error(R.mipmap.greybg)
                    .into(holder.postImage!!)
            }
        }
        if (data.your_avatar != null){
            Glide.with(context)
                .load(data.your_avatar)
                .placeholder(R.drawable.progress_animation)
                .error(R.mipmap.greybg)
                .into(holder.myImage!!)
        }

        if (data.NoOfcomment == 0){
            holder.noOfComment?.visibility = View.GONE
            holder.viewAllComment?.visibility = View.GONE
        }
        if (data.media_type == 3 || data.media_type == 4){
            holder.isProfile?.visibility = View.VISIBLE
            holder.isProfile?.text = data.title
        }
        if (data.image_url?.size!! > 1) {
            holder.imagelen?.text = "${(data.image_url.size.minus(1)).toString()}+ more"
            holder.imagelen?.visibility = View.VISIBLE
        }else{
            holder.imagelen?.visibility = View.GONE
        }
        if (data.description == null){
            holder.caption?.isVisible = false
        }else{

            holder.caption?.isVisible = true
            holder.caption?.text = data.description
        }
        holder.postsName?.text = data.creator_full_name
        holder.time?.text = data.created_at

        try {
            val img = data.image_url[0]
            val params = holder.postImage!!.layoutParams
            val height = getImageHeight(img)
            params.height = height
            holder.postImage!!.layoutParams = params
            holder.postImage!!.scaleType = ImageView.ScaleType.CENTER_CROP
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
    private fun getImageHeight(img: ImageUrl): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        //val h = displayMetrics.heightPixels
        if (img.width!! > width) {
            val num: Double = (img.width / width).toDouble()
            val height: Int? = img.heigth?.div(num)?.toInt()
            Log.d(PicturePostView.TAG, height.toString())
            return height!!
        }else{
            val num = width-img.width
            val num2 = img.heigth?.plus(num)
            Log.d(PicturePostView.TAG, num2.toString())
            return num2!!
        }
    }
}
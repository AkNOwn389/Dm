package com.aknown389.dm.pageView.homeFeed.recyclerviewItem

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.aknown389.dm.R
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.activities.PhotoViewActivity
import com.aknown389.dm.activities.PostViewActivity
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.dialog.ShowFriendDailog
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.dialog.ShowMyDialog
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.remote.Comment
import com.aknown389.dm.pageView.homeFeed.utility.GlobalSetter
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedRecyclerViewHolder
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.remote.LikePost
import com.aknown389.dm.pageView.photoView.models.Parcel
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.Reaction
import com.google.gson.Gson
import javax.inject.Inject


class PicturePostView @Inject constructor(
    private val holder: HomeFeedRecyclerViewHolder,
    private val currentItem: PostDataModel,
    private val context:Context,
    private val postListdata:ArrayList<PostDataModel>,
    private val parent:ViewGroup,
    private val adapterContext: HomeFeedCardViewAdapter,
    private val token:String
) {
    private val gson = Gson()
    private var imageToDisplay:String? = null
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



    private fun getImageHeight(img: ImageUrl): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        //val h = displayMetrics.heightPixels
        if (img.width!! > width) {
            val num: Double = (img.width / width).toDouble()
            val height: Int? = img.height?.div(num)?.toInt()
            Log.d(TAG, height.toString())
            return height!!
        }else{
            val num = width-img.width
            val num2 = img.height?.plus(num)
            Log.d(TAG, num2.toString())
            return num2!!
        }
    }
    private fun setupListerner(
        holder: HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
        setDiaLog(holder, currentItem)
        holder.likePost?.setReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactionsGift)
        holder.likePost?.setDisplayReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactions)
        holder.likePost?.setEnableReactionTooltip(true)
        holder.likePost?.enableReactionDialogDynamicPosition(true)

        holder.likePost?.apply {
            setOnReactionChangeListener(object : ReactImageButton.OnReactionChangeListener {
                override fun onReactionButtonClick(reaction: Reaction?) {
                    likePost(reaction?.reactText!!, "react")
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
                Log.d(TAG, "onDialogOpened")
            }

            override fun onDialogDismiss() {
                Log.d(TAG, "onDialogDismiss")
            }
        })
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
        holder.postImage?.setOnClickListener {
            if (currentItem.image_url?.size!! > 1){
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
            }else{
                val images = ArrayList<ImageUrl>()
                currentItem.image_url[0]?.let { it1 -> images.add(it1) }
                val parcel = Parcel(postId = currentItem.id,
                    userAvatar = currentItem.creator_avatar,
                    username = currentItem.creator,
                    userFullName = currentItem.creator_full_name,
                    images = images)
                if (images.size != 0){
                    (context as? AppCompatActivity).let {
                        val intent = Intent(it, PhotoViewActivity::class.java)
                        intent.putExtra("parcel", gson.toJson(parcel))
                        it?.startActivity(intent)
                        it?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    }
                }
            }
        }
        holder.commentBtn?.setOnClickListener {
            val bundle:Bundle = Bundle()
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
            val bundle:Bundle = Bundle()
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
            val bundle:Bundle = Bundle()
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
        try {
            val img = currentItem.image_url?.get(0)
            val params = holder.postImage!!.layoutParams
            val height = getImageHeight(img!!)
            params.height = height
            holder.postImage!!.layoutParams = params
            holder.postImage!!.scaleType = ImageView.ScaleType.CENTER_CROP
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
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

        try {
            val img = currentItem.image_url?.get(0)
            if (img != null) {
                if (img.imgW1000 != null) {
                    Glide.with(context)
                        .load(img.imgW1000)
                        .error(R.mipmap.greybg)
                        .into(holder.postImage!!)
                } else {
                    Glide.with(context)
                        .load(img.original)
                        .error(R.mipmap.greybg)
                        .into(holder.postImage!!)
                }
            }

            if (currentItem.image_url!!.size > 1) {
                holder.imagelenght?.isVisible = true
                holder.imagelenght?.text = "${currentItem.image_url.size - 1}+ more"
            } else {
                holder.imagelenght?.isVisible = false
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            e.stackTrace
        }
    }
    companion object{
        const val TAG = "PicturePostView"
    }
}

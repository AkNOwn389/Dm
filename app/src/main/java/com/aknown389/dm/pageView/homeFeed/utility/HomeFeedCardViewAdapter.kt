package com.aknown389.dm.pageView.homeFeed.utility

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R
import com.aknown389.dm.activities.HomePostActivity
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.diffUtil.HomeFeedDiffUtil
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.ChangeProfileView
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.MultiVideoPostView
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.SingleVideoPostView
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.TextPostView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.NullPointerException


class HomeFeedCardViewAdapter(
    private val requestManager: RequestManager
): RecyclerView.Adapter<ViewHolder>() {
    private val TAG = "HOME PAGE LOG"
    private lateinit var context:Context
    private lateinit var manager: DataManager
    private lateinit var token: String
    private lateinit var username:String
    private lateinit var commentFragment: CommentDialog
    private lateinit var parent: ViewGroup
    val postListdata = ArrayList<PostDataModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFeedRecyclerViewHolder {
        this.context = parent.context
        this.parent = parent
        return when(viewType){
            99 -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_feed_create_post_bar, parent, false))
            1 -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed_cardview_text, parent, false))
            2 -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed_cardview, parent, false))
            3 -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed_change_profile_picture, parent, false))
            6 -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_card_video, parent, false))
            7 -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_card_multi_video, parent, false))
            else -> HomeFeedRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed_cardview, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return try {
            postListdata[position].media_type!!
        }catch (e:Exception){
            1
        }
    }

    override fun getItemCount(): Int {
        return postListdata.size
    }

    override fun onBindViewHolder(viewGroup: ViewHolder, position: Int) {
        try {
            this.manager = DataManager(context)
            this.commentFragment = CommentDialog()
            this.token = manager.getAccessToken().toString()
            this.username = manager.getUserData()!!.user ?: "null"
        }catch (e:NullPointerException){
            e.printStackTrace()
        }
        (viewGroup as HomeFeedRecyclerViewHolder).onBind(requestManager = this.requestManager)
        val currentItem = postListdata[position]
        when (currentItem.media_type){
            99 -> (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main){
                loadCreatePostBar(viewGroup, currentItem)
            }
            1 -> (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
                TextPostView(
                    holder = viewGroup,
                    postListdata = postListdata,
                    adapterContext = this@HomeFeedCardViewAdapter,
                    context = context,
                    token = token,
                    currentItem = currentItem,
                    parent = parent
                )
            }
            2 ->(context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main){
                PicturePostView(
                    holder = viewGroup,
                    postListdata = postListdata,
                    adapterContext = this@HomeFeedCardViewAdapter,
                    context = context,
                    token = token,
                    currentItem = currentItem,
                    parent = parent
                )
            }
            3 ->(context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main){
                ChangeProfileView(
                    holder = viewGroup,
                    postListdata = postListdata,
                    adapterContext = this@HomeFeedCardViewAdapter,
                    context = context,
                    token = token,
                    currentItem = currentItem,
                    parent = parent
                )
            }
            6 -> (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main){
            SingleVideoPostView(
                holder = viewGroup,
                postListdata = postListdata,
                adapterContext = this@HomeFeedCardViewAdapter,
                context = context,
                token = token,
                currentItem = currentItem,
                parent = parent,
                requestManager = requestManager
            )
        }
            7 -> (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main){
            MultiVideoPostView(
                holder = viewGroup,
                postListdata = postListdata,
                adapterContext = this@HomeFeedCardViewAdapter,
                context = context,
                token = token,
                currentItem = currentItem,
                parent = parent,
                requestManager = requestManager
            )
        }
            else -> (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main){
                PicturePostView(
                    holder = viewGroup,
                    postListdata = postListdata,
                    adapterContext = this@HomeFeedCardViewAdapter,
                    context = context,
                    token = token,
                    currentItem = currentItem,
                    parent = parent
                )
            }
        }
    }
    private fun loadCreatePostBar(
        holder: HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
        setCreatePostBarImage(holder)
        holder.createPostBar?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, HomePostActivity::class.java)
                it.startActivity(intent)
            }
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun setCreatePostBarImage(holder: HomeFeedRecyclerViewHolder){
        GlobalScope.launch(Dispatchers.Main) {
            val response = try {
                RetrofitInstance.api.getAvatarR(username, token)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                Glide.with(context)
                    .load(response.body()!!.avatar)
                    .placeholder(R.drawable.progress_animation)
                    .override(100, 100)
                    .error(R.mipmap.greybg)
                    .into(holder.createpostbarimage!!)
            }
        }
    }

    fun setData(newData: ArrayList<PostDataModel>) {
        val diffResult = DiffUtil.calculateDiff(HomeFeedDiffUtil(old = this.postListdata, new = newData))
        this.postListdata.addAll(newData)
        diffResult.dispatchUpdatesTo(object : ListUpdateCallback{
            override fun onInserted(position: Int, count: Int) {
                notifyItemInserted(position)
            }
            override fun onRemoved(position: Int, count: Int) {
                notifyItemRemoved(position)
                try {
                    if (position != 0){
                        postListdata.removeAt(position)
                    }
                }catch (E:Exception){
                    E.printStackTrace()
                }
            }
            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyItemRangeChanged(position, count, payload)
            }
        })
    }
}
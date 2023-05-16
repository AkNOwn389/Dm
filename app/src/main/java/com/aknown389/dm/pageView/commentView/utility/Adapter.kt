package com.aknown389.dm.pageView.commentView.utility



import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R
import com.aknown389.dm.pageView.commentView.items.DeletedComment
import com.aknown389.dm.pageView.commentView.items.ImageComment
import com.aknown389.dm.pageView.commentView.items.TextComment
import com.aknown389.dm.pageView.commentView.mapper.toPostReaction
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.models.NewChangeReaction
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Adapter(): RecyclerView.Adapter<ViewHolder>() {
    private lateinit var context: Context
    private lateinit var token:String
    private lateinit var manager:DataManager
    private lateinit var parent: ViewGroup
    private var postType:String? = null
    private var comments:ArrayList<Data> = ArrayList()
    private var holderList:ArrayList<CommentViewHolder> = ArrayList()
    override fun getItemViewType(position: Int): Int {
        val item = comments[position]
        return when(item.type){
            1-> {if (item.me == true){11}else{1}}
            2 -> {if (item.me == true){22}else{2}}
            3 -> {if (item.me == true){33}else{3}}
            else -> 0
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        this.context = parent.context
        this.manager = DataManager(context)
        this.parent = parent
        this.token = manager.getAccessToken().toString()
        return when (viewType){
            1 -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_view, parent, false))
            2 -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_view_image_others, parent, false))
            3 -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_for_deleted_comment, parent, false))
            11 -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_view_my_comment, parent, false))
            22 -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_view_image, parent, false))
            33 -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_for_my_deleted_comment, parent, false))
            else -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_view, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Exc", position.toString())
        holderList.add(holder as CommentViewHolder)
        val myitem = comments[position]
        when(myitem.type){
            1 -> {
                TextComment(
                    holder = holder as CommentViewHolder,
                    currentItem = myitem,
                    adapterContext = this,
                    context = context,
                    comments = comments,
                    parent = parent,
                    postType = postType,
                    )
            }
            2 -> {
                ImageComment(
                    holder = holder as CommentViewHolder,
                    currentItem = myitem,
                    adapterContext = this,
                    token = token,
                    context = context,
                    comments = comments,
                    parent = parent,
                    postType = postType,
                )
            }
            3 -> {
                DeletedComment(
                    holder = holder as CommentViewHolder,
                    currentItem = myitem,
                    context = context,
                )
            }
        }
    }

    fun setData(data:ArrayList<Data>){
        this.comments = data
    }
    fun setPostType(type:String){
        this.postType = type
    }

    fun newCommentDeleted(data: Data) {
        (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
            for (i in comments){
                if (i.id == data.id){
                    val pos = comments.indexOf(i)
                    i.type = 3
                    i.comments = ""
                    i.user = ""
                    i.avatar = ""
                    notifyItemChanged(pos)
                }
            }
        }
    }
    fun newChangeReaction(data: NewChangeReaction){
        for (i in holderList){
            if (i.myData != null && i.myData?.id != null){
                if (data.id == i.myData?.id){
                    i.newReactions(reactions = data.reactions.toPostReaction())
                }
            }
        }
    }

}
package com.aknown389.dm.activities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityPostViewBinding
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.postViewPage.Models.Data
import com.aknown389.dm.pageView.postViewPage.PostViewAdapter
import com.aknown389.dm.pageView.postViewPage.remote.instance.Instance
import com.aknown389.dm.api.retroInstance.CommentInstance
import com.aknown389.dm.pageView.postViewPage.Models.ToDisplayDataModel
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PostViewActivity : AppCompatActivity() {
    private var binding: ActivityPostViewBinding?=null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostViewAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var manager:DataManager
    private lateinit var token:String
    private lateinit var postDataList:ArrayList<ToDisplayDataModel>
    private var postID:String? = null
    private var userAvatar:String? = null
    private var userFullName:String? = null
    private var username:String? = null
    private var noOfComment:Int? = null
    private var noOfLike:Int? = null
    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityPostViewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setVal()
        setUI()
        setListener()
        loadView()
    }
    private fun Finish(){
        finish()
    }

    private fun loadView() {
        this.isLoading = true
        binding?.shimmerPlaceholder?.visibility = View.VISIBLE
        binding?.shimmerPlaceholder?.startShimmer()
        invisible()
        lifecycleScope.launch(Dispatchers.Main) {
            val response = try {
                  Instance(context = this@PostViewActivity).api.getPostData(postID.toString())
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful ) {
                if (response.body()!!.message == "Not found" || !response.body()!!.status) {
                    Toast.makeText(this@PostViewActivity, "Post not exists", Toast.LENGTH_SHORT)
                        .show()
                    Finish()
                }
                if (response.body()!!.status) {
                    delay(500)
                    binding?.recyclerView?.visibility = View.VISIBLE
                    binding?.shimmerPlaceholder?.stopShimmer()
                    binding?.shimmerPlaceholder?.visibility = View.GONE
                    visible()
                    val body = response.body()!!.data
                    setData(body)
                    for (i in body.image_url!!) {
                        val image = ToDisplayDataModel(
                            NoOfcomment = i.NoOfcomment,
                            NoOflike = i.NoOflike,
                            media_type = 1,
                            image_url = i.url_w1000,
                            id = i.id,
                            creator = body.creator,
                            creator_avatar = body.creator_avatar,
                            creator_full_name = body.creator_full_name,
                            is_like = i.is_like,
                            reactions = i.reactions,
                            reactionType = i.reactionType

                        )
                        if (image !in postDataList) {
                            this@PostViewActivity.postDataList.add(image)
                            this@PostViewActivity.adapter.notifyItemInserted(
                                postDataList.size.minus(1)
                            )
                            delay(200)
                        }
                    }
                    for (i in body.videos_url) {
                        val image = ToDisplayDataModel(
                            NoOfcomment = body.NoOfcomment,
                            NoOflike = body.NoOflike,
                            media_type = 2,
                            id = body.id.toString(),
                            videos_url = i.w1000,
                            creator = body.creator,
                            creator_avatar = body.creator_avatar,
                            creator_full_name = body.creator_full_name,
                            is_like = body.is_like,
                            reactions = body.reactions
                        )
                        if (image !in postDataList) {
                            this@PostViewActivity.postDataList.add(image)
                            this@PostViewActivity.adapter.notifyItemInserted(postDataList.size - 1)
                            delay(200)
                        }
                    }

                    val bottom = ToDisplayDataModel(
                        NoOfcomment = body.NoOfcomment,
                        NoOflike = body.NoOflike,
                        media_type = 3,
                        id = body.id,
                        creator = body.creator,
                        creator_avatar = body.creator_avatar,
                        creator_full_name = body.creator_full_name,
                        is_like = body.is_like,
                        caption = body.description,
                        title = body.title,
                        reactions = body.reactions,
                        reactionType = body.reactionType
                    )
                    if (bottom !in postDataList) {
                        this@PostViewActivity.postDataList.add(bottom)
                        this@PostViewActivity.adapter.notifyItemInserted(postDataList.size - 1)
                    }
                    loadComment()
                    delay(3000)
                    setCommentListener()
                }
            }
            this@PostViewActivity.isLoading = false
        }
    }
    private fun setCommentListener(){
        lifecycleScope.launch {
            while (true){
                if (isActive){
                    val response = try {
                        CommentInstance(applicationContext).api.getComment(postId = postID.toString(), page = 1)
                    }catch (e:Exception){
                        e.printStackTrace()
                        continue
                    }
                    if (response.isSuccessful && response.body()!!.status){
                        if (response.body()!!.data.isEmpty()){
                            delay(4000)
                            continue
                        }
                        for (i in response.body()!!.data){
                            val type = if (i.me == true){ 5 }else{ 4 }
                            val data = ToDisplayDataModel(
                                comments = i.comments,
                                avatar= i.avatar,
                                created = i.created,
                                commentId = i.id.toString(),
                                post_id = i.postId,
                                user = i.user,
                                type = i.type,
                                me = i.me,
                                Followed = i.followed,
                                Follower = i.follower,
                                user_full_name = i.userFullName,
                                media_type = type,
                                NoOflike = i.noOfLike
                            )
                            var go = true
                            for (comment in postDataList){
                                if (data.commentId == comment.commentId){
                                    go = false
                                }
                            }
                            if (go){
                                this@PostViewActivity.postDataList.add(data)
                                this@PostViewActivity.adapter.notifyItemInserted(postDataList.size -1)
                                try {
                                    if (layoutManager.findFirstCompletelyVisibleItemPosition()+ layoutManager.childCount > adapter.itemCount -3){
                                        this@PostViewActivity.layoutManager.scrollToPosition(postDataList.size-1)
                                    }
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    delay(4000)
                }else{return@launch}
            }
        }
    }

    private fun loadComment() {
        lifecycleScope.launch {
            val response = try {
                CommentInstance(applicationContext).api.getComment(postId = postID.toString(), page = 1)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                if (response.body()!!.data.isEmpty()){
                    return@launch
                }
                for (i in response.body()!!.data.reversed()){
                    val type = if (i.me == true){ 5 }else{ 4 }
                    val data = ToDisplayDataModel(
                        comments = i.comments,
                        avatar= i.avatar,
                        created = i.created,
                        commentId = i.id.toString(),
                        post_id = i.postId,
                        user = i.user,
                        type = i.type,
                        me = i.me,
                        Followed = i.followed,
                        Follower = i.follower,
                        user_full_name = i.userFullName,
                        media_type = type,
                        NoOflike = i.noOfLike
                    )
                    this@PostViewActivity.postDataList.add(data)
                    this@PostViewActivity.adapter.notifyItemInserted(postDataList.size-1)
                }
            }
        }
    }

    private fun setData(body: Data) {
        when (body.privacy) {
            'P' -> {
                binding?.privacy?.text = getString(R.string.fublic)
            }

            'F' -> {
                binding?.privacy?.text = getString(R.string.friendsSmall)
            }

            'O' -> {
                binding?.privacy?.text = getString(R.string.only_me)
            }
        }
        try {
            //binding?.postCardHomeTimeCreated?.text = body.created_at
            binding?.postCardHomeTimeCreated?.text = buildString {
                append(body.created_at)
                append("･")
            }
            if (body.media_type == 3 || body.media_type == 4) {
                binding?.title?.text = body.title
                binding?.title?.isVisible = true
                binding?.cardView44?.isVisible = true
            } else {
                binding?.title?.isVisible = false
                binding?.cardView44?.isVisible = false
            }
        }catch (e:Exception){return}
    }

    private fun visible(){
        binding?.userImage?.visibility = View.VISIBLE
        binding?.userName?.visibility = View.VISIBLE
        binding?.postCardHomeTimeCreated?.visibility = View.VISIBLE
    }
    private fun invisible(){
        binding?.recyclerView?.visibility = View.GONE
        binding?.userImage?.visibility = View.GONE
        binding?.userName?.visibility = View.GONE
        binding?.postCardHomeTimeCreated?.visibility = View.GONE
    }
    private fun setUI() {
        binding?.userName?.text = userFullName
        Glide.with(this)
            .load(userAvatar)
            .placeholder(R.drawable.progress_animation)
            .into(binding?.userImage!!)
    }

    private fun setListener() {
        binding?.backbtn?.setOnClickListener {
            finish()
        }
    }

    private fun setVal() {
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        this.postID = intent.getStringExtra("postId")
        this.noOfComment = intent.getStringExtra("noOfComment")?.toInt()
        this.noOfLike = intent.getStringExtra("like")?.toInt()
        this.username = intent.getStringExtra("username")
        this.userFullName = intent.getStringExtra("user_full_name")
        this.userAvatar = intent.getStringExtra("userAvatar")
        this.recyclerView = binding?.recyclerView!!
        this.layoutManager = LinearLayoutManager(this)
        this.postDataList = ArrayList<ToDisplayDataModel>()
        this.adapter = PostViewAdapter(postDataList)
        recyclerView.layoutManager = this.layoutManager
        recyclerView.adapter = adapter
    }

    override fun onPause() {
        adapter.onPlayerPause()
        super.onPause()
    }
    override fun onDestroy() {
        binding = null
        adapter.onRelease()
        super.onDestroy()
    }
}
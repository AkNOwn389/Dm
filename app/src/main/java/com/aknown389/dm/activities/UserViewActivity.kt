package com.aknown389.dm.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityUserViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.api.retroInstance.ProfileInstance
import com.aknown389.dm.api.retroInstance.UsersInstance
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.models.userviewModels.Data
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.snackbar
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class UserViewActivity : AppCompatActivity() {
    private var binding: ActivityUserViewBinding? = null
    private lateinit var username: String
    private lateinit var user_name:TextView
    private lateinit var userName:TextView
    private lateinit var userPosts:TextView
    private lateinit var userFollowing:TextView
    private lateinit var userFollowers:TextView
    private lateinit var userImage:ImageView
    private lateinit var onBack:ImageButton
    private lateinit var menuBtn:ImageButton
    private lateinit var userBackgroundImage:ImageView
    private lateinit var swiper:SwipeRefreshLayout
    private lateinit var layotroot:ConstraintLayout
    private lateinit var userBio:TextView
    private lateinit var userData:Data
    private lateinit var manager: DataManager
    private lateinit var token:String
    private lateinit var layOutManager:LinearLayoutManager
    private lateinit var adapter: HomeFeedCardViewAdapter
    private var postList = ArrayList<PostDataModel>()
    private lateinit var shimmer:ShimmerFrameLayout
    private var page by Delegates.notNull<Int>()
    private var isLoading = false
    private var hasMorePage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityUserViewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setVal()
        getUserData()
        setListener()
        loadPostList()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
    private fun updatePost() {
        this.page+=1
        lifecycleScope.launch{
            this@UserViewActivity.isLoading = true
            val response = try {
                PostInstance.api.postview(token = token, user = username, page = page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                if (!response.body()!!.hasMorePage){
                    this@UserViewActivity.hasMorePage = false
                }
                for (x in response.body()!!.data){
                    this@UserViewActivity.postList.add(postList.size-1, x)
                    this@UserViewActivity.adapter.notifyItemInserted(postList.size-1)
                }

            }
            this@UserViewActivity.isLoading = false
        }
    }
    private fun initGlide(): RequestManager {
        val options = RequestOptions()
            .placeholder(R.mipmap.white_background)
            .error(R.mipmap.white_background)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }
    private fun loadPostList() {
        this.page = 1
        this.hasMorePage = true
        this.shimmer.startShimmer()
        this.shimmer.isVisible = true
        binding?.recyClerView?.isVisible = false
        lifecycleScope.launch {
            this@UserViewActivity.isLoading = true
            val response = try {
                PostInstance.api.postview(token, username, page)
            }catch (e:java.lang.Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                for (i in response.body()!!.data){
                    this@UserViewActivity.postList.add(i)
                    adapter.notifyItemInserted(postList.size-1)
                }
            }
            delay(1000)
            withContext(Dispatchers.Main){
                this@UserViewActivity.shimmer.stopShimmer()
                this@UserViewActivity.shimmer.isVisible = false
                this@UserViewActivity.binding?.recyClerView?.isVisible = true
            }
            this@UserViewActivity.isLoading = false
        }
    }

    private fun setListener() {
        swiper.setOnRefreshListener {
            getUserData()
            loadPostList()
            swiper.isRefreshing = false
        }
        onBack.setOnClickListener {
            finish()
        }
        binding?.userviewfollowbtn?.setOnClickListener {
            lifecycleScope.launch {
                val response = try {
                    UsersInstance.api.followUser(token, username)
                }catch (e:Exception){
                    e.printStackTrace()
                    return@launch
                }
                if (response.isSuccessful){
                    val body = response.body()!!
                    if (body.message == "following"){
                        binding?.userviewfollowbtn?.text = getString(R.string.followed)
                    }else{
                        binding?.userviewfollowbtn?.text = getString(R.string.follow)
                    }
                }
            }
        }
        binding?.recyClerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val visibleItemCount: Int = layOutManager.childCount
                val pastVisibleItem: Int = layOutManager.findFirstCompletelyVisibleItemPosition()

                if (!isLoading){
                    if (visibleItemCount + pastVisibleItem > adapter.itemCount-1){
                        if (!isLoading && hasMorePage){
                            updatePost()
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        binding?.recyClerView?.onResumePlayer()
        super.onResume()
    }
    override fun onPause() {
        binding?.recyClerView?.onPausePlayer()
        super.onPause()
    }



    private fun setVal() {
        this.layOutManager = LinearLayoutManager(this)
        this.adapter = HomeFeedCardViewAdapter(initGlide())
        binding?.recyClerView?.adapter = adapter
        this@UserViewActivity.binding?.recyClerView?.layoutManager = layOutManager
        this.shimmer = findViewById(R.id.userviewshimmering)
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        this.user_name = findViewById(R.id.userviewName)
        this.userName = findViewById(R.id.userviewUsername)
        this.userPosts = findViewById(R.id.userviewuserpostslenght)
        this.userFollowers = findViewById(R.id.userviewfollowers)
        this.userFollowing = findViewById(R.id.userviewfollowing)
        this.userBio = findViewById(R.id.userviewBio)
        this.userImage = findViewById(R.id.userviewprofileimageImageview)
        this.userBackgroundImage = findViewById(R.id.userviewbackgroundPictureImageview)
        this.swiper = findViewById(R.id.userviewswiper)
        this.layotroot = findViewById(R.id.fragmentUserviewRoot)
        this.onBack = findViewById(R.id.userviewbackbtn)
        this.menuBtn = findViewById(R.id.userviewmenubtn)
        this.username = intent?.getStringExtra("username").toString()
    }

    private fun getUserData() {
        lifecycleScope.launch {
            val response = try {
                ProfileInstance.api.profileview(token, username)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                this@UserViewActivity.userData = response.body()!!.data
                withContext(Dispatchers.Main){
                    user_name.text = userData.name
                    userName.text = userData.user
                    userBio.text = userData.bio
                    userFollowers.text = userData.followers.toString()
                    userFollowing.text = userData.following.toString()
                    userPosts.text = userData.post_lenght.toString()
                    if (userData.isFollowing){
                        binding?.userviewfollowbtn?.text = getString(R.string.followed)
                    }

                    Glide.with(this@UserViewActivity)
                        .load(userData.profileimg)
                        .error(R.mipmap.greybg)
                        .placeholder(R.drawable.progress_animation)
                        .into(userImage)
                    Glide.with(this@UserViewActivity)
                        .load(userData.bgimg)
                        .error(R.mipmap.greybg)
                        .placeholder(R.mipmap.greybg)
                        .into(userBackgroundImage)
                }
            }else{
                layotroot.snackbar("Internet Error")
            }
        }
    }
}
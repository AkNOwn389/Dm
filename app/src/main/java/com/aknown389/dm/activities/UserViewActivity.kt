package com.aknown389.dm.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
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
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.models.userviewModels.Data
import com.aknown389.dm.pageView.userProfileView.viewModel.UserProfileViewModel
import com.aknown389.dm.pageView.userProfileView.viewModel.UserViewModelProvider
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.snackbar
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class UserViewActivity : AppCompatActivity() {
    private var isShimmering: Boolean = false
    private var binding: ActivityUserViewBinding? = null
    private lateinit var username: String
    private lateinit var userData:Data
    private lateinit var manager: DataManager
    private lateinit var token:String
    private lateinit var layOutManager:LinearLayoutManager
    private lateinit var adapter: HomeFeedCardViewAdapter
    private lateinit var shimmer:ShimmerFrameLayout
    private lateinit var viewModel:UserProfileViewModel


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
        viewModel.updatePost(token, username)
    }
    private fun initGlide(): RequestManager {
        val options = RequestOptions()
            .placeholder(R.mipmap.white_background)
            .error(R.mipmap.white_background)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }
    private fun loadPostList() {
        startShimmer()
        viewModel.loadPostList(token, username)
    }

    private fun startShimmer() {
        shimmer.startShimmer()
        shimmer.isVisible = true
        binding?.recyClerView?.isVisible = false
        isShimmering = true
    }

    private fun stopShimmer(){
        shimmer.stopShimmer()
        shimmer.isVisible = false
        binding?.recyClerView?.isVisible = true
        isShimmering = false
    }

    private fun setListener() {
        binding?.userviewswiper?.setOnRefreshListener {
            getUserData()
            loadPostList()
            binding?.userviewswiper?.isRefreshing = false
        }
        binding?.userviewbackbtn?.setOnClickListener {
            finish()
        }
        viewModel._response.observe(this) {response ->
            adapter.setData(response as ArrayList<PostDataModel>)
            if (isShimmering){
                stopShimmer()
            }
        }
        viewModel._userDataResponse.observe(this){ response ->
            try {
                this.userData = response.data
                binding?.apply {
                    userviewName.text = userData.name
                    userviewUsername.text = userData.user
                    userviewBio.text = userData.bio
                    userviewfollowers.text = userData.followers.toString()
                    userviewfollowing.text = userData.following.toString()
                    userviewuserpostslenght.text = userData.post_lenght.toString()
                    Glide.with(this@UserViewActivity)
                        .load(userData.profileimg)
                        .error(R.mipmap.greybg)
                        .placeholder(R.drawable.progress_animation)
                        .into(userviewprofileimageImageview)
                    Glide.with(this@UserViewActivity)
                        .load(userData.bgimg)
                        .error(R.mipmap.greybg)
                        .placeholder(R.mipmap.greybg)
                        .into(userviewbackgroundPictureImageview)
                }
                if (userData.isFollowing){
                    binding?.userviewfollowbtn?.text = getString(R.string.followed)
                }
            }catch (e:Exception){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }

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

                if (!viewModel.isLoading){
                    if (visibleItemCount + pastVisibleItem > adapter.itemCount-1){
                        if (!viewModel.isLoading && viewModel.hasMorePage){
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
        binding?.recyClerView?.setMediaObjects(adapter.postListdata)
        this@UserViewActivity.binding?.recyClerView?.layoutManager = layOutManager
        this.shimmer = findViewById(R.id.userviewshimmering)
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        this.username = intent?.getStringExtra("username").toString()
        val database = AppDataBase.getDatabase(this)
        this.viewModel = ViewModelProvider(this, UserViewModelProvider(database))[UserProfileViewModel::class.java]
    }

    private fun getUserData() {
        viewModel.getUserdata(token, username)
    }
}
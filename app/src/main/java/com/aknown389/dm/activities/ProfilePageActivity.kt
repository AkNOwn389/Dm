package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityProfilepageBinding
import com.bumptech.glide.Glide
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.dialogs.DialogProfileSettings
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.pageView.homeFeed.utility.SpeedLinearLayoutManager
import com.aknown389.dm.pageView.profile.dataClass.toPostDataModel
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.pageView.profile.viewModels.ProfileViewModel
import com.aknown389.dm.pageView.profile.viewModels.ProfileViewModelFactory
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.launch
import java.lang.Exception

class ProfilePageActivity : AppCompatActivity()
{
    private var isProgressRunning: Boolean = false
    private var binding: ActivityProfilepageBinding? = null
    private lateinit var viewModel: ProfileViewModel
    private lateinit var dialog: DialogProfileSettings
    private lateinit var token:String
    private lateinit var manager: DataManager
    private lateinit var adapter: HomeFeedCardViewAdapter
    private lateinit var layoutManager: SpeedLinearLayoutManager
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilepageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setValue()
        setup()
    }

    private fun setup(){
        lifecycleScope.launch {
            loadMe()
            setListener()
            loadPostList()
        }
    }

    private fun startUpdatingProgress(){
        binding?.updatingProgress?.visibility = View.VISIBLE
        isProgressRunning = true
    }
    private fun stopUpdatingProgress(){
        binding?.updatingProgress?.visibility = View.GONE
        isProgressRunning = false
    }

    private fun updateActivity() {
        startUpdatingProgress()
        viewModel.updateActivity()
    }

    private fun loadPostList() {
        startUpdatingProgress()
        viewModel.loadPostList()
    }

    private fun setValue() {
        manager = DataManager(this)
        token = manager.getAccessToken().toString()
        val repository = Repository()
        val db = AppDataBase.getDatabase(this)
        val viewModelFactory = ProfileViewModelFactory(repository = repository, db = db, token = token)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
        adapter = HomeFeedCardViewAdapter(initGlide())
        layoutManager = SpeedLinearLayoutManager(this)
        binding?.recyclerView?.setMediaObjects(adapter.postListdata)
        binding?.recyclerView?.adapter = adapter
        binding?.recyclerView?.layoutManager = layoutManager
        this.dialog = DialogProfileSettings()
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
        handler = Handler(Looper.getMainLooper())
    }

    private fun backToTop(){
        val goToTop = Runnable {
            // Send a message to the server via WebSocket
            layoutManager.scrollToPosition(0)
        }

        val smoothScroller = object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0
        layoutManager.startSmoothScroll(smoothScroller)
        handler?.postDelayed(goToTop, 2000)
    }
    private fun initGlide(): RequestManager {
        val options = RequestOptions()
            .error(R.mipmap.greybg)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    private fun setListener() {
        binding?.backToTop?.setOnClickListener {
            backToTop()
        }
        binding?.editTextProfileSearch?.setOnClickListener {
            Intent(this, MainSearchActivity::class.java).also {
                startActivity(it)
            }
        }

        binding?.profileMenuButton?.setOnClickListener {
            dialog.show(supportFragmentManager, null)
        }
        binding?.ProfileBackButton?.setOnClickListener {
            finish()
        }
        binding?.swiper?.setOnRefreshListener {
            loadMe()
        }

        viewModel.myDetailsResponse.observe(this) {
            if (it != null){
                val profile = it.toPostDataModel()
                var go = true
                for (i in adapter.postListdata){
                    if (profile.id == i.id){
                        go = false
                    }
                }
                if (go){
                    adapter.postListdata.add(0, profile)
                    adapter.notifyItemInserted(0)
                }
                if (!go || binding?.swiper?.isRefreshing == true){
                    binding?.swiper?.isRefreshing = false
                    adapter.postListdata[0] = profile
                    adapter.notifyItemChanged(0)
                }
            }
        }
        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutManager.findLastVisibleItemPosition() > 10 && adapter.postListdata.size >= 10){
                    if (binding?.backToTop?.visibility == View.GONE){
                        val animation =
                            AnimationUtils.loadAnimation(this@ProfilePageActivity, com.udevel.widgetlab.R.anim.abc_slide_in_top)
                        animation.duration = 800
                        binding?.backToTop?.startAnimation(animation)
                        binding?.backToTop?.visibility = View.VISIBLE
                    }
                }
                if (layoutManager.findLastVisibleItemPosition() < 10){
                    if (binding?.backToTop?.visibility == View.VISIBLE){
                        val animation =
                            AnimationUtils.loadAnimation(this@ProfilePageActivity, com.udevel.widgetlab.R.anim.abc_slide_out_top)
                        animation.duration = 800
                        binding?.backToTop?.startAnimation(animation)
                        binding?.backToTop?.visibility = View.GONE
                    }
                }
                if (!viewModel.isLoading){
                    if (layoutManager.findLastVisibleItemPosition() >= adapter.itemCount-1){
                        if (!viewModel.isLoading && viewModel.hasMorePage){
                            updateActivity()
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        viewModel._response.observe(this){
            val newData = it.data as ArrayList<PostDataModel>
            try{
                for (i in adapter.postListdata){
                    for (x in newData){
                        if (x.id == i.id){
                            newData.remove(x)
                        }
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
            for (i in newData) {
                if (i.image_url.isNullOrEmpty() && i.media_type == 5) {
                    i.media_type = 7
                }
            }
            if (newData.size != 0){
                adapter.setData(newData)
            }
            if (isProgressRunning){
                stopUpdatingProgress()
            }
        }
    }


    private fun loadMe() {
        viewModel.me()
    }

    override fun onResume() {
        binding?.recyclerView?.onResumePlayer()
        super.onResume()
    }
    override fun onPause() {
        binding?.recyclerView?.onPausePlayer()
        super.onPause()
    }
    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
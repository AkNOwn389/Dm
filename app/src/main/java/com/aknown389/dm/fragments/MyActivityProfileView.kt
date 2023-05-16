package com.aknown389.dm.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.databinding.FragmentMyActivityProfileViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.pageView.homeFeed.utility.SpeedLinearLayoutManager
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.properties.Delegates


class MyActivityProfileView : Fragment() {
    private var binding: FragmentMyActivityProfileViewBinding? = null
    private lateinit var context: Context
    private var isLoading = false
    private var hasMorePage = true
    private var page by Delegates.notNull<Int>()
    private lateinit var token:String
    private  var activityList = ArrayList<PostDataModel>()
    private lateinit var manager: DataManager
    private lateinit var adapter: HomeFeedCardViewAdapter
    private lateinit var layoutManager: SpeedLinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            this.context = container.context
            binding = FragmentMyActivityProfileViewBinding.inflate(layoutInflater)
            manager = DataManager(context)
            token = manager.getAccessToken().toString()
            layoutManager = SpeedLinearLayoutManager(this.context)
        }
        this.binding = FragmentMyActivityProfileViewBinding.inflate(layoutInflater, container, false)
        binding?.recyClerView?.isNestedScrollingEnabled = false
        return  binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.recyClerView?.layoutManager = this.layoutManager
        adapter = HomeFeedCardViewAdapter(initGlide())
        binding?.recyClerView?.adapter =adapter
        binding?.recyClerView?.setMediaObjects(activityList)
        //LOAD
        loadPostList()

        binding?.recyClerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!isLoading){
                    if (layoutManager.findLastVisibleItemPosition() >= adapter.itemCount-1){
                        if (!isLoading && hasMorePage){
                            updateActivity()
                        }
                    }
                }
            }
        })
    }

    private fun updateActivity() {
        this.isLoading = true
        this.page +=1
        lifecycleScope.launch {
            val res = try {
                PostInstance.api.getPostList(token, page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (res.isSuccessful && res.body() != null){
                val r = res.body()!!
                if(!r.hasMorePage){
                    this@MyActivityProfileView.hasMorePage = false
                }
                adapter.setData(r.data as ArrayList<PostDataModel>)
            }
            isLoading = false
        }
    }

    private fun loadPostList() {
        this.isLoading = true
        this.page = 1
        this.hasMorePage = true
        binding?.recyClerView?.visibility = View.INVISIBLE
        binding?.shimmer?.visibility = View.VISIBLE
        binding?.shimmer?.startShimmer()
        lifecycleScope.launch {
            val res = try {
                PostInstance.api.getPostList(token, page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (res.isSuccessful && res.body() != null){
                val r = res.body()!!
                adapter.setData(r.data as ArrayList<PostDataModel>)
            }
            this@MyActivityProfileView.isLoading = false
            delay(1000)
            binding?.recyClerView?.visibility = View.VISIBLE
            binding?.shimmer?.stopShimmer()
            binding?.shimmer?.visibility = View.GONE
        }
    }
    private fun initGlide(): RequestManager {
        val options = RequestOptions()
            .placeholder(R.mipmap.white_background)
            .error(R.mipmap.white_background)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }
    override fun onResume() {
        binding?.recyClerView?.onResumePlayer()
        super.onResume()
    }
    override fun onPause() {
        binding?.recyClerView?.onPausePlayer()
        super.onPause()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
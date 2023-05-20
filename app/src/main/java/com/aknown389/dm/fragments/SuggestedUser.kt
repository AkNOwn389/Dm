package com.aknown389.dm.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aknown389.dm.R
import com.aknown389.dm.activities.ActivityFollowing
import com.aknown389.dm.activities.FriendRequests
import com.aknown389.dm.activities.FriendsAllActivity
import com.aknown389.dm.api.retroInstance.UsersInstance
import com.aknown389.dm.databinding.ActivityShowfriendBinding
import com.aknown389.dm.pageView.friends.SuggestedFriendAdapter
import com.aknown389.dm.models.profileModel.UserDisplayData
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SuggestedUser : Fragment() {
    private var binding: ActivityShowfriendBinding? = null
    private var context:Context? = null
    private lateinit var token:String
    private lateinit var manager: DataManager
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: SuggestedFriendAdapter
    private lateinit var rvSuggested: RecyclerView
    private var suggestedList = ArrayList<UserDisplayData>()
    private lateinit var swiper: SwipeRefreshLayout
    private lateinit var NoContent: TextView
    private var page: Int = 1
    private var isLoading = false
    private var hasMorePage = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVal(view)
        setListener()
        refreshSuggested()

    }

    private fun setListener() {
        rvSuggested.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount: Int = layoutManager.childCount
                val pastVisibleItem: Int = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (!isLoading){
                    if (visibleItemCount + pastVisibleItem > adapter.itemCount-1){
                        if (!isLoading && hasMorePage){
                            updateSuggested()
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        swiper.setOnRefreshListener {
            if (!isLoading){
                refreshSuggested()
                swiper.isRefreshing = false
            }
        }
        binding?.showFriends?.setOnClickListener {
            loadFriendAll()
        }
        binding?.showfollower?.setOnClickListener {
            loadFriendResquest()
        }
        binding?.showfollowing?.setOnClickListener {
            loadFollowing()
        }
    }

    private fun loadFollowing() {
        Intent(requireContext(), ActivityFollowing::class.java).also {
            startActivity(it)
        }
    }

    private fun setVal(view: View) {
        this.manager = DataManager(requireContext())
        this.token = manager.getAccessToken().toString()
        NoContent = view.findViewById(R.id.suggestedFriendNoContent)
        swiper = view.findViewById(R.id.suggestedFriendSwipeRefresh)
        this.layoutManager = LinearLayoutManager(context)
        rvSuggested = view.findViewById(R.id.rvSuggestedFriendContainer)
        this.adapter = SuggestedFriendAdapter(suggestedList)
        rvSuggested.layoutManager = this.layoutManager
        rvSuggested.adapter = this.adapter
    }

    private fun loadFriendAll() {
        Intent(requireContext(), FriendsAllActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun loadFriendResquest(){
        Intent(requireContext(), FriendRequests::class.java).also {
            startActivity(it)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.context = requireContext()
        if (binding == null){
            this.binding = ActivityShowfriendBinding.inflate(layoutInflater, container, false)
        }
        return binding?.root
    }

    private fun updateSuggested() {
        this.page +=1
        this.isLoading = true

        lifecycleScope.launch {
            val response = try {
                UsersInstance.api.getSuggestedFriend(token, page)
            }catch (e: Exception){
                isLoading = false
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                if (!response.body()!!.hasMorePage){
                    this@SuggestedUser.hasMorePage = false
                }
                for (i in response.body()!!.data){
                    suggestedList.add(i)
                    adapter.notifyItemInserted(suggestedList.size -1)
                }
                isLoading = false

            }
        }
    }

    private fun invicible(){
        binding?.suggestedFriendNoContent?.isVisible = false
        binding?.showfollowing?.visibility = View.GONE
        binding?.showfollower?.visibility = View.GONE
        binding?.showFriends?.visibility = View.GONE
        binding?.rvSuggestedFriendContainer?.visibility = View.GONE
    }
    private fun visible(){
        binding?.suggestedFriendNoContent?.isVisible = true
        binding?.showfollowing?.visibility = View.VISIBLE
        binding?.showfollower?.visibility = View.VISIBLE
        binding?.showFriends?.visibility = View.VISIBLE
        binding?.rvSuggestedFriendContainer?.visibility = View.VISIBLE
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun refreshSuggested() {
        this.page = 1
        this.hasMorePage = true
        invicible()
        binding?.shimmer?.startShimmer()
        binding?.shimmer?.visibility = View.VISIBLE
        try {
            suggestedList.clear()
            adapter.notifyDataSetChanged()
        }catch (e:Exception){
            e.printStackTrace()
        }
        lifecycleScope.launch {
            val response = try {
                UsersInstance.api.getSuggestedFriend(token, page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful && response.body()!!.status){
                for (i in response.body()!!.data){
                    this@SuggestedUser.suggestedList.add(i)
                    this@SuggestedUser.adapter.notifyItemInserted(suggestedList.size-1)
                }
            }
            isLoading = false
            swiper.isRefreshing = false
            delay(500)
            binding?.shimmer?.stopShimmer()
            binding?.shimmer?.visibility = View.GONE
            visible()
            binding?.rvSuggestedFriendContainer?.visibility = View.VISIBLE
            try {
                if (response.body()!!.data.isEmpty()) {
                    NoContent.isVisible = true
                }
            }catch (e:Exception){
                Log.d(TAG, e.printStackTrace().toString())
            }
        }
    }
    companion object{
        val TAG = "SuggestedUser"
    }
}
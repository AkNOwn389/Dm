package com.aknown389.dm.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.databinding.ActivityFriendRequestsBinding
import com.aknown389.dm.pageView.friends.FriendRequestAdapter
import com.aknown389.dm.api.retroInstance.UsersInstance
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.profileModel.UserDisplayData
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.pageView.main.viewModel.MainViewModel
import com.aknown389.dm.pageView.main.viewModel.MainViewModelFactory
import kotlinx.coroutines.launch


class FriendRequests : AppCompatActivity() {
    private lateinit var token:String
    private lateinit var manager: DataManager
    private lateinit var viewModel: MainViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: FriendRequestAdapter
    private var followerList = ArrayList<UserDisplayData>()
    private var binding: ActivityFriendRequestsBinding? = null
    private var page: Int = 1
    private var isLoading = false
    private var hasMorePage = true


    override fun onCreate(savedInstanceState: Bundle?) {
        this.binding = ActivityFriendRequestsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setVal()
        setListener()
        refreshFollowers()
    }

    private fun setListener() {
        binding?.rvFriendRequestContainer?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount: Int = layoutManager.childCount
                val pastVisibleItem: Int = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (!isLoading){
                    if (visibleItemCount + pastVisibleItem > adapter.itemCount-1){
                        if (!isLoading && hasMorePage){
                            updateFollowers()
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        binding?.FriendRequestSwipeRefresh?.setOnRefreshListener {
            if (!isLoading && hasMorePage ){
                refreshFollowers()
                binding?.FriendRequestSwipeRefresh?.isRefreshing = false
            }else{
                binding?.FriendRequestSwipeRefresh?.isRefreshing = false
            }
        }
        binding?.backbtn?.setOnClickListener {
            finish()
        }
    }

    private fun setVal() {
        manager = DataManager(this)
        token = manager.getAccessToken().toString()
        val repository = Repository()
        val db = AppDataBase.getDatabase(this)
        val viewModelFactory = MainViewModelFactory(repository = repository, token = token, dataBase = db)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        layoutManager = LinearLayoutManager(this)
        this.adapter = FriendRequestAdapter(followerList)
        binding?.rvFriendRequestContainer?.adapter = adapter
        binding?.rvFriendRequestContainer?.layoutManager = layoutManager
    }

    private fun updateFollowers(){
        this.page +=1
        this.isLoading = true
        lifecycleScope.launch {
            val it = try {
                UsersInstance.api.getFollower(token, page)
            }catch (e : Exception){
                isLoading = false
                binding?.FriendRequestSwipeRefresh?.isRefreshing = false
                return@launch
            }
            if (it.isSuccessful && it.body() != null){
                if (!it.body()!!.hasMorePage){
                    this@FriendRequests.hasMorePage = false
                }
                if (it.body()!!.message == "you have no followers at this time."){
                    binding?.friendRequestTextViewNoContent?.isVisible = true
                }else {
                    for (i in it.body()!!.data){
                        followerList.add(i)
                        adapter.notifyItemInserted(followerList.size -1)
                    }
                    isLoading = false
                }
            }
        }
    }
    private fun refreshFollowers() {
        this.page = 1
        this.hasMorePage = true
        lifecycleScope.launch {
            this@FriendRequests.isLoading = true
            val response = try {
                UsersInstance.api.getFollower(token, page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                if (!response.body()!!.hasMorePage){
                    this@FriendRequests.hasMorePage = false
                }
                if (response.body()!!.message == "you have no followers at this time."){
                    binding?.friendRequestTextViewNoContent?.isVisible = true
                }else{
                    for (i in response.body()!!.data){
                        followerList.add(i)
                        adapter.notifyItemInserted(followerList.size -1)
                    }
                }
            }
            this@FriendRequests.isLoading = false
        }
    }
    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
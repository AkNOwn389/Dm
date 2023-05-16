package com.aknown389.dm.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityFriendsAllBinding
import com.aknown389.dm.pageView.friends.FriendsAdapter
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.showFriendModels.showFriendData
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.pageView.main.viewModel.MainViewModel
import com.aknown389.dm.pageView.main.viewModel.MainViewModelFactory

class FriendsAllActivity : AppCompatActivity() {
    private lateinit var token:String
    private lateinit var manager: DataManager
    private lateinit var viewModel: MainViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: FriendsAdapter
    private lateinit var rvSuggested: RecyclerView
    private var friendList = ArrayList<showFriendData>()
    private lateinit var swiper: SwipeRefreshLayout
    private var binding: ActivityFriendsAllBinding? = null
    private lateinit var nodata: TextView
    private var page = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFriendsAllBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setVal()
        setListener()
        refreshFriends()
    }

    private fun setListener() {
        rvSuggested.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount: Int = layoutManager.childCount
                val pastVisibleItem: Int = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (!viewModel.isLoading && viewModel.hasMorePage){
                    if (visibleItemCount + pastVisibleItem > adapter.itemCount-1){
                        updateFriends()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        swiper.setOnRefreshListener {
            if (!isLoading){
                refreshFriends()
                swiper.isRefreshing = false
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
        val db:AppDataBase = AppDataBase.getDatabase(this)
        val viewModelFactory = MainViewModelFactory(repository = repository, dataBase = db, token = token)
        nodata = findViewById(R.id.TextViewNoFriendsData)
        swiper = findViewById(R.id.FriendsSwipeRefresh)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        layoutManager = LinearLayoutManager(this)
        rvSuggested = findViewById(R.id.rvFriendsContainer)
        adapter = FriendsAdapter(friendList)
        rvSuggested.adapter = adapter
        rvSuggested.layoutManager = layoutManager
    }

    private fun refreshFriends(){
        this.page = 1
        viewModel.getFriends(page)
        viewModel.getfriendresponse.observe(this){ response ->
            if (response.message == "you have no friends at time."){
                nodata.isVisible = true
            }else {
                nodata.visibility = View.GONE
                for (i in response.data){
                    var go = true
                    for (x in friendList){
                        if (i.user == x.user){
                            go = false
                        }
                    }
                    if (go){
                        friendList.add(i)
                        adapter.notifyItemInserted(friendList.size-1)
                    }
                }
            }
            swiper.isRefreshing = false
        }
    }


    private fun updateFriends() {
        this.page +=1
        viewModel.getFriends(page)
        viewModel.getfriendresponse.observe(this){ response ->
            if (response.message == "you have no friends at time."){
                nodata.isVisible = true
            }else{
                nodata.isVisible = false
                for (i in response.data){
                    var go = true
                    for (x in friendList){
                        if (i.user == x.user){
                            go = false
                        }
                    }
                    if (go){
                        friendList.add(i)
                        adapter.notifyItemInserted(friendList.size-1)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}

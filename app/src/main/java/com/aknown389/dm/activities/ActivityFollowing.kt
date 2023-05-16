package com.aknown389.dm.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.databinding.ActivityFollowingBinding
import com.aknown389.dm.pageView.friends.FollowingActivityAdapter
import com.aknown389.dm.api.retroInstance.UsersInstance
import com.aknown389.dm.models.profileModel.UserDisplayData
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.launch

class ActivityFollowing : AppCompatActivity() {
    private var binding: ActivityFollowingBinding? = null
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: FollowingActivityAdapter
    private lateinit var token:String
    private lateinit var manager:DataManager
    private var users = ArrayList<UserDisplayData>()
    private var isLoading = false
    private var hasMorePage = true
    private var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        this.binding = ActivityFollowingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setVal()
        setUI()
        setListener()
        loadUsers()
    }

    private fun setVal() {
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        this.adapter = FollowingActivityAdapter(users)
        this.layoutManager = LinearLayoutManager(this)
        binding?.recyclerView?.layoutManager = this.layoutManager
        binding?.recyclerView?.adapter = this.adapter
    }

    private fun loadUsers() {
        this.page = 1
        this.isLoading = true
        lifecycleScope.launch {
            val response = try {
                UsersInstance.api.getFollowing(token, page)
            }catch (e:Exception){
                Toast.makeText(this@ActivityFollowing, "${e.message}", Toast.LENGTH_SHORT).show()
                return@launch
            }
            if (response.isSuccessful && response.body()!!.status){
                binding?.noContent?.isVisible = response.body()!!.data.isEmpty()
                if (!response.body()!!.hasMorePage){
                    this@ActivityFollowing.hasMorePage = false
                }
                for (i in response.body()!!.data){
                    users.add(i)
                    adapter.notifyItemInserted(users.size-1)
                }
            }
            this@ActivityFollowing.isLoading = false
        }
    }

    private fun setListener() {
        binding?.backbtn?.setOnClickListener {
            finish()
        }
        binding?.swipeRefresh?.setOnRefreshListener {
            if (!isLoading && hasMorePage){
                loadUsers()
                binding?.swipeRefresh?.isRefreshing = false
            }else{
                binding?.swipeRefresh?.isRefreshing = false
            }
        }
        binding?.recyclerView?.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (layoutManager.findFirstCompletelyVisibleItemPosition() + layoutManager.childCount > adapter.itemCount -1){
                    if (!isLoading && hasMorePage){
                        updateUsers()
                    }
                }

                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }
    private fun updateUsers(){
        this.page +=1
        this.isLoading = true
        lifecycleScope.launch {
            val response = try {
                UsersInstance.api.getFollowing(token, page)
            }catch (e:Exception){
                Log.d("Exception", e.message.toString())
                return@launch
            }
            if (response.isSuccessful && response.body()!!.status){
                if (response.body()!!.data.isEmpty()){
                    return@launch
                }
                if (!response.body()!!.hasMorePage){
                    this@ActivityFollowing.hasMorePage = false
                }
                for (i in response.body()!!.data){
                    users.add(i)
                    adapter.notifyItemInserted(users.size-1)
                }
            }
            this@ActivityFollowing.isLoading = false
        }
    }

    private fun setUI() {

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
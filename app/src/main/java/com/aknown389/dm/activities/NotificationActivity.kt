package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityNotificationBinding
import com.aknown389.dm.pageView.notification.utilities.NotificationAdapter
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.pageView.notification.remote.repository.Repository
import com.aknown389.dm.pageView.notification.viewModel.NotificationViewModelFactory
import com.aknown389.dm.pageView.notification.viewModel.ViewModel
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class NotificationActivity : AppCompatActivity() {
    private var binding: ActivityNotificationBinding? = null
    private lateinit var backBtn:ImageButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var rvNotification:RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var manager:DataManager
    private lateinit var token:String
    private lateinit var viewModel:ViewModel
    private var progressVisible = false
    private var swipeRefreshLayoutVisible = false
    private var shimmerVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        val repository = Repository()
        val dataBase = AppDataBase
        val viewModelFactory = NotificationViewModelFactory(repository, dataBase.getDatabase(this))
        this.viewModel = ViewModelProvider(this, viewModelFactory)[ViewModel::class.java]
        setVal()
        loadNotification()
        setListener()
    }

    private fun setListener() {
        rvNotification.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!viewModel.isLoading){
                    if (layoutManager.findLastVisibleItemPosition() > adapter.itemCount-1){
                        if (!viewModel.isLoading && viewModel.hasMorePage){
                            updateNotification()
                        }
                    }
                }
            }
        })
        swipeRefreshLayout.setOnRefreshListener {
            if (!viewModel.isLoading){
                swipeRefreshLayoutVisible = true
                loadNotification()
                binding?.notificationSwiperRefresh?.isRefreshing = false
            }else{
                binding?.notificationSwiperRefresh?.isRefreshing = false
            }
        }

        backBtn.setOnClickListener {
            finish()
        }
        binding?.searchBtn?.setOnClickListener {
            Intent(this, MainSearchActivity::class.java).also {
                startActivity(it)
            }
        }
        viewModel.response.observe(this){ newData ->
            for (i in adapter.notificationList) {
                val iterator = newData.iterator()
                while (iterator.hasNext()) {
                    val x = iterator.next()
                    if (i.id == x.id) {
                        iterator.remove()
                    }
                }
            }
            adapter.setData(newData)
            if (progressVisible){
                binding?.progress?.visibility = View.GONE
                progressVisible = false
            }
            if (swipeRefreshLayoutVisible){
                binding?.notificationSwiperRefresh?.isRefreshing = false
                swipeRefreshLayoutVisible = false
            }
            if (shimmerVisible){
                stopShimmer()
            }
        }
    }

    private fun startShimmer(){
        binding?.shimmer?.visibility = View.VISIBLE
        binding?.shimmer?.startShimmer()
        binding?.rvNotificationActivity?.visibility = View.GONE
        shimmerVisible = true
    }

    private fun stopShimmer(){
        lifecycleScope.launch {
            delay(1.seconds)
            binding?.shimmer?.visibility = View.GONE
            binding?.shimmer?.stopShimmer()
            binding?.rvNotificationActivity?.visibility = View.VISIBLE
            shimmerVisible = false
        }
    }

    private fun updateNotification() {
        binding?.progress?.visibility = View.VISIBLE
        progressVisible = true
        viewModel.updateNotification(this)
    }

    private fun loadNotification() {
        startShimmer()
        viewModel.loadNotification(this)
    }

    private fun setVal() {
        this.layoutManager = LinearLayoutManager(this)
        this.rvNotification = findViewById(R.id.rvNotificationActivity)
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        this.backBtn = findViewById(R.id.notificationbackbtn)
        this.swipeRefreshLayout = findViewById(R.id.notificationSwiperRefresh)
        adapter = NotificationAdapter()
        rvNotification.adapter = adapter
        rvNotification.layoutManager = this@NotificationActivity.layoutManager
    }
}
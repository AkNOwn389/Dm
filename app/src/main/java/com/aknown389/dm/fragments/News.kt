package com.aknown389.dm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.databinding.FragmentNewsBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.NewsDataEntities
import com.aknown389.dm.pageView.newsView.NewsAdapter
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.pageView.newsView.viewModel.NewViewModel
import com.aknown389.dm.pageView.newsView.viewModel.NewsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class News : Fragment() {
    private var progressRunning: Boolean = false
    private var isShimmering: Boolean = false
    private var binding: FragmentNewsBinding? = null
    private lateinit var adapter: NewsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val newsList = ArrayList<NewsDataEntities>()
    private lateinit var manager:DataManager
    private lateinit var token:String
    private lateinit var viewModel: NewViewModel
    private var page = 1
    private var isLoading = false
    private var hasMorePage = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValue()
        setListener()
        setViewUI()
        refreshNews()
    }

    private fun setViewUI() {

    }

    private fun setListener() {
        recyclerViewListener()
        newsResponseObserver()
        binding?.swiper?.setOnRefreshListener {
            if (!isLoading && hasMorePage){
                refreshNews()
            }
        }
    }

    private fun newsResponseObserver() {
        viewModel.newsResponse.observe(viewLifecycleOwner){ response ->
            try{
                if (adapter.oldList.size != 1){
                    for (i in adapter.oldList){
                        if (i != adapter.oldList.first()){
                            for (x in response){
                                if (x.id == i.id){
                                    response.remove(x)
                                }
                            }
                        }
                    }
                }
            }catch (e:Exception) {
                e.printStackTrace()
            }
            if (response.size != 0){
                adapter.setData(response)
            }
            if (adapter.oldList.size <= 18){
                layoutManager.scrollToPosition(0)
            }
            if (this.isShimmering){
                stopShimmer()
            }
            if (this.progressRunning){
                binding?.progress?.visibility = View.GONE
                progressRunning = false
            }
            if (adapter.oldList.size < 1){
                binding?.noContent?.visibility = View.VISIBLE
            }else{
                binding?.noContent?.visibility = View.GONE
            }
        }
    }

    private fun setValue() {
        this.manager = DataManager(requireContext())
        this.token = manager.getAccessToken().toString()
        this.adapter = NewsAdapter()
        this.layoutManager = LinearLayoutManager(requireContext())
        val dao:AppDataBase = AppDataBase.getDatabase(requireContext())
        val viewModelFactory = NewsViewModelFactory(dao = dao)
        viewModel = ViewModelProvider(this, viewModelFactory)[NewViewModel::class.java]
        binding?.recyclerView?.adapter = adapter
        binding?.recyclerView?.layoutManager = layoutManager
    }
    private fun startShimmer(){
        lifecycleScope.launch(Dispatchers.Main) {
            binding?.shimmer?.visibility = View.VISIBLE
            binding?.recyclerView?.visibility = View.INVISIBLE
            binding?.noContent?.visibility = View.GONE
            binding?.shimmer?.startShimmer()
            this@News.isShimmering = true
        }

    }
    private fun stopShimmer(){
        lifecycleScope.launch(Dispatchers.Main) {
            delay(500)
            binding?.shimmer?.visibility = View.INVISIBLE
            binding?.recyclerView?.visibility = View.VISIBLE
            binding?.shimmer?.stopShimmer()
            binding?.swiper?.isRefreshing = false
            this@News.isShimmering = false
        }
    }
    private fun refreshNews(){
        startShimmer()
        adapter.oldList.clear()
        val bar = NewsDataEntities(id = "0", newsType = 99)
        adapter.oldList.add(0, bar)
        viewModel.getNews(requireContext())
    }
    private fun updateNews() {
        binding?.progress?.visibility = View.VISIBLE
        this.progressRunning = true
        viewModel.updateNews(requireContext())
    }
    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
    private fun recyclerViewListener() {
        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!isLoading){
                    if (layoutManager.findLastVisibleItemPosition() >= adapter.itemCount-5){
                        if (!viewModel.isLoading && viewModel.hasMorePage){
                            updateNews()
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }
}



package com.aknown389.dm.activities

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aknown389.dm.databinding.ActivityProfileGalleryBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.pageView.profile.ProfilePageGalleryGridAdapter
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.pageView.profile.viewModels.ProfileGalleryDisplayViewModel
import com.aknown389.dm.pageView.profile.viewModels.ProfileGalleryDisplayModelFactory

class ProfileGalleryPost() : AppCompatActivity() {
    private var binding: ActivityProfileGalleryBinding? = null
    private lateinit var viewModel: ProfileGalleryDisplayViewModel
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: ProfilePageGalleryGridAdapter
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileGalleryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setVal()
        setListener()
        loadGallery()
    }

    private fun setListener() {
        binding?.Back?.setOnClickListener {
            finish()
        }
        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lastVisibleItemPositions = layoutManager.findLastCompletelyVisibleItemPositions(null)
                if (!viewModel.isLoading){
                    if (lastVisibleItemPositions.last()+lastVisibleItemPositions.first() >= adapter.itemCount-1){
                        if (!viewModel.isLoading && viewModel.hasMorePage){
                            updateGallery()
                        }
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        viewModel._mygalleryresponse.observe(this) { response ->
            if (response.isNotEmpty()) {
                for (i in response){
                    adapter.addData(i)
                }
            }
        }
    }

    private fun setVal() {
        val manager = DataManager(this)
        token = manager.getAccessToken().toString()
        val database: AppDataBase = AppDataBase.getDatabase(this)
        val repository = Repository()
        val viewModelFactory = ProfileGalleryDisplayModelFactory(repository, token, database)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileGalleryDisplayViewModel::class.java]
        layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
        this.adapter = ProfilePageGalleryGridAdapter()
        binding?.recyclerView?.adapter = adapter
        binding?.recyclerView?.layoutManager = this.layoutManager
    }


    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun updateGallery(){
        viewModel.updateGallery()

    }
    private fun loadGallery() {
        viewModel.myGallery()
    }
}
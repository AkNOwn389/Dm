package com.aknown389.dm.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aknown389.dm.databinding.FragmentProfileGalleryAdapterBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.pageView.profile.ProfilePageGalleryGridAdapter
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.pageView.profile.viewModels.ProfileGalleryDisplayViewModel
import com.aknown389.dm.pageView.profile.viewModels.ProfileGalleryDisplayModelFactory

class ProfileGalleryPost() : Fragment() {
    private var binding: FragmentProfileGalleryAdapterBinding? = null
    private lateinit var viewModel: ProfileGalleryDisplayViewModel
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: ProfilePageGalleryGridAdapter
    private lateinit var token: String
    private lateinit var context:Context

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVal()
        setListener()

    }

    private fun setListener() {
        binding?.profileStagerredAdapter?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
        viewModel._mygalleryresponse.observe(viewLifecycleOwner) { response ->
            if (response.isNotEmpty()) {
                for (i in response){
                    adapter.addData(i)
                }
            }
        }
    }

    private fun setVal() {
        layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
        this.adapter = ProfilePageGalleryGridAdapter()
        binding?.profileStagerredAdapter?.adapter = adapter
        binding?.profileStagerredAdapter?.layoutManager = this.layoutManager
    }


    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun onResume() {
        loadGallery()
        super.onResume()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (container != null) {
            this.context = requireContext()
            val manager = DataManager(context)
            token = manager.getAccessToken().toString()
            val database: AppDataBase = AppDataBase.getDatabase(requireContext())
            val repository = Repository()
            val viewModelFactory = ProfileGalleryDisplayModelFactory(repository, token, database)
            viewModel = ViewModelProvider(this, viewModelFactory)[ProfileGalleryDisplayViewModel::class.java]
        }
        binding = FragmentProfileGalleryAdapterBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    private fun updateGallery(){
        viewModel.updateGallery()

    }
    private fun loadGallery() {
        viewModel.myGallery()
    }
}
package com.aknown389.dm.activities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityMainSearchBinding
import com.aknown389.dm.pageView.mainSearch.utility.Adapter
import com.aknown389.dm.pageView.mainSearch.viewModel.SearchViewModel
import com.aknown389.dm.utils.DataManager

class MainSearchActivity : AppCompatActivity() {
    private var binding: ActivityMainSearchBinding? = null
    private lateinit var token:String
    private lateinit var manager:DataManager
    private lateinit var adapter: Adapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SearchViewModel
    private var searchtype :String = "users"
    private var text:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        this.binding = ActivityMainSearchBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setVal()
        setListener()
        setUI()
        loadRecentSearch()

    }
    private fun loadRecentSearch() {
        viewModel.loadRecent(this.applicationContext)
    }
    private fun setUI() {
        binding?.buttonUsers?.setBackgroundResource(R.drawable.chat_item_ripple_2)

    }

    private fun setListener() {
        setRecentObserver()
        setObserver()
        setUpdateObserver()
        binding?.inputSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!viewModel.isLoading){
                    this@MainSearchActivity.text = s.toString()
                    search(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding?.backbtn?.setOnClickListener {
            finish()
        }
        binding?.buttonSearch?.setOnClickListener {
            if (!viewModel.isLoading){
                if (text != null){
                    search(text.toString())
                }else{
                    Toast.makeText(this, "Please input some text", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding?.buttonUsers?.setOnClickListener {
            this.searchtype = "users"
            binding?.buttonUsers?.setBackgroundResource(R.drawable.chat_item_ripple_2)
            binding?.buttonPosts?.setBackgroundResource(R.drawable.chat_item_ripple)
            binding?.buttonvideos?.setBackgroundResource(R.drawable.chat_item_ripple)
            if (text != null && !viewModel.isLoading){
                search(text = text.toString())
            }
        }
        binding?.buttonPosts?.setOnClickListener {
            this.searchtype = "posts"
            binding?.buttonPosts?.setBackgroundResource(R.drawable.chat_item_ripple_2)
            binding?.buttonUsers?.setBackgroundResource(R.drawable.chat_item_ripple)
            binding?.buttonvideos?.setBackgroundResource(R.drawable.chat_item_ripple)
            if (text != null && !viewModel.isLoading){
                search(text = text.toString())
            }
        }
        binding?.buttonvideos?.setOnClickListener {
            this.searchtype = "videos"
            binding?.buttonvideos?.setBackgroundResource(R.drawable.chat_item_ripple_2)
            binding?.buttonUsers?.setBackgroundResource(R.drawable.chat_item_ripple)
            binding?.buttonPosts?.setBackgroundResource(R.drawable.chat_item_ripple)
            if (text != null && !viewModel.isLoading){
                search(text = text.toString())
            }
        }
        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!viewModel.isLoading){
                    if (layoutManager.findLastVisibleItemPosition() > adapter.itemCount-1){
                        if (!viewModel.isLoading && viewModel.hasMorePage){
                            updateSearch()
                        }
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun setRecentObserver() {
        viewModel.recentData.observe(this){response ->
            adapter.setData(response)
        }
    }

    private fun setUpdateObserver() {
        viewModel.responseUpdate.observe(this) {response ->
            adapter.updateData(response)
        }
    }

    private fun setObserver() {
        viewModel.responseData.observe(this){ response ->
            adapter.setData(response)
        }
    }

    private fun updateSearch(){
        viewModel.updateSearch(this.applicationContext, text.toString(), searchtype)
    }
    private fun search(text:String){
        viewModel.search(this.applicationContext, text, searchtype)

    }

    private fun setVal() {
        this.viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        this.layoutManager = LinearLayoutManager(this)
        this.adapter = Adapter()
        this.recyclerView = binding?.recyclerView!!
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = adapter

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
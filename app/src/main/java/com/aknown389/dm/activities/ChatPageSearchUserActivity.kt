package com.aknown389.dm.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.databinding.ActivityChatPageSearchUserBinding
import com.aknown389.dm.pageView.chatSearch.ChatPageSearchAdapter
import com.aknown389.dm.api.retroInstance.ProfileInstance
import com.aknown389.dm.models.searchActivityModels.Data
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatPageSearchUserActivity : AppCompatActivity() {
    private var binding: ActivityChatPageSearchUserBinding? = null
    private lateinit var userslist:ArrayList<Data>
    private lateinit var adapter: ChatPageSearchAdapter
    private lateinit var rvView:RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var token:String
    private lateinit var manager:DataManager
    private lateinit var hinahanap:String
    private var hasMorePage = true
    private var isLoading = false
    private var page =1
    override fun onCreate(savedInstanceState: Bundle?) {
        this.binding = ActivityChatPageSearchUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setVal()
        setListener()
    }

    private fun setListener() {
        binding?.chatpagesearchedittext?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isLoading){
                    this@ChatPageSearchUserActivity.hinahanap = s.toString()
                    //searchUser(s.toString())
                }
                searchUser(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        rvView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val visibleItemCount: Int = layoutManager.childCount
                val pastVisibleItem: Int = layoutManager.findFirstCompletelyVisibleItemPosition()

                if (!isLoading){
                    if (visibleItemCount + pastVisibleItem > adapter.itemCount-1){
                        if (!isLoading && hasMorePage){
                            updateList()
                        }
                    }
                }
            }
        })
        binding?.chatPageSearchUserBackBtn?.setOnClickListener {
            finish()
        }
        binding?.chatpagesearchbtn?.setOnClickListener {
            searchUser(hinahanap)
        }
    }

    private fun updateList() {
        this.isLoading = true
        this.page+=1
        lifecycleScope.launch {
            val response = try {
                ProfileInstance.api.searchuser(token = token, user = hinahanap, page=page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                if (!response.body()!!.hasMorePage){
                    this@ChatPageSearchUserActivity.hasMorePage = false
                }
                for (x in response.body()!!.data){
                    userslist.add(x)
                    adapter.notifyItemInserted(userslist.size-1)
                }
            }
        }
    }

    private fun searchUser(s:String) {
        var user:String? = null
        if ("/" in s.toString()){
            user = s.replace("/", "")
        }else{
            user = s
        }
        this.isLoading = true
        this.page = 1
        if (s.isEmpty()){
            return
        }
        lifecycleScope.launch {
            delay(800)
            this@ChatPageSearchUserActivity.isLoading = true
            val response = try {
                ProfileInstance.api.searchuser(token = token, user = user, page = page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                this@ChatPageSearchUserActivity.userslist = response.body()!!.data as ArrayList<Data>
                this@ChatPageSearchUserActivity.adapter = ChatPageSearchAdapter(userslist)
                this@ChatPageSearchUserActivity.rvView.adapter = adapter
                this@ChatPageSearchUserActivity.rvView.layoutManager = layoutManager
            }
            isLoading = false
        }
    }

    private fun setVal() {
        this.rvView = binding?.chatpagesearchrecyclerview!!
        this.layoutManager = LinearLayoutManager(this)
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
    }
}
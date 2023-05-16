package com.aknown389.dm.pageView.mainSearch.utility

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.pageView.mainSearch.dataClass.MainSearchItemData
import com.aknown389.dm.pageView.mainSearch.diffUtil.MainSearchDiffUtil
import com.aknown389.dm.pageView.mainSearch.recyclerviewItem.PostSearchView
import com.aknown389.dm.pageView.mainSearch.recyclerviewItem.RecentView
import com.aknown389.dm.pageView.mainSearch.recyclerviewItem.UserSearchView
import com.aknown389.dm.utils.DataManager

class Adapter():RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var token:String
    private lateinit var manager:DataManager
    private lateinit var context:Context
    private lateinit var parent: ViewGroup
    var searchItem = ArrayList<MainSearchItemData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSearchViewHolder {
        this.context = parent.context
        this.manager = DataManager(parent.context)
        this.token = manager.getAccessToken().toString()
        this.parent = parent
        return when(viewType){
            99 -> MainSearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_search_recent_top, parent, false))
            0 -> MainSearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_search_recent_user, parent, false))
            1 -> MainSearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_search_user, parent, false))
            2 -> MainSearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed_cardview, parent, false))
            else -> MainSearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_search_user, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return try {
            searchItem[position].searchType!!
        } catch (e:Exception){
            1
        }
    }

    override fun getItemCount() = searchItem.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = searchItem[position]
        val holder2 = holder as MainSearchViewHolder
        when(data.searchType){
            0 -> RecentView(
                data = data,
                adapter = this,
                context = context,
                parent = parent,
                holder = holder2,
                token = token,
                searchItem = searchItem
            )
            1 -> UserSearchView(
                data = data,
                adapter = this,
                context = context,
                parent = parent,
                holder = holder2,
                token = token,
                searchItem = searchItem
            )
            2 -> PostSearchView(
                data = data,
                adapter = this,
                context = context,
                parent = parent,
                holder = holder2,
                token = token,
                searchItem = searchItem
            )
        }
    }
    fun setData(new:ArrayList<MainSearchItemData>){
        val diffresult = DiffUtil.calculateDiff(MainSearchDiffUtil(this.searchItem, new))
        this.searchItem = new
        diffresult.dispatchUpdatesTo(this)
    }
    fun updateData(new:ArrayList<MainSearchItemData>){
        val diffresult = DiffUtil.calculateDiff(MainSearchDiffUtil(this.searchItem, new))
        this.searchItem.addAll(new)
        diffresult.dispatchUpdatesTo(this)
    }
}
package com.aknown389.dm.pageView.newsView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.db.local.NewsDataEntities
import com.aknown389.dm.pageView.newsView.diffUtil.NewsDiffUtil
import com.aknown389.dm.pageView.newsView.NewsCardView
import com.aknown389.dm.pageView.newsView.NewsViewHolder
import com.aknown389.dm.utils.DataManager

class NewsAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var oldList = ArrayList<NewsDataEntities>()
    private var parent:ViewGroup? = null
    private var context:Context? = null
    private lateinit var token:String
    private lateinit var manager:DataManager


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.parent = parent
        this.context = parent.context
        this.manager = DataManager(parent.context)
        this.token = manager.getAccessToken().toString()
        return when(viewType){
            1 -> NewsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_news_cardview, parent, false))
            99 -> NewsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_news_search_bar, parent, false))
            else -> NewsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_news_cardview, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return try {
            oldList[position].newsType!!
        }catch (e:Exception){
            1
        }
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            when(oldList[position].newsType){
                1 -> {
                    NewsCardView(newsList=oldList,
                        adapter = this,
                        holder = holder as NewsViewHolder,
                        context = context!!,
                        token = token,
                        currentItem = oldList[position])
                }
                99 -> {
                    showSearchBar(oldList[position], holder as NewsViewHolder)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun showSearchBar(data: NewsDataEntities, holder: NewsViewHolder) {
        holder.inputSearch!!.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {
                TODO("Not yet implemented")
            }

        })
        holder.buttonSearch!!.setOnClickListener{

        }
    }
    fun setData(newList:ArrayList<NewsDataEntities>){
        val diffResult = DiffUtil.calculateDiff(NewsDiffUtil(new = newList, old = oldList))
        oldList.addAll(newList)
        diffResult.dispatchUpdatesTo(object :ListUpdateCallback{
            override fun onInserted(position: Int, count: Int) {
                notifyItemInserted(position)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRemoved(position)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyItemChanged(position, payload)
            }
        })
        //diffResult.dispatchUpdatesTo(this)
    }
}
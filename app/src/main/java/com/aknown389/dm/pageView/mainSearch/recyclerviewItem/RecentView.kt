package com.aknown389.dm.pageView.mainSearch.recyclerviewItem

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.aknown389.dm.R
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.models.mainSearchActivityModels.Data
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.mainSearch.utility.Adapter
import com.aknown389.dm.pageView.mainSearch.utility.MainSearchViewHolder
import com.aknown389.dm.pageView.mainSearch.utility.SaveRecent

class RecentView(
    private val adapter: Adapter,
    private val context: Context,
    private val parent: ViewGroup,
    private val holder: MainSearchViewHolder,
    private val token:String,
    private val searchItem: ArrayList<Data>,
    private val data: Data



) {

    init {
        loadUI()
        setListener()
    }
    private fun loadUI() {
        var name:String? =null
        name = when(data.name){
            "" -> data.username
            null -> data.username
            else -> data.name
        }
        Glide.with(context)
            .load(data.profileimg)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.image!!)
        holder.name?.text = name
        holder.location?.text = data.location
    }
    private fun setListener() {
        holder.name?.setOnClickListener {
            SaveRecent(
                data = data,
                holder = holder,
                token = token,
                adapter = adapter,
                context = context
            )
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", data.user)
                intent.putExtra("userAvatar", data.profileimg)
                intent.putExtra("name", data.name)
                it.startActivity(intent)
            }
        }
        holder.image?.setOnClickListener {
            SaveRecent(
                data = data,
                holder = holder,
                token = token,
                adapter = adapter,
                context = context
            )
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", data.user)
                intent.putExtra("userAvatar", data.profileimg)
                intent.putExtra("name", data.name)
                it.startActivity(intent)
            }
        }
        holder.body?.setOnClickListener {
            SaveRecent(
                data = data,
                holder = holder,
                token = token,
                adapter = adapter,
                context = context
            )
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", data.user)
                intent.putExtra("userAvatar", data.profileimg)
                intent.putExtra("name", data.name)
                it.startActivity(intent)
            }
        }
    }
}
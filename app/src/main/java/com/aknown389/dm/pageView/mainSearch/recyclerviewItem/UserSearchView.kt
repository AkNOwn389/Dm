package com.aknown389.dm.pageView.mainSearch.recyclerviewItem

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aknown389.dm.R
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.models.mainSearchActivityModels.Data
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.mainSearch.utility.DeniedUser
import com.aknown389.dm.pageView.mainSearch.utility.FollowUser
import com.aknown389.dm.pageView.mainSearch.utility.Adapter
import com.aknown389.dm.pageView.mainSearch.utility.MainSearchViewHolder
import com.aknown389.dm.pageView.mainSearch.utility.SaveRecent

class UserSearchView(
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

    private fun setListener() {
        holder.button1?.setOnClickListener {
            FollowUser(
                data = data,
                holder = holder,
                token = token,
                adapter = adapter,
                context = context
            )
        }
        holder.button2?.setOnClickListener {
            DeniedUser(
                data = data,
                holder = holder,
                token = token,
                adapter = adapter,
                context = context
            )
        }
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
                intent.putExtra("username", data.username)
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
                intent.putExtra("username", data.username)
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
                intent.putExtra("username", data.username)
                intent.putExtra("userAvatar", data.profileimg)
                intent.putExtra("name", data.name)
                it.startActivity(intent)
            }
        }
    }

    private fun loadUI() {
        if (data.isFollowed == true && data.isFollower == true){
            holder.button2?.isVisible = false
            holder.button1?.text = context.getString(R.string.followed)
        }
        if (data.isFollowed == false && data.isFollower == false){
            holder.button2?.isVisible = false
            holder.button1?.text = context.getString(R.string.follow)
        }
        if (data.isFollowed == false && data.isFollower == true){
            holder.button2?.isVisible = true
            holder.button1?.text = context.getString(R.string.followback)
        }
        if (data.isFollowed == true && data.isFollower == false){
            holder.button2?.isVisible = false
            holder.button1?.text = context.getString(R.string.followed)
        }
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
            .override(200, 200)
            .into(holder.image!!)

        holder.name?.text = name
        holder.location?.text = data.location
    }

}
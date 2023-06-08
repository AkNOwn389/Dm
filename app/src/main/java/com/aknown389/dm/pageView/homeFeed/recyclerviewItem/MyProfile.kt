package com.aknown389.dm.pageView.homeFeed.recyclerviewItem

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.aknown389.dm.R
import com.aknown389.dm.activities.ActivityFollowing
import com.aknown389.dm.activities.FriendRequests
import com.aknown389.dm.activities.MyGalleryVideos
import com.aknown389.dm.activities.ProfileGalleryPost
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedRecyclerViewHolder
import com.bumptech.glide.Glide
import javax.inject.Inject

class MyProfile @Inject constructor(
    private val holder: HomeFeedRecyclerViewHolder,
    private val currentItem: PostDataModel,
    private val context: Context,
    private val postListdata:ArrayList<PostDataModel>,
    private val parent: ViewGroup,
    private val adapterContext: HomeFeedCardViewAdapter,
    private val token:String,
) {
    init {
        setUI()
        setListener()
    }

    private fun setListener() {
        holder.profileFollowingLength?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, ActivityFollowing::class.java)
                it.startActivity(intent)
            }
        }
        holder.profileFollowerLength?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, FriendRequests::class.java)
                it.startActivity(intent)
            }
        }
        holder.profileSeeFollowed?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, ActivityFollowing::class.java)
                it.startActivity(intent)
            }
        }
        holder.profileSeeAllFollowers?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, FriendRequests::class.java)
                it.startActivity(intent)
            }
        }
        holder.profilePhotos?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, ProfileGalleryPost::class.java)
                it.startActivity(intent)
            }
        }
        holder.profileVideos?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, MyGalleryVideos::class.java)
                it.startActivity(intent)
            }
        }
    }

    private fun setUI() {
        loadProfilePicture(currentItem.profileImage)
        loadBackgroundImage(currentItem.backgroundImage)
        holder.profileFollowingLength?.text = currentItem.following.toString()
        holder.profileFollowerLength?.text = currentItem.followers.toString()
        holder.profileBio?.text = currentItem.bio
        holder.profileName?.text = currentItem.name
        holder.profileUserName?.text = currentItem.username
        holder.profilePostLength?.text = currentItem.post_lenght.toString()
    }

    private fun loadBackgroundImage(image: String?){
        try {
            Glide.with(context)
                .load(image)
                .override(700, 700)
                .into(holder.profileBackgroundImage!!)
        }catch (e:java.lang.Exception){
            return
        }
    }
    private fun loadProfilePicture(image:String?){
        try {
            Glide.with(context)
                .load(image)
                .override(300, 300)
                .error(R.mipmap.greybg)
                .placeholder(R.mipmap.greybg)
                .into(holder.profileImage!!)
        }catch (e:Exception){
            return
        }
    }
}
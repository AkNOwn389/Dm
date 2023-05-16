package com.aknown389.dm.pageView.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.models.profileModel.ProfilePhotoCard
import com.google.android.material.imageview.ShapeableImageView

class ProfilePageAdapter(private val photoList : ArrayList<ProfilePhotoCard>) : RecyclerView.Adapter<ProfilePageAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profile_cardview, parent, false)
        return MyViewHolder(itemView)
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val Image: ShapeableImageView = itemView.findViewById(R.id.ImageCardProfile)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = photoList[position]
        holder.Image.setImageResource(currentItem.Image)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}



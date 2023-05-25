package com.aknown389.dm.pageView.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ItemMainSearchRecentTopBinding
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.profileGalleryModels.ImageDataModel
import com.aknown389.dm.pageView.profile.diffUtil.GalleryDiffUtil

class ProfilePageGalleryGridAdapter(): RecyclerView.Adapter<ProfilePageGalleryGridAdapter.MyViewHolder>() {
    private lateinit var context:Context
    private var photoList:ArrayList<ImageUrl> = ArrayList()

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProfileGallery: ImageView? = itemView.findViewById(R.id.imageProfileGallery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_gallery_grid_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = photoList[position]
        Glide.with(context)
            .load(currentItem.imgW250)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.imageProfileGallery!!)

        holder.imageProfileGallery.setOnClickListener {
            //Toast.makeText(context, "you click me", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    fun setData(new:List<ImageUrl>){
        val diffresult = DiffUtil.calculateDiff(GalleryDiffUtil(new = new, old = photoList))
        photoList.addAll(new)
        diffresult.dispatchUpdatesTo(this)
    }
}
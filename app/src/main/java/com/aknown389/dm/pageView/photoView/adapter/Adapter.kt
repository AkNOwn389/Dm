package com.aknown389.dm.pageView.photoView.adapter

import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ItemViewpagerPhotoViewBinding
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.pageView.indexViewPager.ViewPagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView

class Adapter(private val imageUrls: List<ImageUrl>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private lateinit var parent: ViewGroup
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var progressBar:ProgressBar? = view.findViewById(R.id.progress)
        var photoView:PhotoView? = view.findViewById(R.id.photo_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager_photo_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = imageUrls[position]
        try {
            holder.progressBar?.visibility = View.VISIBLE
            val image = item.imgW1000
            Glide.with(holder.photoView!!)
                .load(image)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.progressBar?.visibility = View.GONE
                        return false
                    }
                })
                .into(holder.photoView!!)
        }catch (e:Exception){
            Toast.makeText(parent.context, "$e", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}
package com.aknown389.dm.pageView.videoGallery.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ItemVideoGalleryBinding
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.VideoUrl
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.aknown389.dm.pageView.videoGallery.diffUtil.VideoGalleryDiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class VideGalleryAdapter:RecyclerView.Adapter<VideGalleryAdapter.ViewHolder>() {
    val videoList = ArrayList<VideoUrl>()
    private lateinit var parent:ViewGroup
    inner class ViewHolder(view: ItemVideoGalleryBinding): RecyclerView.ViewHolder(view.root){
        val binding:ItemVideoGalleryBinding = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemVideoGalleryBinding.inflate(inflater, parent, false))

    }

    override fun getItemCount(): Int = videoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = videoList[position]
        val binding:ItemVideoGalleryBinding = holder.binding
        try {
            val params = binding.imageView.layoutParams
            val height = getImageHeight(item)
            params.height = height / 3
            binding.imageView.layoutParams = params
            binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
        try {

            binding.progress.visibility = View.VISIBLE
            binding.play.visibility = View.GONE
            Glide.with(parent.context)
                .load(item.thumbnail)
                .addListener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.play.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                        return false
                    }

                })
                .placeholder(R.mipmap.greybg)
                .into(binding.imageView)
            binding.imageView.setOnClickListener {

            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getImageHeight(img: VideoUrl): Int {
        val windowManager = parent.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        //val h = displayMetrics.heightPixels
        if (img.width!! > width) {
            val num: Double = (img.width / width).toDouble()
            val height: Int? = img.height?.div(num)?.toInt()
            Log.d(PicturePostView.TAG, height.toString())
            return height!!
        }else{
            val num = width-img.width
            val num2 = img.height?.plus(num)
            Log.d(PicturePostView.TAG, num2.toString())
            return num2!!
        }
    }

    fun update(new:ArrayList<VideoUrl>){
        val diffResult = DiffUtil.calculateDiff(VideoGalleryDiffUtil(videoList, new))
        videoList.addAll(new)
        diffResult.dispatchUpdatesTo(this)
    }
}
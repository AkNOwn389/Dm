package com.aknown389.dm.pageView.profile

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.aknown389.dm.R
import com.aknown389.dm.activities.PhotoViewActivity
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.aknown389.dm.pageView.photoView.models.Parcel
import com.aknown389.dm.pageView.profile.diffUtil.GalleryDiffUtil
import com.aknown389.dm.utils.DataManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson

class ProfilePageGalleryGridAdapter(): RecyclerView.Adapter<ProfilePageGalleryGridAdapter.MyViewHolder>() {
    private lateinit var context:Context
    var photoList:ArrayList<ImageUrl> = ArrayList()
    private lateinit var parent: ViewGroup
    private lateinit var manager:DataManager
    private var gson = Gson()

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProfileGallery: ImageView? = itemView.findViewById(R.id.imageProfileGallery)
        val progress:ProgressBar = itemView.findViewById(R.id.progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context = parent.context
        this.parent = parent
        manager = DataManager(parent.context)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_gallery_grid_item, parent, false)
        return MyViewHolder(itemView)
    }
    private fun getImageHeight(img: ImageUrl): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = photoList[position]
        holder.progress.visibility = View.VISIBLE
        try {
            val params = holder.imageProfileGallery!!.layoutParams
            val height = getImageHeight(currentItem)
            params.height = height / 3
            holder.imageProfileGallery.layoutParams = params
            holder.imageProfileGallery.scaleType = ImageView.ScaleType.CENTER_CROP
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
        Glide.with(context)
            .load(currentItem.imgW250)
            .placeholder(R.mipmap.greybg)
            .error(R.mipmap.greybg)
            .listener(object :RequestListener<Drawable>{
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
                    holder.progress.visibility = View.GONE
                    return false
                }
            })
            .into(holder.imageProfileGallery!!)

        holder.imageProfileGallery.setOnClickListener {
            //Toast.makeText(context, "you click me", Toast.LENGTH_SHORT).show()
            (parent.context as? AppCompatActivity?).let {
                val pos = photoList.indexOf(currentItem)
                val parcel = Parcel(
                    userAvatar = manager.getUserData()?.profileimg,
                    username = manager.getUserData()?.user,
                    images = photoList,
                    myPosition = pos
                )
                val intent = Intent(it, PhotoViewActivity::class.java)
                    .putExtra("parcel", gson.toJson(parcel))
                it?.startActivity(intent)
                it?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    fun addData(new:ImageUrl){
        if (new !in photoList){
            photoList.add(new)
            notifyItemInserted(photoList.size -1)
        }
    }
    fun setData(new:List<ImageUrl>){
        val diffResult = DiffUtil.calculateDiff(GalleryDiffUtil(new = new, old = photoList))
        photoList.addAll(new)
        diffResult.dispatchUpdatesTo(this)
    }
}
package com.aknown389.dm.pageView.gallery.utilities

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.pageView.gallery.models.FolderList
import java.lang.Exception
const val TAG = "galleryAdapter"
class GalleryAdapter:RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    val folderList = ArrayList<FolderList>()
    private lateinit var parent:ViewGroup


    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val folderName:TextView = view.findViewById(R.id.folderName)
        val date:TextView = view.findViewById(R.id.date)
        val count:TextView = view.findViewById(R.id.count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_my_gallery_folder, parent, false))
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val item = folderList[position]
            holder.folderName.text = item.folderName
            holder.count.text = item.itemCount.toString()
            holder.date.text = item.dateCreated
        }catch (e:Exception){
            Log.d(TAG, e.printStackTrace().toString())
        }
    }
}
package com.aknown389.dm.pageView.homeFeed.recyclerviewItem.dialog

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aknown389.dm.R
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedRecyclerViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ShowMyDialog(
    private val context: Context,
    private val parent: ViewGroup,
    private val holder: HomeFeedRecyclerViewHolder,
    private val token:String,
    private val postListdata:ArrayList<PostDataModel>,
    private val currentItem: PostDataModel,
    private val adapterContext: HomeFeedCardViewAdapter
    ) {



    init {
        showMyDialog(holder, currentItem)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showMyDialog(holder: HomeFeedRecyclerViewHolder, currentItem: PostDataModel) {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_my_home_feed_cardview, parent, false)
        val changePrivacy: TextView? = view.findViewById(R.id.changePrivacy)
        val delete: TextView? = view.findViewById(R.id.deletePosts)
        val EditPosts: TextView? = view.findViewById(R.id.editPosts)
        val hidePosts: TextView? = view.findViewById(R.id.ArchivePosts)
        dialog.setContentView(view)
        dialog.show()
        changePrivacy?.setOnClickListener {
            showDialogChangePostPrivacy(holder, currentItem)
        }
        delete?.setOnClickListener {
            val builder = AlertDialog.Builder((context as AppCompatActivity))
            builder.setPositiveButton("Yes") { _, _ ->
                GlobalScope.launch(Dispatchers.Main) {
                    val response = try {
                        PostInstance.api.deletePost(token, currentItem.id.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@launch
                    }
                    if (response.isSuccessful && response.body()!!.status) {
                        val body = response.body()!!
                        if (body.message == "posts deleted." || body.status) {
                            try {
                                val pos = postListdata.indexOf(currentItem)
                                adapterContext.notifyItemRemoved(pos)
                                postListdata.removeAt(pos)
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    dialog.dismiss()
                }
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.setTitle("Delete posts?")
            builder.setMessage("Are you sure you want yo delete?")
            builder.create().show()
        }
    }
    private fun showDialogChangePostPrivacy(
        holder: HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel
    ) {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_change_privacy, parent, false)
        val public: TextView? = view.findViewById(R.id.dialogPublic)
        val friends: TextView? = view.findViewById(R.id.friends)
        val onlyMe: TextView? = view.findViewById(R.id.onlyMe)
        dialog.setContentView(view)
        dialog.show()

        public?.setOnClickListener {
            changePrivacy(holder, currentItem, "Public")
            dialog.dismiss()
        }
        friends?.setOnClickListener {
            changePrivacy(holder, currentItem, "Friends")
            dialog.dismiss()
        }
        onlyMe?.setOnClickListener {
            changePrivacy(holder, currentItem, "Only-Me")
            dialog.dismiss()
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun changePrivacy(
        holder: HomeFeedRecyclerViewHolder,
        currentItem: PostDataModel,
        privacy: String
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            if (isActive) {
                val response = try {
                    PostInstance.api.changePrivacy(token, currentItem.id.toString(), privacy)
                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                    return@launch
                }
                if (response.isSuccessful) {
                    if (response.body()!!.status) {
                        val pos = postListdata.indexOf(currentItem)
                        when (privacy) {
                            "Public" -> currentItem.privacy = 'P'
                            "Friends" -> currentItem.privacy = 'F'
                            "Only-Me" -> currentItem.privacy = 'O'
                        }
                        adapterContext.notifyItemChanged(pos, currentItem)
                        return@launch
                    }
                }
            }
        }
    }
}
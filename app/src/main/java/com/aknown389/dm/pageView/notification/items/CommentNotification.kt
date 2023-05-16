package com.aknown389.dm.pageView.notification.items

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.aknown389.dm.R
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.pageView.notification.models.NotificationDataClass
import com.aknown389.dm.pageView.notification.utilities.NotificationAdapter
import com.aknown389.dm.pageView.notification.dailogs.Dialog
import com.aknown389.dm.pageView.notification.remote.ReportSeen
import com.aknown389.dm.pageView.notification.remote.instance.Instance
import com.aknown389.dm.pageView.notification.utilities.NotificationsViewHolder
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
class CommentNotification @Inject constructor(
    val holder: NotificationsViewHolder,
    val adapter: NotificationAdapter,
    val context: Context,
    val parent: ViewGroup,
    val notifications:ArrayList<NotificationDataClass>
) {
    private var data: NotificationDataClass = holder.viewHolderData!!

    init {
        holder.item3?.text = data.title
        Glide.with(context)
            .load(data.subjectImage)
            .placeholder(R.mipmap.greybg)
            .error(R.mipmap.greybg)
            .into(holder.subjectImage!!)
        if (!data.seen){
            holder.item3Body?.setBackgroundResource(R.drawable.chat_item_ripple_2)
        }else{
            holder.item3Body?.setBackgroundResource(R.drawable.chat_item_ripple)
        }
        holder.item3Body?.setOnLongClickListener {
            Dialog(adapter = adapter, constraintLayout = holder.item1Body!!, context = context, parent = parent, holder = holder, notification = notifications)
            true
        }
        holder.item3Body?.setOnClickListener {
            if (!data.seen){
                ReportSeen(data.id, constraintLayout =  holder.item3Body!!, context = context)
            }
            GlobalScope.launch {
                val response = try {
                    Instance(context).api.getPostData(id = data.subject_id)
                }catch (e:Exception){
                    e.printStackTrace()
                    return@launch
                }
                if (response.isSuccessful){
                    if (response.body()!!.status) {
                        val resBody = response.body()!!.data
                        val bundle: Bundle = Bundle()
                        val reactions = Gson().toJson(resBody.reactions)
                        bundle.putString("postId", resBody.id)
                        bundle.putString("username", data.user)
                        bundle.putInt("like", resBody.NoOflike ?:0)
                        bundle.putString("time", resBody.created_at)
                        bundle.putString("postType", "posts")
                        bundle.putString("reactions", reactions)
                        val dialog = CommentDialog()
                        dialog.arguments = bundle
                        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
                        dialog.show((context as? AppCompatActivity)?.supportFragmentManager!!, "comments")
                    }
                }
            }
        }
    }
}
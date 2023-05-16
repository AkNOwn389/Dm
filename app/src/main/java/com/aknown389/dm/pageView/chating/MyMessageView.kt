package com.aknown389.dm.pageView.chating

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.api.retroInstance.ChatInstance
import com.aknown389.dm.models.chatmodels.NullableMessage
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyMessageView(
    private val adapter: UserChatAdapter,
    private val messagelist: ArrayList<NullableMessage>,
    private val holder: UserChatViewHolder,
    private val data: NullableMessage,
    private val context:Context,
    private val parent: ViewGroup,
    private val token:String
) {


    init {
        loadUI()
        loadListener()
    }

    private fun loadListener() {
        holder.messageBody?.setOnLongClickListener {
            val dialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.chat_user_dialog, parent, false)
            val delete: ImageButton? = view.findViewById(R.id.messageDelete)
            val share: ImageButton? = view.findViewById(R.id.messageShare)
            val reply: ImageButton? = view.findViewById(R.id.messageReply)
            val archive: ImageButton? = view.findViewById(R.id.messageArchive)
            dialog.setContentView(view)
            dialog.show()
            delete?.setOnClickListener {
                Toast.makeText(context, "this", Toast.LENGTH_SHORT).show()
            }
            setListenerDialog(delete, share, reply, archive, data, dialog)
            true
        }
    }
    private fun setListenerDialog(
        delete: ImageButton?,
        share: ImageButton?,
        reply: ImageButton?,
        archive: ImageButton?,
        data: NullableMessage,
        dialog: BottomSheetDialog
    ) {
        delete?.setOnClickListener {
            (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
                val response = try {
                    ChatInstance.api.chatDelete(token, data.id!!)
                }catch (e:Exception){
                    e.printStackTrace()
                    return@launch
                }
                if (response.isSuccessful){
                    if (response.body()!!.status){
                        val ind = messagelist.indexOf(data)
                        adapter.notifyItemRemoved(ind)
                        messagelist.removeAt(ind)
                        dialog.dismiss()
                    }
                }
            }
        }
        share?.setOnClickListener {
            Toast.makeText(context, "this", Toast.LENGTH_SHORT).show()
        }
        reply?.setOnClickListener {
            Toast.makeText(context, "this", Toast.LENGTH_SHORT).show()
        }
        archive?.setOnClickListener {
            Toast.makeText(context, "this", Toast.LENGTH_SHORT).show()

        }
    }

    private fun loadUI() {
        holder.messageBody?.setText(data.message_body)
    }

}
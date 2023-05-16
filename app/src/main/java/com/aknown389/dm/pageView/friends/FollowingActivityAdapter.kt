package com.aknown389.dm.pageView.friends

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.api.retroInstance.UsersInstance
import com.aknown389.dm.models.profileModel.UserDisplayData
import com.bumptech.glide.Glide
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FollowingActivityAdapter(private val userList: ArrayList<UserDisplayData>): RecyclerView.Adapter<FollowingActivityAdapter.ViewHolder>() {
    private lateinit var parent:ViewGroup
    private lateinit var token:String
    private lateinit var manager:DataManager

    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view){
        val image:ImageView? = view.findViewById(R.id.followingImageView)
        val name:TextView? = view.findViewById(R.id.textFollowingNameTag)
        val body:ConstraintLayout? = view.findViewById(R.id.body)
        val cancelBtn:Button? = view.findViewById(R.id.btnCansel)
        val location:TextView? = view.findViewById(R.id.location)
        val menu:ImageButton? = view.findViewById(R.id.menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent
        this.manager = DataManager(parent.context)
        this.token = manager.getAccessToken().toString()
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_following_cardview, parent, false))
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = userList[position]
        setUI(holder, data)
        setListener(holder, data)
    }

    private fun setListener(
        holder: ViewHolder,
        data: UserDisplayData
    ) {
        holder.image?.setOnClickListener {
            (parent.context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", data.name)
                it.startActivity(intent)
            }
        }
        holder.name?.setOnClickListener {
            (parent.context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", data.name)
                it.startActivity(intent)
            }
        }
        holder.body?.setOnClickListener {
            (parent.context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", data.name)
                it.startActivity(intent)
            }
        }
        holder.cancelBtn?.setOnClickListener {
            val builder = AlertDialog.Builder((parent.context as AppCompatActivity))
            builder.setPositiveButton("Yes"){_, _ ->
                canselRequest(holder, data)
            }
            builder.setNegativeButton("No"){_, _ -> }
            builder.setTitle("Cancel request?")
            builder.setMessage("Are you sure you want yo cancel?")
            builder.create().show()

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun canselRequest(holder: ViewHolder, data: UserDisplayData) {
        GlobalScope.launch(Dispatchers.Main) {
            if (isActive){
                val response = try {
                    UsersInstance.api.canselRequest(token, user = data.user)
                }catch (e:Exception){
                    e.printStackTrace()
                    return@launch
                }
                if (response.isSuccessful && response.body()!!.status){
                    try {
                        val pos = userList.indexOf(data)
                        holder.cancelBtn?.setBackgroundColor(parent.context.getColor(R.color.blue))
                        holder.cancelBtn?.text = parent.context.getString(R.string.follow)
                        delay(1000)
                        notifyItemRemoved(pos)
                        userList.removeAt(pos)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }else{
                    return@launch
                }
            }
        }
    }

    private fun setUI(holder: ViewHolder, data: UserDisplayData) {
        holder.name?.text = data.name
        holder.location?.text = data.location
        Glide.with(parent.context)
            .load(data.profileimg)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.image!!)
    }
}
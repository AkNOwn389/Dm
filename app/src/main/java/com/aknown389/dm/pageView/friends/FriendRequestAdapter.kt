package com.aknown389.dm.pageView.friends

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.api.retroInstance.UsersInstance
import com.aknown389.dm.models.profileModel.UserDisplayData
import com.aknown389.dm.models.userviewModels.FollowUserResponseModel
import com.bumptech.glide.Glide
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendRequestAdapter(private val itemdata: ArrayList<UserDisplayData>): RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var token: String
    private lateinit var manager: DataManager
    private var isLoading = false

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnfollowback:Button? = itemView.findViewById(R.id.btnFollowback)
        val followersName:TextView? = itemView.findViewById(R.id.textFollowersNameTag)
        val followersImageView:ImageView? = itemView.findViewById(R.id.followersImageView)
        val btnDenied: Button? = itemView.findViewById(R.id.btnDenied)
        val location:TextView? = itemView.findViewById(R.id.location)
        val body:ConstraintLayout? = itemView.findViewById(R.id.itembody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        this.manager = DataManager(parent.context)
        this.token = manager.getAccessToken().toString()

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_request_adapter, parent, false)
        return ViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return itemdata.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myitem = itemdata[position]
        setUI(holder, myitem)
        setListener(holder, myitem)
    }

    private fun setListener(holder: ViewHolder, myitem: UserDisplayData) {
        holder.followersImageView?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", myitem.user)
                it.startActivity(intent)
            }
        }
        holder.btnfollowback?.setOnClickListener {
            followback(myitem.user, holder)
        }
        holder.btnDenied?.setOnClickListener {
            deniedRequest(myitem, holder)
        }
        holder.body?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", myitem.user)
                it.startActivity(intent)
            }
        }
        holder.followersName?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", myitem.user)
                it.startActivity(intent)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deniedRequest(myitem: UserDisplayData, holder: ViewHolder) {
        GlobalScope.launch(Dispatchers.Main) {
            if (isActive){
                val response = try {
                    UsersInstance.api.deniedRequest(token = token, user = myitem.user)
                }catch (e:Exception){
                    e.printStackTrace()
                    return@launch
                }
                if (response.isSuccessful && response.body()!!.status){
                    val pos = itemdata.indexOf(myitem)
                    notifyItemRemoved(pos)
                    itemdata.removeAt(pos)
                }else{return@launch}
            }else{return@launch}
        }
    }

    private fun setUI(holder: ViewHolder, myitem: UserDisplayData) {
        if (myitem.Following){
            holder.btnfollowback?.text  = context.getString(R.string.followed)
            holder.btnDenied?.isVisible = false
        }else{
            holder.btnfollowback?.text  = context.getString(R.string.followback)
            holder.btnDenied?.isVisible = true
        }
        holder.location?.text = myitem.location
        holder.followersName?.text = myitem.name
        Glide.with(context)
            .load(myitem.profileimg)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.followersImageView!!)
    }

    private fun followback(user: String, holder: ViewHolder) {
        if (!isLoading) {
            this.isLoading = true
            val requests = RetrofitInstance.retrofitBuilder.followUser(token, user)
            requests.enqueue(object : Callback<FollowUserResponseModel?> {
                override fun onResponse(
                    call: Call<FollowUserResponseModel?>,
                    response: Response<FollowUserResponseModel?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status) {
                            if (response.body()!!.message == "following") {
                                holder.btnfollowback?.text = context.getString(R.string.followed)
                                holder.btnDenied?.isVisible = false
                            } else if (response.body()!!.message == "unfollowed") {
                                holder.btnfollowback?.text = context.getString(R.string.followback)
                                holder.btnDenied?.isVisible = true
                            }
                        } else {
                            Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    this@FriendRequestAdapter.isLoading = false
                }

                override fun onFailure(call: Call<FollowUserResponseModel?>, t: Throwable) {
                    this@FriendRequestAdapter.isLoading = false
                }
            })
        }
    }
}
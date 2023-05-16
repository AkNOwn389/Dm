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
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.models.profileModel.UserDisplayData
import com.aknown389.dm.models.userviewModels.FollowUserResponseModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.aknown389.dm.utils.DataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuggestedFriendAdapter(private val useritemData: ArrayList<UserDisplayData>):
    RecyclerView.Adapter<SuggestedFriendAdapter.ViewHolder>() {
    private lateinit var context:Context
    private lateinit var token:String
    private lateinit var manager:DataManager
    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val suggestedImage: ImageView = itemView.findViewById(R.id.followersImageView)
        val suggestedName: TextView = itemView.findViewById(R.id.textFollowersNameTag)
        val suggestedFollowBtn: Button = itemView.findViewById(R.id.btnFollowSuggested)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        this.manager = DataManager(parent.context)
        this.token = manager.getAccessToken().toString()

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.suggested_friend_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return useritemData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myItem = useritemData[position]
        setUI(holder, myItem)
        setListerner(holder, myItem)
    }

    private fun setListerner(holder: ViewHolder, myItem: UserDisplayData) {
        holder.suggestedImage.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", myItem.user)
                it.startActivity(intent)
            }
        }
        holder.suggestedFollowBtn.setOnClickListener {
            followuser(myItem.user, holder)
        }
    }

    private fun setUI(holder: ViewHolder, myItem: UserDisplayData) {
        holder.suggestedName.setText(myItem.name)
        if (myItem.Following){
            holder.suggestedFollowBtn.text = "followed"
        }else{
            holder.suggestedFollowBtn.text = "follow"
        }
        val option = RequestOptions().placeholder(R.mipmap.greybg)
        Glide.with(context)
            .load(myItem.profileimg)
            .apply(option)
            .into(holder.suggestedImage)
    }

    private fun followuser(user: String, holder: ViewHolder) {
        val requests = RetrofitInstance.retrofitBuilder.followUser(token, user)
        requests.enqueue(object : Callback<FollowUserResponseModel?> {
            override fun onResponse(
                call: Call<FollowUserResponseModel?>,
                response: Response<FollowUserResponseModel?>
            ) {
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.status){
                        if (response.body()!!.message == "following"){
                            holder.suggestedFollowBtn.text = "followed"
                        }else if (response.body()!!.message == "unfollowed"){
                            holder.suggestedFollowBtn.text = "follow"
                        }
                    }else{
                        Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<FollowUserResponseModel?>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
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
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.models.showFriendModels.showFriendData
import com.aknown389.dm.models.userviewModels.FollowUserResponseModel
import com.bumptech.glide.Glide
import com.aknown389.dm.utils.DataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsAdapter(private val useritemData: ArrayList<showFriendData>):
    RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    private lateinit var context:Context
    private lateinit var token:String
    private lateinit var fragmentUserView: UserViewActivity
    private lateinit var manager:DataManager

    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val friendsImage: ImageView? = itemView.findViewById(R.id.friendsImageView)
        val friendsName: TextView? = itemView.findViewById(R.id.textFriendsNameTag)
        val unFollowBtn: Button? = itemView.findViewById(R.id.btnUnfollow)
        val location:TextView? = itemView.findViewById(R.id.location)
        val body:ConstraintLayout? = itemView.findViewById(R.id.body)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        this.manager = DataManager(parent.context)
        this.fragmentUserView = UserViewActivity()
        this.token = manager.getAccessToken().toString()

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_friends, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myItem = useritemData[position]
        setUI(myItem, holder)
        holder.friendsImage?.setOnClickListener {
            (context as? MainFragmentContainerActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", myItem.user)
                it.startActivity(intent)
            }
            /*
            val bundle = Bundle()
            bundle.putString("username", myItem.user)
            fragmentUserView.arguments = bundle
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                replace(R.id.mainFragmentContainer, fragmentUserView)
                commit()
            }

             */
        }
        holder.unFollowBtn?.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder((context as AppCompatActivity))
            builder.setPositiveButton("Yes"){_, _ ->
                unfollowuser(myItem.user, holder)
            }
            builder.setNegativeButton("No"){_, _ -> }
            builder.setTitle("Unfollow?")
            builder.setMessage("Are you sure you want yo unfollow ${myItem.name}?")
            builder.create().show()
        }
        holder.body?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", myItem.user)
                it.startActivity(intent)
            }
        }
        holder.friendsName?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", myItem.user)
                it.startActivity(intent)
            }
        }
        holder.friendsImage?.setOnClickListener {
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", myItem.user)
                it.startActivity(intent)
            }
        }
    }

    private fun setUI(myItem: showFriendData, holder: ViewHolder) {
        holder.friendsName?.text = myItem.name
        holder.location?.text = myItem.location
        holder.friendsImage?.let {
            Glide.with(context)
                .load(myItem.profileimg)
                .placeholder(R.drawable.progress_animation)
                .error(R.mipmap.greybg)
                .into(it)
        }
    }

    override fun getItemCount(): Int {
        return useritemData.size
    }

    private fun unfollowuser(user: String, holder: ViewHolder) {
        val requests = RetrofitInstance.retrofitBuilder.followUser(token, user)
        requests.enqueue(object : Callback<FollowUserResponseModel?> {
            override fun onResponse(
                call: Call<FollowUserResponseModel?>,
                response: Response<FollowUserResponseModel?>
            ) {
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.status){
                        if (response.body()!!.message == "following"){
                            holder.unFollowBtn?.text = context.getString(R.string.unfollow)
                        }else if (response.body()!!.message == "unfollowed"){
                            holder.unFollowBtn?.text = context.getString(R.string.follow)
                        }
                    }else{
                        Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<FollowUserResponseModel?>, t: Throwable) {
            }
        })
    }
}
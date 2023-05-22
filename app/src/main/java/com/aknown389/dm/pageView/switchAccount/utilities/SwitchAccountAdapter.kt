package com.aknown389.dm.pageView.switchAccount.utilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.databinding.DialogItemSwitchAccountBinding
import com.aknown389.dm.databinding.DialogSwitchAccountMenuBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.pageView.switchAccount.models.UserDetailBody
import com.aknown389.dm.pageView.switchAccount.remote.repository.SwitchAccountRepository
import com.aknown389.dm.utils.DataManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify

class SwitchAccountAdapter:RecyclerView.Adapter<SwitchAccountAdapter.ViewHolder>() {
    private lateinit var parent: ViewGroup
    private var repository: SwitchAccountRepository = SwitchAccountRepository()
    var accountList:ArrayList<UserAccountDataClass> = ArrayList()
    private lateinit var dataBase: AppDataBase
    private lateinit var manager: DataManager
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val name:TextView = view.findViewById(R.id.accountName)
        val lastLogin: TextView = view.findViewById(R.id.lastLogin)
        val username:TextView = view.findViewById(R.id.userName)
        val userImage:ImageView = view.findViewById(R.id.profileImage)
        val root: ConstraintLayout = view.findViewById(R.id.root)
        val menu: ImageButton = view.findViewById(R.id.menu)

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SwitchAccountAdapter.ViewHolder {
        this.dataBase = AppDataBase.getDatabase(parent.context.applicationContext)
        this.manager = DataManager(parent.context.applicationContext)
        this.parent = parent
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_switch_account, parent, false))
    }

    override fun onBindViewHolder(holder: SwitchAccountAdapter.ViewHolder, position: Int) {
        val item = accountList[position]
        holder.username.text = item.info.user
        if (item.info.name != null){
            holder.name.visibility = View.VISIBLE
            holder.name.text = item.info.name
        }else{
            holder.name.visibility = View.GONE
        }
        if (item.lastLogin != null){
            holder.lastLogin.text = item.lastLogin
        }else{
            holder.lastLogin.visibility = View.GONE
        }
        if (item.info.profileimg != null){
            Glide.with(parent.context.applicationContext)
                .load(item.info.profileimg)
                .placeholder(R.mipmap.greybg)
                .error(R.mipmap.greybg)
                .into(holder.userImage)
        }
        holder.root.setOnClickListener {
            showOption(item)
        }
        holder.menu.setOnClickListener {
            showMenuDialog(item)
        }
        updateData(item, holder)
    }
    private fun updateData(item: UserAccountDataClass, holder: ViewHolder) {
        (parent.context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
            try {
                val body = UserDetailBody(username = item.info.user)
                val response = repository.getUserSwitchAccountDetail(body, parent.context.applicationContext)
                if (response.isSuccessful){
                    val resBody = response.body()!!
                    if (holder.lastLogin.text != resBody.lastLogin){
                        holder.lastLogin.text = resBody.lastLogin
                        item.lastLogin = resBody.lastLogin
                        try {
                            withContext(Dispatchers.IO){
                                dataBase.userDao().updateAccountLastLogin(id = item.info.id, lastLogin = resBody.lastLogin.toString())
                            }
                        }catch (e:Exception){
                            Toast.makeText(parent.context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (holder.name.text != item.info.name){
                        holder.name.text = item.info.name
                        item.info.name = resBody.name.toString()
                        try {
                            withContext(Dispatchers.IO){
                                dataBase.userDao().updateAccountName(id = item.info.id, name = resBody.name.toString())
                            }
                        }catch (e:Exception){
                            Toast.makeText(parent.context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (item.info.profileimg != resBody.image){
                        Glide.with(parent.context)
                            .load(resBody.image)
                            .placeholder(R.mipmap.greybg)
                            .error(R.mipmap.greybg)
                            .into(holder.userImage)
                        item.info.profileimg = resBody.image.toString()
                        try {
                            withContext(Dispatchers.IO){
                                dataBase.userDao().updateAccountImage(id = item.info.profileimg!!, image = resBody.image.toString())
                            }
                        }catch (e:Exception){
                            Toast.makeText(parent.context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }catch (e:Exception){
                Toast.makeText(parent.context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showOption(item: UserAccountDataClass) {
        val dialog:BottomSheetDialog = BottomSheetDialog(parent.context, R.style.BottomSheetTheme)
        val binding = DialogItemSwitchAccountBinding.inflate(LayoutInflater.from(parent.context.applicationContext), parent, false)
        dialog.setContentView(binding.root)
        dialog.show()
        binding.cansel.setOnClickListener {  }
        binding.login.setOnClickListener {  }
    }

    private fun switchAccount(item: UserAccountDataClass){

    }

    private fun showMenuDialog(item:UserAccountDataClass){
        val dialog:BottomSheetDialog = BottomSheetDialog(parent.context, R.style.BottomSheetTheme)
        val binding = DialogSwitchAccountMenuBinding.inflate(LayoutInflater.from(parent.context.applicationContext), parent, false)
        dialog.setContentView(binding.root)
        dialog.show()
        binding.apply {
            removeAccount.setOnClickListener {
                val builder = AlertDialog.Builder(parent.context)
                builder.setPositiveButton("Yes") { _, _ ->
                    (parent.context as? AppCompatActivity)?.lifecycleScope?.launch {
                        dataBase.userDao().deleteAccountById(item.id)
                        val pos = accountList.indexOf(item)
                        notifyItemRemoved(pos)
                        accountList.remove(item)
                    }
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.setTitle("remove account?")
                builder.setMessage("Are you sure you want to remove?")
                builder.create().show()
            }
        }

    }
    override fun getItemCount(): Int = accountList.size

    fun setData(account:ArrayList<UserAccountDataClass>){
        for (i in account){
            accountList.add(i)
            notifyItemInserted(accountList.size-1)
        }
    }
    fun addData(account:UserAccountDataClass){
        accountList.add(account)
        notifyItemInserted(accountList.size -1)
    }
}
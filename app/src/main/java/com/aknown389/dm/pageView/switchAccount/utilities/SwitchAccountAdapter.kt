package com.aknown389.dm.pageView.switchAccount.utilities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.activities.MainActivity
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.databinding.DialogItemSwitchAccountBinding
import com.aknown389.dm.databinding.DialogSwitchAccountMenuBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.dialogs.LoadingAlertDialog
import com.aknown389.dm.models.loginRegModels.LoginModel
import com.aknown389.dm.models.loginRegModels.LoginResponseModelV2
import com.aknown389.dm.models.loginRegModels.Token
import com.aknown389.dm.pageView.switchAccount.models.IsAuthenticatedResponse
import com.aknown389.dm.pageView.switchAccount.models.RefreshTokenBody
import com.aknown389.dm.pageView.switchAccount.models.RefreshTokenResponse
import com.aknown389.dm.pageView.switchAccount.models.UserDetailBody
import com.aknown389.dm.pageView.switchAccount.remote.instance.SwitchAccountInstance2
import com.aknown389.dm.pageView.switchAccount.remote.repository.SwitchAccountRepository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.getStringTime
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udevel.widgetlab.TypingIndicatorView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SwitchAccountAdapter:RecyclerView.Adapter<SwitchAccountAdapter.ViewHolder>() {
    private var isLoadingShowing: Boolean = false
    private lateinit var parent: ViewGroup
    private lateinit var loadingAlertDialog: LoadingAlertDialog
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
        loadingAlertDialog  = LoadingAlertDialog(parent.context)
        this.parent = parent
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_switch_account, parent, false))
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
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
            holder.lastLogin.text = "last login ${item.lastLogin}"
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
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateData(item: UserAccountDataClass, holder: ViewHolder) {
        (parent.context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
            try {
                val body = UserDetailBody(username = item.info.user)
                val response = repository.getUserSwitchAccountDetail(body, parent.context.applicationContext)
                if (response.isSuccessful){
                    val resBody = response.body()!!
                    if (holder.lastLogin.text != resBody.lastLoginDisplay){
                        "last login ${resBody.lastLoginDisplay}".also { holder.lastLogin.text = it }
                        item.lastLogin = resBody.lastLoginDisplay
                        try {
                            withContext(Dispatchers.IO){
                                dataBase.userDao().updateAccountLastLogin(id = item.info.id, lastLogin = resBody.lastLoginDisplay.toString())
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
        binding.cansel.setOnClickListener { dialog.dismiss() }
        binding.login.setOnClickListener {
            loadingAlertDialog.start()
            isLoadingShowing = true
            switchAccount(item) }
    }


    private suspend fun checkCredential(item: UserAccountDataClass): Boolean {
        return try {
            val accessToken = "${item.tokenType} ${item.accessToken}"
            val response:Response<IsAuthenticatedResponse> = repository.isAuthenticatedForOtherUser(token = accessToken)
            response.body()!!.status
        }catch (e:Exception){
            false
        }
    }

    private fun switchAccount(item: UserAccountDataClass){
        (parent.context as? AppCompatActivity)?.lifecycleScope?.launch {
            if (checkCredential(item)){
                val token = Token(refreshToken = item.refreshToken,
                accessToken = item.accessToken,
                tokenType = item.tokenType)
                manager.deleteAllData()
                manager.saveToken(token)
                manager.saveUserData(item.info)
                goToMainActivity()
            }else{
                val newToken = getNewToken(item)
                if (newToken != null){
                    withContext(Dispatchers.IO){
                        try {
                            dataBase.userDao().updateAccountRefreshToken(id = item.id, refreshToken = newToken.refresh_token)
                            dataBase.userDao().updateAccountToken(id = item.id, accessToken = newToken.accesstoken)
                        }catch (e:java.lang.Exception){
                            Toast.makeText(parent.context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    try {
                        val token = Token(refreshToken = newToken.refresh_token,
                            accessToken = newToken.accesstoken,
                            tokenType = item.tokenType)
                        manager.deleteAllData()
                        manager.saveToken(token)
                        manager.saveUserData(item.info)
                        goToMainActivity()
                    }catch (e:Exception){
                        Toast.makeText(parent.context, e.message, Toast.LENGTH_LONG).show()
                    }
                }else{
                    //gamitan na ng login dialog
                    if (isLoadingShowing){
                        loadingAlertDialog.dismiss()
                        isLoadingShowing = false
                    }
                    val inflater = LayoutInflater.from(parent.context)
                    val view = inflater.inflate(R.layout.dialog_login, parent, false)
                    val emailEditText = view.findViewById<EditText>(R.id.email)
                    val passwordEditText = view.findViewById<EditText>(R.id.password)
                    val dialog = AlertDialog.Builder(parent.context)
                        .setTitle("Login")
                        .setView(view)
                        .setPositiveButton("Login") { _, _ ->
                            val email = emailEditText.text.toString()
                            val password = passwordEditText.text.toString()
                            // Submit email and password to server
                            login(email, password, view)
                        }
                        .setNegativeButton("Cancel", null)
                        .create()
                    dialog.show()
                }
            }
        }
    }

    private fun login(username: String, password: String, view: View){
        val emaileditText = view.findViewById<EditText>(R.id.email)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val progress:TypingIndicatorView = view.findViewById(R.id.progress)
        if (username == "" || username.isBlank()) {
            Toast.makeText(parent.context, "Please input email", Toast.LENGTH_SHORT).show()
            emaileditText.error = "Empty"
            return
        }
        if (password == "" || password.isBlank()) {
            Toast.makeText(parent.context, "Please input password", Toast.LENGTH_SHORT).show()
            passwordEditText.error = "Empty"
            return
        }
        val body = LoginModel(username, password)
        progress.isVisible = true
        loadingAlertDialog.start()
        isLoadingShowing = true
        val requestBody = RetrofitInstance.retrofitBuilder.loginC(body)
        requestBody.enqueue(object : Callback<LoginResponseModelV2?> {
            override fun onResponse(
                call: Call<LoginResponseModelV2?>,
                response: Response<LoginResponseModelV2?>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    val res = response.body()!!
                    if (res.status) {
                        progress.isVisible = false
                        Toast.makeText(parent.context, res.message, Toast.LENGTH_SHORT)
                            .show()
                        manager.saveToken(value = res.token)
                        manager.saveUserData(data = res.info)
                        val data = UserAccountDataClass(
                            id = res.info.id,
                            info = res.info,
                            accessToken = res.token.accessToken!!,
                            refreshToken = res.token.refreshToken.toString(),
                            tokenType = res.token.tokenType!!
                        )
                        saveAccountInDb(data)
                        goToMainActivity()
                    } else {
                        progress.isVisible = false
                        if (res.status_code == 1) {
                            passwordEditText.error = "invalid"
                        } else {
                            if (res.status_code == 2) {
                                emaileditText.error = "invalid"
                            }
                        }
                        Toast.makeText(parent.context, res.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    progress.isVisible = false
                    Toast.makeText(parent.context, "Internet error", Toast.LENGTH_SHORT)
                        .show()

                }
            }

            override fun onFailure(call: Call<LoginResponseModelV2?>, t: Throwable) {
                progress.isVisible = false
                Toast.makeText(parent.context, "Connection Error", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }
    private fun saveAccountInDb(account:UserAccountDataClass){
        (parent.context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.IO) {
            dataBase.userDao().insertAccount(account)
            Log.d("switchAccount", "new account save in db $account")
        }
    }

    private fun goToMainActivity(){
        if (isLoadingShowing){
            loadingAlertDialog.dismiss()
            isLoadingShowing = false
        }
        (parent.context as? AppCompatActivity)?.let {
            val intent = Intent(it, MainActivity::class.java)
            it.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            it.startActivity(intent)
            it.finishAffinity()
        }
    }

    private suspend fun getNewToken(item: UserAccountDataClass): RefreshTokenResponse? {
        return try {
            val accessToken = "${item.tokenType} ${item.accessToken}"
            val body = RefreshTokenBody(refresh = item.refreshToken)
            val response = repository.refreshToken(token = accessToken, body = body)
            if (response.body()?.status == true){
                response.body()
            }else{
                null
            }
        }catch (e:Exception){
            Log.d(TAG, e.printStackTrace().toString())
            Toast.makeText(parent.context, e.message, Toast.LENGTH_SHORT).show()
            null}
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
    val TAG = "SwitchAccountAdapter"
}
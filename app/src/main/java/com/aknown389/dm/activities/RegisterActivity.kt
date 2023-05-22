package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityRegisterBinding
import com.aknown389.dm.api.retroInstance.LoginRegisterInstance
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.dialogs.DialogVerifyOtp
import com.aknown389.dm.models.loginRegModels.RegReqCodeModel
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.snackbar
import com.aknown389.dm.pageView.main.viewModel.MainViewModel
import com.aknown389.dm.pageView.main.viewModel.MainViewModelFactory
import com.aknown389.dm.pageView.registerPage.models.RegisterDataParcel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private var usernameToRegister: String? = null
    private var emailToRegister: String? = null
    private var registerId: String? = null
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var appDataBase: AppDataBase
    private lateinit var gson: Gson
    private var isRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setValue()
        setListener()
    }

    private fun checkCredentials():Boolean{
        val username = binding.registerUsername.text.toString()
        val email = binding.registerEmail.text.toString()
        val name = binding.registerName.text.toString()
        val password = binding.registerPassword.text.toString()
        val confirmPassword = binding.registerComfimPassword.text.toString()
        if (username == "" || username.isEmpty()){
            Toast.makeText(this, "Please input username", Toast.LENGTH_SHORT).show()
            binding.registerUsername.error = "Empty"
            return false
        }
        if (email == "" || email.isEmpty()){
            Toast.makeText(this, "Please input email", Toast.LENGTH_SHORT).show()
            binding.registerEmail.error = "Empty"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please input valid email", Toast.LENGTH_SHORT).show()
            binding.registerEmail.error = "invalid"
            return false
        }
        if (name == "" || name.isEmpty()){
            Toast.makeText(this, "Please input name", Toast.LENGTH_SHORT).show()
            binding.registerName.error = "Empty"
            return false
        }
        if (name.split(" ").size < 2){
            Toast.makeText(this, "Must have an surename", Toast.LENGTH_SHORT).show()
            binding.registerName.error = "must have surename"
            return false
        }else if (name.split(" ")[0].length < 3){
            Toast.makeText(this, "Must have 4 character first name", Toast.LENGTH_SHORT).show()
            binding.registerName.error = "too short"
            return false
        }else if (name.split(" ")[0].length < 3){
            Toast.makeText(this, "Must have 4 character last name", Toast.LENGTH_SHORT).show()
            binding.registerName.error = "too short"
            return false
        }
        if (password == "" || password.isEmpty()){
            Toast.makeText(this, "Please input password", Toast.LENGTH_SHORT).show()
            binding.registerPassword.error = "Empty"
            return false
        }
        if (confirmPassword == "" || confirmPassword.isEmpty()){
            Toast.makeText(this, "Please input comfirm password", Toast.LENGTH_SHORT).show()
            binding.registerComfimPassword.error = "Empty"
            return false
        }
        if (password != confirmPassword){
            Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show()
            binding.registerComfimPassword.error = "Not match"
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show()
            binding.registerPassword.error = "Too short"
            binding.registerComfimPassword.error = "Too short"
            return false
        }
        return true
    }

    private fun beginRegister(regId: String) {
        val username = binding.registerUsername.text.toString()
        val email = binding.registerEmail.text.toString()
        val name = binding.registerName.text.toString()
        val password = binding.registerPassword.text.toString()
        val confirmPassword = binding.registerComfimPassword.text.toString()
        val bundle =Bundle()
        val parcel = RegisterDataParcel(
            username = username,
            email = email,
            name = name,
            password = password,
            comfirmPassword = confirmPassword,
            registerId = regId
        )
        val bottomSheet = DialogVerifyOtp()
        bottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
        bundle.putString("parcel", gson.toJson(parcel))
        bottomSheet.arguments = bundle
        bottomSheet.show(supportFragmentManager, "verifyOtp")
    }

    private fun setListener() {
        binding.registerBtn.setOnClickListener {
            if (checkCredentials()){
                val username = binding.registerUsername.text.toString()
                val email = binding.registerEmail.text.toString()
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(this, "Please input valid email", Toast.LENGTH_SHORT).show()
                    binding.registerEmail.error = "invalid"
                    return@setOnClickListener
                }
                val body = RegReqCodeModel(email = email, username = username)
                getRegisterCode(body)
            }
        }
    }

    private fun setValue() {
        val db:AppDataBase = AppDataBase.getDatabase(this)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository = repository, dataBase = db, token = "null")
        appDataBase = AppDataBase.getDatabase(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        gson = Gson()
    }

    private fun getRegisterCode(body: RegReqCodeModel) {
        if (isRequested && binding.registerEmail.text.toString() == emailToRegister && binding.registerUsername.text.toString() == usernameToRegister){
            registerId?.let { beginRegister(it) }
        }else{
            binding.registerProgressbar.isVisible = true
            lifecycleScope.launch {
                val response = try {
                    LoginRegisterInstance.api.getRegCode(body)
                }catch (e:Exception){
                    return@launch
                }
                if (response.isSuccessful && response.body() != null){
                    val resBody = response.body()!!
                    if (resBody.status) {
                        this@RegisterActivity.registerId = resBody.data.regId
                        this@RegisterActivity.emailToRegister = resBody.data.email
                        this@RegisterActivity.usernameToRegister = resBody.data.username
                        this@RegisterActivity.isRequested = true
                        beginRegister(resBody.data.regId)
                    }else if (resBody.message == "already requested please wait 5 minutes"){
                        beginRegister(registerId.toString())
                        this@RegisterActivity.emailToRegister = body.email
                        this@RegisterActivity.usernameToRegister = body.username
                    }else{
                        binding.registerActivityRoot.snackbar(resBody.message)
                    }
                }
                binding.registerProgressbar.isVisible = false
            }
        }
    }
    fun goBack(view:View){
        finish()
    }
}
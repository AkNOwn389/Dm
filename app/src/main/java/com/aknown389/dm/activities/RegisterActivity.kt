package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.databinding.ActivityRegisterBinding
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.models.loginRegModels.RegisterModel
import com.aknown389.dm.models.loginRegModels.RegisterResModelV2
import com.aknown389.dm.api.retroInstance.LoginRegisterInstance
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.loginRegModels.RegReqCodeModel
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.snackbar
import com.aknown389.dm.pageView.main.viewModel.MainViewModel
import com.aknown389.dm.pageView.main.viewModel.MainViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var reGbody: RegisterModel
    private var isRequest = false
    private lateinit var regId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setValue()
        setListener()
    }

    private fun beginRegister(){
        val username = binding.registerUsername.text.toString()
        val email = binding.registerEmail.text.toString()
        val name = binding.registerName.text.toString()
        val password = binding.registerPassword.text.toString()
        val comfirmpassword = binding.registerComfimPassword.text.toString()
        val regCode = binding.registerCodeeditText.text.toString()
        if (username == "" || username.isEmpty()){
            Toast.makeText(this, "Please input username", Toast.LENGTH_SHORT).show()
            binding.registerUsername.error = "Empty"
            return
        }
        if (email == "" || email.isEmpty()){
            Toast.makeText(this, "Please input email", Toast.LENGTH_SHORT).show()
            binding.registerEmail.error = "Empty"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please input valid email", Toast.LENGTH_SHORT).show()
            binding.registerEmail.error = "invalid"
            return
        }
        if (name == "" || name.isEmpty()){
            Toast.makeText(this, "Please input name", Toast.LENGTH_SHORT).show()
            binding.registerName.error = "Empty"
            return
        }
        if (password == "" || password.isEmpty()){
            Toast.makeText(this, "Please input password", Toast.LENGTH_SHORT).show()
            binding.registerPassword.error = "Empty"
            return
        }
        if (comfirmpassword == "" || comfirmpassword.isEmpty()){
            Toast.makeText(this, "Please input comfirm password", Toast.LENGTH_SHORT).show()
            binding.registerComfimPassword.error = "Empty"
            return
        }
        if (password != comfirmpassword){
            Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show()
            binding.registerComfimPassword.error = "Not match"
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show()
            binding.registerPassword.error = "Too short"
            binding.registerComfimPassword.error = "Too short"
            return
        }
        if (regCode == "" || regCode.isEmpty()){
            Toast.makeText(this, "Please requests code", Toast.LENGTH_SHORT).show()
            return
        }
        if (regId.isEmpty()){
            Toast.makeText(this, "Please requests code", Toast.LENGTH_SHORT).show()
            return
        }
        binding.registerProgressbar.isVisible=true
        reGbody = RegisterModel(regCode.toInt(), regId, username, email, name, password, comfirmpassword)
        val responseData = RetrofitInstance.retrofitBuilder.register(reGbody)
        responseData.enqueue(object : Callback<RegisterResModelV2?> {
            override fun onResponse(
                call: Call<RegisterResModelV2?>,
                response: Response<RegisterResModelV2?>
            ) {
                val resBody: RegisterResModelV2
                try {
                    resBody = response.body()!!
                }catch (e: java.lang.NullPointerException){
                    binding.registerProgressbar.isVisible = false
                    return
                }
                binding.registerProgressbar.isVisible=false
                if (resBody.status){
                    Toast.makeText(this@RegisterActivity, resBody.message, Toast.LENGTH_SHORT).show()
                    val manager = DataManager(this@RegisterActivity)
                    manager.saveUserData(data = resBody.info)
                    manager.saveToken(value = resBody.token)
                    Intent(this@RegisterActivity, MainFragmentContainerActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }else{
                    Toast.makeText(this@RegisterActivity, resBody.message, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RegisterResModelV2?>, t: Throwable) {
                binding.registerProgressbar.isVisible=false
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setListener() {
        binding.registerRequestCodebtn.setOnClickListener {
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
        binding.registerBtn.setOnClickListener() {
            beginRegister()
        }
    }

    private fun setValue() {
        val db:AppDataBase = AppDataBase.getDatabase(this)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository = repository, dataBase = db, token = "null")
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private fun getRegisterCode(body: RegReqCodeModel) {
        binding.registerProgressbar.isVisible = true
        lifecycleScope.launch {
            val response = try {
                LoginRegisterInstance.api.getRegCode(body)
            }catch (e:Exception){
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                val resBody = response.body()!!
                if (resBody.status){
                    this@RegisterActivity.regId = resBody.data.regId
                    Toast.makeText(this@RegisterActivity, "email validation created", Toast.LENGTH_SHORT).show()
                    this@RegisterActivity.isRequest = true
                }else{
                    //Toast.makeText(this@registerActivity, response.message, Toast.LENGTH_SHORT).show()
                    binding.registerActivityRoot.snackbar(resBody.message)
                    this@RegisterActivity.isRequest = false
                }
            }
            binding.registerProgressbar.isVisible = false
        }
    }

    fun goBack(view: View){
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}
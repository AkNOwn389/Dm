package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityLoginBinding
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.models.loginRegModels.LoginModel
import com.aknown389.dm.models.loginRegModels.LoginResponseModelV2
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dataBase: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setValue()
        setListener()
    }

    private fun setListener() {
        binding.donthaveanaccount.setOnClickListener() {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
        binding.loginBtn.setOnClickListener() {login()}
    }

    private fun login(){
        val username = binding.loginEmail.text.toString()
        val password = binding.loginPassword.text.toString()
        if (username == "" || username.isBlank()) {
            Toast.makeText(this, "Please input email", Toast.LENGTH_SHORT).show()
            binding.loginEmail.error = "Empty"
            return
        }
        if (password == "" || password.isBlank()) {
            Toast.makeText(this, "Please input password", Toast.LENGTH_SHORT).show()
            binding.loginPassword.error = "Empty"
            return
        }
        val body = LoginModel(username, password)
        binding.loginProgressBar.isVisible = true

        val requestBody = RetrofitInstance.retrofitBuilder.loginC(body)
        requestBody.enqueue(object : Callback<LoginResponseModelV2?> {
            override fun onResponse(
                call: Call<LoginResponseModelV2?>,
                response: Response<LoginResponseModelV2?>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    val res = response.body()!!
                    if (res.status) {
                        binding.loginProgressBar.isVisible = false
                        Toast.makeText(this@LoginActivity, res.message, Toast.LENGTH_SHORT)
                            .show()
                        val manager = DataManager(this@LoginActivity)
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
                        Intent(
                            this@LoginActivity,
                            MainFragmentContainerActivity::class.java
                        ).also {
                            startActivity(it)
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            finishAffinity()
                        }
                    } else {
                        binding.loginProgressBar.isVisible = false
                        if (res.status_code == 1) {
                            binding.loginPassword.error = "invalid"
                        } else {
                            if (res.status_code == 2) {
                                binding.loginEmail.error = "invalid"
                            }
                        }
                        Toast.makeText(this@LoginActivity, res.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    binding.loginProgressBar.isVisible = false
                    Toast.makeText(this@LoginActivity, "Internet error", Toast.LENGTH_SHORT)
                        .show()

                }
            }

            override fun onFailure(call: Call<LoginResponseModelV2?>, t: Throwable) {
                binding.loginProgressBar.isVisible = false
                Toast.makeText(this@LoginActivity, "Connection Error", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }
    private fun setValue() {
        dataBase = AppDataBase.getDatabase(this)
    }

    private fun saveAccountInDb(account:UserAccountDataClass){
        lifecycleScope.launch(Dispatchers.IO) {
            dataBase.userDao().insertAccount(account)
            Log.d("switchAccount", "new account save in db $account")
        }
    }
}

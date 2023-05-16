package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.aknown389.dm.databinding.ActivityLoginBinding
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.models.loginRegModels.LoginModel
import com.aknown389.dm.models.loginRegModels.LoginResponseModelV2
import com.aknown389.dm.utils.DataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.donthaveanaccount.setOnClickListener(){
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }



        binding.loginBtn.setOnClickListener(){
            val username = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if (username == "" || username == null){
                Toast.makeText(this, "Please input email", Toast.LENGTH_SHORT).show()
                binding.loginEmail.error = "Empty"
                return@setOnClickListener
            }
            if (password == "" || password == null){
                Toast.makeText(this, "Please input password", Toast.LENGTH_SHORT).show()
                binding.loginPassword.error = "Empty"
                return@setOnClickListener
            }
            val body = LoginModel(username, password)
            binding.loginProgressBar.isVisible = true

            val requestbody = RetrofitInstance.retrofitBuilder.loginC(body)
            requestbody.enqueue(object : Callback<LoginResponseModelV2?> {
                override fun onResponse(
                    call: Call<LoginResponseModelV2?>,
                    response: Response<LoginResponseModelV2?>
                ) {
                    if (response.isSuccessful && response.body()!=null){

                        val res = response.body()!!
                        if (res.status){
                            binding.loginProgressBar.isVisible = false
                            Toast.makeText(this@LoginActivity, res.message, Toast.LENGTH_SHORT).show()
                            val manager = DataManager(this@LoginActivity)
                            manager.saveToken(value = res.token)
                            manager.saveUserData(data = res.info)
                            Intent(this@LoginActivity, MainFragmentContainerActivity::class.java).also {
                                startActivity(it)
                                finish()
                            }
                        }else{
                            binding.loginProgressBar.isVisible = false
                            if (res.status_code == 1){
                                binding.loginPassword.error = "invalid"
                            }else {
                                if(res.status_code == 2){
                                    binding.loginEmail.error = "invalid"
                                }
                            }
                            Toast.makeText(this@LoginActivity, res.message, Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        binding.loginProgressBar.isVisible = false
                        Toast.makeText(this@LoginActivity, "Internet error", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<LoginResponseModelV2?>, t: Throwable) {
                    binding.loginProgressBar.isVisible = false
                    Toast.makeText(this@LoginActivity, "Connection Error", Toast.LENGTH_SHORT).show()
                }
            })
            }
        }
    }

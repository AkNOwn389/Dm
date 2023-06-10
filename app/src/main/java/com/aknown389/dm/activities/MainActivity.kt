package com.aknown389.dm.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityMainBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.models.loginRegModels.Token
import com.aknown389.dm.pageView.main.viewModel.MainViewModel
import com.aknown389.dm.pageView.main.viewModel.MainViewModelFactory
import com.aknown389.dm.pageView.switchAccount.models.RefreshTokenBody
import com.aknown389.dm.pageView.switchAccount.models.RefreshTokenResponse
import com.aknown389.dm.pageView.switchAccount.remote.repository.SwitchAccountRepository
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.Constants.Companion.APP_VERSION
import com.aknown389.dm.utils.Constants.Companion.CHANNEL_ID
import com.aknown389.dm.utils.DataManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var repository = SwitchAccountRepository()
    private var manager: DataManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setVal()
        checkUserData()
        behavior()
        //binding.louncherProgressBar.isVisible = true
        binding.appVersion.text = APP_VERSION
    }

    private fun behavior(){
        viewModel.bahavior()
        viewModel.loginBahaviorResponse.observe(this){
            Log.d(TAG, "login status code: ${it.status_code}")
            when(it.status_code){
                403 -> updateToken()
                401 -> updateToken()
                200 -> goToMain()
                else -> goToMain()
            }
        }
    }

    private suspend fun getNewToken(): RefreshTokenResponse? {
        return try {
            val tokenType = "Bearer"
            val accessToken = "$tokenType ${manager?.getAccessToken()}"
            val body = manager?.getRefreshToken()?.let { RefreshTokenBody(refresh = it) }
            val response = body?.let { repository.refreshToken(token = accessToken, body = it) }
            if (response?.body()?.status == true){
                response.body()
            }else{
                null
            }
        }catch (e:Exception){
            Log.d(TAG, e.printStackTrace().toString())
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            null
        }
    }
    private fun updateToken() {
        lifecycleScope.launch {
            val newToken = getNewToken()
            if (newToken != null){
                val token = Token(
                    refreshToken = newToken.refresh_token,
                    accessToken = newToken.accesstoken,
                    tokenType = "Bearer"
                )
                manager?.saveToken(token)
                goToMain()
            }else{
                goToLogin()
            }
        }
    }

    private fun goToLogin(){
        Intent(this@MainActivity, LoginActivity::class.java).also {
            binding.louncherProgressBar.isVisible = false
            startActivity(it)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }
    private fun goToMain(){
        Intent(this@MainActivity, MainFragmentContainerActivity::class.java).also {
            binding.louncherProgressBar.isVisible = false
            startActivity(it)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }

    private fun checkUserData() {
        val key = manager!!.getAccessToken()
        Log.d(TAG, key.toString())
        if (key == null) {
            goToLogin()
        }
    }

    private fun setVal() {
        manager = DataManager(this)
        createNotificationChannel()
        val repository = Repository()
        val db:AppDataBase = AppDataBase.getDatabase(this)
        val viewModelFactory = MainViewModelFactory(repository = repository, dataBase = db, token = manager!!.getAccessToken().toString())
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

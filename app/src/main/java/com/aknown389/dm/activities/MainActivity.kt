package com.aknown389.dm.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityMainBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.pageView.main.viewModel.MainViewModel
import com.aknown389.dm.pageView.main.viewModel.MainViewModelFactory
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.Constants.Companion.APP_VERSION
import com.aknown389.dm.utils.Constants.Companion.CHANNEL_ID
import com.aknown389.dm.utils.DataManager

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var manager: DataManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setVal()
        bahavior()
        //checkUserData()
        binding.louncherProgressBar.isVisible = true
        binding.appVersion.text = APP_VERSION
    }

    fun bahavior(){
        viewModel.bahavior()
        viewModel.loginBahaviorResponse.observe(this){
            Log.d(TAG, "login status code: ${it.status_code}")
            when(it.status_code){
                403 -> goToLogin()
                401 -> goToLogin()
                200 -> goToMain()
                else -> goToMain()
            }
        }
    }
    private fun goToLogin(){
        Intent(this@MainActivity, LoginActivity::class.java).also {
            binding.louncherProgressBar.isVisible = false
            startActivity(it)
            finish()
        }
    }
    private fun goToMain(){
        Intent(this@MainActivity, MainFragmentContainerActivity::class.java).also {
            binding.louncherProgressBar.isVisible = false
            startActivity(it)
            finish()
        }
    }

    private fun checkUserData() {
        val key = manager!!.getAccessToken()
        Log.d(TAG, key.toString())
        if (key == null) {
            goToLogin()
        }else{
            goToMain()
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

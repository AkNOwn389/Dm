package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.size.ViewSizeResolver
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivitySwitchAccountBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.pageView.switchAccount.utilities.SwitchAccountAdapter
import com.aknown389.dm.pageView.switchAccount.viewModel.SwitchAccountViewModel
import com.aknown389.dm.pageView.switchAccount.viewModel.SwitchAccountViewModelFactory
import com.aknown389.dm.utils.DataManager

class SwitchAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySwitchAccountBinding
    private lateinit var adapter: SwitchAccountAdapter
    private lateinit var layoutManager:LinearLayoutManager
    private lateinit var viewModel: SwitchAccountViewModel
    private lateinit var dataBase: AppDataBase
    private lateinit var manager:DataManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwitchAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        iniT()
    }

    private fun iniT() {
        setValue()
        setUI()
        setListener()
        loadAccount()
    }

    private fun loadAccount() {
        viewModel.getUserAccountFromDataBase(this)
    }

    private fun setListener() {
        val currentAccount = manager.getUserData()
        viewModel.userAccount.observe(this){ data ->
            binding.noContent.visibility = View.GONE
            try {
                for (i in data){
                    if (i.info.user != currentAccount?.user){
                        if (i !in adapter.accountList){
                            adapter.addData(i)
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun setUI() {

    }

    private fun setValue() {
        manager = DataManager(this)
        dataBase = AppDataBase.getDatabase(this)
        adapter = SwitchAccountAdapter()
        layoutManager = LinearLayoutManager(this)
        binding.apply {
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            backBtn.setOnClickListener {
                finish()
            }
            loginNewAccount.setOnClickListener {
                Intent(this@SwitchAccountActivity, LoginActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(androidx.appcompat.R.anim.abc_slide_in_bottom, R.anim.fade_out)
                }
            }
        }
        viewModel = ViewModelProvider(this, SwitchAccountViewModelFactory(appDataBase = dataBase))[SwitchAccountViewModel::class.java]
    }
}
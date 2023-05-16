package com.aknown389.dm.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityChatBinding
import com.aknown389.dm.fragments.ChatHomeContainer

class ChatActivity : AppCompatActivity() {

    private var binding: ActivityChatBinding?=null
    private lateinit var mainChatActivity: ChatHomeContainer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        mainChatActivity = ChatHomeContainer()
        loadMainView()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun loadMainView() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.ChatflFragmentContainer, mainChatActivity)
            commit()
        }
    }
}
package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityPasswordManagerBinding

class PasswordManagerActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPasswordManagerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
    }

    private fun setListener() {
        binding.changePassword.setOnClickListener {
            Intent(this, ChangePasswordActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
            }
        }
        binding.Back.setOnClickListener {
            finish()
        }
    }
}
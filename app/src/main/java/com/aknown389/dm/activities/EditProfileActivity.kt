package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aknown389.dm.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnChangeProfilePicture.setOnClickListener {
            Intent(this, ChangeProfilePictureActivity::class.java).also {
                startActivity(it)
            }
        }
        binding.ImageBtnEditProfileBack.setOnClickListener {
            finish()
        }
        binding.btnChangeCover.setOnClickListener {
            Intent(this, ChangeProfileCoverActivity::class.java).also {
                startActivity(it)
            }
        }
        binding.updateAboutBtn.setOnClickListener {
            Intent(this, UpdateAboutActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}
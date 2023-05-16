package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityProfilepageBinding
import com.bumptech.glide.Glide
import com.aknown389.dm.fragments.ProfileGalleryPost
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.dialogs.DialogProfileSettings
import com.aknown389.dm.fragments.MyActivityProfileView
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.pageView.profile.viewModels.ProfileViewModel
import com.aknown389.dm.pageView.profile.viewModels.ProfileViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfilepageAtivity : AppCompatActivity()
{

    private var binding: ActivityProfilepageBinding? = null
    private lateinit var viewModel: ProfileViewModel
    private lateinit var stagerredFragment: ProfileGalleryPost
    private lateinit var activityFragment: MyActivityProfileView
    private lateinit var dialog: DialogProfileSettings
    private lateinit var token:String
    private lateinit var manager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilepageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        //Load
        //init
        setValue()
        loadMe()
        loadGallery()
        setListener()

    }

    private fun setValue() {
        manager = DataManager(this)
        token = manager.getAccessToken().toString()
        val repository = Repository()
        val db = AppDataBase.getDatabase(this)
        val viewModelFactory = ProfileViewModelFactory(repository = repository, db = db, token = token)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
        stagerredFragment = ProfileGalleryPost()
        activityFragment = MyActivityProfileView()
        this.dialog = DialogProfileSettings()
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
    }

    private fun setListener() {
        binding?.editTextProfileSearch?.setOnClickListener {
            Intent(this, MainSearchActivity::class.java).also {
                startActivity(it)
            }
        }
        binding?.profilefollowing?.setOnClickListener {
            Intent(this, ActivityFollowing::class.java).also {
                startActivity(it)
            }
        }
        binding?.profilefollowers?.setOnClickListener {
            Intent(this, FriendRequests::class.java).also {
                startActivity(it)
            }
        }

        binding?.profilepostcount?.setOnClickListener {
            lifecycleScope.launch {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragmentProfile, activityFragment)
                    commit()
                }
                delay(100)
                binding?.profilepostcount?.setBackgroundResource(R.drawable.profile_nav_bg_blue)
                binding?.txtPostTag?.setBackgroundResource(R.drawable.profile_nav_bg)
            }
        }
        binding?.profileMenuButton?.setOnClickListener {
            dialog.show(supportFragmentManager, null)
        }

        binding?.txtActivityTag?.setOnClickListener {
            lifecycleScope.launch {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragmentProfile, activityFragment)
                    commit()
                }
                delay(100)
                binding?.txtActivityTag?.setBackgroundResource(R.drawable.profile_nav_bg_blue)
                binding?.txtPostTag?.setBackgroundResource(R.drawable.profile_nav_bg)
            }

        }
        binding?.txtPostTag?.setOnClickListener {
            lifecycleScope.launch {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragmentProfile, stagerredFragment)
                    commit()
                }
                delay(100)
                binding?.txtPostTag?.setBackgroundResource(R.drawable.profile_nav_bg_blue)
                binding?.txtActivityTag?.setBackgroundResource(R.drawable.profile_nav_bg)
            }
        }
        binding?.ProfileBackButton?.setOnClickListener {
            finish()
        }
        binding?.profilepagenestedswiper?.setOnRefreshListener {
            loadMe()
            loadGallery()
            binding?.profilepagenestedswiper?.isRefreshing = false
        }
    }

    private fun loadGallery() {
        lifecycleScope.launch {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragmentProfile, stagerredFragment)
                commit()
            }
            delay(100)
            binding?.txtPostTag?.setBackgroundResource(R.drawable.profile_nav_bg_blue)
            binding?.txtActivityTag?.setBackgroundResource(R.drawable.profile_nav_bg)
        }
    }

    private fun loadMe() {
        viewModel.me()
        viewModel.myDetailsResponse.observe(this) {
            Glide.with(this@ProfilepageAtivity)
                .load(it.profileimg)
                .override(300, 300)
                .error(R.mipmap.greybg)
                .placeholder(R.mipmap.greybg)
                .into(binding!!.ImageProfile)
            Glide.with(this@ProfilepageAtivity)
                .load(it.bgimg)
                .override(700, 700)
                .into(binding!!.profileBackgroundCover)
            binding?.profilefollowing?.text = it.following.toString()
            binding?.profilefollowers?.text = it.followers.toString()
            binding?.profileBio?.text = it.bio
            binding?.txtProfileName?.text = it.name
            binding?.profilepostcount?.text = it.post_lenght.toString()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
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

class ProfilePageActivity : AppCompatActivity()
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
        binding?.profileMenuButton?.setOnClickListener {
            dialog.show(supportFragmentManager, null)
        }
        binding?.ProfileBackButton?.setOnClickListener {
            finish()
        }
        binding?.profilepagenestedswiper?.setOnRefreshListener {
            loadMe()
            loadGallery()
            binding?.profilepagenestedswiper?.isRefreshing = false
        }

        viewModel.myDetailsResponse.observe(this) {
            if (it != null){
                loadProfilePicture(it.profileimg)
                loadBackgroundImage(it.bgimg)
                binding?.profilefollowing?.text = it.following.toString()
                binding?.profilefollowers?.text = it.followers.toString()
                binding?.profileBio?.text = it.bio
                binding?.txtProfileName?.text = it.name
                binding?.profilepostcount?.text = it.post_lenght.toString()
            }
        }
    }
    private fun loadBackgroundImage(image: String?){
        try {
            Glide.with(this@ProfilePageActivity)
                .load(image)
                .override(700, 700)
                .into(binding!!.profileBackgroundCover)
        }catch (e:java.lang.Exception){
            return
        }
    }
    private fun loadProfilePicture(image:String?){
        try {
            Glide.with(this@ProfilePageActivity)
                .load(image)
                .override(300, 300)
                .error(R.mipmap.greybg)
                .placeholder(R.mipmap.greybg)
                .into(binding!!.ImageProfile)
        }catch (e:Exception){
            return
        }
    }

    private fun loadGallery() {

    }

    private fun loadMe() {
        viewModel.me()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
package com.aknown389.dm.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aknown389.dm.databinding.ActivityPhotoViewBinding
import com.aknown389.dm.utils.DataManager

class PhotoViewActivity : AppCompatActivity() {
    private var binding: ActivityPhotoViewBinding? = null
    private lateinit var posId:String
    private lateinit var manager:DataManager
    private lateinit var token:String
    private var userAvatr:String? = null
    private var username:String? = null
    private var userFullName:String? = null
    private var NoOfLike:String? = null
    private var NoOfComment:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        this.binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setVal()
    }

    private fun setVal() {
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        this.posId = intent!!.getStringExtra("postId").toString()
        this.userAvatr = intent.getStringExtra("userAvatar").toString()
        this.username = intent.getStringExtra("username")
        this.userFullName = intent.getStringExtra("user_full_name")
        this.NoOfLike = intent.getStringExtra("like").toString()
        this.NoOfComment = intent.getStringExtra("noOfComment").toString()

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
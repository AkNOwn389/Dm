package com.aknown389.dm.activities

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityPhotoViewBinding
import com.aknown389.dm.databinding.DialogAlertDeleteBinding
import com.aknown389.dm.databinding.DialogPhotoviewMenuBinding
import com.aknown389.dm.databinding.DialogPhotoviewMenuMeBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.dialogs.DownloadingAlertDialog
import com.aknown389.dm.dialogs.LoadingAlertDialog
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.aknown389.dm.pageView.photoView.adapter.Adapter
import com.aknown389.dm.pageView.photoView.models.Parcel
import com.aknown389.dm.pageView.photoView.remote.LikeImagePost
import com.aknown389.dm.pageView.photoView.utilities.Setter.iconSetterBaseOnLike
import com.aknown389.dm.pageView.photoView.utilities.Setter.setDefaultReaction
import com.aknown389.dm.pageView.profile.dataClass.DeleteImageDataClass
import com.aknown389.dm.pageView.profile.viewModels.ProfileGalleryDisplayModelFactory
import com.aknown389.dm.pageView.profile.viewModels.ProfileGalleryDisplayViewModel
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.Reaction
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.StorageObject
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.NullPointerException

class PhotoViewActivity : AppCompatActivity() {
    private var isLoadingAlertDialog: Boolean = false
    private var binding: ActivityPhotoViewBinding? = null
    private lateinit var manager:DataManager
    private lateinit var adapter:Adapter
    private lateinit var token:String
    private lateinit var parcel: Parcel
    private lateinit var viewPager:ViewPager2
    private var images:ArrayList<ImageUrl> = ArrayList()
    private lateinit var viewModel:ProfileGalleryDisplayViewModel
    private lateinit var gson:Gson
    private lateinit var loadingAlertDialog:DownloadingAlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        this.binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setVal()
        setMainViewer()
        setListener()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun likePost(reactionType:String, reactType:String){
        val item = images[viewPager.currentItem]
        LikeImagePost(binding, currentItem = item, postType = "postImage" , token= token, reactionType = reactionType, context = this.applicationContext, reactType = reactType)
    }



    private fun setListener() {
        viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                val item = images[position]
                setUI(item)
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

    }

    private fun setUI(item: ImageUrl) {
        binding?.NoOfLikes?.text = item.noOfLike.toString()
        binding?.NoOfComments?.text = item.noOfComment.toString()
        iconSetterBaseOnLike(currentItem = item, holder = binding!!)
        binding?.likePost?.setEnableReactionTooltip(true)
        binding?.likePost?.setReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactionsGift)
        binding?.likePost?.setDisplayReactions(*com.aknown389.dm.reactionTesting.FbReactions.reactions)
        binding?.likePost?.apply {
            setOnReactionChangeListener(object : ReactImageButton.OnReactionChangeListener {
                override fun onReactionButtonClick(reaction: Reaction?) {
                    likePost(reaction?.reactText!!, "react")
                    Log.d("ReactImageButton", "$reaction")
                }
            })
            setOnButtonClickListener(object : ReactImageButton.OnButtonClickListener{
                override fun onBackToDefault(reaction: Reaction?) {
                    likePost("None", "unReact")
                }

                override fun onFirstReactionClick(reaction: Reaction?) {
                    likePost("Like", "react")
                }
            })
        }
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                // Night mode is not active on device
                try{
                    if (item.isLike!!) {
                        binding?.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                        setDefaultReaction(binding, item)
                    } else {
                        binding?.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                    }
                }catch (e:NullPointerException){
                    e.printStackTrace()
                }

            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active on device
                try {
                    if (item.isLike!!) {
                        binding?.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                        setDefaultReaction(holder = binding, item)
                    } else {
                        binding?.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                    }
                }catch (e:NullPointerException){
                    e.printStackTrace()
                }
            }
        }
        binding?.likePost?.setOnReactionDialogStateListener(object :
            ReactImageButton.OnReactionDialogStateListener {
            override fun onDialogOpened() {
                Log.d(PicturePostView.TAG, "onDialogOpened")
            }

            override fun onDialogDismiss() {
                Log.d(PicturePostView.TAG, "onDialogDismiss")
            }
        })
        binding?.backbtn?.setOnClickListener {
            finish()
        }
        binding?.menu?.setOnClickListener {
            val dialog = AlertDialog.Builder(this).create()
            val inflater = LayoutInflater.from(this)
            if (parcel.username == manager.getUserData()?.user){
                val view = DialogPhotoviewMenuMeBinding.inflate(inflater, binding?.root, false)
                dialog.setView(view.root)
                view.apply {
                    try{
                        download.setOnClickListener {
                            loadingAlertDialog.start()
                            lifecycleScope.launch(Dispatchers.IO){
                                val result = (StorageObject.downloadImage2(
                                    this@PhotoViewActivity,
                                    parcel.images?.get(viewPager.currentItem)?.imgW1000!!,
                                    parcel.username!!))
                                withContext(Dispatchers.Main){
                                    if (result){
                                        Toast.makeText(
                                            this@PhotoViewActivity,
                                            "Image save",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }else{
                                        Toast.makeText(
                                            this@PhotoViewActivity,
                                            "Download error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                dialog.dismiss()
                                loadingAlertDialog.dismiss()
                            }
                        }
                    }catch (e:Exception){
                        return@setOnClickListener
                    }
                    try {
                        delete.setOnClickListener {
                            dialog.dismiss()

                            val deleteDialog = AlertDialog.Builder(this@PhotoViewActivity).create()
                            val deleteInflater = LayoutInflater.from(this@PhotoViewActivity)
                            val deleteView = DialogAlertDeleteBinding.inflate(deleteInflater, binding!!.root, false)
                            deleteDialog.setView(deleteView.root)
                            if (deleteDialog.window != null){
                                deleteDialog.window?.setBackgroundDrawable(ColorDrawable(0))
                                deleteDialog.show()
                                deleteView.apply {
                                    yes.setOnClickListener {
                                        val body = parcel.images?.get(viewPager.currentItem)?.let { it1 -> it1.id?.let { it2 -> DeleteImageDataClass(imageId = it2) } }
                                        if (body != null){
                                            viewModel.deleteImage(body, context = this@PhotoViewActivity)
                                        }
                                        loadingAlertDialog.start()
                                        isLoadingAlertDialog = true
                                        deleteDialog.dismiss()
                                    }
                                    cansel.setOnClickListener {
                                        deleteDialog.dismiss()
                                    }
                                }
                            }
                            viewModel._normal_response.observe(this@PhotoViewActivity){
                                if (it.status){
                                    Toast.makeText(
                                        this@PhotoViewActivity,
                                        "Image delete",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                if (isLoadingAlertDialog){
                                    loadingAlertDialog.dismiss()
                                    isLoadingAlertDialog = false
                                }
                            }
                        }
                    }catch (e:Exception){
                        return@setOnClickListener
                    }
                    try {
                        report.setOnClickListener {  }
                    }catch (e:Exception){
                        return@setOnClickListener
                    }

                }
            }else{
                val view = DialogPhotoviewMenuBinding.inflate(inflater, binding?.root, false)
                dialog.setView(view.root)
                view.apply {
                    try{
                        download.setOnClickListener {
                            loadingAlertDialog.start()
                            lifecycleScope.launch(Dispatchers.IO){
                                val result = (StorageObject.downloadImage2(
                                    this@PhotoViewActivity,
                                    parcel.images?.get(viewPager.currentItem)?.imgW1000!!,
                                    parcel.username!!))
                                withContext(Dispatchers.Main){
                                    if (result){
                                        Toast.makeText(
                                            this@PhotoViewActivity,
                                            "Image save",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }else{
                                        Toast.makeText(
                                            this@PhotoViewActivity,
                                            "Download error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                dialog.dismiss()
                                loadingAlertDialog.dismiss()
                            }
                        }
                    }catch (e:Exception){
                        return@setOnClickListener
                    }
                    try {
                        report.setOnClickListener {  }
                    }catch (e:Exception){
                        return@setOnClickListener
                    }

                }
            }
            if (dialog.window != null){
                dialog.window?.setBackgroundDrawable(ColorDrawable(0))
                dialog.show()
            }
        }
        binding?.CommentBtn?.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("postId", item.id)
            bundle.putInt("noOfcomment", item.noOfComment ?:0)
            bundle.putInt("like", item.noOfLike ?:0)
            val reactions = Gson().toJson(item.reactions)
            bundle.putString("postType", "postImage")
            bundle.putString("reactions", reactions)
            val dialog = CommentDialog()
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
            dialog.show(supportFragmentManager, "comments")
        }
    }

    private fun setMainViewer() {
        Log.d("PhotoViewActivity", parcel.toString())
        for (i in parcel.images!!){
            if (i != null) {
                images.add(i)
                adapter.notifyItemInserted(images.size-1)
            }
        }
        //viewPager.currentItem = parcel.myPosition
        viewPager.setCurrentItem(parcel.myPosition, false)
        parcel.images!![parcel.myPosition]?.let { setUI(it) }
    }

    private fun setVal() {
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        val database: AppDataBase = AppDataBase.getDatabase(this)
        val repository = Repository()
        val viewModelFactory = ProfileGalleryDisplayModelFactory(repository, token, database)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileGalleryDisplayViewModel::class.java]
        this.gson = Gson()
        this.viewPager = binding?.viewPager!!
        this.adapter = Adapter(images)
        viewPager.adapter = adapter
        loadingAlertDialog = DownloadingAlertDialog(this)
        try {
            this.parcel = gson.fromJson(intent?.getStringExtra("parcel"), Parcel::class.java)
        }catch (e:NullPointerException){
            Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        binding = null
        viewPager.adapter = null
        super.onDestroy()
    }
}
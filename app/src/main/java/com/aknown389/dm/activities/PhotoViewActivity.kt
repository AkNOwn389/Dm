package com.aknown389.dm.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityPhotoViewBinding
import com.aknown389.dm.dialogs.CommentDialog
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.pageView.homeFeed.recyclerviewItem.PicturePostView
import com.aknown389.dm.pageView.photoView.adapter.Adapter
import com.aknown389.dm.pageView.photoView.models.Parcel
import com.aknown389.dm.pageView.photoView.remote.LikeImagePost
import com.aknown389.dm.pageView.photoView.utilities.Setter.iconSetterBaseOnLike
import com.aknown389.dm.pageView.photoView.utilities.Setter.setDefaultReaction
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.aknown389.dm.reactionTesting.Reaction
import com.aknown389.dm.utils.DataManager
import com.google.gson.Gson
import java.lang.NullPointerException

class PhotoViewActivity : AppCompatActivity() {
    private var binding: ActivityPhotoViewBinding? = null
    private lateinit var manager:DataManager
    private lateinit var adapter:Adapter
    private lateinit var token:String
    private lateinit var parcel: Parcel
    private lateinit var viewPager:ViewPager2
    private var images:ArrayList<ImageUrl> = ArrayList()
    private lateinit var gson:Gson
    override fun onCreate(savedInstanceState: Bundle?) {
        this.binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setVal()
        setMainViewer()
        setListener()
    }

    private fun likePost(reactionType:String, reactType:String){
        val item = images[viewPager.currentItem]
        LikeImagePost(binding, currentItem = item, postType = "posts" , token= token, reactionType = reactionType, context = this.applicationContext, reactType = reactType)
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
                if (item.isLike!!) {
                    binding?.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                    setDefaultReaction(binding, item)
                } else {
                    binding?.likePost?.defaultReaction =  (com.aknown389.dm.reactionTesting.FbReactions.defaultReactDay)
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active on device
                if (item.isLike!!) {
                    binding?.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
                    setDefaultReaction(holder = binding, item)
                } else {
                    binding?.likePost?.defaultReaction = (com.aknown389.dm.reactionTesting.FbReactions.defaultReactNight)
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
        this.gson = Gson()
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        this.viewPager = binding?.viewPager!!
        this.adapter = Adapter(images)
        viewPager.adapter = adapter
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
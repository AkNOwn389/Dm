package com.aknown389.dm.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityHomePostBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.aknown389.dm.pageView.createPost.HomePostAdapter
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.classes.ProgressRequestBody
import com.aknown389.dm.models.homepostmodels.uploadFileModel
import com.aknown389.dm.models.postmodel.UploadTextPostBody
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.Constants.Companion.CHANNEL_ID
import com.aknown389.dm.utils.Constants.Companion.REQUEST_CODE_IMAGE_PICKER
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.getFileName
import com.aknown389.dm.utils.snackbar
import com.aknown389.dm.pageView.createPost.viewModel.CreatePostViewModel
import com.aknown389.dm.pageView.createPost.viewModel.CreatePostModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class HomePostActivity : AppCompatActivity(), ProgressRequestBody.UploadCallbacks {
    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var binding: ActivityHomePostBinding? = null
    private lateinit var viewModel: CreatePostViewModel
    //private var selectedImage: Uri? = null
    private lateinit var token: String
    private lateinit var manager: DataManager
    private lateinit var username: String
    private lateinit var adapter: HomePostAdapter
    private lateinit var layoutManager:LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var fileToUploadList = ArrayList<uploadFileModel>()
    private var notification: NotificationCompat.Builder? = null
    private lateinit var compat: NotificationManagerCompat

    private fun setVal(){
        val repository = Repository()
        val viewModelFactory = CreatePostModelFactory(repository)
        this.viewModel = ViewModelProvider(this, viewModelFactory)[CreatePostViewModel::class.java]
        this.manager = DataManager(this)
        this.username = manager.getUserData()!!.user
        this.token = manager.getAccessToken().toString()
        this.layoutManager = LinearLayoutManager(this)
        this.recyclerView = binding?.creatPostImageRecyclerView!!
        recyclerView.layoutManager = layoutManager
        this.adapter = HomePostAdapter(fileToUploadList)
        this.recyclerView.adapter = adapter
        this.compat = NotificationManagerCompat.from(this)
        pickMediaListiner()
    }
    private fun pickMediaListiner(){
        // Registers a photo picker activity launcher in multi-select mode.
        // In this example, the app lets the user select up to 5 media files.
        pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
               for (file in uris){
                   val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                   contentResolver?.takePersistableUriPermission(file, flag)
                   val mimeType = contentResolver.getType(file)
                   if (mimeType?.startsWith("video") == true) {
                       val item = uploadFileModel(file = file, type = 2)
                       fileToUploadList.add(item)
                       this.adapter.notifyItemInserted(fileToUploadList.size-1)
                   }else if(mimeType?.startsWith("image") == true){
                       val item = uploadFileModel(file = file, type = 1)
                       fileToUploadList.add(item)
                       this.adapter.notifyItemInserted(fileToUploadList.size-1)
                   }
               }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePostBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setVal()
        loadAvatar(token, username)
        setPrivacy()
        binding?.createPostPrivacySpinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    binding!!.postPrivacy.text = parent.getItemAtPosition(position).toString()
                    manager.saveDefaultPostPrivacy(value = parent.getItemAtPosition(position).toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding!!.addvideo.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            //openVideoChooser()
        }
        binding!!.imagebtnpostBack.setOnClickListener {
            finish()
        }

        binding!!.addImage.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            //openImageChooser()
            addDetector()
        }
        binding!!.bntUpload.setOnClickListener {
            uploadImage()
        }
    }
    private fun addDetector(){
        lifecycleScope.launch {
            while (true){
                if (isActive){
                    delay(500)
                    if (fileToUploadList.size > 0){
                        binding?.addImage?.text = this@HomePostActivity.getString(R.string.add_more)
                    }else{
                        binding?.addImage?.text = (getString(R.string.add_image))
                    }
                }else{
                    return@launch
                }
            }
        }
    }
    private fun setPrivacy(){
        val position:Int = when (manager.getDefaultPostPrivacy()){
            "public"-> 0
            "private"-> 1
            "only-me"-> 2
            else -> 1
        }
        binding?.createPostPrivacySpinner?.setSelection(position)
        val privacy = binding?.createPostPrivacySpinner?.selectedItem.toString()
        binding?.postPrivacy?.text = privacy
    }


    private fun loadAvatar(token:String, username: String) {
        viewModel.getAvatar(token, username)
        viewModel.requestAvatarresponse.observe(this@HomePostActivity) {
            if (it.isSuccessful && it.body() != null){
                val res = it.body()!!
                if (res.status){
                    val option = RequestOptions().placeholder(R.mipmap.greybg)
                    Glide.with(this@HomePostActivity)
                        .load(res.avatar)
                        .apply(option)
                        .into(binding?.createPostUserAvatar!!)
                    binding?.createPostUsername?.text = res.name
                }
            }
        }
    }
    private fun openVideoChooser(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_OPEN_DOCUMENT).also{
            it.type = "image/*"
            val mineTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mineTypes)
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            it.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE_IMAGE_PICKER -> {
                    if (data!!.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                            val item = uploadFileModel(file = imageUri, type = 1)
                            fileToUploadList.add(item)
                            this.adapter.notifyItemInserted(fileToUploadList.size-1)
                        }
                    }else if (data.data != null){
                        val selectedImage:Uri = data.data!!
                        val item = uploadFileModel(file = selectedImage, type = 1)
                        fileToUploadList.add(item)
                        this.adapter.notifyItemInserted(fileToUploadList.size-1)
                    }
                }
                REQUEST_VIDEO_CAPTURE -> {
                    val selectedVideo:Uri? = data?.data
                    val item = uploadFileModel(file = selectedVideo!!, type = 2)
                    fileToUploadList.add(item)
                    this.adapter.notifyItemInserted(fileToUploadList.size-1)
                }
            }
        }
    }

    override fun onProgressUpdate(percentage: Int) {
        binding?.createPostprogressBar?.progress = percentage
        updateUploadMediaNotification(progress = percentage, isFinish = false)
    }

    private fun makeImageRequest(img:Uri?): File {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(img!!, "r", null)
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(img))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        return file
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun uploadWithImage(){
        val imageToUpload = ArrayList<MultipartBody.Part>()
        var caption: String = binding?.createPostText?.text.toString()
        if (caption.isBlank()) {
            caption = ""
        }
        val privacy: Char = when(binding?.createPostPrivacySpinner?.selectedItem.toString()){
            "public" -> 'P'
            "friends" -> 'F'
            "only-me" -> 'O'
            else -> 'P'
        }
        for (img in fileToUploadList){
            val image = makeImageRequest(img.file)
            val contentType = image.extension
            val contentName = when(img.type){
                1 -> "image"
                2 -> "video"
                else -> "image"
            }
            val fileBody = ProgressRequestBody(image, this@HomePostActivity, contentType)
            val multipartBody = MultipartBody.Part.createFormData(name = contentName, image.name, fileBody)
            imageToUpload.add(multipartBody)
        }
        binding?.createPostprogressBar?.isVisible  = true
        binding?.progressAnimation?.visibility = View.VISIBLE
        binding?.createPostprogressBar?.progress  = 0
        GlobalScope.launch(Dispatchers.Default) {
            createUploadMediaNotification()
            val response = try {
                PostInstance.api.uploadMultipleImages(
                    key = token,
                    privacy = privacy.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                    caption = caption.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                    images = imageToUpload)
            }catch (e:Exception){
                Log.d("Exception in create post", e.message.toString())
                return@launch
            }
            if (response.isSuccessful && response.body()!!.status){
                binding?.createPostprogressBar?.progress  = 100
                binding?.createPostprogressBar?.isVisible  = false
                binding?.progressAnimation?.visibility = View.GONE
                updateUploadMediaNotification(progress = 100, isFinish = true)
                finish()
            }else {
                binding?.createPostprogressBar?.isVisible = false
                binding?.progressAnimation?.isVisible = false
                updateUploadMediaNotification(progress = 100, isError = true)
                try {
                    binding?.creatPostRoot?.snackbar(response.body()!!.message)
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
        }
        finish()
    }
    private fun uploadTextPost(){
        val caption = binding?.createPostText?.text.toString()
        if (caption.isBlank()){
            binding?.creatPostRoot?.snackbar("please input text")
            return
        }
        val privacy: Char = when(binding?.createPostPrivacySpinner?.selectedItem.toString()){
            "public" -> 'P'
            "friends" -> 'F'
            "only-me" -> 'O'
            else -> 'P'
        }
        val body = UploadTextPostBody(caption, privacy = privacy.toString())
        binding?.progressAnimation?.isVisible = true
        viewModel.uploadTextPost(token, body)
        createUploadMediaNotification()
        viewModel.uploadTextresponse.observe(this){ response ->
            if (response.isSuccessful){
                val res = response.body()!!
                if (res.status){
                    Toast.makeText(this@HomePostActivity, "Post uploaded", Toast.LENGTH_SHORT).show()
                    updateUploadMediaNotification(progress = 100, isFinish = true)
                    finish()
                }else{
                    binding?.createPostprogressBar?.isVisible = false
                    binding?.progressAnimation?.isVisible = false
                    updateUploadMediaNotification(progress = 100, isError = true)
                    try {
                        binding?.creatPostRoot?.snackbar(res.message)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                return@observe
            }
            binding?.progressAnimation?.isVisible = false
            return@observe
        }
        viewModel.uploadPostresponse.value = null
    }
    private fun createUploadMediaNotification() {
        this.notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_dm_launcher_round)
            setContentTitle("Uploading your post.")
            setProgress(100, 0, false)
            setAllowSystemGeneratedContextualActions(true)
            setOngoing(true)
            setOnlyAlertOnce(true)
            priority = NotificationCompat.PRIORITY_HIGH
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            compat.notify(notificationId.toInt(), notification!!.build())
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "no notification permission")
        }
    }
    private fun updateUploadMediaNotification(progress: Int, isFinish:Boolean=false, isError:Boolean=false) {
        if (progress != 100) {
            // Progress is lower than 100%, updating progress
            notification!!.setContentText("${progress}%")
            notification!!.setProgress(100, progress, false)
        }
        if (isFinish){
            // Progress is 100%, changing notification content
            notification!!.setContentText("Uploading finished.")
                .setProgress(0, 0, false)
                .setOnlyAlertOnce(false)
                .setOngoing(false)
        }
        if (isError){
            notification!!.setContentText("Uploading failed.")
                .setProgress(0, 0, false)
                .setOnlyAlertOnce(false)
                .setOngoing(false)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            compat.notify(notificationId.toInt(), notification!!.build())
        } else {
            return
        }
        
    }
    override fun uploadImage() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (fileToUploadList.isNotEmpty()){
                uploadWithImage()
            }else{
                uploadTextPost()
            }
        }
    }

    override fun onPause() {
        adapter.onPlayerPause()
        super.onPause()
    }
    override fun onDestroy() {
        adapter.releasPlayer()
        binding?.creatPostImageRecyclerView?.adapter = null
        binding = null
        super.onDestroy()
    }
    companion object{
        const val REQUEST_VIDEO_CAPTURE = 34691
        const val notificationId = 26264842746557
        const val CHANNEL_NAME = "DM"
        private val TAG = "HomePost"
    }
}
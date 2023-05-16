package com.aknown389.dm.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityChangeProfilePictureBinding
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.classes.ProgressRequestBody
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.Constants.Companion.CHANNEL_ID
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.getFileName
import com.aknown389.dm.utils.snackbar
import com.aknown389.dm.pageView.changeProfile.viewModel.ChangeProfilePictureViewModel
import com.aknown389.dm.pageView.changeProfile.viewModel.ChangeProfilePictureFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ChangeProfilePictureActivity : AppCompatActivity(), ProgressRequestBody.UploadCallbacks {
    private var binding: ActivityChangeProfilePictureBinding? = null
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var viewModel: ChangeProfilePictureViewModel
    private var notification: NotificationCompat.Builder? = null
    private lateinit var compat: NotificationManagerCompat
    private var selectedImage: Uri? = null
    private lateinit var token: String
    private var isUploading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeProfilePictureBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setValues()
        setListener()
    }


    private fun setListener() {
        binding?.pictureForChangeProfile?.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding?.btnUploadUpdate?.setOnClickListener {
            if (!isUploading){
                uploadImage()
            }else{
                Toast.makeText(this, "Uploading", Toast.LENGTH_SHORT).show()
            }
        }
        binding?.changeprofilepicturebackbtn?.setOnClickListener {
            finish()
        }
        pickPhotoListener()
    }

    private fun setValues() {
        val repository = Repository()
        val viewModelFactory = ChangeProfilePictureFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ChangeProfilePictureViewModel::class.java]
        val manager = DataManager(this)
        token = manager.getAccessToken().toString()
        this.compat = NotificationManagerCompat.from(this)
    }

    private fun pickPhotoListener(){
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver?.takePersistableUriPermission(uri, flag)
                selectedImage = uri
                binding?.pictureForChangeProfile?.setImageURI(selectedImage)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    override fun uploadImage() {
        if (selectedImage != null) {
            isUploading=true
            val parcelFileDescriptor = contentResolver.openFileDescriptor(
                selectedImage!!, "r", null
            ) ?: return

            var caption: String = binding?.changeProfileCaption?.text.toString()
            if (caption.isBlank()) {
                caption = ""
            }

            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            binding?.changeProfilepictureProgress?.isVisible = true
            binding?.changeProfilepictureProgress?.progress = 0

            val fileBody = ProgressRequestBody(file, this@ChangeProfilePictureActivity, "image")
            val image = MultipartBody.Part.createFormData(name = "image", file.name, fileBody)
            val body = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), caption)
            createUploadMediaNotification()
            GlobalScope.launch(Dispatchers.Main) {
                val response = try {
                    RetrofitInstance.api.updateProfilePicture(token=token, image, body)
                }catch (e:Exception){
                    e.printStackTrace()
                    return@launch
                }
                if (response.isSuccessful){
                    val responseData = response.body()!!
                    if (responseData.status) {
                        binding?.changeProfilepictureProgress?.progress = 100
                        binding?.changeProfilepictureProgress?.isVisible = false
                        updateUploadMediaNotification(progress = 100, isFinish = true)
                        finish()
                    } else {
                        updateUploadMediaNotification(progress = 100, isError = true)
                        binding?.changeProfilepictureProgress?.isVisible = false
                        binding?.changeProfileRoot?.snackbar(responseData.message)
                        isUploading = false
                    }
                }else{
                    isUploading = false
                }
            }
            finish()
        }else{
            binding?.changeProfileRoot?.snackbar("Pick image first")
        }
    }
    override fun onProgressUpdate(percentage: Int) {
        binding?.changeProfilepictureProgress?.progress = percentage
        updateUploadMediaNotification(progress = percentage)
    }
    private fun createUploadMediaNotification() {
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_dm_launcher_round)
            .setContentTitle("Uploading image")
            //.setContentText("Uploading in Progress")
            .setProgress(100, 0, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        compat.notify(notificationId, notification!!.build())
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

        // Update the notification
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        compat.notify(notificationId, notification!!.build())
    }
    companion object{
        const val notificationId = 26
        const val CHANNEL_NAME = "DM"
    }
}
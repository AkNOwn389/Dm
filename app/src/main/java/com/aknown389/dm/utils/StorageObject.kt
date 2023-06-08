package com.aknown389.dm.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.NetworkOnMainThreadException
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date

object StorageObject {
    @SuppressLint("SimpleDateFormat")
    suspend fun downloadImage(context: Context, url: String):Boolean {
        val direct = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), "DirectMessage")
        if (!direct.exists()) {
            direct.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "Image_$timeStamp.jpg"
        val file = File(direct, fileName)
        if (file.exists()) file.delete()
        try {
            val fos = FileOutputStream(file)
            val bitmap = BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            return true
        }catch (e: NetworkOnMainThreadException){
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Internet Error", Toast.LENGTH_SHORT).show()
            }
            return false
        } catch (e: FileNotFoundException){
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Link corrupted", Toast.LENGTH_SHORT).show()
            }
            Log.d("StorageObject", e.stackTraceToString())
            e.printStackTrace()
            return false
        }catch (e: Exception) {
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
            Log.d("StorageObject", e.stackTraceToString())
            e.printStackTrace()
            return false
        }
    }

    @SuppressLint("SimpleDateFormat")
    suspend fun downloadImage2(context: Context, url: String, username:String):Boolean{
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "Image_${username}_$timeStamp.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/DirectMessage")
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try {
            uri?.let {
                val fos = context.contentResolver.openOutputStream(it)
                fos?.let { outputStream ->
                    val bitmap = BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
                    if (bitmap.rowBytes > 1){
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        return true
                    }else{
                        return false
                    }
                }
            }
            return true
        }catch (e:Exception){
            uri?.let { context.contentResolver.delete(it, null, null)}
            e.printStackTrace()
            Log.d("Exception", e.stackTraceToString())
            return false
        }
    }
}
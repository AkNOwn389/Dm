package com.aknown389.dm.pageView.commentView.items

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aknown389.dm.R
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.api.retroInstance.CommentInstance
import com.aknown389.dm.classes.ProgressRequestBody
import com.aknown389.dm.pageView.commentView.dialogs.DialogOption
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.utility.Adapter
import com.aknown389.dm.pageView.commentView.utility.CommentViewHolder
import com.aknown389.dm.pageView.commentView.mapper.toCommentData
import com.aknown389.dm.pageView.commentView.utility.Setter
import com.aknown389.dm.utils.getFileName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ImageComment(
    private val holder: CommentViewHolder,
    private var currentItem: Data,
    private val context: Context,
    private val comments: ArrayList<Data>,
    private val parent: ViewGroup,
    private val adapterContext: Adapter,
    private val token: String,
    private val postType: String?
): ProgressRequestBody.UploadCallbacks {
    override fun onProgressUpdate(percentage: Int) {
        holder.progress?.progress  = percentage
    }

    override fun uploadImage() {

    }
    init {
        if (currentItem.sendKoBa == true){
            sendItem(currentItem, holder)
            loadSendingUi(currentItem, holder)
        }else{
            holder.progress?.visibility = View.GONE
            holder.sending?.visibility = View.GONE
            holder.myData = currentItem
            loadCommentUI(holder)
            setListener(holder)
        }
    }

    private fun loadSendingUi(currentItem: Data, holder: CommentViewHolder) {
        holder.commentBody?.text = currentItem.comments
        holder.commentUsername?.text = context.getString(R.string.me)
        holder.commentLikeLenght?.visibility = View.GONE
        Glide.with(context)
            .load(currentItem.imageToUpload)
            .placeholder(R.mipmap.greybg)
            .error(R.mipmap.white_background)
            .into(holder.imageContainer!!)
    }

    @SuppressLint("Recycle")
    private fun makeImageRequest(img: Uri?): File {
        val parcelFileDescriptor = context.contentResolver?.openFileDescriptor(img!!, "r", null)
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val file = context.contentResolver?.getFileName(img!!)
            ?.let { File(context.cacheDir, it) }
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        return file!!
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun sendItem(currentItem: Data, holder: CommentViewHolder) {
        val file = makeImageRequest(currentItem.imageToUpload)
        val fileBody = ProgressRequestBody(file, this@ImageComment)
        val multipartBody = MultipartBody.Part.createFormData(name = "image", file.name, fileBody)
        holder.progress?.visibility = View.VISIBLE
        holder.sending?.visibility = View.VISIBLE
        holder.progress?.progress  = 0
        GlobalScope.launch(Dispatchers.Main) {
            holder.sendingProgress?.visibility = View.VISIBLE
            val response = try {
                CommentInstance(context).api.sendCommentFile(
                type = "image",
                postId = currentItem.postId!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                comment = currentItem.comments!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                file = multipartBody
            )}catch (e:Exception){
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("CommentViewActivity", response.toString())
            if (response.isSuccessful){
                val pos = comments.indexOf(currentItem)
                holder.progress?.progress = 100
                holder.progress?.visibility = View.GONE
                holder.sendingProgress?.visibility = View.GONE
                holder.sending?.visibility = View.VISIBLE
                holder.sending?.setImageResource(R.drawable.check_alt_flat_icon)
                this@ImageComment.currentItem = response.body()!!.data.toCommentData()
                comments[pos] = response.body()!!.data.toCommentData()
                init2(this@ImageComment.currentItem, holder)
            }else{
                holder.sending?.setImageResource(R.drawable.error)
                holder.sendingProgress?.visibility = View.GONE
                holder.sending?.visibility = View.VISIBLE
            }
        }
    }

    private fun init2(currentItem: Data, holder: CommentViewHolder){
        holder.myData = currentItem
        loadCommentUI(holder)
        setListener(holder)
    }

    private fun loadCommentUI(holder: CommentViewHolder) {
        Setter.iconSetterBaseOnLike(holder, currentItem, context)
        holder.commentTime?.text = holder.myData?.created
        holder.commentBody?.text = holder.myData?.comments
        try {
            Glide.with(context)
                .load(currentItem.image?.get(0)!!.imgW250)
                .placeholder(R.mipmap.greybg)
                .error(R.mipmap.white_background)
                .into(holder.imageContainer!!)
            holder.imageContainer?.visibility = View.VISIBLE
        }catch (e:Exception){
            e.printStackTrace()
            holder.imageContainer?.visibility = View.GONE
        }

        try {
            if (holder.myData?.me == true){
                holder.commentUsername?.text = context.getString(R.string.me)
            }else{
                holder.commentUsername?.text = holder.myData?.userFullName
                Glide.with(context)
                    .load(holder.myData?.avatar)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.mipmap.greybg)
                    .into(holder.commentUserImage!!)
            }
        }catch (e:Exception){
            Log.d("CommentViewActivity", e.stackTrace.toString())
        }
    }
    private fun setListener(holder: CommentViewHolder) {
        holder.commentUsername?.setOnClickListener {
            if (holder.myData?.me == true){
                return@setOnClickListener
            }
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", holder.myData?.user)
                it.startActivity(intent)
            }
        }
        holder.commentUserImage?.setOnClickListener {
            if (holder.myData?.me == true){
                return@setOnClickListener
            }
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", holder.myData?.user)
                it.startActivity(intent)
            }
        }
        holder.imageContainer?.setOnLongClickListener {
            if (postType != null) {
                DialogOption(
                    holder = holder,
                    context = context,
                    adapter = adapterContext,
                    comments = comments,
                    parent = parent,
                    myitem = holder.myData!!,
                    postType = postType
                )
            }
            true
        }
        holder.commentBody?.setOnLongClickListener {
            if (postType != null) {
                DialogOption(
                    holder = holder,
                    context = context,
                    adapter = adapterContext,
                    comments = comments,
                    parent = parent,
                    myitem = holder.myData!!,
                    postType = postType
                )
            }
            true
        }
    }
}
package com.aknown389.dm.dialogs

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R
import com.aknown389.dm.activities.HomePostActivity
import com.aknown389.dm.api.retroInstance.CommentInstance
import com.aknown389.dm.databinding.DialogCommentViewBinding
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.chating.TypingData
import com.aknown389.dm.pageView.commentView.utility.Adapter
import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.pageView.commentView.models.CommentDeletedModels
import com.aknown389.dm.pageView.commentView.models.CommentWebsocketInterface
import com.aknown389.dm.pageView.commentView.models.Handshake
import com.aknown389.dm.pageView.commentView.models.Info
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.models.NewChangeReaction
import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.Constants.Companion.REQUEST_IMAGE_CAPTURE
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.NetInterceptor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.IOException
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class CommentDialog : BottomSheetDialogFragment(){
    private var postType: String = "posts"
    private var TAG = "CommentViewActivity"
    private var binding: DialogCommentViewBinding? = null
    private lateinit var adapter: Adapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var postId:String
    private lateinit var manager:DataManager
    private lateinit var token:String
    private lateinit var noOflike:String
    private lateinit var noOfComment:String
    private lateinit var gson: Gson
    private lateinit var parent:ViewGroup
    private var commentInstance: CommentInstance? = null
    private var commentReactions:PostReactions? = null
    private var hasMorePage = true
    private var isLoading = false
    private var isClose = true
    private var isDetach = false
    private var info: Info? = null
    private var page by Delegates.notNull<Int>()
    private var websocket:WebSocket? = null
    private var imageTosend:Uri? = null
    private var videoTosend:Uri? = null
    private var handler:Handler? = null
    private val hideAgain = Runnable {
        hideOption()
    }
    private val stopPopUpRunnable = Runnable {
        val animation =
            AnimationUtils.loadAnimation(requireContext(), com.udevel.widgetlab.R.anim.abc_shrink_fade_out_from_bottom)
        binding?.popUpMessageCard?.startAnimation(animation)
        binding?.popUpMessageCard?.visibility = View.GONE
    }

    private lateinit var client: OkHttpClient
    private var commentlist = ArrayList<Data>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (container != null) {
            this.parent = container
        }
        binding = DialogCommentViewBinding.inflate(layoutInflater, container, false)
        binding?.commentViewRecyclerview?.isNestedScrollingEnabled = false
        return binding?.root!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            bottomSheet.layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        }
        return bottomSheetDialog
    }

    private fun init() {
        setVal()
        setUI()
        setListener()
        loadComments()
    }

    private fun loadComments() {
        this.page = 1
        this.hasMorePage = true
        this.isLoading = true
        lifecycleScope.launch(Dispatchers.Main) {
            binding?.commentViewRecyclerview?.isVisible = false
            binding?.commenviewShimmerFrame?.isVisible = true
            binding?.commenviewShimmerFrame?.startShimmer()
            val response = try {
                commentInstance?.api?.getComment(postId = postId, page = page)
            }catch (e:Exception){
                e.printStackTrace()
                binding?.commenviewShimmerFrame?.stopShimmer()
                binding?.commenviewShimmerFrame?.isVisible = false
                binding?.commentViewRecyclerview?.isVisible = true
                this@CommentDialog.isLoading = false
                return@launch
            }
            if (response?.isSuccessful!!){
                if (!response.body()!!.hasMorePage){
                    this@CommentDialog.hasMorePage = false
                }
                for (i in response.body()!!.data){
                    commentlist.add(i)
                    adapter.notifyItemInserted(commentlist.size-1)
                }
            }
            withContext(Dispatchers.Main){
                binding?.commenviewShimmerFrame?.stopShimmer()
                binding?.commenviewShimmerFrame?.isVisible = false
                binding?.commentViewRecyclerview?.isVisible = true
                this@CommentDialog.isLoading = false
                connectToSocket()
            }
        }
    }
    private fun connectToSocket(){
        lifecycleScope.launch {
            this@CommentDialog.client = OkHttpClient.Builder()
                .addInterceptor(NetInterceptor())
                .readTimeout(0, TimeUnit.SECONDS)
                .build()
            val request: Request = Request.Builder()
                .url("${Constants.WEBSOCKET_BASE_URL}/ws/commentRoom/${postId}")
                .addHeader("Authorization", token)
                .build()
            try {
                this@CommentDialog.websocket = client.newWebSocket(request, CommentListener())
                this@CommentDialog.isClose = false
            }catch (e:Exception){
                this@CommentDialog.isClose = true
                e.printStackTrace()
            }
        }
    }

    private fun setListener() {
        val stopTypingRunnable = Runnable {
            // Send a message to the server via WebSocket
            websocket?.send("{\"type\":6}")
        }

        binding?.commentViewEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                hideOption()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                // Reset the timer
                websocket?.send("{\"type\":5}")
                handler?.removeCallbacks(stopTypingRunnable)
                handler?.postDelayed(stopTypingRunnable, 2000)
            }

        })
        binding?.commentViewEditText?.setOnEditorActionListener { v, actionId, event ->
            when(actionId){
                EditorInfo.IME_ACTION_DONE -> {
                    Log.d(TAG, "Key press called")
                    if (!binding?.commentViewEditText!!.text.isNullOrEmpty()){
                        sendComment()
                    }
                }
            }
            hideOption()
            true
        }
        binding?.commentviewSwiper?.setOnRefreshListener {
            if (!isLoading && hasMorePage){
                updateComment()
            }else{
                binding?.commentviewSwiper?.isRefreshing = false
            }
        }
        binding?.commentViewEditText?.setOnClickListener {
            binding?.commentViewRecyclerview!!.scrollToPosition(0)
        }
        binding?.popUpMessage?.setOnClickListener {
            layoutManager.scrollToPosition(0)
            binding?.popUpMessageCard?.isVisible = false
        }
        binding?.commentViewRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (layoutManager.findLastVisibleItemPosition() <= 7){
                    try {
                        binding?.popUpMessageCard?.visibility = View.GONE
                    }catch (e:Exception){
                        Log.d("Exception on commenview", e.message.toString())
                    }
                }

                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        binding?.addFileBtn?.setOnClickListener {
            hidesShowOption()
        }
        binding?.ImageviewAddPhotosUserChat?.setOnClickListener {
            //openImageChooser()
            takePicture()
        }
        binding?.removeFile?.setOnClickListener {
            this.imageTosend = null
            this.videoTosend = null
            binding?.imageContainer?.visibility = View.GONE
            binding?.removeFile?.visibility = View.GONE
        }
        binding?.chatCapturePicture?.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    private fun takePicture(){
        // Registers a photo picker activity launcher in single-select mode.

            // Include only one of the following calls to launch(), depending on the types
            // of media that you want to let the user choose from.

            // Launch the photo picker and let the user choose images and videos.
       // pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))

            // Launch the photo picker and let the user choose only images.
        //pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            // Launch the photo picker and let the user choose only videos.
        //pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))

            // Launch the photo picker and let the user choose only images/videos of a
        // specific MIME type, such as GIFs.
        val mimeType = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType)))
    }

    private fun hideOption(){
        lifecycleScope.launch {
            if (binding?.cardViewSendImage?.isVisible == true || binding?.capturePictureOnChat?.isVisible == true) {
                try {
                    val animation =
                        AnimationUtils.loadAnimation(requireContext(), R.anim.exit_to_left)
                    binding?.capturePictureOnChat?.isVisible = false
                    binding?.capturePictureOnChat?.startAnimation(animation)
                    binding?.cardViewSendImage?.isVisible = false
                    binding?.cardViewSendImage?.startAnimation(animation)
                    binding?.addFileBtn?.setImageResource(R.drawable.baseline_arrow_back_ios_new_24)
                }catch (e:IllegalStateException){
                    e.printStackTrace()
                }
                editTextAnimOut()
            }
        }
    }

    private fun hidesShowOption(){
        lifecycleScope.launch {
            if (binding?.cardViewSendImage?.isVisible == true || binding?.capturePictureOnChat?.isVisible == true) {
                try {
                    val animation =
                        AnimationUtils.loadAnimation(requireContext(), R.anim.exit_to_left)
                    binding?.capturePictureOnChat?.isVisible = false
                    binding?.capturePictureOnChat?.startAnimation(animation)
                    binding?.cardViewSendImage?.isVisible = false
                    binding?.cardViewSendImage?.startAnimation(animation)
                    binding?.addFileBtn?.setImageResource(R.drawable.baseline_arrow_back_ios_new_24)
                }catch (e:IllegalStateException){
                    e.printStackTrace()
                }
                editTextAnimOut()
            } else if (!binding?.cardViewSendImage?.isVisible!! || !binding?.capturePictureOnChat?.isVisible!!) {
                try {
                    val animation =
                        AnimationUtils.loadAnimation(requireContext(), R.anim.enter_from_left)
                    binding?.capturePictureOnChat?.isVisible = true
                    binding?.capturePictureOnChat?.startAnimation(animation)
                    binding?.cardViewSendImage?.isVisible = true
                    binding?.cardViewSendImage?.startAnimation(animation)
                    binding?.addFileBtn?.setImageResource(R.drawable.arrow_forward_ios)
                }catch (e:IllegalStateException){
                    e.printStackTrace()
                }
                editTextAnimIn()
            }
        }
    }
    private fun editTextAnimIn(){
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val seventyPercent:Double = 0.82 * width
        val tertyPercent:Double = 0.50 * width


        val editText = binding?.commentViewEditText
        val animator = ValueAnimator.ofInt(seventyPercent.toInt(), tertyPercent.toInt())
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = editText?.layoutParams
            layoutParams?.width = value
            editText?.layoutParams = layoutParams
        }
        animator.duration = 100
        animator.start()
        handler?.removeCallbacks(hideAgain)
        handler?.postDelayed(hideAgain, 7000)
    }
    private fun editTextAnimOut(){
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val seventypercent:Double = 0.82 * width
        val tertiPercent:Double = 0.50 * width


        val editText = binding?.commentViewEditText
        val animator = ValueAnimator.ofInt(tertiPercent.toInt(), seventypercent.toInt())
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = editText?.layoutParams
            layoutParams?.width = value
            editText?.layoutParams = layoutParams
        }
        animator.duration = 100
        animator.start()
    }

    private fun popUpNewChat(){
        val animation =
            AnimationUtils.loadAnimation(requireContext(), com.udevel.widgetlab.R.anim.abc_popup_enter)
        binding?.popUpMessageCard?.visibility = View.VISIBLE
        binding?.popUpMessageCard?.startAnimation(animation)
        return
    }

    private fun updateComment() {
        this.page += 1
        this.isLoading = true
        lifecycleScope.launch {
            val response = try {
                commentInstance?.api?.getComment(postId = postId, page = page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response?.isSuccessful!!){
                if (!response.body()!!.hasMorePage){
                    this@CommentDialog.hasMorePage = false
                }
                for (i in response.body()!!.data){
                    this@CommentDialog.commentlist.add(i)
                    this@CommentDialog.adapter.notifyItemInserted(commentlist.size-1)
                }
            }
            this@CommentDialog.isLoading = false
            binding?.commentviewSwiper?.isRefreshing = false
        }
    }

    private fun sendComment() {
        val mycomment = binding?.commentViewEditText?.text
        if (mycomment.isNullOrEmpty() && imageTosend == null && videoTosend == null){
            return
        }
        if (this.isClose){
            return
        }
        lifecycleScope.launch {
            val data = if (imageTosend != null){
                Data(
                    comments = mycomment.toString(),
                    me = true,
                    user = manager.getUserData()!!.user,
                    userFullName = info!!.name,
                    avatar = info!!.profileimg,
                    postId = postId,
                    noOfLike = 0,
                    isLike = false,
                    type = 2,
                    imageToUpload = imageTosend,
                    sendKoBa = true,
                    reactions = PostReactions(0,0,0,0,0,0)
                )
            }else if (videoTosend != null){
                Data(
                    comments = mycomment.toString(),
                    me = true,
                    user = manager.getUserData()!!.user,
                    userFullName = info!!.name,
                    avatar = info!!.profileimg,
                    postId = postId,
                    noOfLike = 0,
                    isLike = false,
                    type = 3,
                    videoToUpload = imageTosend,
                    sendKoBa = true,
                    reactions = PostReactions(0,0,0,0,0,0)
                )
            }else{
                Data(
                    comments = mycomment.toString(),
                    me = true,
                    user = manager.getUserData()!!.user,
                    userFullName = info!!.name,
                    avatar = info!!.profileimg,
                    postId = postId,
                    noOfLike = 0,
                    isLike = false,
                    type = 1,
                    sendKoBa = true,
                    reactions = PostReactions(0,0,0,0,0,0)
                )
            }
            binding?.imageContainer?.visibility = View.GONE
            binding?.removeFile?.visibility = View.GONE
            imageTosend = null
            videoTosend = null
            binding?.commentViewEditText?.text = null
            this@CommentDialog.commentlist.add(0, data)
            this@CommentDialog.adapter.notifyItemInserted(0)
            this@CommentDialog.binding?.commentViewRecyclerview!!.scrollToPosition(0)
        }
    }
    private fun openImageChooser() {
        Intent(Intent.ACTION_OPEN_DOCUMENT).also{
            it.type = "image/*"
            val mineTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mineTypes)
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            it.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(it, Constants.REQUEST_CODE_IMAGE_PICKER)
        }
    }
    private fun openVideoChooser(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, HomePostActivity.REQUEST_VIDEO_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                Constants.REQUEST_CODE_IMAGE_PICKER -> {
                    if (data?.data != null){
                        this.imageTosend = data.data!!
                        disPlayImage()
                    }
                }
                HomePostActivity.REQUEST_VIDEO_CAPTURE -> {
                    this.videoTosend = data?.data
                    disPlayVideo()
                }
                Constants.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    saveImageToGallery(imageBitmap)
                    disPlayImage()
                }
            }
        }
    }
    private fun saveImageToGallery(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "image.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            uri?.let { uri ->
                val stream = resolver.openOutputStream(uri)
                stream?.let { stream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                        throw IOException("Failed to save bitmap.")
                    }
                } ?: throw IOException("Failed to get output stream.")
            } ?: throw IOException("Failed to create new MediaStore record.")
        } catch (e: IOException) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
            throw e
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            uri.let { uri ->
                resolver.update(uri, contentValues, null, null)
            }
        }
        imageTosend = uri
    }
    private fun disPlayVideo() {
        binding?.removeFile?.visibility = View.VISIBLE
        binding?.imageContainer?.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(videoTosend)
            .placeholder(R.mipmap.greybg)
            .error(R.mipmap.white_background)
            .into(binding?.imageContainer!!)
    }

    private fun disPlayImage() {
        binding?.removeFile?.visibility = View.VISIBLE
        binding?.imageContainer?.visibility = View.VISIBLE
        if (imageTosend != null){
            Glide.with(requireContext())
                .load(imageTosend)
                .placeholder(R.mipmap.greybg)
                .override(700, 700)
                .error(R.mipmap.white_background)
                .into(binding?.imageContainer!!)
        }
    }

    private fun setUI() {
        if (arguments?.getInt("like", 0) == 0){
            binding?.commentviewLikeTextView?.isVisible = false
        }else{
            binding?.commentviewLikeTextView?.text = noOflike
        }
    }

    private fun setVal() {
        editTextAnimOut()
        this.layoutManager = LinearLayoutManager(requireContext())
        this.postId = arguments?.getString("postId").toString()
        this.noOflike = arguments?.getInt("like", 0).toString()
        this.noOfComment = arguments?.getInt("noOfcomment", 0).toString()
        this.postType = arguments?.getString("postType", null).toString()
        this.gson = Gson()
        this.manager = DataManager(requireContext())
        this.token = manager.getAccessToken().toString()
        this.adapter = Adapter()
        this.handler = Handler(Looper.getMainLooper())
        this.commentInstance = CommentInstance(requireContext().applicationContext)
        commentReactions = gson.fromJson(arguments?.getString("reactions"), PostReactions::class.java)
        adapter.setData(commentlist)
        adapter.setPostType(postType)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = false
        layoutManager.isSmoothScrollbarEnabled = true
        binding?.commentViewRecyclerview!!.adapter = adapter
        binding?.commentViewRecyclerview!!.layoutManager = layoutManager
        if (postType.isBlank()){
            dismiss()
        }
        setTopReactions()
        pickPhotoListener()
    }

    private fun pickPhotoListener(){
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(uri, flag)
                imageTosend = uri
                disPlayImage()
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    private fun setTopReactions() {
        val animation = AnimationUtils.loadAnimation(requireContext(), com.udevel.widgetlab.R.anim.abc_popup_enter)
        if (commentReactions?.Love == 0) {
            binding?.icLove?.visibility = View.GONE
        } else {
            binding?.icLove?.visibility = View.VISIBLE
            binding?.icLove?.startAnimation(animation)
        }
        if (commentReactions?.Like == 0) {
            binding?.icLike?.visibility = View.GONE
        } else {
            binding?.icLike?.visibility = View.VISIBLE
            binding?.icLike?.startAnimation(animation)
        }
        if (commentReactions?.Happy == 0) {
            binding?.icHappy?.visibility = View.GONE
        } else {
            binding?.icHappy?.visibility = View.VISIBLE
            binding?.icHappy?.startAnimation(animation)
        }
        if (commentReactions?.Wow == 0) {
            binding?.icWow?.visibility = View.GONE
        } else {
            binding?.icWow?.visibility = View.VISIBLE
            binding?.icWow?.startAnimation(animation)
        }
        if (commentReactions?.Sad == 0) {
            binding?.icSad?.visibility = View.GONE
        } else {
            binding?.icSad?.visibility = View.VISIBLE
            binding?.icSad?.startAnimation(animation)
        }
        if (commentReactions?.Angry == 0) {
            binding?.icAngry?.visibility = View.GONE
        } else {
            binding?.icAngry?.visibility = View.VISIBLE
            binding?.icAngry?.startAnimation(animation)
        }
        if (commentReactions?.Like == 0
            && commentReactions?.Happy == 0
            && commentReactions?.Love == 0
            && commentReactions?.Wow == 0
            && commentReactions?.Sad == 0
            && commentReactions?.Angry == 0
        ) {
            binding?.commentviewLikeTextView?.visibility = View.GONE
        } else {
            binding?.commentviewLikeTextView?.visibility = View.VISIBLE
            val num = commentReactions
            val noOfAllLike = num?.Like!! + num.Love!! + num.Happy!! + num.Wow!! + num.Sad!! + num.Angry!!
            val noToDisplay = if (noOfAllLike > 1000){
                ((noOfAllLike / 1000).toDouble().toString() + "k")
            } else {
                noOfAllLike.toString()
            }
            binding?.commentviewLikeTextView?.text = noToDisplay
        }
    }

    private fun startHanshake(text: String) {
        val data = gson.fromJson(text, Handshake::class.java)
        if (data.status){
            this.info = data.info
        }
    }

    private fun newChangeReactions(text: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val data = gson.fromJson(text, NewChangeReaction::class.java)
            adapter.newChangeReaction(data)
        }
    }
    private fun newVideoComment(text: String) {
        val data = gson.fromJson(text, Data::class.java)
        if (data.user != info!!.user){
            newComment(text)
        }
    }
    private fun newImageComment(text: String) {
        val data = gson.fromJson(text, Data::class.java)
        if (data.user != info!!.user){
            newComment(text)
        }
    }
    private fun newCommentDeleted(text: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val data = gson.fromJson(text, CommentDeletedModels::class.java)
            for (i in commentlist){
                if (i.id == data.commentId){
                    val pos = commentlist.indexOf(i)
                    i.type = 3
                    i.comments = ""
                    i.user = ""
                    i.avatar = ""
                    i.image = null
                    i.video = null
                    adapter.notifyItemChanged(pos)
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun newComment(text:String){
        Log.d(TAG, "newCommentReceive: $text")
        val data = gson.fromJson(text, Data::class.java)
        if (data.id == null){
            return
        }else if(data.user == info!!.user){
            return
        }
        lifecycleScope.launch(Dispatchers.Main) {
            delay(100)
            var go = true
            for (i in commentlist){
                if (i.id == null){
                    if (data.comments == i.comments && data.user == i.user){
                        val pos = commentlist.indexOf(i)
                        commentlist[pos] = data
                        adapter.notifyItemChanged(pos)
                        go = false
                    }
                }else if (data.id == i.id && data.user == i.user){
                    val pos = commentlist.indexOf(i)
                    commentlist[pos] = data
                    adapter.notifyItemChanged(pos)
                    go = false
                }
            }
            if (go){
                commentlist.add(0, data)
                adapter.notifyItemInserted(0)
                try {
                    val count = if (adapter.itemCount == 0){ 0 } else { 10 }
                    if (layoutManager.findLastVisibleItemPosition() <= count || adapter.itemCount == 0){
                        this@CommentDialog.layoutManager.scrollToPosition(0)
                    }else{
                        popUpNewChat()
                        handler?.removeCallbacks(stopPopUpRunnable)
                        handler?.postDelayed(stopPopUpRunnable, 4000)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }
    private fun userTyping(text: String){
        lifecycleScope.launch(Dispatchers.Main) {
            val data = gson.fromJson(text, TypingData::class.java)
            Log.d(TAG, manager.getUserData()!!.user)
            if (data.user != manager.getUserData()?.user){
                if (binding?.commentTyping?.visibility == View.GONE){
                    binding?.commentTyping?.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun userStopTyping(text: String){
        lifecycleScope.launch(Dispatchers.Main) {
            val data = gson.fromJson(text, TypingData::class.java)
            if (data.user != manager.getUserData()?.user){
                if (binding?.commentTyping?.visibility == View.VISIBLE){
                    binding?.commentTyping?.visibility = View.GONE
                }
            }
        }
    }

    inner class CommentListener:WebSocketListener(){
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            this@CommentDialog.isClose = true
            if (!this@CommentDialog.isDetach){
                this@CommentDialog.connectToSocket()
            }
            super.onClosed(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (!this@CommentDialog.isDetach){
                connectToSocket()
            }
            super.onFailure(webSocket, t, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, text)
            lifecycleScope.launch {
                val res = gson.fromJson(text, CommentWebsocketInterface::class.java)
                if (res.type != null){
                    when(res.type){
                        "handshake" -> {startHanshake(text)}
                        "new_comment_message" -> {newComment(text)}
                        "new_image_comment" -> {newImageComment(text)}
                        "user_typing" -> {this@CommentDialog.userTyping(text)}
                        "user_stop_typing" -> {this@CommentDialog.userStopTyping(text)}
                        "new_video_comment" -> {newVideoComment(text)}
                        "new_comment_deleted" -> {newCommentDeleted(text)}
                        "new_reaction_change" -> {newChangeReactions(text)}
                    }
                }
            }
            super.onMessage(webSocket, text)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            this@CommentDialog.isClose = false
            super.onOpen(webSocket, response)
        }
    }




    override fun onDetach() {
        Log.d(TAG, "OnDetach")
        this.isDetach = true
        websocket?.close(1000, null)
        super.onDetach()
    }

    override fun onStart() {
        Log.d(TAG, "Onstart")
        if (!this@CommentDialog.isDetach && isClose){
            this@CommentDialog.connectToSocket()
        }
        this.isDetach = false
        super.onStart()
    }
    companion object{

    }
}
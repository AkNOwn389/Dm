package com.aknown389.dm.classes

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class ProgressRequestBody(private val mFile: File,
        private val mListener: UploadCallbacks,
        private val content_type: String? = null) : RequestBody() {


    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
        fun uploadImage()
    }

    inner class OnFinishUpdate():Runnable{
        override fun run() {
            mListener.onProgressUpdate(100)
        }

    }
    inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) :
        Runnable {
        override fun run() {
            mListener.onProgressUpdate((100 * mUploaded / mTotal).toInt())
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 1024
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    override fun contentType(): MediaType? {
        return ("$content_type/*").toMediaTypeOrNull()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val `in` = FileInputStream(mFile)
        var uploaded: Long = 0
        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (`in`.read(buffer).also { read = it } != -1) {

                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength))
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        } finally {
            val handler = Handler(Looper.getMainLooper())
            `in`.close()
            handler.post(OnFinishUpdate())
        }
    }
}
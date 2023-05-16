package com.aknown389.dm.utils

import android.os.Build
import android.util.Log
import com.aknown389.dm.utils.dataClass.GetHostDataClass
import com.aknown389.dm.utils.remote.UtilsInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Constants {
    private var host:String? = null
    init {
        getHost()
    }
    private fun getHost(){
        val response = try {
            UtilsInstance.api.getHomeName()
        }catch (e:Exception){
            return
        }
        response.enqueue(object : Callback<GetHostDataClass?> {
            override fun onResponse(
                call: Call<GetHostDataClass?>,
                response: Response<GetHostDataClass?>
            ) {
                this@Constants.host = response.body()!!.name
            }
            override fun onFailure(call: Call<GetHostDataClass?>, t: Throwable) {
                Log.d( "Constance","Constance Error $t")
            }
        })
    }
    companion object{
        private const val debug = false
        private const val DEBUGHOST = "192.168.0.115:8000"
        private const val HOST = "e0d1-124-105-235-119.ngrok-free.app"
        val BASE_URL = if (!debug){"https://$HOST"}else{"http://$DEBUGHOST"}
        val WEBSOCKET_ACTIVE_URL =if (!debug){"wss://$HOST/user/connect"}else{"ws://$DEBUGHOST/user/connect"}
        val WEBSOCKET_BASE_URL =if (!debug){"wss://$HOST"}else{"ws://$DEBUGHOST"}
        const val APP_VERSION = "1.0.7"
        const val APP_BUILD = 1
        const val REQUEST_CODE_IMAGE_PICKER = 12937
        const val REQUEST_IMAGE_CAPTURE = 4374
        const val CHANNEL_ID = "com.AkNOwn389.directMessage.inc"
        var API_VERSION = Build.VERSION.SDK_INT
        var PHONE_NAME: String = Build.DEVICE
        const val minBufferMs = 5000
        const val maxBufferMs = 20000
        const val bufferForPlaybackMs = 2500
        const val bufferForPlaybackAfterRebufferMs = 5000
    }
}
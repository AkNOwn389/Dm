package com.aknown389.dm.utils

import android.os.Build

class Constants {
    companion object{
        const val BASE_URL = "http://192.168.0.115:8000"
        const val WEBSOCKET_ACTIVE_URL = "ws://192.168.0.115:8000/user/connect"
        const val WEBSOCKET_BASE_URL = "ws://192.168.0.115:8000"
        const val APP_VERSION = "1.0.5"
        const val APP_BUILD = 1
        const val REQUEST_CODE_IMAGE_PICKER = 12937
        const val REQUEST_IMAGE_CAPTURE = 4374
        const val NORMAL_CLOSING_CODE = 1000
        const val CHANNEL_ID = "com.AkNOwn389.directMessage.inc"
        var API_VERSION = Build.VERSION.SDK_INT
        var PHONE_NAME: String = Build.DEVICE


        const val minBufferMs = 5000
        const val maxBufferMs = 20000
        const val bufferForPlaybackMs = 2500
        const val bufferForPlaybackAfterRebufferMs = 5000

        const val DATABASE_VERSION = 1

    }
}
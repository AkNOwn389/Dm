package com.aknown389.dm.activities

import java.util.concurrent.TimeUnit
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.aknown389.dm.R
import com.aknown389.dm.fragments.Index
import com.aknown389.dm.utils.Constants.Companion.WEBSOCKET_ACTIVE_URL
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.NetInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class MainFragmentContainerActivity : AppCompatActivity() {
    private lateinit var manager:DataManager
    private lateinit var token:String
    private lateinit var index: Index
    private lateinit var client: OkHttpClient
    private lateinit var ws:WebSocket
    private var isClose = false
    private var isDestroy = false
    val res:MutableLiveData<String> = MutableLiveData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragment_container)
        index = Index()
        loadIndex()
        setVal()
        activeStatusActivate()

    }

    private fun setVal() {
        this.manager = DataManager(this)
        this.token = manager.getAccessToken().toString()
        this.client = OkHttpClient.Builder()
            .addInterceptor(NetInterceptor())
            .readTimeout(0, TimeUnit.SECONDS)
            .build()
    }

    override fun onStart() {
        if (this.isClose){
            activeStatusActivate()
        }
        super.onStart()
    }

    override fun onRestart() {
        isDestroy = false
        super.onRestart()
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onResume() {
        this.isDestroy = false
        super.onResume()
    }
    override fun onDestroy() {
        ws.close(1000, "App Destroyed")
        this.isDestroy = true
        super.onDestroy()
    }
    private fun activeStatusActivate() {

        val request:Request = Request.Builder()
            .url(WEBSOCKET_ACTIVE_URL)
            .addHeader("Authorization", token)
            .build()
        try {
            if (!isDestroy){
                this.ws = client.newWebSocket(request, Listener())
                this.isClose = false
            }
        }catch (e:Exception){
            this.isClose = true
            e.printStackTrace()
        }


    }

    private fun loadIndex() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragmentContainer, index)
            commit()
        }
    }
    inner class Listener: WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            this@MainFragmentContainerActivity.activeStatusActivate()
            this@MainFragmentContainerActivity.isClose = true
            LOG("close $reason")
        }


        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            this@MainFragmentContainerActivity.activeStatusActivate()
            LOG("Failure ${t.message}")
            this@MainFragmentContainerActivity.isClose = true
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            LOG("message from string: $text")
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            this@MainFragmentContainerActivity.isClose = false
            super.onOpen(webSocket, response)
        }

        private fun LOG(text: String) {
            Log.d("SOCKET", text)
        }
    }
}
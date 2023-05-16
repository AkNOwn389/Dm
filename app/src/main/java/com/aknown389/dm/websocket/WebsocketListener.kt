package com.aknown389.dm.websocket

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class Websocketlistener: WebSocketListener() {
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSING_CODE, null)
        LOG("Socket close: $reason / $code")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        LOG("Failure ${t.message}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        LOG("message from string: $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        LOG("message from bytes: $bytes")

    }

    override fun onOpen(webSocket: WebSocket, response: Response) {

        super.onOpen(webSocket, response)
        webSocket.send("hello world")
    }
    private fun LOG(text: String){
        Log.d("SOCKET", text)
    }
    companion object{
        const val NORMAL_CLOSING_CODE = 1000
    }
}
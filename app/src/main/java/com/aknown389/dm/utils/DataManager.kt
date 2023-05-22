package com.aknown389.dm.utils

import android.content.Context
import com.aknown389.dm.models.loginRegModels.Info
import com.aknown389.dm.models.loginRegModels.Token
import com.google.gson.Gson

private const val SHARED_PREFS: String = "SEMIPRIVATE"
private const val SETTINGS_PREFS: String = "SETTINGS"
private const val ACCESSTOKEN_KEY: String = "myTokenOI)E)@(@$834943727k"
private const val USERINFO_KEY:String = "info{)(*{YU)Y[0[awhy0h"

class DataManager(private val context: Context){
    private var gson:Gson = Gson()
    fun saveUserData(data:Info){
        val text = gson.toJson(data)
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(USERINFO_KEY, text)
        editor.apply()
    }
    fun getUserData(): Info?{
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)!!
        val info = sharedPreferences.getString(USERINFO_KEY, null) ?: return null
        return gson.fromJson(info, Info::class.java)
    }

    fun saveDefaultPostPrivacy(value: String ,key: String = "postPrivacy"){
        val sharedPreferences = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun saveDefaultVolumestate(value: String, key: String = "volumeState"){
        val sharedPreferences = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun getDefaultVolumeState(key: String = "volumeState"): String?{
        val sharedPreferences = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(key, null)
    }
    fun getDefaultPostPrivacy(key: String = "postPrivacy"): String? {
        val sharedPreferences = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(key, null)
    }
    fun saveToken(value:Token){
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val text = gson.toJson(value)
        editor.putString(ACCESSTOKEN_KEY, text)
        editor.apply()
    }
    fun getRefreshToken(): String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(
            ACCESSTOKEN_KEY, null) ?: return null
        val data = gson.fromJson(sharedPreferences, Token::class.java)
        return data.refreshToken
    }
    fun getAccessToken(): String? {
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(
            ACCESSTOKEN_KEY, null) ?: return null
        val data = gson.fromJson(sharedPrefs, Token::class.java)
        return "${data.tokenType} ${data.accessToken}"
    }
    fun delete(){
        try {
            context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)!!.edit().remove(ACCESSTOKEN_KEY).apply()
            context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)!!.edit().remove(USERINFO_KEY).apply()
        }catch (e:Exception){
            return
        }

    }

    fun deleteAlldata(){
        try {
            context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)!!.edit().clear().apply()
        }catch (e:Exception){
            return
        }

    }
}
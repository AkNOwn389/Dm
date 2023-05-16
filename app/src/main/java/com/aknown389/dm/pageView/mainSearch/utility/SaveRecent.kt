package com.aknown389.dm.pageView.mainSearch.utility

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.pageView.mainSearch.dataClass.MainSearchItemData
import com.aknown389.dm.pageView.mainSearch.remote.instance.Instance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SaveRecent(
    private val token:String,
    private val data: MainSearchItemData,
    private val holder: MainSearchViewHolder,
    private val context: Context,
    private val adapter: Adapter

) {

    init {
        (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
            val response = try {
                Instance(context.applicationContext).api.saveRecent(user = data.user.toString())
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful && response.body()!!.status){
                Log.d("SaveRecent", response.body()!!.message)
            }
            return@launch
        }
    }
}
package com.aknown389.dm.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.activities.RegisterActivity
import com.aknown389.dm.api.retroInstance.LoginRegisterInstance
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.databinding.FragmentDialogVerifyOtpBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.models.loginRegModels.RegReqCodeModel
import com.aknown389.dm.models.loginRegModels.RegisterModel
import com.aknown389.dm.models.loginRegModels.RegisterResModelV2
import com.aknown389.dm.pageView.registerPage.models.RegisterDataParcel
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.snackbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.time.seconds

class DialogVerifyOtp : BottomSheetDialogFragment() {
    private var isLoading: Boolean = false
    private lateinit var parcel:RegisterDataParcel
    private var binding: FragmentDialogVerifyOtpBinding? = null
    private lateinit var appDataBase:AppDataBase
    private var gson: Gson = Gson()
    private var isWarning = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            bottomSheet.layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDialogVerifyOtpBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValue()
        setCodeInputListener()
        //setCountDown()
    }

    private fun setCountDown() {
        binding?.countDown?.visibility = View.VISIBLE
        binding?.resendOtp?.visibility = View.GONE
        val countDownTimer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding?.countDown?.text = String.format("%02d:%02d", minutes, seconds)
            }
            override fun onFinish() {
                binding?.countDown?.visibility = View.GONE
                binding?.resendOtp?.visibility = View.VISIBLE
            }
        }
        countDownTimer.start()
    }

    private fun register(otpCode:String){
        this.isLoading = true
        binding?.indicator?.visibility = View.VISIBLE
        val registerBody = RegisterModel(otpCode, parcel.registerId, parcel.username, parcel.email, parcel.name, parcel.password, parcel.comfirmPassword)
        val responseData = RetrofitInstance.retrofitBuilder.register(registerBody)
        responseData.enqueue(object : Callback<RegisterResModelV2?> {
            override fun onResponse(
                call: Call<RegisterResModelV2?>,
                response: Response<RegisterResModelV2?>
            ) {
                val resBody: RegisterResModelV2
                try {
                    resBody = response.body()!!
                }catch (e: java.lang.NullPointerException){
                    binding?.progressBar?.isVisible = false
                    return
                }
                binding?.progressBar?.isVisible=false
                binding?.indicator?.visibility = View.GONE
                if (resBody.status){
                    Toast.makeText(requireContext(), resBody.message, Toast.LENGTH_SHORT).show()
                    val manager = DataManager(requireContext())
                    manager.saveUserData(data = resBody.info)
                    manager.saveToken(value = resBody.token)
                    val data = UserAccountDataClass(
                        id = resBody.info.id,
                        info = resBody.info,
                        accessToken = resBody.token.accessToken!!,
                        refreshToken = resBody.token.refreshToken.toString(),
                        tokenType = resBody.token.tokenType!!
                    )
                    saveAccountInDb(data)
                    Intent(requireContext(), MainFragmentContainerActivity::class.java).also {
                        startActivity(it)
                        (requireContext() as? RegisterActivity)?.finishAffinity()
                    }
                }else{
                    warningEditTextbackground()
                    Toast.makeText(requireContext(), resBody.message, Toast.LENGTH_SHORT).show()
                }
                this@DialogVerifyOtp.isLoading = false
            }
            override fun onFailure(call: Call<RegisterResModelV2?>, t: Throwable) {
                binding?.progressBar?.isVisible=false
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                this@DialogVerifyOtp.isLoading = false
                binding?.indicator?.visibility = View.GONE
            }
        })
    }

    private fun saveAccountInDb(account: UserAccountDataClass){
        lifecycleScope.launch(Dispatchers.IO) {
            appDataBase.userDao().insertAccount(account)
        }
    }


    private fun backToNormalBackground(){
        if (isWarning){
            binding?.apply {
                code1.setBackgroundResource(R.drawable.verify_otp_custom_background)
                code2.setBackgroundResource(R.drawable.verify_otp_custom_background)
                code3.setBackgroundResource(R.drawable.verify_otp_custom_background)
                code4.setBackgroundResource(R.drawable.verify_otp_custom_background)
                code5.setBackgroundResource(R.drawable.verify_otp_custom_background)
                code6.setBackgroundResource(R.drawable.verify_otp_custom_background)
            }
            isWarning = false
        }
    }
    private fun warningEditTextbackground(){
        binding?.apply {
            code1.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code2.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code3.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code4.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code5.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code6.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
        }
        isWarning = true
    }
    private fun setCodeInputListener() {
        binding?.code1?.requestFocus()
        binding?.verifyCode?.setOnClickListener {
            val code1 = binding?.code1?.text.toString()
            val code2 = binding?.code2?.text.toString()
            val code3 = binding?.code3?.text.toString()
            val code4 = binding?.code4?.text.toString()
            val code5 = binding?.code5?.text.toString()
            val code6 = binding?.code6?.text.toString()
            if ((code1 == "")
                || (code2 == "")
                || (code3 == "")
                || (code4 == "")
                || (code5 == "")
                || (code6 == "")
                || code1.isNullOrEmpty()
                || code2.isNullOrEmpty()
                || code3.isNullOrEmpty()
                || code4.isNullOrEmpty()
                || code5.isNullOrEmpty()
                || code6.isNullOrEmpty()
            ){
                return@setOnClickListener
            }else{
                val otpCode:String = (code1 + code2 + code3 + code4 + code5 + code6)
                if (!isLoading){
                    register(otpCode)
                }
            }
        }
        binding?.resendOtp?.setOnClickListener {
            getRegisterCode()
        }
        binding?.apply {
            code1.setOnKeyListener { _, keyCode, _ ->
                if (binding?.code1?.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    binding?.code2?.requestFocus()
                }
                false
            }
            code2.setOnKeyListener { _, keyCode, _ ->
                if (binding?.code2?.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    binding?.code3?.requestFocus()
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground()
                    code2.postDelayed({
                        code1.requestFocus()
                    }, 10)
                    binding?.code2?.text = null
                }
                false
            }
            code3.setOnKeyListener { _, keyCode, _ ->
                if (binding?.code3?.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    binding?.code4?.requestFocus()
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground()
                    code3.postDelayed({
                        code2.requestFocus()
                    }, 10)
                    binding?.code3?.text = null
                }
                false
            }
            code4.setOnKeyListener { _, keyCode, _ ->
                if (binding?.code4?.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    binding?.code5?.requestFocus()
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground()
                    code4.postDelayed({
                        code3.requestFocus()
                    }, 10)
                    binding?.code4?.text = null
                }
                false
            }
            code5.setOnKeyListener { _, keyCode, _ ->
                if (binding?.code5?.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    binding?.code6?.requestFocus()
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground()
                    code5.postDelayed({
                        code4.requestFocus()
                    }, 10)
                    binding?.code5?.text = null
                }
                false
            }
            code6.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground()
                    code6.postDelayed({
                        code5.requestFocus()
                    }, 10)
                    binding?.code6?.text = null
                }
                false
            }
        }
    }
    private fun getRegisterCode() {
        val body = RegReqCodeModel(email = parcel.email, username = parcel.username)
        binding?.indicator?.isVisible = true
        lifecycleScope.launch {
            val response = try {
                LoginRegisterInstance.api.getRegCode(body)
            }catch (e:Exception){
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                val resBody = response.body()!!
                if (resBody.status){
                    parcel.registerId = resBody.data.regId
                    binding?.apply {
                        code1.text = null
                        code2.text = null
                        code3.text = null
                        code4.text = null
                        code5.text = null
                        code6.text = null
                    }
                    backToNormalBackground()
                    Toast.makeText(requireContext(), "Please wait for email verification.", Toast.LENGTH_SHORT)
                        .show()
                    this@DialogVerifyOtp.setCountDown()
                }else{
                    binding?.root?.snackbar(resBody.message)
                }
            }
            binding?.indicator?.isVisible = false
        }
    }

    private fun setValue() {
        parcel = gson.fromJson(arguments?.getString("parcel"), RegisterDataParcel::class.java)
        appDataBase = AppDataBase.getDatabase(requireContext())
    }
}
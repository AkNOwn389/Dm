package com.aknown389.dm.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityRecoverAccountBinding
import com.aknown389.dm.databinding.DialogRecoverAccountVerifyEmailBinding
import com.aknown389.dm.dialogs.LoadingAlertDialog
import com.aknown389.dm.pageView.recoverAccountView.models.RecoveryAccountVerifyOtp
import com.aknown389.dm.pageView.recoverAccountView.models.RequestAccountRecovery
import com.aknown389.dm.pageView.recoverAccountView.viewModel.RecoveryAccountViewModel
import com.aknown389.dm.utils.snackbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import java.lang.NullPointerException
import java.util.regex.Pattern

const val TAG = "RecoverAccountActivity"
class RecoverAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecoverAccountBinding
    private lateinit var viewModel:RecoveryAccountViewModel
    private lateinit var dialog:BottomSheetDialog
    private lateinit var loadingAlertDialog: LoadingAlertDialog
    private var view:DialogRecoverAccountVerifyEmailBinding? = null
    private var gson = Gson()
    private var isWarning = false
    private var isMaxAttempt = false
    private var isAlertDialogLoading = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        iniT()
    }

    private fun iniT() {
        setValue()
        setUI()
        setListener()
    }

    private fun setListener() {
        binding.submitEmailBtn.setOnClickListener {
            loadingAlertDialog.start()
            isAlertDialogLoading = true
            startRecovery()
        }
        viewModel._request_recovery_response.observe(this){ response ->
            Log.d(TAG, response.toString())
            if (response != null){
                if (response.status){
                    viewModel.requestAccountRecoveryResponseData = response.data
                    if (!dialog.isShowing){
                        isMaxAttempt = false
                        showDialog()
                    }
                }else{
                    try {
                        when(response.statusCode){
                            0 -> {
                                isMaxAttempt = true
                                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            }
                            1 -> {
                                isMaxAttempt = false
                                viewModel.requestAccountRecoveryResponseData = response.data
                                if (!dialog.isShowing){
                                    showDialog()
                                }
                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    try {
                        view?.root?.snackbar(response.message)
                    }catch (e:Exception){
                        view?.root?.snackbar(e.message.toString())
                    }
                }
            }else{
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
            if (isAlertDialogLoading){
                loadingAlertDialog.dismiss()
                isAlertDialogLoading = false
            }
        }
        viewModel._verify_code_response.observe(this){ response ->
            Log.d(TAG, response.toString())
            if (response != null){
                if (response.status){
                    if (dialog.isShowing){
                        dialog.dismiss()
                    }
                    viewModel.verifyOtpResponseData = response.data
                    Intent(this, RecoveryAccountChangePasswordActivity::class.java).also {
                        val parcel = gson.toJson(viewModel.verifyOtpResponseData)
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right)
                        it.putExtra("parcel", parcel)
                        startActivity(it)
                    }
                }else{
                    try {
                        if (response.remaining_attempt != null){
                            viewModel.attempt = response.remaining_attempt
                            view?.attemptNumber?.text = response.remaining_attempt.toString()
                            if (response.remaining_attempt >= 5){
                                dialog.dismiss()
                                isMaxAttempt = true
                            }else{
                                isMaxAttempt = false
                            }
                        }else{
                            viewModel.attempt  = 5
                            view?.attemptNumber?.text = "5"
                        }
                        when(response.status_code){
                            0 -> {
                                isMaxAttempt = true
                                dialog.dismiss()
                            }
                            1 -> {
                                isMaxAttempt = true
                            }
                        }
                    }catch (e:Exception){
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                    view?.let { warningEditTextBackground(it) }
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
            if (isAlertDialogLoading){
                loadingAlertDialog.dismiss()
                isAlertDialogLoading = false
            }
        }
    }

    private fun checkCredential(): Boolean{
        val email = binding.submitEmail.text.toString()
        if (email == "" || email.isEmpty()){
            Toast.makeText(this, "Please input your email", Toast.LENGTH_SHORT).show()
            binding.submitEmail.error = "empty"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please input valid email", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
    private fun startRecovery() {
        if(checkCredential()){
            val email = binding.submitEmail.text.toString()
            val body = RequestAccountRecovery(email=email)
            viewModel.requestAccountRecovery(body)
        }
    }

    private fun setUI() {

    }

    private fun setValue() {
        viewModel = RecoveryAccountViewModel()
        val inflater = LayoutInflater.from(this)
        dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        view = DialogRecoverAccountVerifyEmailBinding.inflate(inflater, null, false)
        loadingAlertDialog = LoadingAlertDialog(this)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(androidx.appcompat.R.anim.abc_slide_out_bottom, androidx.appcompat.R.anim.abc_slide_out_bottom)
    }

    @SuppressLint("InflateParams")
    private fun showDialog(){
        dialog.setContentView(view!!.root)
        dialog.show()
        view!!.attemptNumber.text = viewModel.attempt.toString()
        try {
            viewModel.countDown.observe(this){count ->
                view!!.countDown.text = count
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        setCodeInputListener(view!!)
    }

    private fun backToNormalBackground(view: DialogRecoverAccountVerifyEmailBinding){
        if (isWarning){
            view.apply {
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
    private fun warningEditTextBackground(view: DialogRecoverAccountVerifyEmailBinding){
        view.apply {
            code1.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code2.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code3.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code4.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code5.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
            code6.setBackgroundResource(R.drawable.verify_otp_custom_background_warning)
        }
        isWarning = true
    }

    private fun setCodeInputListener(view:DialogRecoverAccountVerifyEmailBinding) {
        view.code1.requestFocus()
        view.verifyCode.setOnClickListener {
            val code1 = view.code1.text.toString()
            val code2 = view.code2.text.toString()
            val code3 = view.code3.text.toString()
            val code4 = view.code4.text.toString()
            val code5 = view.code5.text.toString()
            val code6 = view.code6.text.toString()
            if ((code1 == "")
                || (code2 == "")
                || (code3 == "")
                || (code4 == "")
                || (code5 == "")
                || (code6 == "")
                || code1.isEmpty()
                || code2.isEmpty()
                || code3.isEmpty()
                || code4.isEmpty()
                || code5.isEmpty()
                || code6.isEmpty()
            ){
                return@setOnClickListener
            }else{
                val otpCode:String = (code1 + code2 + code3 + code4 + code5 + code6)
                if (!viewModel.isLoading && !isMaxAttempt){
                    loadingAlertDialog.start()
                    isAlertDialogLoading = true
                    try {
                        val body = RecoveryAccountVerifyOtp(
                            code = otpCode,
                            email = viewModel.requestAccountRecoveryResponseData?.email!!,
                            requestId = viewModel.requestAccountRecoveryResponseData?.requestId!!)
                        viewModel.verifyCode(body = body)
                    }catch (e:NullPointerException){
                        e.message?.let { it1 -> view.root.snackbar(it1) }
                    }
                }
                if (isMaxAttempt){
                    dialog.dismiss()
                }
            }
        }
        view.resendOtp.setOnClickListener {
            startRecovery()
        }
        view.apply {
            code1.setOnKeyListener { _, keyCode, _ ->
                if (view.code1.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    view.code2.requestFocus()
                }
                false
            }
            code2.setOnKeyListener { _, keyCode, _ ->
                if (view.code2.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    view.code3.requestFocus()
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground(view)
                    code2.postDelayed({
                        code1.requestFocus()
                    }, 10)
                    view.code2.text = null
                }
                false
            }
            code3.setOnKeyListener { _, keyCode, _ ->
                if (view.code3.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    view.code4.requestFocus()
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground(view)
                    code3.postDelayed({
                        code2.requestFocus()
                    }, 10)
                    view.code3.text = null
                }
                false
            }
            code4.setOnKeyListener { _, keyCode, _ ->
                if (view.code4.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    view.code5.requestFocus()
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground(view)
                    code4.postDelayed({
                        code3.requestFocus()
                    }, 10)
                    view.code4.text = null
                }
                false
            }
            code5.setOnKeyListener { _, keyCode, _ ->
                if (view.code5.text.toString().length == 1 && keyCode != KeyEvent.KEYCODE_DEL){
                    view.code6.requestFocus()
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground(view)
                    code5.postDelayed({
                        code4.requestFocus()
                    }, 10)
                    view.code5.text = null
                }
                false
            }
            code6.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backToNormalBackground(view)
                    code6.postDelayed({
                        code5.requestFocus()
                    }, 10)
                    view.code6.text = null
                }
                false
            }
        }
    }
}
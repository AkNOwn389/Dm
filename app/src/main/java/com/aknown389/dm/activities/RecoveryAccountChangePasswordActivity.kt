package com.aknown389.dm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityRecoveryAccountChangePasswordBinding
import com.aknown389.dm.dialogs.LoadingAlertDialog
import com.aknown389.dm.pageView.recoveryChangePassword.viewModels.ChangePasswordViewModel
import com.aknown389.dm.pageView.recoverAccountView.models.AccountRecoveryChangePasswordBody
import com.aknown389.dm.pageView.recoverAccountView.models.RecoveryAccountVerifyOtpResponseData
import com.aknown389.dm.utils.snackbar
import com.google.gson.Gson

class RecoveryAccountChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecoveryAccountChangePasswordBinding
    private var verifyOtpResponseData: RecoveryAccountVerifyOtpResponseData? = null
    private lateinit var viewModel:ChangePasswordViewModel
    private lateinit var loadingAlertDialog: LoadingAlertDialog
    private var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoveryAccountChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        iniT()
    }

    private fun iniT() {
        setValue()
        setListener()
    }

    private fun setListener() {
        binding.apply {
            submit.setOnClickListener {
                submitNewPassword()
            }
        }
        viewModel._response.observe(this){ response ->
            if (response != null){
                if (response.status){
                    Toast.makeText(
                        this@RecoveryAccountChangePasswordActivity,
                        "reset successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(this@RecoveryAccountChangePasswordActivity, LoginActivity::class.java).also {
                        startActivity(it)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finishAffinity()
                    }
                }else{
                    try {
                        Toast.makeText(
                            this@RecoveryAccountChangePasswordActivity,
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }catch (e:Exception){
                        binding.root.snackbar("something went wrong.")
                    }
                }
            }
            if (viewModel.isLoadingShowing){
                loadingAlertDialog.dismiss()
                viewModel.isLoadingShowing = false
            }
        }
    }

    private fun checkCredential():Boolean{
        val newPassword = binding.newPassword.text.toString()
        val confirm = binding.confirmPassword.text.toString()
        if (newPassword.isBlank() || newPassword == ""){
            binding.newPassword.error = "empty"
            return false
        }
        if (confirm.isBlank() || confirm == ""){
            binding.confirmPassword.error = "empty"
            return false
        }
        if (newPassword != confirm){
            binding.root.snackbar("password and confirm password not match")
            return false
        }
        return true
    }
    private fun submitNewPassword() {
        if (checkCredential()){
            val newPassword = binding.newPassword.text.toString()
            val confirm = binding.confirmPassword.text.toString()
            if (verifyOtpResponseData != null){
                val body = AccountRecoveryChangePasswordBody(newPassword = newPassword,
                    newPasswordConfirm = confirm,
                    recoveryKey = verifyOtpResponseData?.recoveryKey!!,
                    user = verifyOtpResponseData?.user!!,
                    userId = verifyOtpResponseData?.userId!!,
                    email = verifyOtpResponseData?.email!!)
                loadingAlertDialog.start()
                viewModel.isLoadingShowing = true
                viewModel.submit(body)
            }else{
                Toast.makeText(this, "Credentials missing", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setValue() {
        viewModel = ChangePasswordViewModel()
        loadingAlertDialog = LoadingAlertDialog(this)
        verifyOtpResponseData = gson.fromJson(intent.getStringExtra("parcel"), RecoveryAccountVerifyOtpResponseData::class.java)
    }
}
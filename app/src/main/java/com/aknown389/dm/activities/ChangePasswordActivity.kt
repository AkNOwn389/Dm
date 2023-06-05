package com.aknown389.dm.activities

import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.databinding.ActivityChangePasswordBinding
import com.aknown389.dm.databinding.ChangePasswordSuccessDialogBinding
import com.aknown389.dm.dialogs.LoadingAlertDialog
import com.aknown389.dm.pageView.changePassword.dataClass.ChangePasswordBody
import com.aknown389.dm.pageView.changePassword.viewmodel.ChangePasswordViewModel
import com.aknown389.dm.pageView.changePassword.viewmodel.ChangePasswordViewModelFactory
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {
    private var isAlertDialogShowing: Boolean = false
    private var binding:ActivityChangePasswordBinding? = null
    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var loadingAlertDialog: LoadingAlertDialog
    private lateinit var manager:DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        iniT()
    }

    private fun iniT() {
        setValue()
        setUI()
        setListener()
    }

    private fun checkCridentials(password:String?, newPassword:String?, confirmNewPassword:String?):Boolean{
        if (newPassword != confirmNewPassword){
            binding?.root?.snackbar("password not match")
            return false
        }
        if (password?.isBlank() == true){
            binding?.root?.snackbar("please input password")
            binding?.password?.error = "empty"
            return false
        }
        if (newPassword?.isBlank()==true){
            binding?.root?.snackbar("please input new password")
            binding?.newPassword?.error = "empty"
            return false
        }
        if (password == newPassword){
            binding?.root?.snackbar("password are same")
            binding?.newPassword?.text = null
            binding?.confirmPassword?.text = null
            return false
        }
        return true
    }

    private fun exit() {
        lifecycleScope.launch {
            delay(1000)
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
            super.finish()
        }
    }

    private fun setListener() {
        viewModel._responseChangePassword.observe(this){ response ->
            if (response.status){
                val dialog = AlertDialog.Builder(this).create()
                val inflater = LayoutInflater.from(this)
                val view = ChangePasswordSuccessDialogBinding.inflate(inflater, binding?.root, false)
                dialog.setView(view.root)
                if (dialog.window != null){
                    dialog.window?.setBackgroundDrawable(ColorDrawable(0))
                    dialog.show()
                }
                view.apply {
                    newPassword.text = viewModel.newPassword
                    see.setOnClickListener {
                        if (newPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                            newPassword.inputType = InputType.TYPE_CLASS_TEXT
                        } else {
                            newPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }
                        newPassword.refreshDrawableState()
                    }
                    okey.setOnClickListener {
                        dialog.dismiss()
                        exit()
                    }
                }
            }else{
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }
            if (isAlertDialogShowing){
                loadingAlertDialog.dismiss()
                isAlertDialogShowing = false
            }
        }
        binding?.apply {
            submit.setOnClickListener {

                val password = binding?.password?.text?.toString()
                val newPassword = binding?.newPassword?.text?.toString()
                val confirmNewPassword = binding?.confirmPassword?.text?.toString()

                if (checkCridentials(password, newPassword, confirmNewPassword)){
                    val device = "${Build.DEVICE} (${Build.VERSION.SDK_INT})"
                    val body = ChangePasswordBody(
                        password!!,
                        newPassword!!,
                        confirmNewPassword!!,
                        device = device
                    )
                    viewModel.newPassword = newPassword
                    viewModel.submit(this@ChangePasswordActivity, body = body)
                    loadingAlertDialog.start()
                    isAlertDialogShowing = true
                }
            }
            Back.setOnClickListener {
                finish()
            }




        }
    }

    private fun setUI() {
    }

    private fun setValue() {
        viewModel = ViewModelProvider(this, ChangePasswordViewModelFactory())[ChangePasswordViewModel::class.java]
        manager = DataManager(this)
        loadingAlertDialog = LoadingAlertDialog(this)
    }


    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
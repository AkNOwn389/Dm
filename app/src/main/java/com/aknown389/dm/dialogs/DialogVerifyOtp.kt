package com.aknown389.dm.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.activities.MainFragmentContainerActivity
import com.aknown389.dm.api.retroInstance.LoginRegisterInstance
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.databinding.FragmentDialogVerifyOtpBinding
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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DialogVerifyOtp : BottomSheetDialogFragment() {
    private lateinit var parcel:RegisterDataParcel
    private var binding: FragmentDialogVerifyOtpBinding? = null
    private var gson: Gson = Gson()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
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
    }
    private fun register(otpCode:String){
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
                if (resBody.status){
                    Toast.makeText(requireContext(), resBody.message, Toast.LENGTH_SHORT).show()
                    val manager = DataManager(requireContext())
                    manager.saveUserData(data = resBody.info)
                    manager.saveToken(value = resBody.token)
                    Intent(requireContext(), MainFragmentContainerActivity::class.java).also {
                        startActivity(it)
                        dismissAllowingStateLoss()
                    }
                }else{
                    Toast.makeText(requireContext(), resBody.message, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RegisterResModelV2?>, t: Throwable) {
                binding?.progressBar?.isVisible=false
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setCodeInputListener() {
        binding?.verifyCode?.setOnClickListener {
            val code1 = binding?.code1?.text.toString()
            val code2 = binding?.code2?.text.toString()
            val code3 = binding?.code3?.text.toString()
            val code4 = binding?.code4?.text.toString()
            val code5 = binding?.code5?.text.toString()
            val code6 = binding?.code6?.text.toString()
            if (code1.isEmpty() && code2.isEmpty() && code3.isEmpty() && code4.isEmpty() && code5.isEmpty() && code6.isEmpty()){
                return@setOnClickListener
            }else{
                val otpCode:String = (code1 + code2 + code3 + code4 + code5 + code6)
                register(otpCode)
            }
        }
        binding?.resendOtp?.setOnClickListener {
            getRegisterCode()
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
                }else{
                    binding?.root?.snackbar(resBody.message)
                }
            }
            binding?.indicator?.isVisible = false
        }
    }

    private fun setValue() {
        parcel = gson.fromJson(arguments?.getString("parcel"), RegisterDataParcel::class.java)
    }
}
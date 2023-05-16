package com.aknown389.dm.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.activities.EditProfileActivity
import com.aknown389.dm.activities.LoginActivity
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.databinding.DialogProfileSettingsBinding
import com.aknown389.dm.db.AppDataBase
import com.bumptech.glide.Glide
import com.aknown389.dm.models.logoutmodel.LogOutBodyModel
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.utils.snackbar
import com.aknown389.dm.pageView.main.viewModel.MainViewModel
import com.aknown389.dm.pageView.main.viewModel.MainViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DialogProfileSettings : BottomSheetDialogFragment() {
    private var binding: DialogProfileSettingsBinding? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var manager: DataManager
    private lateinit var token:String
    private var username:String? = null
    private var parent:ViewGroup? = null
    private var context: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (container != null) {
            this.parent = container
            this.context = container.context
        }
        binding = DialogProfileSettingsBinding.inflate(layoutInflater, container, false)
        return binding?.root!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            bottomSheet.layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        }
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValues()
        setUI()
        setListener()
    }

    private fun setListener() {
        binding?.EditProfileBtn?.setOnClickListener {
            (requireContext() as? AppCompatActivity)?.let {
                val intent = Intent(it, EditProfileActivity::class.java)
                it.startActivity(intent)
            }
            dismiss()
        }
        binding?.btnLogout?.setOnClickListener {
            logOut()
        }
        binding?.avatarConstrainSettings?.setOnClickListener {

        }
    }

    private fun setUI() {
        loadAvatar()
    }

    private fun setValues() {
        manager = DataManager(requireContext())
        token = manager.getAccessToken().toString()
        val repository = Repository()
        val db:AppDataBase = AppDataBase.getDatabase(requireContext())
        val viewModelFactory = MainViewModelFactory(repository = repository, dataBase = db, token = token)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        username = manager.getUserData()!!.user
    }

    private fun logOut() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_, _ ->
            val token = manager.getAccessToken()
            val refresh = manager.getRefreshToken()
            val body = LogOutBodyModel(refresh!!)
            requireActivity().lifecycleScope.launch(Dispatchers.Main) {
                val response = try {
                    RetrofitInstance.api.logout(token!!, body)
                }catch (e:Exception){
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                if (response.isSuccessful && response.body() != null){
                    val res = response.body()!!
                    if (res.status){
                        Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                        manager.deleteAlldata()
                        requireActivity().let {
                            val intent = Intent(it, LoginActivity::class.java)
                            it.startActivity(intent)
                            it.finishAffinity()
                        }
                        dismiss()
                    }else if (res.status == false){
                        Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }else{
                    binding?.settingsRoot!!.snackbar("something went wrong")
                }
            }
        }
        builder.setNegativeButton("No"){_, _ -> }
        builder.setTitle("Logout?")
        builder.setMessage("Are you sure you want yo logout?")
        builder.create().show()

    }

    private fun loadAvatar() {
        viewModel.me()
        viewModel.myDetailsResponse.observe(this) {
            Glide.with(this)
                .load(it.profileimg)
                .override(300, 300)
                .placeholder(R.mipmap.greybg)
                .error(R.mipmap.greybg)
                .into(binding!!.imageAvatarSettings)
            binding?.NameUserOnSettings!!.text = it.name
            binding?.usernamesettings!!.text = (it.username)

        }
    }
}
package com.aknown389.dm.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.databinding.ActivityUpdateAboutBinding
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.profileModel.UpdateDetailsBodyModel
import com.aknown389.dm.repository.Repository
import com.aknown389.dm.utils.DataManager
import com.aknown389.dm.pageView.main.viewModel.MainViewModel
import com.aknown389.dm.pageView.main.viewModel.MainViewModelFactory

class UpdateAboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateAboutBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init
        setValues()
        loadDetails()
        setListener()
    }

    private fun setListener() {
        binding.btnUpdateAbount.setOnClickListener {
            uploadAboutData()
        }
    }

    private fun setValues() {
        val manager = DataManager(this)
        token = manager.getAccessToken().toString()
        val repository = Repository()
        val db:AppDataBase = AppDataBase.getDatabase(this)
        val viewModelFactory = MainViewModelFactory(repository = repository, dataBase = db, token = token)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private fun uploadAboutData() {
        val name: String = binding.editTextMyName.text.toString()
        val bio: String = binding.editTextMyBio.text.toString()
        val location: String = binding.editTextMyLocation.text.toString()
        val body = UpdateDetailsBodyModel(name, bio, location)
        viewModel.updateDetails(body)
        viewModel.updatedetailsresponse.observe(this@UpdateAboutActivity){response ->
            if (response.isSuccessful && response.body() != null){
                val resBody = response.body()!!
                if (resBody.status){
                    Toast.makeText(this, "details update", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this@UpdateAboutActivity, resBody.message, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@UpdateAboutActivity, "something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

    }
    @SuppressLint("SetTextI18n")
    private fun loadDetails() {
        viewModel.me()
        viewModel.myDetailsResponse.observe(this@UpdateAboutActivity){resBody ->
            binding.editTextMyName.setText(resBody.name)
            binding.textViewMyName.text = "Current name: ${resBody.name}"
            binding.editTextMyBio.setText(resBody.bio)
            binding.editTextMyLocation.setText(resBody.location)
        }
    }
}
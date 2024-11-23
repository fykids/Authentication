package com.yosua.authentication.view.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.yosua.authentication.custom.MyEditText
import com.yosua.authentication.databinding.ActivityRegisterBinding
import com.yosua.authentication.model.Result
import com.yosua.authentication.view.ViewModelFactory
import com.yosua.authentication.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding

    private lateinit var myEditText : MyEditText

    private val viewModel : RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myEditText = binding.passwordEditText

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.toString()
            val password = myEditText.text.toString()

            registerUser(name, email, password)
        }

        viewModel.registerStatus.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    // Tampilkan indikator loading jika diperlukan
                }

                is Result.Success -> {
                    Toast.makeText(this@RegisterActivity, result.data.message, Toast.LENGTH_SHORT)
                        .show()
                    // Arahkan ke halaman berikutnya jika berhasil
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                }

                is Result.Error -> {
                    Toast.makeText(this@RegisterActivity, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun registerUser(name : String, email : String, password : String) {
        viewModel.register(name, email, password)
    }
}
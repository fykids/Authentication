package com.yosua.authentication.view.login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.yosua.authentication.databinding.ActivityLoginBinding
import com.yosua.authentication.model.Result
import com.yosua.authentication.view.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private val viewModel : LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            loginUser(email, password)
        }

        viewModel.loginStatus.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    // Tampilkan indikator loading jika diperlukan
                }

                is Result.Success -> {
                    Toast.makeText(this@LoginActivity, result.data.message, Toast.LENGTH_SHORT)
                        .show()
                    /*
                     Disini implementasi success login,
                     ketika berhasil jangan lupa atur backpress agar ketika tombol back ditekan
                     tidak kembali lagi ke login
                     */
                }

                is Result.Error -> {
                    Toast.makeText(this@LoginActivity, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loginUser(email : String, password : String) {
        viewModel.login(email, password)

    }
}
package com.yosua.authentication.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.yosua.authentication.custom.MyEditText
import com.yosua.authentication.databinding.ActivityLoginBinding
import com.yosua.authentication.model.Result
import com.yosua.authentication.model.di.Injection
import com.yosua.authentication.view.ViewModelFactory
import com.yosua.authentication.view.main.DashboardActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    private lateinit var myEditText : MyEditText

    private val viewModel : LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myEditText = binding.passwordEditText

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = myEditText.text.toString()
            loginUser(email, password)
        }

        viewModel.loginStatus.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    // Tampilkan indikator loading jika diperlukan
                }

                is Result.Success -> {
                    viewModel.saveSession(result.data.loginResult)
                    Toast.makeText(this@LoginActivity, result.data.message, Toast.LENGTH_SHORT)
                        .show()
                    /*
                     Disini implementasi success login,
                     ketika berhasil jangan lupa atur backpress agar ketika tombol back ditekan
                     tidak kembali lagi ke login
                     */
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
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
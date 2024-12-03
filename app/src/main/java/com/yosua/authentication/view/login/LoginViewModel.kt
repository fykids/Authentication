package com.yosua.authentication.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.Result
import com.yosua.authentication.model.remote.response.ErrorRespone
import com.yosua.authentication.model.remote.response.LoginResponse
import com.yosua.authentication.model.remote.response.LoginResult
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val appRepository : AppRepository) : ViewModel() {

    private val _loginStatus = MutableLiveData<Result<LoginResponse>>()
    val loginStatus : LiveData<Result<LoginResponse>> = _loginStatus

    fun login(
        email : String,
        password : String,
    ) {
        viewModelScope.launch {
            _loginStatus.value = Result.Loading
            try {
                val response = appRepository.login(email, password)
                _loginStatus.value = response
            } catch (e : HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorRespone::class.java)
                val errorMessage = errorBody.message ?: "Terjadi kesalahan pada server"
                _loginStatus.value = Result.Error(errorMessage)
            } catch (e : Exception) {
                _loginStatus.value = Result.Error("Login Gagal: ${e.message}")
            }
        }
    }

    fun saveSession(login: LoginResult) {
        viewModelScope.launch{
            appRepository.saveSession(login)
        }
    }
}
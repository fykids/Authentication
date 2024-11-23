package com.yosua.authentication.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.Result
import com.yosua.authentication.model.remote.response.ErrorRespone
import com.yosua.authentication.model.remote.response.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(private val appRepository : AppRepository) : ViewModel() {

    private val _registerStatus = MutableLiveData<Result<RegisterResponse>>()
    val registerStatus : LiveData<Result<RegisterResponse>> = _registerStatus

    fun register(
        name : String,
        email : String,
        password : String,
    ) {
        viewModelScope.launch {
            _registerStatus.value = Result.Loading
            try {
                val response = appRepository.register(name, email, password)
                _registerStatus.value = response
            } catch (e : HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorRespone::class.java)
                val errorMessage = errorBody.message ?: "Terjadi kesalahan pada server"
                _registerStatus.value = Result.Error(errorMessage)
            } catch (e : Exception) {
                _registerStatus.value = Result.Error("Daftar Gagal: ${e.message}")
            }
        }
    }
}
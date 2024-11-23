package com.yosua.authentication.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.Result
import com.yosua.authentication.model.remote.response.AllStoryResponse
import com.yosua.authentication.model.remote.response.LoginResult
import kotlinx.coroutines.launch

class DashboardViewModel(private val appRepository : AppRepository) : ViewModel() {
    private val _stories = MutableLiveData<Result<AllStoryResponse>>()
    val stories: LiveData<Result<AllStoryResponse>> = _stories

    fun getAllStories(){
        _stories.value = Result.Loading
        viewModelScope.launch{
            try {
                val response = appRepository.getAllStories()
                _stories.value = response
            } catch (e: Exception) {
                _stories.value = Result.Error(e.message ?: "Terjadi Kesalahan")
            }
        }
    }

    fun getSession(): LiveData<LoginResult>{
        return appRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch{
            appRepository.logout()
        }
    }
}
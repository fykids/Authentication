package com.yosua.authentication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.Result
import com.yosua.authentication.model.remote.response.AllStoryResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<Result<AllStoryResponse>>()
    val storiesWithLocation: LiveData<Result<AllStoryResponse>> = _storiesWithLocation

    fun getStoriesWithLocation(location: Int) {
        _storiesWithLocation.value = Result.Loading
        viewModelScope.launch {
            try {
                val result = appRepository.getStoriesWithLocation(location)
                _storiesWithLocation.value = result
            } catch (e: Exception) {
                _storiesWithLocation.value = Result.Error(e.message ?: "Terjadi error")
            }
        }
    }
}

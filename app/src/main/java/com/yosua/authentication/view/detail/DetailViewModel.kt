package com.yosua.authentication.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.Result
import com.yosua.authentication.model.remote.response.DetailResponse
import kotlinx.coroutines.launch

class DetailViewModel(private val appRepository : AppRepository) : ViewModel() {
    private val _detailStatus = MutableLiveData<Result<DetailResponse>>()
    val detailStatus : LiveData<Result<DetailResponse>> = _detailStatus

    fun getStory(storyId : String) {
        _detailStatus.value = Result.Loading

        viewModelScope.launch{
            val result = appRepository.getStories(storyId)
            _detailStatus.value = result
        }
    }
}
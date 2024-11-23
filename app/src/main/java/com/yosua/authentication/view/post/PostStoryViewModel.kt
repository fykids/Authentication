package com.yosua.authentication.view.post

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.Result
import com.yosua.authentication.model.remote.response.AddStoryResponse
import com.yosua.authentication.model.utils.createCustomTempFile
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PostStoryViewModel(private val appRepository : AppRepository) : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri : LiveData<Uri?> get() = _currentImageUri

    private val _uploadStatus = MutableLiveData<Result<AddStoryResponse>>()
    val uploadStatus: LiveData<Result<AddStoryResponse>> get() = _uploadStatus

    fun uploadImage(filePart: MultipartBody.Part, descriptionBody: RequestBody) {
        _uploadStatus.value = Result.Loading
        viewModelScope.launch {
            try {
                val result = appRepository.uploadImage(filePart, descriptionBody)
                if (result is Result.Success) {
                    _uploadStatus.value = Result.Success(result.data)
                } else if (result is Result.Error) {
                    _uploadStatus.value = Result.Error(result.error)
                }
            } catch (e: Exception) {
                _uploadStatus.value = Result.Error(e.message ?: "Terjadi error")
            }
        }
    }


    fun setImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
}
package com.yosua.authentication.model

import com.yosua.authentication.model.pref.UserPreference
import com.yosua.authentication.model.remote.network.ApiService
import com.yosua.authentication.model.remote.response.AllStoryResponse
import com.yosua.authentication.model.remote.response.DetailResponse
import com.yosua.authentication.model.remote.response.LoginResponse
import com.yosua.authentication.model.remote.response.LoginResult
import com.yosua.authentication.model.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

class AppRepository private constructor(
    private val apiService : ApiService,
    private val userPref : UserPreference,
) {

    suspend fun register(
        name : String,
        email : String,
        password : String,
    ) : Result<RegisterResponse> {
        return try {
            val response = apiService.registerUser(name, email, password)
            Result.Success(response)
        } catch (e : Exception) {
            Result.Error(e.message ?: "Terjadi error")
        }
    }

    suspend fun login(
        email : String, password : String,
    ) : Result<LoginResponse> {
        return try {
            val response = apiService.loginUser(email, password)
            Result.Success(response)
        } catch (e : Exception) {
            Result.Error(e.message ?: "Error")
        }
    }

    suspend fun getAllStories() : Result<AllStoryResponse> {
        return try {
            val response = apiService.getAllStories()
            Result.Success(response)
        } catch (e : Exception) {
            Result.Error(e.message ?: "Terjadi error")
        }
    }

    suspend fun getStories(storyId : String) : Result<DetailResponse> {
        return try {
            // Memanggil API dengan header Bearer token
            val response = apiService.getStories(storyId)

            // Mengecek apakah respons berhasil
            if (response.isSuccessful) {
                // Mengecek jika body response ada
                val responseBody = response.body()
                if (responseBody != null) {
                    Result.Success(responseBody)
                } else {
                    Result.Error("Response body kosong")
                }
            } else {
                // Menangani response gagal
                Result.Error("Gagal memuat detail cerita: ${response.code()} - ${response.message()}")
            }
        } catch (e : Exception) {
            // Menangani exception lainnya
            Result.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    suspend fun saveSession(user : LoginResult) {
        userPref.saveSession(user)
    }

    fun getSession() : Flow<LoginResult> {
        return userPref.getSession()
    }

    suspend fun logout() {
        userPref.logout()
    }

    companion object {
        @Volatile
        private var instance : AppRepository? = null
        fun getInstance(
            apiService : ApiService,
            userPref : UserPreference,
        ) : AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(apiService, userPref)
            }.also { instance = it }
    }
}
package com.yosua.authentication.model

import com.yosua.authentication.model.pref.UserPreference
import com.yosua.authentication.model.remote.network.ApiService
import com.yosua.authentication.model.remote.response.AllStoryResponse
import com.yosua.authentication.model.remote.response.DetailResponse
import com.yosua.authentication.model.remote.response.LoginResponse
import com.yosua.authentication.model.remote.response.LoginResult
import com.yosua.authentication.model.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.Response

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
    ): Result<LoginResponse> {
        return try {
            val response = apiService.loginUser(email, password)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message?:"Error")
        }
    }

    suspend fun getAllStories(): Result<AllStoryResponse> {
        return try {
            val response = apiService.getAllStories()
            Result.Success(response)
        } catch (e: Exception){
            Result.Error(e.message ?: "Terjadi error")
        }
    }

    suspend fun getStories(storyId: String): Result<DetailResponse>{
        return try {
            val token = userPref.getSession().first().token
            val response: Response<DetailResponse> = apiService.getStories(storyId, "Bearer $token")
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Gagal memuat detail cerita: ${response.message()}")
            }
        } catch (e: Exception){
            Result.Error("Error: ${e.message}")
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
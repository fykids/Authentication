package com.yosua.authentication.model

import com.yosua.authentication.model.pref.UserPreference
import com.yosua.authentication.model.remote.network.ApiService
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
    ): Result<LoginResponse> {
        return try {
            val response = apiService.loginUser(email, password)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message?:"Error")
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
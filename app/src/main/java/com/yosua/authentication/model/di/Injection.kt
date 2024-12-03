package com.yosua.authentication.model.di

import android.content.Context
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.pref.UserPreference
import com.yosua.authentication.model.pref.dataStore
import com.yosua.authentication.model.remote.network.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provide(context : Context): AppRepository{
        val userPref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(context)
        return AppRepository.getInstance(apiService, userPref)
    }
}
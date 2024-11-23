package com.yosua.authentication.model.di

import android.content.Context
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.pref.UserPreference
import com.yosua.authentication.model.pref.dataStore
import com.yosua.authentication.model.remote.network.ApiConfig

object Injection {
    fun provide(context : Context): AppRepository{
        val apiService = ApiConfig.getApiService()
        val userPref = UserPreference.getInstance(context.dataStore)
        return AppRepository.getInstance(apiService, userPref)
    }
}
package com.yosua.authentication.model.di

import android.content.Context
import com.yosua.authentication.model.StoryRepository
import com.yosua.authentication.model.database.StoryDatabase
import com.yosua.authentication.model.remote.network.ApiConfig

object InjectionPaging {
    fun provideRepository(context : Context) : StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(context)
        return StoryRepository(database, apiService)
    }
}
package com.yosua.authentication.model

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.yosua.authentication.model.data.StoryPagingSource
import com.yosua.authentication.model.database.StoryDatabase
import com.yosua.authentication.model.remote.network.ApiService
import com.yosua.authentication.model.remote.response.ListStoryItem
import kotlinx.coroutines.flow.Flow

class StoryRepository(
    private val storyDatabase : StoryDatabase,
    private val apiService : ApiService,
) {
    fun getStories() : Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).flow
    }
}
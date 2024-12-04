package com.yosua.authentication.model.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.yosua.authentication.model.database.RemoteKeys
import com.yosua.authentication.model.database.StoryDatabase
import com.yosua.authentication.model.remote.network.ApiService
import com.yosua.authentication.model.remote.response.ListStoryItem
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database : StoryDatabase,
    private val apiService : ApiService,
) : RemoteMediator<Int, ListStoryItem>() {

    override suspend fun load(
        loadType : LoadType,
        state : PagingState<Int, ListStoryItem>,
    ) : MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getStoriesPaging(page as Int, state.config.pageSize)

            val endPaginationReached = responseData.listStory.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    RemoteKeys(id = it!!.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.remoteKeysDao().insertAll(keys)
                @Suppress("UNCHECKED_CAST")
                database.storyDao().insertStory(responseData.listStory as List<ListStoryItem>)
            }
            return MediatorResult.Success(endPaginationReached)
        } catch (exception : Exception) {
            return MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize() : InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state : PagingState<Int, ListStoryItem>) : RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeys(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state : PagingState<Int, ListStoryItem>) : RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeys(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state : PagingState<Int, ListStoryItem>) : RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeys(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
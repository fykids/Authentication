package com.yosua.authentication

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yosua.authentication.model.data.StoryRemoteMediator
import com.yosua.authentication.model.database.StoryDatabase
import com.yosua.authentication.model.remote.network.ApiService
import com.yosua.authentication.model.remote.response.AddStoryResponse
import com.yosua.authentication.model.remote.response.AllStoryResponse
import com.yosua.authentication.model.remote.response.DetailResponse
import com.yosua.authentication.model.remote.response.ListStoryItem
import com.yosua.authentication.model.remote.response.LoginResponse
import com.yosua.authentication.model.remote.response.LoginResult
import com.yosua.authentication.model.remote.response.RegisterResponse
import com.yosua.authentication.model.remote.response.Story
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@ExperimentalPagingApi
@OptIn(ExperimentalStdlibApi::class)
@ExperimentalStdlibApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {
    private var mockApi : ApiService = FakeApiService()
    private var mockDb : StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        @OptIn(ExperimentalPagingApi::class)
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun registerUser(
        name : String,
        email : String,
        password : String
    ) : RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "Registration Success"
        )
    }

    override suspend fun loginUser(
        email : String,
        password : String
    ) : LoginResponse {
        return LoginResponse(
            error = false,
            message = "Login Success",
            loginResult = LoginResult(
                userId = "1",
                name = "Tantrum",
                token = "122"
            )
        )
    }

//    override suspend fun getAllStories() : Response<AllStoryResponse> {
//        val dummyStoryList = ListStoryItem(
//            "https://example.com/photo.jpg",
//            "2024-01-01T00:00:00Z",
//            "Story Title",
//            "Story Description",
//            5.0,
//            "Story Id",
//            10.0
//        )
//        val allStoryResponse = AllStoryResponse(
//            error = false,
//            message = "Stories fetched successfully",
//            listStory = listOf(dummyStoryList)
//        )
//        return Response.success(allStoryResponse)
//    }

    override suspend fun getStories(storyId : String) : Response<DetailResponse> {
        val detailResponse = DetailResponse(
            error = false,
            message = "Story details fetched successfully",
            story = Story(
                "https://example.com/photo.jpg",
                "2024-01-01T00:00:00Z",
                "Story Title",
                "Story Description",
                5.0,
                storyId,
                10.0
            )
        )
        return Response.success(detailResponse)
    }

    override suspend fun uploadImage(
        file : MultipartBody.Part,
        description : RequestBody
    ) : Response<AddStoryResponse> {
        val addStoryResponse = AddStoryResponse(
            error = false,
            message = "Image uploaded successfully",
        )
        return Response.success(addStoryResponse)
    }

    override suspend fun getStoriesPaging(
        page : Int,
        size : Int
    ) : AllStoryResponse {
        val dummyStoryList = ListStoryItem(
            "https://example.com/photo.jpg",
            "2024-01-01T00:00:00Z",
            "Story Title",
            "Story Description",
            5.0,
            "Story Id",
            10.0
        )

        return AllStoryResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listOf(dummyStoryList)
        )
    }

    override suspend fun getStoriesWithLocation(location : Int) : Response<AllStoryResponse> {
        val dummyStoryList = ListStoryItem(
            "https://example.com/photo.jpg",
            "2024-01-01T00:00:00Z",
            "Story Title",
            "Story Description",
            5.0,
            "Story Id",
            10.0
        )

        val allStoryResponse = AllStoryResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listOf(dummyStoryList)
        )

        return Response.success(allStoryResponse)
    }

}
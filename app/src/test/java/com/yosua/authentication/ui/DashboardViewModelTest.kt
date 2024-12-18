package com.yosua.authentication.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.yosua.authentication.DataDummy
import com.yosua.authentication.MainDispatcherRule
import com.yosua.authentication.getOrAwaitValue
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.StoryRepository
import com.yosua.authentication.model.remote.response.ListStoryItem
import com.yosua.authentication.view.main.DashboardViewModel
import com.yosua.authentication.view.main.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
@RunWith(MockitoJUnitRunner::class)
class DashboardViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository : StoryRepository

    @Mock
    private lateinit var appRepository : AppRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data : PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        @Suppress("UNCHECKED_CAST")
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStory.asFlow())
        val dashboardViewModel =
            DashboardViewModel(appRepository = appRepository, storyRepository = storyRepository)
        val actualStory : LiveData<PagingData<ListStoryItem>> =
            dashboardViewModel.stories.asLiveData()
        val result = actualStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(result)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest{
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStory.asFlow())

        val dashboardViewModel = DashboardViewModel(appRepository = appRepository, storyRepository = storyRepository)
        val actualStory: LiveData<PagingData<ListStoryItem>> = dashboardViewModel.stories.asLiveData()
        val result = actualStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(result)

        Assert.assertEquals(0,differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items : List<ListStoryItem>) : PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state : PagingState<Int, LiveData<List<ListStoryItem>>>) : Int? {
        return null
    }

    override suspend fun load(params : LoadParams<Int>) : LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), null, null)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position : Int, count : Int) {}
    override fun onRemoved(position : Int, count : Int) {}
    override fun onMoved(fromPosition : Int, toPosition : Int) {}
    override fun onChanged(position : Int, count : Int, payload : Any?) {}
}
